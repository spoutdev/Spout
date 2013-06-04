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

import java.awt.Dimension;
import java.awt.Toolkit;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import org.spout.api.Client;
import org.spout.api.Platform;
import org.spout.api.audio.SoundManager;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.AnnotatedCommandExecutorFactory;
import org.spout.api.component.entity.CameraComponent;
import org.spout.api.datatable.SerializableMap;
import org.spout.api.event.engine.EngineStartEvent;
import org.spout.api.event.engine.EngineStopEvent;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.math.Vector2;
import org.spout.api.plugin.PluginStore;
import org.spout.api.protocol.CommonPipelineFactory;
import org.spout.api.protocol.PortBinding;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.Session;
import org.spout.api.render.RenderMode;
import org.spout.api.resource.FileSystem;

import org.spout.engine.audio.AudioConfiguration;
import org.spout.engine.audio.SpoutSoundManager;
import org.spout.engine.command.InputCommands;
import org.spout.engine.command.RendererCommands;
import org.spout.engine.entity.SpoutClientPlayer;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.filesystem.ClientFileSystem;
import org.spout.engine.gui.SpoutScreenStack;
import org.spout.engine.input.SpoutInputManager;
import org.spout.engine.listener.SpoutClientListener;
import org.spout.engine.listener.channel.SpoutClientConnectListener;
import org.spout.engine.protocol.SpoutClientSession;
import org.spout.engine.util.thread.threadfactory.NamedThreadFactory;
import org.spout.engine.world.SpoutClientWorld;
import org.spout.engine.world.SpoutWorld;

public class SpoutClient extends SpoutEngine implements Client {
	private final AtomicReference<PortBinding> potentialBinding = new AtomicReference<PortBinding>();
	private final AtomicReference<SpoutClientSession> session = new AtomicReference<SpoutClientSession>();
	private final AtomicReference<SpoutClientWorld> world = new AtomicReference<SpoutClientWorld>();
	private final ClientBootstrap bootstrap = new ClientBootstrap();
	private final FileSystem filesystem = new ClientFileSystem();
	// Handle stopping
	private volatile boolean rendering = true;
	private boolean ccoverride = false;
	private String stopMessage = null;
	private SpoutRenderer renderer;
	private SoundManager soundManager;
	private SpoutInputManager inputManager;

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

		ExecutorService executorBoss = Executors.newCachedThreadPool(new NamedThreadFactory("SpoutClient - Boss", true));
		ExecutorService executorWorker = Executors.newCachedThreadPool(new NamedThreadFactory("SpoutClient - Worker", true));
		ChannelFactory factory = new NioClientSocketChannelFactory(executorBoss, executorWorker);
		bootstrap.setFactory(factory);

		ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this, true);
		bootstrap.setPipelineFactory(pipelineFactory);
		super.init(args);

		this.ccoverride = args.ccoverride;

		inputManager = new SpoutInputManager();

		// initialize sound system
		soundManager = new SpoutSoundManager();
		soundManager.init();

		// configure sound system
		AudioConfiguration audioConfig = new AudioConfiguration();
		audioConfig.load();
		soundManager.setGain(AudioConfiguration.SOUND_VOLUME.getFloat());
		soundManager.setMusicGain(AudioConfiguration.MUSIC_VOLUME.getFloat());
	}

	@Override
	public void start() {
		start(true);
	}

	@Override
	public void start(boolean checkWorlds) {
		super.start(checkWorlds);

		getEventManager().registerEvents(new SpoutClientListener(this), this);

		// Register commands
		AnnotatedCommandExecutorFactory.create(new InputCommands(this));
		AnnotatedCommandExecutorFactory.create(new RendererCommands(this));

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.renderer = getScheduler().startRenderThread(new Vector2(dim.getWidth() * 0.75f, dim.getHeight() * 0.75f), ccoverride, null);
		getScheduler().startGuiThread();

		//TODO Maybe a better way of alerting plugins the client is done?
		if (EngineStartEvent.getHandlerList().getRegisteredListeners().length != 0) {
			getEventManager().callEvent(new EngineStartEvent());
		}

		filesystem.postStartup();
	}

	@Override
	public SpoutPlayer getPlayer() {
		//TODO This is bad, rethink this
		return (SpoutClientPlayer) world.get().getPlayers().get(0);
	}

	@Override
	public CommandSource getCommandSource() {
		if (session.get() != null) {
			return getPlayer();
		} else {
			return super.getCommandSource();
		}
	}

	@Override
	public PluginStore getPluginStore() {
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
		return "Spout Client";
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

		// de-init OpenAL
		soundManager.destroy();
		soundManager = null;

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

	@Override
	public SpoutClientWorld getWorld(String name, boolean exact) {
		SpoutClientWorld world = this.world.get();
		if (world == null) {
			return null;
		}

		if ((exact && world.getName().equals(name))
				|| world.getName().startsWith(name)) {
			return world;
		} else {
			return null;
		}
	}

	@Override
	public SpoutWorld getWorld(UUID uid) {
		SpoutWorld world = this.world.get();
		if (world != null && world.getUID().equals(uid)) {
			return world;
		} else {
			return null;
		}
	}

	@Override
	public Collection<World> getWorlds() {
		return Collections.<World>singletonList(world.get());
	}

	@Override
	public SpoutClientWorld getDefaultWorld() {
		return world.get();
	}

	public SpoutClientWorld worldChanged(String name, UUID uuid, byte[] data) {
		SpoutClientWorld world = new SpoutClientWorld(name, this, uuid, getEngineItemMap(), getEngineItemMap());

		//Load in datatable
		SerializableMap map = world.getDatatable();
		try {
			map.deserialize(data);
		} catch (IOException e) {
			throw new RuntimeException("Unable to deserialize data", e);
		}

		SpoutWorld oldWorld = this.world.getAndSet(world);
		if (oldWorld != null) {
			if (!scheduler.removeAsyncManager(oldWorld)) {
				throw new IllegalStateException("Unable to remove old world from scheduler");
			}
		}
		if (!scheduler.addAsyncManager(world)) {
			this.world.compareAndSet(world, null);
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
		//TODO Re-write Client sessions, this is bad...
		this.session.set(session);
		getSessionRegistry().add(session);
		final SpoutClientPlayer p = new SpoutClientPlayer(this, "Spouty", null, SpoutConfiguration.VIEW_DISTANCE.getInt() * Chunk.BLOCKS.SIZE);
		p.connect(session, p.getScene().getTransform());
		session.setPlayer(p);
		players.putIfAbsent(p.getName(), p);
	}

	@Override
	public FileSystem getFileSystem() {
		return filesystem;
	}

	@Override
	public World getWorld() {
		return world.get();
	}

	private void unpackLwjgl(String path) {
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
			getLogger().severe("Error loading natives of operating system type: " + SystemUtils.OS_NAME);
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
	public SpoutScreenStack getScreenStack() {
		return renderer.getScreenStack();
	}

	public SpoutRenderer getRenderer() {
		return renderer;
	}
}
