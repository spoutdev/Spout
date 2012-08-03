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

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
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
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;
import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.command.CommandRegistrationsFactory;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.AnnotatedCommandRegistrationFactory;
import org.spout.api.command.annotated.SimpleInjector;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.keyboard.Input;
import org.spout.api.material.BlockMaterial;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.plugin.Platform;
import org.spout.api.plugin.PluginStore;
import org.spout.api.protocol.CommonPipelineFactory;
import org.spout.api.protocol.PortBinding;
import org.spout.api.protocol.Protocol;
import org.spout.api.render.BasicCamera;
import org.spout.api.render.Camera;
import org.spout.api.render.RenderMode;
import org.spout.api.util.StringMap;
import org.spout.engine.command.InputCommands;
import org.spout.engine.filesystem.ClientFileSystem;
import org.spout.engine.input.SpoutInput;
import org.spout.engine.listener.SpoutClientConnectListener;
import org.spout.engine.listener.SpoutClientListener;
import org.spout.engine.mesh.BaseMesh;
import org.spout.engine.player.SpoutPlayer;
import org.spout.engine.protocol.SpoutClientSession;
import org.spout.engine.renderer.WorldRenderer;
import org.spout.engine.util.MacOSXUtils;
import org.spout.engine.util.thread.threadfactory.NamedThreadFactory;
import org.spout.engine.world.SpoutWorld;

public class SpoutClient extends SpoutEngine implements Client {
	private final Input inputManager = new SpoutInput();
	private final String name = "Spout Client";
	private final Vector2 resolution = new Vector2(640, 480);
	private final boolean[] sides = { true, true, true, true, true, true };
	private final float aspectRatio = resolution.getX() / resolution.getY();

	private Camera activeCamera;
	private WorldRenderer worldRenderer;

	private final AtomicReference<SpoutClientSession> session = new AtomicReference<SpoutClientSession>();
	private SpoutPlayer activePlayer;

	// Handle stopping
	private volatile boolean rendering = true;
	private String stopMessage = null;
	private final ClientBootstrap bootstrap = new ClientBootstrap();

	private BaseMesh cube;
	private Transform loc;

	public SpoutClient() {
		this.filesystem = new ClientFileSystem();
	}

	@Override
	public void init(Arguments args) {
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
	}

	@Override
	public void start() {
		start(false);
	}

	@Override
	public void start(boolean checkWorlds) {
		super.start(checkWorlds);
		scheduler.startRenderThread();
		getEventManager().registerEvents(new SpoutClientListener(this), this);
		CommandRegistrationsFactory<Class<?>> commandRegFactory = new AnnotatedCommandRegistrationFactory(new SimpleInjector(this));

		// Register commands
		getRootCommand().addSubCommands(this, InputCommands.class, commandRegFactory);
		activePlayer = new SpoutPlayer("Spouty", this);
		// activePlayer = new SpoutPlayer("Spouty");
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
	public Input getInput() {
		return inputManager;
	}

	@Override
	public PortBinding getAddress() {
		return session.get().getActiveAddress();
	}

	@Override
	public void stop(String message) {
		rendering = false;
		stopMessage = message;
		Runnable finalTask = new Runnable() {
			public void run() {
				bootstrap.getFactory().releaseExternalResources();
				boundProtocols.clear();
			}
		};
		scheduler.submitFinalTask(finalTask);
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

	public SpoutWorld updateWorld(String name, UUID uuid, byte[] datatable) {
		DatatableMap map = new GenericDatatableMap();
		map.decompress(datatable);

		return new SpoutWorld(name, this, 0, 0, null, uuid, new StringMap(getEngineItemMap(), new MemoryStore<Integer>(), 0, Short.MAX_VALUE, name + "ItemMap"), map);
	}

	public ClientBootstrap getBootstrap() {
		return bootstrap;
	}

	@Override
	public SpoutClientSession newSession(Channel channel) {
		Protocol protocol = getProtocol(channel.getLocalAddress());
		return new SpoutClientSession(this, channel, protocol);
	}

	public void connect(final PortBinding binding) {
		getBootstrap().connect(binding.getAddress()).addListener(new SpoutClientConnectListener(this, binding));
	}

	public void disconnected() {
		this.session.set(null);
	}

	public void setSession(SpoutClientSession session) {
		this.session.set(session);
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

		Spout.getFilesystem().postStartup();

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		loc = new Transform(new Point(null, 0, 0, 0), Quaternion.IDENTITY, Vector3.ONE);

		GL11.glClearColor(1, 1, 1, 0);

		worldRenderer = new WorldRenderer(this);
		worldRenderer.setup();
	}

	public void render(float dt) {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		worldRenderer.render();
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

	private Color getColor(BlockMaterial m) {
		if (!m.isSolid()) {
			return new Color(0, 0, 0);
		}
		switch (m.getId()) {
		case 78:
			return new Color(255, 255, 255);
		case 24:
		case 12:
			return new Color(210, 210, 150);
		case 10:
			return new Color(200, 50, 50);
		case 9:
		case 8:
			return new Color(150, 150, 200);
		case 7:
			return new Color(50, 50, 50);
		case 4:
			return new Color(100, 100, 100);
		case 17:
		case 3:
			return new Color(110, 75, 35);
		case 18:
		case 2:
			return new Color(55, 140, 55);
		case 21:
		case 16:
		case 15:
		case 14:
		case 13:
		case 1:
		default:
			return new Color(150, 150, 150);
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
}
