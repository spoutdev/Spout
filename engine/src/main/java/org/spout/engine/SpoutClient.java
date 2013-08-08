/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import org.spout.api.Client;
import org.spout.api.Platform;
import org.spout.api.audio.SoundManager;
import org.spout.api.command.annotated.AnnotatedCommandExecutorFactory;
import org.spout.api.component.entity.PlayerNetworkComponent;
import org.spout.api.datatable.ManagedHashMap;
import org.spout.api.datatable.SerializableMap;
import org.spout.api.entity.Entity;
import org.spout.api.event.engine.EngineStartEvent;
import org.spout.api.event.engine.EngineStopEvent;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Vector2;
import org.spout.api.protocol.CommonHandler;
import org.spout.api.protocol.CommonPipelineFactory;
import org.spout.api.protocol.PortBinding;
import org.spout.api.protocol.Protocol;
import org.spout.api.render.RenderMode;
import org.spout.api.resource.FileSystem;
import org.spout.engine.audio.AudioConfiguration;
import org.spout.engine.audio.SpoutSoundManager;
import org.spout.engine.command.InputCommands;
import org.spout.engine.command.RendererCommands;
import org.spout.engine.entity.SpoutClientPlayer;
import org.spout.engine.filesystem.ClientFileSystem;
import org.spout.engine.gui.SpoutScreenStack;
import org.spout.engine.input.SpoutInputManager;
import org.spout.engine.protocol.PortBindingImpl;
import org.spout.engine.protocol.SpoutClientSession;
import org.spout.engine.util.thread.threadfactory.NamedThreadFactory;
import org.spout.engine.world.SpoutClientWorld;
import org.spout.engine.world.SpoutWorld;

public class SpoutClient extends SpoutEngine implements Client {
	private final AtomicReference<SpoutClientPlayer> player = new AtomicReference<>();
	private final AtomicReference<SpoutClientWorld> world = new AtomicReference<>();
	private final ClientBootstrap bootstrap = new ClientBootstrap();
	private final ClientFileSystem filesystem = new ClientFileSystem();
	private final SessionTask sessionTask = new SessionTask();
	// Handle stopping
	private volatile boolean rendering = true;
	private boolean ccoverride = false;
	private String stopMessage = null;
	private SpoutRenderer renderer;
	private SoundManager soundManager;
	private SpoutInputManager inputManager;

	public SpoutClient() {
		logFile = "client-log-%D.txt";
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
			unpackNatives(args.path);
		}

		ExecutorService executorBoss = Executors.newCachedThreadPool(new NamedThreadFactory("SpoutClient - Boss", true));
		ExecutorService executorWorker = Executors.newCachedThreadPool(new NamedThreadFactory("SpoutClient - Worker", true));
		ChannelFactory factory = new NioClientSocketChannelFactory(executorBoss, executorWorker);
		bootstrap.setFactory(factory);

		ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory();
		bootstrap.setPipelineFactory(pipelineFactory);
		super.init(args);

		this.ccoverride = args.ccoverride;

		inputManager = new SpoutInputManager();

		// Initialize sound system
		soundManager = new SpoutSoundManager();
		soundManager.init();

