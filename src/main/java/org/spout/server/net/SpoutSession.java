/*
 * This file is part of Spout (http://www.spout.org/).
 *
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
package org.spout.server.net;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.spout.api.ChatColor;
import org.spout.api.Game;
import org.spout.api.Spout;
import org.spout.api.event.player.PlayerKickEvent;
import org.spout.api.event.player.PlayerLeaveEvent;
import org.spout.api.event.storage.PlayerSaveEvent;
import org.spout.api.player.Player;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.protocol.PlayerProtocol;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.bootstrap.BootstrapProtocol;
import org.spout.server.SpoutServer;
import org.spout.server.SpoutWorld;
import org.spout.server.player.SpoutPlayer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;

/**
 * A single connection to the server, which may or may not be associated with a
 * player.
 * 
 */
public final class SpoutSession implements Session {
	/**
	 * The number of ticks which are elapsed before a client is disconnected due
	 * to a timeout.
	 */
	private static final int TIMEOUT_TICKS = 20 * 60;

	/**
	 * The server this session belongs to.
	 */
	private final SpoutServer server;

	/**
	 * The Random for this session
	 */
	private final Random random = new Random();

	/**
	 * The channel associated with this session.
	 */
	private final Channel channel;

	/**
	 * A queue of incoming and unprocessed messages.
	 */
	private final Queue<Message> messageQueue = new ArrayDeque<Message>();

	/**
	 * A timeout counter. This is increment once every tick and if it goes above
	 * a certain value the session is disconnected.
	 */
	private int timeoutCounter = 0;

	/**
	 * The current state.
	 */
	private State state = State.EXCHANGE_HANDSHAKE;

	/**
	 * The player associated with this session (if there is one).
	 */
	private Player player;

	/**
	 * The random long used for client-server handshake
	 */

	private final String sessionId = Long.toString(random.nextLong(), 16).trim();

	/**
	 * The protocol for this session
	 */
	private final AtomicReference<Protocol> protocol;

	/**
	 * Stores the last block placement message to work around a bug in the
	 * vanilla client where duplicate packets are sent.
	 */
	//private BlockPlacementMessage previousPlacement;

	private final BootstrapProtocol bootstrapProtocol;

	/**
	 * Stores if this is Connected
	 * 
	 * @todo Probably add to SpoutAPI
	 */
	private boolean isConnected = false;

	/**
	 * Creates a new session.
	 * 
	 * @param server The server this session belongs to.
	 * @param channel The channel associated with this session.
	 */
	public SpoutSession(SpoutServer server, Channel channel, BootstrapProtocol bootstrapProtocol) {
		this.server = server;
		this.channel = channel;
		this.protocol = new AtomicReference<Protocol>(bootstrapProtocol);
		this.bootstrapProtocol = bootstrapProtocol;
		this.isConnected = true;
	}

	/**
	 * Gets the state of this session.
	 * 
	 * @return The session's state.
	 */
	public State getState() {
		return state;
	}

	/**
	 * Sets the state of this session.
	 * 
	 * @param state The new state.
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Gets the player associated with this session.
	 * 
	 * @return The player, or {@code null} if no player is associated with it.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Sets the player associated with this session.
	 * 
	 * @param player The new player.
	 * @throws IllegalStateException if there is already a player associated
	 *             with this session.
	 */
	public void setPlayer(Player player) {
		if (this.player != null) {
			throw new IllegalStateException();
		}

		this.player = player;
		/*PlayerLoginEvent event = EventFactory.onPlayerLogin(player);
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
			disconnect(event.getKickMessage(), true);
			return;
		}*/

		//String message = EventFactory.onPlayerJoin(player).getJoinMessage();
		//if (message != null) {
		//	server.broadcastMessage(message);
		//}

		//player.loadData();
		//player.saveData();

		//player.getWorld().getRawPlayers().add(player);
		//player.teleport(player.getLocation().add(0, 0.5, 0));

		/*Message userListMessage = new UserListItemMessage(player.getPlayerListName(), true, (short) timeoutCounter);
		for (Player sendPlayer : server.getOnlinePlayers()) {
			//((SpoutPlayer) sendPlayer).getSession().send(userListMessage);
			//send(new UserListItemMessage(sendPlayer.getPlayerListName(), true, (short) ((SpoutPlayer) sendPlayer).getSession().timeoutCounter));
		}*/
	}

	@SuppressWarnings("unchecked")
	public void pulse() {
		timeoutCounter++;

		Message message;
		while ((message = messageQueue.poll()) != null) {
			MessageHandler<Message> handler = (MessageHandler<Message>) this.protocol.get().getHandlerLookupService().find(message.getClass());
			if (handler != null) {
				try {
					handler.handle(this, player, message);
				} catch (Exception e) {
					Spout.getGame().getLogger().log(Level.SEVERE, "Message handler for " + message.getClass().getSimpleName() + " threw exception for player " + this.getPlayer().getName());
					e.printStackTrace();
					disconnect("Message handler exception for " + message.getClass().getSimpleName());
				}
			}
			timeoutCounter = 0;
		}
		if (timeoutCounter >= TIMEOUT_TICKS) {
			disconnect("Timed out", true);
		}
	}

