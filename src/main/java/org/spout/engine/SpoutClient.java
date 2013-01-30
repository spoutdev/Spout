/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.spout.api.Client;
import org.spout.api.FileSystem;
import org.spout.api.Spout;
import org.spout.api.audio.SoundManager;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.component.impl.AnimationComponent;
import org.spout.api.component.impl.CameraComponent;
import org.spout.api.component.impl.HitBlockComponent;
import org.spout.api.component.impl.ModelHolderComponent;
import org.spout.api.datatable.SerializableMap;
import org.spout.api.entity.Entity;
import org.spout.api.event.engine.EngineStartEvent;
import org.spout.api.event.engine.EngineStopEvent;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.gui.ScreenStack;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.model.Model;
import org.spout.api.model.animation.Animation;
import org.spout.api.model.animation.AnimationPlayed;
import org.spout.api.plugin.Platform;
import org.spout.api.plugin.PluginStore;
import org.spout.api.protocol.CommonPipelineFactory;
import org.spout.api.protocol.PortBinding;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.Session;
import org.spout.api.render.Camera;
import org.spout.api.render.Font;
import org.spout.api.render.RenderMode;
import org.spout.engine.audio.SpoutSoundManager;
import org.spout.engine.command.InputManagementCommands;
import org.spout.engine.entity.SpoutClientPlayer;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.entity.component.ClientTextModelComponent;
import org.spout.engine.filesystem.ClientFileSystem;
import org.spout.engine.input.SpoutInputManager;
import org.spout.engine.listener.SpoutClientListener;
import org.spout.engine.listener.channel.SpoutClientConnectListener;
import org.spout.engine.protocol.SpoutClientSession;
import org.spout.engine.resources.ClientEntityPrefab;
import org.spout.engine.resources.ClientFont;
import org.spout.engine.scheduler.SpoutScheduler;
import org.spout.engine.util.thread.threadfactory.NamedThreadFactory;
import org.spout.engine.world.SpoutClientWorld;

public class SpoutClient extends SpoutEngine implements Client {
	private final SoundManager soundManager = new SpoutSoundManager();
	private final String name = "Spout Client";
	private final FileSystem filesystem;
	private Camera activeCamera;
	private final AtomicReference<SpoutClientSession> session = new AtomicReference<SpoutClientSession>();
	private SpoutPlayer activePlayer;
	private final AtomicReference<SpoutClientWorld> activeWorld = new AtomicReference<SpoutClientWorld>();
	private final AtomicReference<PortBinding> potentialBinding = new AtomicReference<PortBinding>();
	// Handle stopping
	private volatile boolean rendering = true;
	private String stopMessage = null;
	private final ClientBootstrap bootstrap = new ClientBootstrap();
	private boolean ccoverride = false;
	SpoutRenderer renderer;
	private SpoutInputManager inputManager;

	public SpoutClient() {
		this.filesystem = new ClientFileSystem();
	}

	@Override
	public void init(SpoutApplication args) {
		boolean inJar = false;

		try {
			CodeSource cs = SpoutClient.class.getProtectionDomain().getCodeSource();
			inJar = cs.getLocation().toURI().getPath().endsWith(".jar");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if (inJar || args.path != null) {
			unpackLwjgl(args.path);
		}

		ExecutorService executorBoss = Executors.newCachedThreadPool(new NamedThreadFactory("SpoutServer - Boss", true));
		ExecutorService executorWorker = Executors.newCachedThreadPool(new NamedThreadFactory("SpoutServer - Worker", true));
		ChannelFactory factory = new NioClientSocketChannelFactory(executorBoss, executorWorker);
		bootstrap.setFactory(factory);

		ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this, true);
		bootstrap.setPipelineFactory(pipelineFactory);
		super.init(args);

		this.ccoverride = args.ccoverride;

		inputManager = new SpoutInputManager();
	}

	@Override
	public void start() {
		start(true);
	}
	
	@Override
	public void start(boolean checkWorlds) {
		super.start(checkWorlds);

		getEventManager().registerEvents(new SpoutClientListener(this), this);
		CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(this));

		// Register commands
		getRootCommand().addSubCommands(this, InputManagementCommands.class, commandRegFactory);

		while (super.getDefaultWorld() == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// TODO : Wait until the world is fully loaded
		}
		
