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
package org.spout.engine.protocol;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;

import org.spout.api.ChatColor;
import org.spout.api.Engine;
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

import org.spout.engine.SpoutServer;
import org.spout.engine.player.SpoutPlayer;

/**
 * A single connection to the server, which may or may not be associated with a
 * player.
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
	 * A queue of outgoing messages that will be sent after the client finishes identification
	 */
	private final Queue<Message> sendQueue = new ConcurrentLinkedQueue<Message>();
	/**
	 * The current state.
	 */
	private State state = State.EXCHANGE_HANDSHAKE;
	/**
	 * The player associated with this session (if there is one).
	 */
	private SpoutPlayer player;
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
	 * @todo Probably add to SpoutAPI
	 */
	private boolean isConnected = false;

	/**
	 * Creates a new session.
	 * @param server  The server this session belongs to.
	 * @param channel The channel associated with this session.
	 */
	public SpoutSession(SpoutServer server, Channel channel, BootstrapProtocol bootstrapProtocol) {
		this.server = server;
		this.channel = channel;
		protocol = new AtomicReference<Protocol>(bootstrapProtocol);
		this.bootstrapProtocol = bootstrapProtocol;
		isConnected = true;
	}

	/**
	 * Gets the state of this session.
	 * @return The session's state.
	 */
	@Override
	public State getState() {
		return state;
	}

	/**
	 * Sets the state of this session.
	 * @param state The new state.
	 */
	@Override
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Gets the player associated with this session.
	 * @return The player, or {@code null} if no player is associated with it.
	 */
	@Override
	public Player getPlayer() {
		return player;
	}

	/**
	 * Sets the player associated with this session.
	 * @param player The new player.
	 * @throws IllegalStateException if there is already a player associated
	 *                               with this session.
	 */
	public void setPlayer(SpoutPlayer player) {
		if (this.player != null) {
			throw new IllegalStateException();
		}

		this.player = player;
	}

	@SuppressWarnings("unchecked")
	public void pulse() {
		Message message;

		if (state == State.GAME) {
			while ((message = sendQueue.poll()) != null) {
				send(message, true);
			}
		}

		while ((message = messageQueue.poll()) != null) {
			MessageHandler<Message> handler = (MessageHandler<Message>) protocol.get().getHandlerLookupService().find(message.getClass());
			if (handler != null) {
				try {
					handler.handle(this, player, message);
				} catch (Exception e) {
					Spout.getEngine().getLogger().log(Level.SEVERE, "Message handler for " + message.getClass().getSimpleName() + " threw exception for player " + (getPlayer() != null ? getPlayer().getName() : "null"));
					e.printStackTrace();
					disconnect("Message handler exception for " + message.getClass().getSimpleName(), false);
				}
			}
		}
	}

	@Override
	public void send(Message message) {
		send(message, false);
	}

	/**
	 * Sends a message to the client.
	 * @param message The message.
	 * @param force   if this message is used in the identification stages of communication
	 */
	@Override
	public void send(Message message, boolean force) {
		try {
			if (force || this.state == State.GAME) {
				channel.write(message);
			} else {
				sendQueue.add(message);
			}
		} catch (Exception e) {
			disconnect("Socket Error!", false);
		}
	}

	@Override
	public void sendAll(Message... messages) {
		sendAll(false, messages);
	}

	@Override
	public void sendAll(boolean force, Message... messages) {
		for (Message msg : messages) {
			send(msg, force);
		}
	}

	@Override
	public boolean disconnect(String reason) {
		return disconnect(reason, true);
	}

	public String getDefaultLeaveMessage() {
		return ChatColor.CYAN + player.getDisplayName() + ChatColor.CYAN + " has left the game";
	}

	@Override
	public boolean disconnect(String reason, boolean kick) {
		if (player != null) {
			PlayerLeaveEvent event;
			if (kick) {
				event = getGame().getEventManager().callEvent(new PlayerKickEvent(player, getDefaultLeaveMessage(), reason));
				if (event.isCancelled()) {
					return false;
				}

				getGame().getLogger().log(Level.INFO, "Player {0} kicked: {1}", new Object[]{player.getName(), reason});
			} else {
				event = new PlayerLeaveEvent(player, getDefaultLeaveMessage());
			}
			dispose(event);
		}
		channel.write(protocol.get().getPlayerProtocol().getKickMessage(reason)).addListener(ChannelFutureListener.CLOSE);
		return true;
	}

	/**
	 * Gets the server associated with this session.
	 * @return The server.
	 */
	public SpoutServer getServer() {
		return server;
	}

	/**
	 * Returns the address of this session.
	 * @return The remote address.
	 */
	@Override
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
	 * @param message The message.
	 * @param <T>     The type of message.
	 */
	@Override
	public <T extends Message> void messageReceived(T message) {
		messageQueue.add(message);
	}

	@Override
	public void dispose() {
		dispose(new PlayerLeaveEvent(player, getDefaultLeaveMessage()));
	}

	public void dispose(PlayerLeaveEvent leaveEvent) {
		if (player != null && isConnected) {
			isConnected = false;

			if (!leaveEvent.hasBeenCalled()) {
				getGame().getEventManager().callEvent(leaveEvent);
			}

			String text = leaveEvent.getMessage();
			if (text != null && text.length() > 0) {
				server.broadcastMessage(text);
			}

			PlayerSaveEvent saveEvent = getGame().getEventManager().callEvent(new PlayerSaveEvent(player));
			if (!saveEvent.isSaved()) {
				
			}

			//If its null or can't be get, just ignore it
			//If disconnect fails, we just ignore it for now.
			try {
				player.disconnect();
			} catch (Exception e) {
			}
			player = null; // in case we are disposed twice
		}
	}

	@Override
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

	@Override
	public Engine getGame() {
		return server;
	}
}
