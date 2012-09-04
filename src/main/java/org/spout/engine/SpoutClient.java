/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine;

import java.awt.Color;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

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
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.spout.api.Client;
import org.spout.api.FileSystem;
import org.spout.api.Spout;
import org.spout.api.audio.SoundManager;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.entity.state.PlayerInputState.Flags;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.plugin.Platform;
import org.spout.api.plugin.PluginStore;
import org.spout.api.protocol.CommonPipelineFactory;
import org.spout.api.protocol.PortBinding;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.Session;
import org.spout.api.render.BasicCamera;
import org.spout.api.render.Camera;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.RenderMode;
import org.spout.engine.audio.SpoutSoundManager;
import org.spout.engine.batcher.PrimitiveBatch;
import org.spout.engine.command.InputManagementCommands;
import org.spout.engine.entity.SpoutClientPlayer;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.filesystem.ClientFileSystem;
import org.spout.engine.input.SpoutInput;
import org.spout.engine.listener.SpoutClientListener;
import org.spout.engine.listener.channel.SpoutClientConnectListener;
import org.spout.engine.protocol.SpoutClientSession;
import org.spout.engine.renderer.WorldRenderer;
import org.spout.engine.util.MacOSXUtils;
import org.spout.engine.util.thread.threadfactory.NamedThreadFactory;
import org.spout.engine.world.SpoutClientWorld;

public class SpoutClient extends SpoutEngine implements Client {
	private final SoundManager soundManager = new SpoutSoundManager();
	private final SpoutInput inputManager = new SpoutInput();
	private final String name = "Spout Client";
	private final Vector2 resolution = new Vector2(640, 480);
	private final boolean[] sides = { true, true, true, true, true, true };
	private final float aspectRatio = resolution.getX() / resolution.getY();
	private final FileSystem filesystem;

	private Camera activeCamera;
	private WorldRenderer worldRenderer;

	private final AtomicReference<SpoutClientSession> session = new AtomicReference<SpoutClientSession>();
	private SpoutPlayer activePlayer;
	private final AtomicReference<SpoutClientWorld> activeWorld = new AtomicReference<SpoutClientWorld>();
	private final AtomicReference<PortBinding> potentialBinding = new AtomicReference<PortBinding>();

	// Handle stopping
	private volatile boolean rendering = true;
	private String stopMessage = null;
	private final ClientBootstrap bootstrap = new ClientBootstrap();

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