	/**
	 * + * Sends a message to the client.
	 * 
	 * @param message The message.
	 */
	public void send(Message message) {
		try {
			channel.write(message);
		} catch (Exception e) {
			disconnect("Socket Error!");
		}
	}

	/**
	 * Disconnects the session with the specified reason. This causes a kick
	 * packet to be sent. When it has been delivered, the channel is closed.
	 * 
	 * @param reason The reason for disconnection.
	 */
	public void disconnect(String reason) {
		disconnect(reason, false);
	}

	/**
	 * Disconnects the session with the specified reason. This causes a kick
	 * packet to be sent. When it has been delivered, the channel is closed.
	 * 
	 * @param reason The reason for disconnection.
	 * @param overrideKick Whether to override the kick event.
	 */
	public void disconnect(String reason, boolean overrideKick) {
		if (player != null && !overrideKick) {
			boolean useMessage = true;
			PlayerKickEvent event = getGame().getEventManager().callEvent(new PlayerKickEvent(player, reason));
			if (event.isCancelled()) {
				return;
			}

			reason = event.getKickReason();

			if (event.getMessage() != null && event.getMessage() != reason) {
				server.broadcastMessage(event.getMessage());
				useMessage = false;
			}

			SpoutServer.logger.log(Level.INFO, "Player {0} kicked: {1}", new Object[] {player.getName(), reason});

			dispose(useMessage);
		}

		channel.write(protocol.get().getPlayerProtocol().getKickMessage(reason)).addListener(ChannelFutureListener.CLOSE);
		channel.close();
	}

	/**
	 * Gets the server associated with this session.
	 * 
	 * @return The server.
	 */
	public SpoutServer getServer() {
		return server;
	}

	/**
	 * Returns the address of this session.
	 * 
	 * @return The remote address.
	 */
	public InetSocketAddress getAddress() {
		SocketAddress addr = channel.getRemoteAddress();
		if (addr instanceof InetSocketAddress) {
			return (InetSocketAddress) addr;
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return SpoutSession.class.getName() + " [address=" + channel.getRemoteAddress() + "]";
	}

	/**
	 * Adds a message to the unprocessed queue.
	 * 
	 * @param message The message.
	 * @param <T> The type of message.
	 */
	public <T extends Message> void messageReceived(T message) {
		messageQueue.add(message);
	}

	/**
	 * Disposes of this session by destroying the associated player, if there is
	 * one.
	 */
	public void dispose(boolean broadcastQuit) {
		if (player != null && this.isConnected) {
			this.isConnected = false;
			String text = getGame().getEventManager().callEvent(new PlayerLeaveEvent(player, ChatColor.CYAN + player.getDisplayName() + ChatColor.CYAN + " has left the game", broadcastQuit)).getMessage();
			if (broadcastQuit && text != null) {
				server.broadcastMessage(text);
			}

			PlayerSaveEvent event = getGame().getEventManager().callEvent(new PlayerSaveEvent(player));
			if (!event.isSaved()) {
				//SaveTaskThread.addTask(new PlayerSaveTask(player));
			}
			//If its null or can't be get , just ignore it
			//If disconnect fails, we just ignore it for now.
			try {
				if (player.getEntity() != null) {
					((SpoutWorld) player.getEntity().getWorld()).removePlayer(player);
					for (Player user : getPlayer().getEntity().getWorld().getPlayers()) {
						user.getNetworkSynchronizer().destroyEntity(getPlayer().getEntity());
					}
				}
				((SpoutPlayer) player).disconnect();
			} catch (Exception e) {
			}
			player = null; // in case we are disposed twice
		}
	}

	public String getSessionId() {
		return sessionId;
	}

	/*public BlockPlacementMessage getPreviousPlacement() {
		return previousPlacement;
	}

	public void setPreviousPlacement(BlockPlacementMessage message) {
		previousPlacement = message;
	}*/

	@Override
	public void setProtocol(Protocol protocol) {
		if (!this.protocol.compareAndSet(bootstrapProtocol, protocol)) {
			throw new IllegalArgumentException("The protocol may only be set once per session");
		} else {
			server.getLogger().info("Setting protocol to " + protocol.getName());
		}
	}

	@Override
	public PlayerProtocol getPlayerProtocol() {
		Protocol protocol = this.protocol.get();
		return protocol == null ? null : protocol.getPlayerProtocol();
	}

	public Game getGame() {
		return server;
	}
}