		// Configure sound system
		AudioConfiguration audioConfig = new AudioConfiguration();
		audioConfig.load();
		soundManager.setGain(AudioConfiguration.SOUND_VOLUME.getFloat());
		soundManager.setMusicGain(AudioConfiguration.MUSIC_VOLUME.getFloat());
	}

	@Override
	public void start() {
		// Completely blank world to allow the player to start in
		worldChanged("NullWorld", UUID.randomUUID(), new ManagedHashMap().serialize());
		if (!connnect()) {
			return;
		}

		super.start();

		// Register commands
		AnnotatedCommandExecutorFactory.create(new InputCommands(this));
		AnnotatedCommandExecutorFactory.create(new RendererCommands(this));

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		this.renderer = getScheduler().startRenderThread(new Vector2(dim.getWidth() * 0.75f, dim.getHeight() * 0.75f), ccoverride, null);
		getScheduler().startMeshThread();
		getScheduler().startGuiThread();

		// TODO: Maybe a better way of alerting plugins the client is done?
		if (EngineStartEvent.getHandlerList().getRegisteredListeners().length != 0) {
			getEventManager().callEvent(new EngineStartEvent());
		}

		inputManager.onClientStart();
		filesystem.postStartup();

		// Send handshake message first
		SpoutClientSession get = (SpoutClientSession) player.get().getNetwork().getSession();
		get.send(true, get.getProtocol().getIntroductionMessage(getPlayer().getName(), (InetSocketAddress) get.getChannel().getRemoteAddress()));
	}

	private boolean connnect() {
		// Connect to server to establish session
		Protocol protocol = null;
		if (getArguments().protocol != null) {
			protocol = Protocol.getProtocol(getArguments().protocol);
		}
		if (protocol == null) {
			protocol = Protocol.getProtocol("Spout");
		}
		String address;
		if (getArguments().server == null) {
			address = "localhost";
		} else {
			address = getArguments().server;
		}
		int port = getArguments().port != -1 ? getArguments().port : protocol.getDefaultPort();
		PortBindingImpl binding = new PortBindingImpl(protocol, new InetSocketAddress(address, port));
		ChannelFuture connect = bootstrap.connect(binding.getAddress());
		try {
			connect.await(10, TimeUnit.SECONDS);
		} catch (InterruptedException ex) {
			getLogger().log(Level.SEVERE, "Connection took too long! Cancelling connect and stopping engine!");
			stop(); // TODO: Make sure that this is fine here
			return false;
		}

		Channel channel = connect.getChannel();
		if (!connect.isSuccess()) {
			getLogger().log(Level.SEVERE, "Could not connect to " + binding, connect.getCause());
			return false;
		}

		getLogger().log(Level.INFO, "Connected to " + address + ":" + port + " with protocol " + protocol.getName());
		CommonHandler handler = channel.getPipeline().get(CommonHandler.class);
		SpoutClientSession session = new SpoutClientSession(this, channel, protocol);
		handler.setSession(session);

		Class<? extends PlayerNetworkComponent> network = session.getProtocol().getClientNetworkComponent(session);
		final SpoutClientPlayer p = new SpoutClientPlayer(this, network, "Spouty", new Transform().setPosition(new Point(getWorld(), 1, 200, 1)));
		session.setPlayer(p);
		p.getNetwork().setSession(session);
		session.getProtocol().initializeClientSession(session);
		player.set(p);
		return true;
	}

	@Override
	protected Runnable getSessionTask() {
		return sessionTask;
	}

	private class SessionTask implements Runnable {
		@Override
		public void run() {
			((SpoutClientSession) player.get().getNetwork().getSession()).pulse();
		}
	}

	@Override
	public List<String> getAllPlayers() {
		return Arrays.asList(getPlayer().getName());
	}

	@Override
	public SpoutClientPlayer getPlayer() {
		return player.get();
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
		return ((SpoutClientSession) player.get().getNetwork().getSession()).getActiveAddress();
	}

	@Override
	public boolean stop(String message) {
		if (!super.stop(message, false)) {
			return false;
		}

		player.get().getNetwork().getSession().dispose();

		// De-init OpenAL
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
				System.out.println(stopMessage);

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

		if ((exact && world.getName().equals(name)) || world.getName().startsWith(name)) {
			return world;
		} else {
			return null;
		}
	}

	@Override
	public SpoutClientWorld getDefaultWorld() {
		return world.get();
	}

	public SpoutClientWorld worldChanged(String name, UUID uuid, byte[] data) {
		System.out.println("WORLD CHANGED!");
		SpoutClientWorld world = new SpoutClientWorld(name, this, uuid);
		// Load in datatable
		SerializableMap map = world.getData();
		try {
			map.deserialize(data);
		} catch (IOException e) {
			throw new RuntimeException("Unable to deserialize data", e);
		}

		SpoutWorld oldWorld = this.world.getAndSet(world);
		return world;
	}

	public ClientBootstrap getBootstrap() {
		return bootstrap;
	}

	@Override
	public SpoutClientSession newSession(Channel channel) {
		// TODO: This really needs to be removed and moved to server only
		throw new UnsupportedOperationException("Can't add new session on client!");
		//return new SpoutClientSession(this, channel, protocol);
	}

	public void disconnected() {
		//getLogger().log(Level.WARNING, "ENGINE DISCONNECTING!");
		//new RuntimeException("Not real exception.").printStackTrace();
		stop("Disconnected for some unknown reason!");
		//this.session.set(null);
	}

	@Override
	public FileSystem getFileSystem() {
		return filesystem;
	}

	@Override
	public Entity getEntity(UUID uid) {
		return world.get().getEntity(uid);
	}

	@Override
	public SpoutClientWorld getWorld() {
		return world.get();
	}

	@Override
	public Collection<World> getWorlds() {
		return Collections.singleton((World) world.get());
	}

	private void unpackNatives(String path) {
		String natives;
		String osPath;
		if (SystemUtils.IS_OS_WINDOWS) {
			natives = "windows.txt";
			osPath = "windows/";
		} else if (SystemUtils.IS_OS_MAC) {
			natives = "osx.txt";
			osPath = "mac/";
		} else if (SystemUtils.IS_OS_LINUX) {
			natives = "linux.txt";
			osPath = "linux/";
		} else {
			getLogger().severe("Error loading natives of operating system type: " + SystemUtils.OS_NAME);
			return;
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(SpoutClient.class.getResourceAsStream("/natives/" + natives)));
		String str;
		List<String> files = new ArrayList<>();
		try {
			while ((str = reader.readLine()) != null) {
				files.add(str);
			}
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, "Error getting native files to copy", e);
		}

		File cacheDir = new File(path == null ? System.getProperty("user.dir") : path, "natives" + File.separator + osPath);
		cacheDir.mkdirs();
		for (String f : files) {
			File target = new File(cacheDir, f);
			if (!target.exists()) {
				try {
					FileUtils.copyInputStreamToFile(SpoutClient.class.getResourceAsStream("/" + f), target);
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

	@Override
	public void startTickRun(int stage, long delta) {
		// TODO: Should this be removed?
	}

	@Override
	public void copySnapshotRun() {
		super.copySnapshotRun();
		getPlayer().copySnapshot();
	}
}