		((SpoutScheduler) Spout.getScheduler()).coreSafeRun("Client setup task", new Runnable() {
			public void run() {
				activePlayer = new SpoutClientPlayer("Spouty", getDefaultWorld().getSpawnPoint(), SpoutConfiguration.VIEW_DISTANCE.getInt() * Chunk.BLOCKS.SIZE);
				activeCamera = activePlayer.add(CameraComponent.class);
				activePlayer.add(HitBlockComponent.class);
				getActiveWorld().spawnEntity(activePlayer);
				Font font = (ClientFont) Spout.getFilesystem().getResource("font://Spout/fonts/ubuntu/Ubuntu-M.ttf");

				for(int i = 0; i < 10; i ++){
					for(int j = 0; j < 10; j ++){

						// Test
						ClientEntityPrefab spoutyType = (ClientEntityPrefab) Spout.getFilesystem().getResource("entity://Spout/entities/Spouty/spouty.sep");

						Entity e = spoutyType.createEntity(getDefaultWorld().getSpawnPoint().getPosition().add(i * 5, 0, j * 5));
						e.setSavable(false); // To prevent entity duplication

						//Animation part
						ModelHolderComponent modelHolderComponent = e.get(ModelHolderComponent.class);
						//EntityRendererComponent renderComponent = e.get(EntityRendererComponent.class);
						AnimationComponent animationComponent = e.get(AnimationComponent.class);
						
						Model model = modelHolderComponent.getModels().get(0);
						
						Animation a1 = model.getAnimations().get("animatest1");
						Animation a2 = model.getAnimations().get("animatest2");

						//Launch first animation
						AnimationPlayed ac = animationComponent.playAnimation(model, a1, true);
						ac.setCurrentTime(j * 0.5f);
						ac.setSpeed(j * 0.5f);

						ClientTextModelComponent tmc = e.add(ClientTextModelComponent.class);
						tmc.setText(new ChatArguments(ChatStyle.BLUE, "Sp", ChatStyle.WHITE, "ou", ChatStyle.RED, "ty"));
						tmc.setSize(0.5f);
						tmc.setTranslation(new Vector3(0, 3f, 0));
						tmc.setFont(font);

						getActiveWorld().spawnEntity(e);
					}
				}

				//The render need the active player to find the world to draw, so we start it after initialize player
				renderer = getScheduler().startRenderThread(new Vector2(1204, 796), ccoverride, null);
			}
		});
		
		getScheduler().startGuiThread();
		