		if (inJar) {
			unpackLwjgl();
		}
		ExecutorService executorBoss = Executors.newCachedThreadPool(new NamedThreadFactory("SpoutServer - Boss", true));
		ExecutorService executorWorker = Executors.newCachedThreadPool(new NamedThreadFactory("SpoutServer - Worker", true));
		ChannelFactory factory = new NioClientSocketChannelFactory(executorBoss, executorWorker);
		bootstrap.setFactory(factory);

		ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this, true);
		bootstrap.setPipelineFactory(pipelineFactory);
		super.init(args);

		getScheduler().startRenderThread();

	}

	@Override
	public void start() {
		start(false);
	}

	@Override
	public void start(boolean checkWorlds) {
		super.start(checkWorlds);
		getEventManager().registerEvents(new SpoutClientListener(this), this);
		CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(this));

		// Register commands
		getRootCommand().addSubCommands(this, InputManagementCommands.class, commandRegFactory);
		activePlayer = new SpoutClientPlayer("Spouty", this);
		activePlayer.setRotation(new Quaternion(0f, 0f, 0f, 0f));
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
	public SpoutInput getInput() {
		return inputManager;
	}
	
	public void doInput() {
		inputManager.pollInput();
		// TODO move this a plugin
		if (activePlayer == null) {
			return;
		}
		for (Flags f : activePlayer.input().getFlagSet()) {
			switch(f) {
				case FORWARD:
					activePlayer.setPosition(activePlayer.getPosition().add(activePlayer.getTransform().forwardVector()));
					break;
				case BACKWARD:
					activePlayer.setPosition(activePlayer.getPosition().subtract(activePlayer.getTransform().forwardVector()));
					break;
				case LEFT:
					activePlayer.setPosition(activePlayer.getPosition().add(activePlayer.getTransform().rightVector()));
					break;
				case RIGHT:
					activePlayer.setPosition(activePlayer.getPosition().subtract(activePlayer.getTransform().rightVector()));
					break;
				case CROUCH:
				case FIRE_1:
				case FIRE_2:
				case INTERACT:
				case JUMP:
				case SELECT_DOWN:
				case SELECT_UP:
			}
		}
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
	}

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

	@Override
	public SpoutClientWorld getDefaultWorld() {
		return activeWorld.get();
	}

	@Override
	public SpoutClientWorld worldChanged(String name, UUID uuid, byte[] datatable) {
		GenericDatatableMap map = new GenericDatatableMap();
		map.decompress(datatable);
		SpoutClientWorld world = new SpoutClientWorld(name, uuid, this, map, getEngineItemMap());
		SpoutClientWorld oldWorld = activeWorld.getAndSet(world);
		if (oldWorld != null) {
			oldWorld.unload(false);
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
		activePlayer.connect(session, null);
		session.setPlayer(activePlayer);
		onlinePlayers.putIfAbsent(activePlayer.getName(), activePlayer);
	}

	private PrimitiveBatch renderer;
	private RenderMaterial mat;
	
	public void initRenderer() {
		createWindow();

		Mouse.setGrabbed(true);

		getLogger().info("SpoutClient Information");
		getLogger().info("Operating System: " + System.getProperty("os.name"));
		getLogger().info("Renderer Mode: " + this.getRenderMode().toString());
		getLogger().info("OpenGL Information");
		getLogger().info("Vendor: " + GL11.glGetString(GL11.GL_VENDOR));
		getLogger().info("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION));
		getLogger().info("GLSL Version: " + GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
		getLogger().info("Max Textures: " + GL11.glGetString(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS));
		String extensions = "Extensions Supported: ";
		if (getArguments().renderMode == RenderMode.GL30) {
			for (int i = 0; i < GL11.glGetInteger(GL30.GL_NUM_EXTENSIONS); i++) {
				extensions += GL30.glGetStringi(GL11.GL_EXTENSIONS, i) + " ";
			}
		} else {
			extensions += GL11.glGetString(GL11.GL_EXTENSIONS);
		}
		getLogger().info(extensions);
		soundManager.init();
		Spout.getFilesystem().postStartup();

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor((135.f/255.0f), 206.f/255.f, 250.f/255.f, 0);

		//worldRenderer = new WorldRenderer(this);
		//worldRenderer.setup();

		renderer = new PrimitiveBatch();
		mat = (RenderMaterial)this.getFilesystem().getResource("material://Spout/resources/resources/materials/BasicMaterial.smt");
		renderer.begin();  	
		renderer.addCube(Vector3.ZERO, Vector3.ONE, Color.RED, sides);
		renderer.end();
	}


	public void render(float dt) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		//worldRenderer.render();

		Vector3 currentPlayerPos = new Vector3(activePlayer.getPosition().getBlockX(), activePlayer.getPosition().getBlockY(), activePlayer.getPosition().getBlockZ());
		activeCamera = new BasicCamera(MathHelper.createPerspective(75, aspectRatio, 0.001f, 1000), MathHelper.createLookAt(currentPlayerPos, currentPlayerPos.add(activePlayer.getTransform().forwardVector().normalize().add(5, 5, 5)), Vector3.UP));
		activeCamera.getFrustum().update(activeCamera.getProjection(), activeCamera.getView());
		Transform loc = new Transform(new Point(null, 0f, 0f, 0f), Quaternion.IDENTITY, Vector3.ONE);
		mat.getShader().setUniform("View", activeCamera.getView());
		mat.getShader().setUniform("Projection", activeCamera.getProjection());
		mat.getShader().setUniform("Model", loc.toMatrix());
		renderer.draw(mat);
	}

	public WorldRenderer getWorldRenderer() {
		return worldRenderer;
	}

	private void createWindow() {
		try {
			Display.setDisplayMode(new DisplayMode((int) resolution.getX(), (int) resolution.getY()));

			if (MacOSXUtils.isMac()) {
				createMacWindow();
			} else {
				if (getRenderMode() == RenderMode.GL11) {
					ContextAttribs ca = new ContextAttribs(1, 5);
					Display.create(new PixelFormat(8, 24, 0), ca);
				} else if (getRenderMode() == RenderMode.GL20) {
					ContextAttribs ca = new ContextAttribs(2, 1);
					Display.create(new PixelFormat(8, 24, 0), ca);
				} else if (getRenderMode() == RenderMode.GL30) {
					ContextAttribs ca = new ContextAttribs(3, 2).withForwardCompatible(false);
					Display.create(new PixelFormat(8, 24, 0), ca);
				}
			}

			Display.setTitle("Spout Client");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	private void createMacWindow() throws LWJGLException {
		if (getRenderMode() == RenderMode.GL30) {
			if (MacOSXUtils.getOSXVersion() >= 7) {
				ContextAttribs ca = new ContextAttribs(3, 2).withProfileCore(true);
				Display.create(new PixelFormat(8, 24, 0), ca);
			} else {
				throw new UnsupportedOperationException("Cannot create a 3.0 context without OSX 10.7_");
			}
		} else {
			Display.create();
		}
	}

	private static void unpackLwjgl() {
		String[] files;
		String osPath;

		if (SystemUtils.IS_OS_WINDOWS) {
			files = new String[] { "jinput-dx8_64.dll", "jinput-dx8.dll", "jinput-raw_64.dll", "jinput-raw.dll", "jinput-wintab.dll", "lwjgl.dll", "lwjgl64.dll", "OpenAL32.dll", "OpenAL64.dll" };
			osPath = "windows/";
		} else if (SystemUtils.IS_OS_MAC) {
			files = new String[] { "libjinput-osx.jnilib", "liblwjgl.jnilib", "openal.dylib", };
			osPath = "mac/";
		} else if (SystemUtils.IS_OS_LINUX) {
			files = new String[] { "liblwjgl.so", "liblwjgl64.so", "libopenal.so", "libopenal64.so", "libjinput-linux.so", "libjinput-linux64.so" };
			osPath = "linux/";
		} else {
			Spout.getEngine().getLogger().log(Level.SEVERE, "Error loading natives of operating system type: " + SystemUtils.OS_NAME);
			return;
		}

		File cacheDir = new File(System.getProperty("user.dir"), "natives/" + osPath);
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
	public FileSystem getFilesystem() {
		return filesystem;
	}

}
