package org.spout.server;


import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.spout.api.Server;
import org.spout.api.protocol.CommonPipelineFactory;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.bootstrap.BootstrapProtocol;
import org.spout.api.util.config.ConfigurationNode;
import org.spout.server.net.SpoutSession;
import org.spout.server.util.bans.BanManager;
import org.spout.server.util.bans.FlatFileBanManager;
import org.spout.server.util.config.SpoutConfiguration;


public class SpoutServer extends SpoutEngine implements Server {
	
	/**
	 * If the server has a whitelist or not.
	 */
	private volatile boolean whitelist = false;

	/**
	 * If the server allows flight.
	 */
	private volatile boolean allowFlight = false;

	/**
	 * A list of all players who can log onto this server, if using a whitelist.
	 */
	private List<String> whitelistedPlayers = new ArrayList<String>();

	/**
	 * The server's ban manager
	 */
	private BanManager banManager;



	
	public SpoutServer(String[] args) {
		super(args);
		
	}

	public static void main(String[] args) {


		SpoutServer server = new SpoutServer(args);
		server.start();

	}

	@Override
	public void start() {
		super.start();

		banManager = new FlatFileBanManager(this);

		getEventManager().registerEvents(new InternalEventListener(this), this);
		
		getLogger().info("Done Loading, ready for players.");
	}

	
	@Override
	public void init() {
		ChannelFactory factory = new NioServerSocketChannelFactory(executor, executor);
		bootstrap.setFactory(factory);

		ChannelPipelineFactory pipelineFactory = new CommonPipelineFactory(this);
		bootstrap.setPipelineFactory(pipelineFactory);
	}
	
	@Override
	public void stop() {
		super.stop();
		bootstrap.getFactory().releaseExternalResources();
	}

	

	/**
	 * The {@link ServerBootstrap} used to initialize Netty.
	 */
	private final ServerBootstrap bootstrap = new ServerBootstrap();

	/**
	 * Binds this server to the specified address.
	 *
	 * @param address The addresss.
	 */
	public boolean bind(SocketAddress address, BootstrapProtocol protocol) {
		if (protocol == null) {
			throw new IllegalArgumentException("Protocol cannot be null");
		}
		if (bootstrapProtocols.containsKey(address)) {
			return false;
		}
		bootstrapProtocols.put(address, protocol);
		group.add(bootstrap.bind(address));
		logger.log(Level.INFO, "Binding to address: {0}...", address);
		return true;
	}

	




	@Override
	public void save(boolean worlds, boolean players) {
		// TODO Auto-generated method stub

	}

	/*	@Override
		public boolean registerRecipe(Recipe recipe) {
			// TODO Auto-generated method stub
			return false;
		}*/

	@Override
	public boolean allowFlight() {
		return allowFlight;
	}


	@Override
	public boolean isWhitelist() {
		return whitelist;
	}

	@Override
	public void setWhitelist(boolean whitelist) {
		this.whitelist = whitelist;
	}

	@Override
	public void updateWhitelist() {
		List<String> whitelist = SpoutConfiguration.WHITELIST.getStringList();
		if (whitelist != null) {
			whitelistedPlayers = whitelist;
		} else {
			whitelistedPlayers = new ArrayList<String>();
		}
	}

	@Override
	public String[] getWhitelistedPlayers() {
		String[] whitelist = new String[whitelistedPlayers.size()];
		for (int i = 0; i < whitelist.length; i++) {
			whitelist[i] = whitelistedPlayers.get(i);
		}
		return whitelist;
	}

	@Override
	public void whitelist(String player) {
		whitelistedPlayers.add(player);
		List<String> whitelist = SpoutConfiguration.WHITELIST.getStringList();
		if (whitelist == null) {
			whitelist = whitelistedPlayers;
		} else {
			whitelist.add(player);
		}
		config.addNode(new ConfigurationNode("whitelist", whitelist));
	}

	@Override
	public void unWhitelist(String player) {
		whitelistedPlayers.remove(player);
	}


	
	@Override
	public Collection<String> getIPBans() {
		return banManager.getIpBans();
	}

	@Override
	public void banIp(String address) {
		banManager.setIpBanned(address, true);
	}

	@Override
	public void unbanIp(String address) {
		banManager.setIpBanned(address, false);
	}

	@Override
	public void banPlayer(String player) {
		banManager.setBanned(player, true);
	}

	@Override
	public void unbanPlayer(String player) {
		banManager.setBanned(player, false);
	}

	public boolean isBanned(String player, String address) {
		return banManager.isBanned(player, address);
	}

	public boolean isIpBanned(String address) {
		return banManager.isIpBanned(address);
	}

	public boolean isPlayerBanned(String player) {
		return banManager.isBanned(player);
	}

	public String getBanMessage(String player) {
		return banManager.getBanMessage(player);
	}

	public String getIpBanMessage(String address) {
		return banManager.getIpBanMessage(address);
	}

	

	@Override
	public Collection<String> getBannedPlayers() {
		return banManager.getBans();
	}

	@Override
	public Session newSession(Channel channel) {
		BootstrapProtocol protocol = getBootstrapProtocol(channel.getLocalAddress());
		return new SpoutSession(this, channel, protocol);
	}




}
