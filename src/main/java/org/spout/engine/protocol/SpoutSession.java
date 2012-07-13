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
package org.spout.engine.protocol;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.datatable.DataMap;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.entity.Entity;
import org.spout.api.event.player.PlayerKickEvent;
import org.spout.api.event.player.PlayerLeaveEvent;
import org.spout.api.event.storage.PlayerSaveEvent;
import org.spout.api.map.DefaultedMap;
import org.spout.api.plugin.Platform;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.protocol.NullNetworkSynchronizer;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.bootstrap.BootstrapProtocol;
import org.spout.api.protocol.proxy.ConnectionInfo;
import org.spout.api.protocol.proxy.ConnectionInfoMessage;
import org.spout.api.protocol.proxy.ProxyStartMessage;
import org.spout.api.protocol.proxy.RedirectMessage;
import org.spout.api.protocol.proxy.TransformableMessage;
import org.spout.engine.SpoutEngine;
import org.spout.engine.SpoutProxy;
import org.spout.engine.player.SpoutPlayer;
import org.spout.engine.world.SpoutWorld;

/**
 * A single connection to the server, which may or may not be associated with a
 * player.
 */
public final class SpoutSession implements Session {
	/**
	 * The number of ticks which are elapsed before a client is disconnected due
	 * to a timeout.
	 */
	@SuppressWarnings("unused")
	private static final int TIMEOUT_TICKS = 20 * 60;
	/**
	 * The server this session belongs to.
	 */
	private final SpoutEngine engine;
	/**
	 * The Random for this session
	 */
	private final Random random = new Random();
	/**
	 * The channel associated with this session.
	 */
	private final Channel channel;
	/**
	 * Information about the connection required for proxying
	 */
	private final AtomicReference<ConnectionInfo> channelInfo = new AtomicReference<ConnectionInfo>();
	/**
	 * The aux channel for proxy connections
	 */
	private final AtomicReference<Channel> auxChannel = new AtomicReference<Channel>();
	/**
	 * Information about the connection required for proxying
	 */
	private final AtomicReference<ConnectionInfo> auxChannelInfo = new AtomicReference<ConnectionInfo>();
	/**
	 * Indicates if the session is operating in proxy mode
	 */
	private final boolean proxy;
	/**
	 * Indicated if the session is in passthrough proxy mode
	 */
	private final AtomicBoolean passthrough = new AtomicBoolean(false);
	/**
	 * Indicates the number of times the proxy has connected to a server for this session
	 */
	private final AtomicInteger connects = new AtomicInteger(0);
	/**
	 * A queue of incoming and unprocessed messages from a client
	 */
	private final Queue<Message> fromDownMessageQueue = new ArrayDeque<Message>();
	/**
	 * A queue of incoming and unprocessed messages from a server
	 */
	private final Queue<Message> fromUpMessageQueue = new ArrayDeque<Message>();
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
	 * A network synchronizer that doesn't do anything, used until a real synchronizer is set.
	 */
	private final NetworkSynchronizer nullSynchronizer = new NullNetworkSynchronizer(this);

	/**
	 * The NetworkSynchronizer being used for this session
	 */
	private final AtomicReference<NetworkSynchronizer> synchronizer = new AtomicReference<NetworkSynchronizer>(nullSynchronizer);

	/**
	 * Data map and Datatable associated with it
	 */
	private final DatatableMap datatableMap;
	private final DataMap dataMap;

	/**
	 * The engine platform
	 */
	private final Platform platform;

	/**
	 * Creates a new session.
	 * @param engine  The server this session belongs to.
	 * @param channel The channel associated with this session.
	 */
	public SpoutSession(SpoutEngine engine, Channel channel, BootstrapProtocol bootstrapProtocol, boolean proxy) {
		this.engine = engine;
		this.channel = channel;
		protocol = new AtomicReference<Protocol>(bootstrapProtocol);
		this.bootstrapProtocol = bootstrapProtocol;
		isConnected = true;
		this.datatableMap = new GenericDatatableMap();
		this.dataMap = new DataMap(this.datatableMap);
		this.proxy = proxy;
		this.platform = engine.getPlatform();
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
	public SpoutPlayer getPlayer() {
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
				send(false, true, message);
			}
		}