		//TODO Maybe a better way of alerting plugins the client is done?
		if (EngineStartEvent.getHandlerList().getRegisteredListeners().length != 0) {
			Spout.getEventManager().callEvent(new EngineStartEvent());
		}
	}

	@Override
	public SpoutPlayer getActivePlayer() {
		return activePlayer;
	}

	@Override
	public CommandSource getCommandSource() {
		if (session.get() != null) {
			return activePlayer;
		} else {
			return super.getCommandSource();
		}
	}

	@Override
	public Camera getActiveCamera() {
		return activeCamera;
	}

	@Override
	public void setActiveCamera(Camera activeCamera) {
		this.activeCamera = activeCamera;
	}

	@Override
	public PluginStore getPluginStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Platform getPlatform() {
		return Platform.CLIENT;
	}

	@Override
	public RenderMode getRenderMode() {
		return getArguments().renderMode;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public SoundManager getSoundManager() {
		return soundManager;
	}

	@Override
	public SpoutInputManager getInputManager() {
		return this.inputManager;
	}

	@Override
	public PortBinding getAddress() {
		return session.get().getActiveAddress();
	}

	@Override
	public boolean stop(String message) {
		if (!super.stop(message, false)) {
			return false;
		}
		rendering = false;
		stopMessage = message;
		Runnable finalTask = new Runnable() {
			@Override
			public void run() {
				EngineStopEvent stopEvent = new EngineStopEvent(stopMessage);
				getEventManager().callEvent(stopEvent);
				stopMessage = stopEvent.getMessage();

				bootstrap.getFactory().releaseExternalResources();
				boundProtocols.clear();
			}
		};
		getScheduler().submitFinalTask(finalTask, true);
		getScheduler().stop();
		return true;
	}

	public boolean isRendering() {
		return rendering;
	}

	public void stopEngine() {
		if (rendering) {
			throw new IllegalStateException("Client is still rendering!");
		}
		super.stop(stopMessage);
	}

	/*@Override //Because there is a conflict when the spout engine tries to load the world
	public SpoutClientWorld getWorld(String name, boolean exact) {
		SpoutClientWorld world = activeWorld.get();
		if (world == null) {
			return null;
		}

		if ((exact && world.getName().equals(name))
				|| world.getName().startsWith(name)) {
			return world;
		} else {
			return null;
		}
	}*/

	@Override
	public SpoutClientWorld getWorld(UUID uid) {
		SpoutClientWorld world = activeWorld.get();
		if (world != null && world.getUID().equals(uid)) {
			return world;
		} else {
			return null;
		}
	}

	@Override
	public Collection<World> getWorlds() {
		return Collections.<World>singletonList(activeWorld.get());
	}

	/*@Override
	public SpoutClientWorld getDefaultWorld() {
		return activeWorld.get();
	}*/

	@Override
	public SpoutClientWorld worldChanged(String name, UUID uuid, byte[] data) {
		SpoutClientWorld world = new SpoutClientWorld(name, uuid, this, getEngineItemMap(), getEngineItemMap());

		SerializableMap map = world.getComponentHolder().getData();
		try {
			map.deserialize(data);
		} catch (IOException e) {
			throw new RuntimeException("Unable to deserialize data", e);
		}

		SpoutClientWorld oldWorld = activeWorld.getAndSet(world);
		if (oldWorld != null) {
			if (!scheduler.removeAsyncManager(oldWorld)) {
				throw new IllegalStateException("Unable to remove old world from scheduler");
			}
			oldWorld.unload(false);
		}
		if (!scheduler.addAsyncManager(world)) {
			activeWorld.compareAndSet(world, null);
			throw new IllegalStateException("Unable to add new world to the scheduler");
		}
		return world;
	}

	public ClientBootstrap getBootstrap() {
		return bootstrap;
	}

	@Override
	public SpoutClientSession newSession(Channel channel) {
		Protocol protocol = potentialBinding.getAndSet(null).getProtocol();
		return new SpoutClientSession(this, channel, protocol);
	}

	public void connect(final PortBinding binding) {
		potentialBinding.set(binding);
		getBootstrap().connect(binding.getAddress()).addListener(new SpoutClientConnectListener(this, binding));
	}

	public void disconnected() {
		Session sess = this.session.getAndSet(null);
		if (sess != null) {
			getSessionRegistry().remove(sess);
		}
	}

	public void setSession(SpoutClientSession session) {
		this.session.set(session);
		getSessionRegistry().add(session);
		activePlayer.connect(session, activePlayer.getTransform().getTransform());
		session.setPlayer(activePlayer);
		players.putIfAbsent(activePlayer.getName(), activePlayer);
	}

	@Override
	public FileSystem getFilesystem() {
		return filesystem;
	}

	public World getActiveWorld() {
		return getActivePlayer().getWorld();
	}

	private static void unpackLwjgl(String path) {
		String[] files;
		String osPath;

		if (SystemUtils.IS_OS_WINDOWS) {
			files = new String[]{"jinput-dx8_64.dll", "jinput-dx8.dll", "jinput-raw_64.dll", "jinput-raw.dll", "jinput-wintab.dll", "lwjgl.dll", "lwjgl64.dll", "OpenAL32.dll", "OpenAL64.dll"};
			osPath = "windows/";
		} else if (SystemUtils.IS_OS_MAC) {
			files = new String[]{"libjinput-osx.jnilib", "liblwjgl.jnilib", "openal.dylib",};
			osPath = "mac/";
		} else if (SystemUtils.IS_OS_LINUX) {
			files = new String[]{"liblwjgl.so", "liblwjgl64.so", "libopenal.so", "libopenal64.so", "libjinput-linux.so", "libjinput-linux64.so"};
			osPath = "linux/";
		} else {
			Spout.getEngine().getLogger().log(Level.SEVERE, "Error loading natives of operating system type: " + SystemUtils.OS_NAME);
			return;
		}

		File cacheDir = new File(path == null ? System.getProperty("user.dir") : path, "natives" + File.separator + osPath);
		cacheDir.mkdirs();
		for (String f : files) {
			File outFile = new File(cacheDir, f);
			if (!outFile.exists()) {
				try {
					FileUtils.copyInputStreamToFile(SpoutClient.class.getResourceAsStream("/" + f), outFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		String nativePath = cacheDir.getAbsolutePath();
		System.setProperty("org.lwjgl.librarypath", nativePath);
		System.setProperty("net.java.games.input.librarypath", nativePath);
	}

	@Override
	public Vector2 getResolution() {
		return renderer.getResolution();
	}

	@Override
	public float getAspectRatio() {
		return renderer.getAspectRatio();
	}

	@Override
	public ScreenStack getScreenStack() {
		return renderer.getScreenStack();
	}

	public SpoutRenderer getRenderer() {
		return renderer;
	}
}
