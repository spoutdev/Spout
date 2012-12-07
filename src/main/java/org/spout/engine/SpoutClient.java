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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
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
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.component.components.CameraComponent;
import org.spout.api.component.components.HitBlockComponent;
import org.spout.api.component.components.PhysicsComponent;
import org.spout.api.component.components.PredictableTransformComponent;
import org.spout.api.datatable.SerializableMap;
import org.spout.api.entity.Entity;
import org.spout.api.entity.state.PlayerInputState;
import org.spout.api.event.server.ClientEnableEvent;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.gui.FullScreen;
import org.spout.api.gui.Screen;
import org.spout.api.gui.ScreenStack;
import org.spout.api.gui.Widget;
import org.spout.api.input.Keyboard;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.model.Model;
import org.spout.api.plugin.Platform;
import org.spout.api.plugin.PluginStore;
import org.spout.api.protocol.CommonPipelineFactory;
import org.spout.api.protocol.PortBinding;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.Session;
import org.spout.api.render.Camera;
import org.spout.api.render.RenderMode;

import org.spout.engine.audio.SpoutSoundManager;
import org.spout.engine.batcher.SpriteBatch;
import org.spout.engine.command.InputManagementCommands;
import org.spout.engine.entity.SpoutClientPlayer;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.entity.component.ClientTextModelComponent;
import org.spout.engine.entity.component.EntityRendererComponent;
import org.spout.engine.filesystem.ClientFileSystem;
import org.spout.engine.input.SpoutInputConfiguration;
import org.spout.engine.input.SpoutInputManager;
import org.spout.engine.listener.SpoutClientListener;
import org.spout.engine.listener.channel.SpoutClientConnectListener;
import org.spout.engine.mesh.BaseMesh;
import org.spout.engine.protocol.SpoutClientSession;
import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.renderer.WorldRenderer;
import org.spout.engine.resources.ClientEntityPrefab;
import org.spout.engine.resources.ClientFont;
import org.spout.engine.util.MacOSXUtils;
import org.spout.engine.util.thread.lock.SpoutSnapshotLock;
import org.spout.engine.util.thread.threadfactory.NamedThreadFactory;
import org.spout.engine.world.SpoutClientWorld;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class SpoutClient extends SpoutEngine implements Client {
	private final SoundManager soundManager = new SpoutSoundManager();
	private final SpoutInputManager inputManager = new SpoutInputManager();
	private final String name = "Spout Client";
	private final Vector2 resolution = new Vector2(1024, 768);
	private final boolean[] sides = {true, true, true, true, true, true};
	private final float aspectRatio = resolution.getX() / resolution.getY();
	private final FileSystem filesystem;
	private Camera activeCamera;
	private WorldRenderer worldRenderer;
	private final AtomicReference<SpoutClientSession> session = new AtomicReference<SpoutClientSession>();
	private SpoutPlayer activePlayer;
	private final AtomicReference<SpoutClientWorld> activeWorld = new AtomicReference<SpoutClientWorld>();
	private final AtomicReference<PortBinding> potentialBinding = new AtomicReference<PortBinding>();
	private boolean ccoverride = false;
	// Handle stopping
	private volatile boolean rendering = true;
	private String stopMessage = null;
	private final ClientBootstrap bootstrap = new ClientBootstrap();
	private boolean wireframe = false;
	// Gui
	private SpriteBatch gui;
	private ScreenStack screenStack;
	private ClientFont font;
	private boolean showDebugInfos = true;
	private ConcurrentLinkedQueue<Runnable> renderTaskQueue = new ConcurrentLinkedQueue<Runnable>();
	
	
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

		this.ccoverride = args.ccoverride;

		inputManager.bind(Keyboard.get(SpoutInputConfiguration.FORWARD.getString()), "forward");
		inputManager.bind(Keyboard.get(SpoutInputConfiguration.BACKWARD.getString()), "backward");
		inputManager.bind(Keyboard.get(SpoutInputConfiguration.LEFT.getString()), "left");
		inputManager.bind(Keyboard.get(SpoutInputConfiguration.RIGHT.getString()), "right");
		inputManager.bind(Keyboard.get(SpoutInputConfiguration.UP.getString()), "jump");
		inputManager.bind(Keyboard.get(SpoutInputConfiguration.DOWN.getString()), "crouch");
		inputManager.bind(Keyboard.KEY_F3, "debug_info");
		inputManager.bind(org.spout.api.input.Mouse.MOUSE_SCROLLDOWN, "select_down");
		inputManager.bind(org.spout.api.input.Mouse.MOUSE_SCROLLUP, "select_up");
		inputManager.bind(org.spout.api.input.Mouse.MOUSE_BUTTON0, "left_click");
		inputManager.bind(org.spout.api.input.Mouse.MOUSE_BUTTON1, "interact");
		inputManager.bind(org.spout.api.input.Mouse.MOUSE_BUTTON2, "fire_2");
	}

	@Override
	public void start() {
		start(true);
	}

	@Override
	public void start(boolean checkWorlds) {
		// Building the screenStack
		FullScreen mainScreen = new FullScreen();
		mainScreen.setTakesInput(false);
		screenStack = new ScreenStack(mainScreen);

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
		font = (ClientFont) Spout.getFilesystem().getResource("font://Spout/resources/resources/fonts/ubuntu/Ubuntu-M.ttf");
		activePlayer = new SpoutClientPlayer("Spouty", super.getDefaultWorld().getSpawnPoint(), SpoutConfiguration.VIEW_DISTANCE.getInt() * Chunk.BLOCKS.SIZE);
		activeCamera = activePlayer.add(CameraComponent.class);
		activePlayer.add(HitBlockComponent.class);
		super.getDefaultWorld().spawnEntity(activePlayer);

		getScheduler().startRenderThread();
		getScheduler().startGuiThread();

		//TODO Maybe a better way of alerting plugins the client is done?
		if (ClientEnableEvent.getHandlerList().getRegisteredListeners().length != 0) {
			Spout.getEventManager().callEvent(new ClientEnableEvent());
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
		return inputManager;
	}

	public void doInput(float dt) {
		// TODO move this a plugin

		if (activePlayer == null) {
			return;
		}

		inputManager.pollInput(activePlayer);

		PlayerInputState inputState = activePlayer.input();
		Transform ts = activePlayer.getTransform().getTransformLive();
		ts.setRotation(MathHelper.rotation(inputState.pitch(), inputState.yaw(), ts.getRotation().getRoll()));

		Point point = ts.getPosition();
		if (inputState.getForward()) {
			point = point.subtract(ts.forwardVector().multiply(activeCamera.getSpeed()).multiply(dt));
		}
		if (inputState.getBackward()) {
			point = point.add(ts.forwardVector().multiply(activeCamera.getSpeed()).multiply(dt));
		}
		if (inputState.getLeft()) {
			point = point.subtract(ts.rightVector().multiply(activeCamera.getSpeed()).multiply(dt));
		}
		if (inputState.getRight()) {
			point = point.add(ts.rightVector().multiply(activeCamera.getSpeed()).multiply(dt));
		}
		if (inputState.getJump()) {
			point = point.add(ts.upVector().multiply(activeCamera.getSpeed()).multiply(dt));
		}
		if (inputState.getCrouch()) {
			point = point.subtract(ts.upVector().multiply(activeCamera.getSpeed()).multiply(dt));
		}
		ts.setPosition(point);

		activePlayer.getTransform().setTransform(ts);
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
		SpoutClientWorld world = new SpoutClientWorld(name, uuid, this, getEngineItemMap());
		
		SerializableMap map = world.getComponentHolder().getData();
		try {
			map.deserialize(data);
		} catch (IOException e) {
			throw new RuntimeException("Unable to deserialize data", e);
		}

		SpoutClientWorld oldWorld = activeWorld.getAndSet(world);
		if (oldWorld != null) {
			if (!oldWorld.getExecutor().haltExecutor()) {
				throw new IllegalStateException("Executor was already halted when halting was attempted");
			}
			oldWorld.unload(false);
		}
		if (!world.getExecutor().startExecutor()) {
			activeWorld.compareAndSet(world, null);
			throw new IllegalStateException("Unable to start executor for new world");
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

	public void initRenderer() {
		createWindow();

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
		//soundManager.init();
		Spout.getFilesystem().postStartup();

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glFrontFace(GL11.GL_CW);
		GL11.glCullFace(GL11.GL_BACK);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glClearColor((135.f / 255.0f), 206.f / 255.f, 250.f / 255.f, 1);

		//Init pool of BatchVertexRenderer
		BatchVertexRenderer.initPool(GL11.GL_TRIANGLES, 10000);
		
		worldRenderer = new WorldRenderer(this);

		gui = SpriteBatch.createSpriteBatch(getRenderMode(), resolution.getX(), resolution.getY());

		// Test
		ClientEntityPrefab spoutyType = (ClientEntityPrefab) Spout.getFilesystem().getResource("entity://Spout/resources/resources/entities/Spouty/spouty.sep");

		Entity e = spoutyType.createEntity(super.getDefaultWorld().getSpawnPoint().getPosition());
		e.setSavable(false); // To prevent entity duplication
		ClientTextModelComponent tmc = e.add(ClientTextModelComponent.class);
		tmc.setText(new ChatArguments(ChatStyle.BLUE, "Sp", ChatStyle.WHITE, "ou", ChatStyle.RED, "ty"));
		tmc.setSize(0.5f);
		tmc.setTranslation(new Vector3(0, 3f, 0));
		tmc.setFont(font);

		super.getDefaultWorld().spawnEntity(e);
	}

	public void updateRender(long limit) {
		worldRenderer.update(limit);
	}

	public void render(float dt) {

		while (renderTaskQueue.peek() != null) {
			Runnable task = renderTaskQueue.poll();
			task.run();
		}

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		Model skydome = (Model)super.getDefaultWorld().getDataMap().get("Skydome");
		if (skydome != null) {
			skydome.getRenderMaterial().getShader().setUniform("View", MathHelper.createIdentity());
			skydome.getRenderMaterial().getShader().setUniform("Projection", getActiveCamera().getProjection());
			BaseMesh skydomeMesh = (BaseMesh)skydome.getMesh();
			if (!skydomeMesh.isBatched()) {
				skydomeMesh.batch();
			}
			skydomeMesh.render(skydome.getRenderMaterial());
		}
		
		doInput(dt);

		for (Entity e : super.getDefaultWorld().getAll()) {
			((PredictableTransformComponent) e.getTransform()).updateRender(dt);
		}

		activeCamera.updateView();

		Mouse.setGrabbed(screenStack.getVisibleScreens().getLast().grabsMouse());

		worldRenderer.render();

		//TODO Remove this when we use SpoutClientWorld
		SpoutSnapshotLock lock = (SpoutSnapshotLock) getScheduler().getSnapshotLock();
		lock.coreReadLock("Render Thread - Render Entities");
		for (Entity e : super.getDefaultWorld().getAll()) {
			EntityRendererComponent r = e.get(EntityRendererComponent.class);
			if (r != null) {
				r.update();
				r.render(activeCamera);
			}
		}
		lock.coreReadUnlock("Render Thread - Render Entities");

		if (wireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}
		gui.begin();
		if (showDebugInfos) {
			Point position = activePlayer.getTransform().getPosition();
			gui.drawText(new ChatArguments("Spout client! Logged as ", ChatStyle.RED, activePlayer.getDisplayName(), ChatStyle.RESET, " in world: ", ChatStyle.RED, getDefaultWorld().getName()), font, -0.95f, 0.9f, 10f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "x: ", position.getX()), font, -0.95f, 0.8f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "y: ", position.getY()), font, -0.95f, 0.7f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "z: ", position.getZ()), font, -0.95f, 0.6f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "fps: ", getScheduler().getFps()), font, -0.95f, 0.5f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "batch: ", worldRenderer.getRended() + "/" + worldRenderer.getBatchWaiting()), font, -0.95f, 0.4f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "ocluded: ", (int) ((float) worldRenderer.getOcluded() / worldRenderer.getRended() * 100) + "%"), font, -0.95f, 0.3f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "culled: ", (int) ((float) worldRenderer.getCulled() / worldRenderer.getRended() * 100), "%"), font, -0.95f, 0.2f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "Update: ", worldRenderer.minUpdate + " / " + worldRenderer.maxUpdate + " / " + (worldRenderer.sumUpdate / Math.max(1, worldRenderer.count))), font, -0.95f, 0.1f, 8f);
			gui.drawText(new ChatArguments(ChatStyle.BLUE, "Render: ", worldRenderer.minRender + " / " + worldRenderer.maxRender + " / " + (worldRenderer.sumRender / Math.max(1, worldRenderer.count))), font, -0.95f, 0.0f, 8f);
		}
		for (Screen screen : screenStack.getVisibleScreens()) {
			for (Widget widget : screen.getWidgets()) {
				gui.draw(widget.getRenderParts());
			}
		}
		gui.render();
		if (wireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		}
	}

	public void toggleDebugInfos() {
		showDebugInfos = !showDebugInfos;
	}

	public WorldRenderer getWorldRenderer() {
		return worldRenderer;
	}

	public ScreenStack getScreenStack() {
		return screenStack;
	}

	@Override
	public Vector2 getResolution() {
		return resolution;
	}

	@Override
	public float getAspectRatio() {
		return aspectRatio;
	}

	private void createWindow() {
		try {
			Display.setDisplayMode(new DisplayMode((int) resolution.getX(), (int) resolution.getY()));

			//Override using ContextAttribs for some videocards that don't support ARB_CREATE_CONTEXT
			if (ccoverride) {
				Display.create(new PixelFormat(8, 24, 0));
				Display.setTitle("Spout Client");
				return;
			}

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

	public void enqueueTask(Runnable task) {
		renderTaskQueue.add(task);
	}

	public void toggleWireframe() {
		if (wireframe) {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			wireframe = false;
		} else {
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
			wireframe = true;
		}
	}
}