		while ((message = fromDownMessageQueue.poll()) != null) {
			MessageHandler<Message> handler = (MessageHandler<Message>) protocol.get().getHandlerLookupService().find(message.getClass());
			if (handler != null) {
				try {
					handler.handle(false, this, player, message);
				} catch (Exception e) {
					Spout.getEngine().getLogger().log(Level.SEVERE, "Message handler for " + message.getClass().getSimpleName() + " threw exception for player " + (getPlayer() != null ? getPlayer().getName() : "null"));
					e.printStackTrace();
					disconnect(false, new Object[] {"Message handler exception for ", message.getClass().getSimpleName()});
				}
			}
		}
		while ((message = fromUpMessageQueue.poll()) != null) {
			MessageHandler<Message> handler = (MessageHandler<Message>) protocol.get().getHandlerLookupService().find(message.getClass());
			if (handler != null) {
				try {
					handler.handle(true, this, player, message);
				} catch (Exception e) {
					Spout.getEngine().getLogger().log(Level.SEVERE, "Message handler for " + message.getClass().getSimpleName() + " threw exception for player " + (getPlayer() != null ? getPlayer().getName() : "null"));
					e.printStackTrace();
					disconnect(false, new Object[] {"Message handler exception for", message.getClass().getSimpleName()});
				}
			}
		}
	}

	@Override
	public void send(boolean upstream, Message message) {
		send(upstream, false, message);
	}

	@Override
	public void send(boolean upstream, boolean force, Message message) {
		if (message == null) {
			return;
		}

		try {
			switch (platform) {
				case SERVER :
					if (upstream) {
						Spout.getLogger().warning("Attempt made to send packet to server");
						break;
					}
					if (force || this.state == State.GAME) {
						if (channel.isOpen()) {
							channel.write(message);
						}
					} else {
						sendQueue.add(message);
					}

					break;
				case CLIENT :
					if (!upstream) {
						Spout.getLogger().warning("Attempt made to send packet to client");
						break;
					}
					if (force || this.state == State.GAME) {
						if (channel.isOpen()) {
							channel.write(message);
						}
					} else {
						sendQueue.add(message);
					}

					break;
				case PROXY :
					if (message instanceof ConnectionInfoMessage) {
						updateConnectionInfo(upstream, !upstream, (ConnectionInfoMessage) message);
					}
					if (upstream) {
						Channel auxChannel = this.auxChannel.get();
						if (auxChannel == null) {
							Spout.getLogger().warning("Attempt made to send data to an unconnected channel");
							break;
						}
						auxChannel.write(message);
					} else {
						if (force || this.state == State.GAME) {
							if (channel.isOpen()) {
								channel.write(message);
							}
						} else {
							sendQueue.add(message);
						}
					}
					break;
				default :
					Spout.getLogger().info("Unknown platform " + platform);
			}
		} catch (Exception e) {
			disconnect(false, new Object[] {"Socket Error!"});
		}
	}

	@Override
	public void sendAll(boolean upstream, Message... messages) {
		sendAll(upstream, false, messages);
	}

	@Override
	public void sendAll(boolean upstream, boolean force, Message... messages) {
		for (Message msg : messages) {
			send(upstream, force, msg);
		}
	}

	@Override
	public boolean disconnect(Object... reason) {
		return disconnect(true, reason);
	}

	public Object[] getDefaultLeaveMessage() {
		if (player == null) {
			return new Object[] {ChatStyle.CYAN, "Unknown", ChatStyle.CYAN , " has left the game"};
		} else {
			return new Object[] {ChatStyle.CYAN, player.getDisplayName(), ChatStyle.CYAN, " has left the game"};
		}
	}

	@Override
	public boolean disconnect(boolean kick, Object... reason) {
		if (player != null) {
			PlayerLeaveEvent event;
			if (kick) {
				event = getEngine().getEventManager().callEvent(new PlayerKickEvent(player, getDefaultLeaveMessage(), reason));
				if (event.isCancelled()) {
					return false;
				}
				reason = ((PlayerKickEvent) event).getKickReason();
				engine.getCommandSource().sendMessage("Player ", player.getName(), " kicked: ", reason);
			} else {
				event = new PlayerLeaveEvent(player, getDefaultLeaveMessage());
			}
			dispose(event);
		}
		Protocol protocol = getProtocol();
		Message kickMessage = null;
		if (protocol != null) {
			kickMessage = protocol.getKickMessage(new ChatArguments(reason));
		}
		if (kickMessage != null) {
			channel.write(kickMessage).addListener(ChannelFutureListener.CLOSE);
		} else {
			channel.close();
		}
		closeAuxChannel(false, reason);
		return true;
	}

	/**
	 * Returns the address of this session.
	 * @return The remote address.
	 */
	@Override
	public InetSocketAddress getAddress() {
		SocketAddress addr = channel.getRemoteAddress();
		if (!(addr instanceof InetSocketAddress)) {
			return null;
		}

		return (InetSocketAddress) addr;
	}

	@Override
	public String toString() {
		return SpoutSession.class.getName() + " [address=" + channel.getRemoteAddress() + "]";
	}

	/**
	 * Adds a message to the unprocessed queue.
	 * @param message The message.
	 */
	@Override
	public void messageReceived(boolean upstream, Message message) {
		if (this.proxy) {
			if (message instanceof ConnectionInfoMessage) {
				updateConnectionInfo(upstream, upstream, (ConnectionInfoMessage) message);
			}
			if (upstream) {
				if (message instanceof ProxyStartMessage) {
					passthrough.compareAndSet(false, true);
				} else if (message instanceof RedirectMessage) {
					RedirectMessage redirect = (RedirectMessage) message;
					if (redirect.isRedirect()) {
						closeAuxChannel(true, "Redirect received");
						auxChannelInfo.set(null);
						ConnectionInfo info = channelInfo.get();
						if (info != null) {
							passthrough.set(false);
							((SpoutProxy) engine).connect(redirect.getHostname(), redirect.getPort(), info.getIdentifier(), this);
							return;
						}
					}
				}
			}
			if (passthrough.get()) {
				if (message instanceof TransformableMessage) {
					message = ((TransformableMessage) message).transform(upstream, connects.get(), channelInfo.get(), auxChannelInfo.get());
				}
				send(!upstream, true, message);
				return;
			}
		}
		if (upstream) {
			fromUpMessageQueue.add(message);
		} else {
			fromDownMessageQueue.add(message);
		}
	}

	@Override
	public void dispose() {
		dispose(new PlayerLeaveEvent(player, getDefaultLeaveMessage()));
	}

	public void dispose(PlayerLeaveEvent leaveEvent) {
		if (player != null && isConnected) {
			isConnected = false;

			if (!leaveEvent.hasBeenCalled()) {
				getEngine().getEventManager().callEvent(leaveEvent);
			}

			Object[] text = leaveEvent.getMessage();
			if (text != null && text.length > 0) {
				engine.broadcastMessage(text);
			}

			PlayerSaveEvent saveEvent = getEngine().getEventManager().callEvent(new PlayerSaveEvent(player));
			if (!saveEvent.isSaved()) {

			}

			//If its null or can't be get, just ignore it
			//If disconnect fails, we just ignore it for now.
			try {
				final Entity entity = player.getEntity();
				if (entity != null) {
					((SpoutWorld) entity.getWorld()).removePlayer(player);
				}
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
			if (this.protocol.get() == protocol) {
				return;
			}
			throw new IllegalArgumentException("The protocol may only be set once per session");
		}
		this.synchronizer.get().setProtocol(protocol);
		engine.getLogger().info("Setting protocol to " + protocol.getName());
	}

	@Override
	public Protocol getProtocol() {
		return this.protocol.get();
	}

	@Override
	public Engine getEngine() {
		return engine;
	}

	@Override
	public boolean isConnected() {
		return channel.isOpen();
	}

	@Override
	public DefaultedMap<String, Serializable> getDataMap() {
		return dataMap;
	}

	@Override
	public void setNetworkSynchronizer(NetworkSynchronizer synchronizer) {
		if (synchronizer == null && player == null) {
			this.synchronizer.set(nullSynchronizer);
		} else if (!this.synchronizer.compareAndSet(nullSynchronizer, synchronizer)) {
			throw new IllegalArgumentException("Network synchronizer may only be set once for a given player login");
		} else {
			synchronizer.setProtocol(protocol.get());
		}
	}

	@Override
	public void bindAuxChannel(Channel c) {
		if (!proxy) {
			throw new UnsupportedOperationException("Aux channel is only supported in proxy mode");
		} else if (c == null) {
			throw new IllegalArgumentException("Channel may not be null");
		} else if (!auxChannel.compareAndSet(null, c)) {
			throw new IllegalStateException("Aux channel may not be set without closing the previously bound channel");
		} else {
			connects.incrementAndGet();
		}
		System.out.println("Binding: " + c + " " + connects.get());
	}

	@Override
	public boolean isPrimary(Channel c) {
		return c == this.channel;
	}

	@Override
	public void closeAuxChannel() {
		closeAuxChannel(true);
	}

	private void closeAuxChannel(boolean openedExpected) {
		closeAuxChannel(openedExpected, "Closing aux channel");
	}

	private void closeAuxChannel(boolean openedExpected, Object... message) {
		Channel c = auxChannel.getAndSet(null);
		if (c != null) {
			Message kickMessage = null;
			Protocol p = protocol.get();
			if (p != null) {
				kickMessage = p.getKickMessage(new ChatArguments(message));
			}
			if (kickMessage != null) {
				c.write(kickMessage).addListener(ChannelFutureListener.CLOSE);
			} else {
				c.close();
			}
		} else if (openedExpected) {
			throw new IllegalStateException("Attempt made to close aux channel when no aux channel was bound");
		}
	}

	@Override
	public NetworkSynchronizer getNetworkSynchronizer() {
		return synchronizer.get();
	}

	private void updateConnectionInfo(boolean auxChannel, boolean upstream, ConnectionInfoMessage info) {
		AtomicReference<ConnectionInfo> ref = auxChannel ? auxChannelInfo : channelInfo;
		boolean success = false;
		while (!success) {
			ConnectionInfo oldInfo = ref.get();
			ConnectionInfo newInfo = info.getConnectionInfo(upstream, oldInfo);
			success = ref.compareAndSet(oldInfo, newInfo);
		}
	}
}
