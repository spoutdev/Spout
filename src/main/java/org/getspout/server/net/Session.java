package org.getspout.server.net;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import org.getspout.server.EventFactory;
import org.getspout.server.SpoutServer;
import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.msg.BlockPlacementMessage;
import org.getspout.server.msg.KickMessage;
import org.getspout.server.msg.Message;
import org.getspout.server.msg.PingMessage;
import org.getspout.server.msg.UserListItemMessage;
import org.getspout.server.msg.handler.HandlerLookupService;
import org.getspout.server.msg.handler.MessageHandler;

/**
 * A single connection to the server, which may or may not be associated with a
 * player.
 *
 * @author Graham Edgecombe
 */
public final class Session {
	/**
	 * The number of ticks which are elapsed before a client is disconnected due
	 * to a timeout.
	 */
	private static final int TIMEOUT_TICKS = 300;

	/**
	 * The state this connection is currently in.
	 */
	public enum State {

		/**
		 * In the exchange handshake state, the server is waiting for the client
		 * to send its initial handshake packet.
		 */
		EXCHANGE_HANDSHAKE,

		/**
		 * In the exchange identification state, the server is waiting for the
		 * client to send its identification packet.
		 */
		EXCHANGE_IDENTIFICATION,

		/**
		 * In the game state the session has an associated player.
		 */
		GAME;
	}

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
	private SpoutPlayer player;

	/**
	 * The random long used for client-server handshake
	 */

	private String sessionId = Long.toString(random.nextLong(), 16).trim();

	/**
	 * Handling ping messages
	 */
	private int pingMessageId;

	/**
	 * Stores the last block placement message to work around a bug in the
	 * vanilla client where duplicate packets are sent.
	 */
	private BlockPlacementMessage previousPlacement;

	/**
	 * Creates a new session.
	 *
	 * @param server The server this session belongs to.
	 * @param channel The channel associated with this session.
	 */
	public Session(SpoutServer server, Channel channel) {
		this.server = server;
		this.channel = channel;
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
	public SpoutPlayer getPlayer() {
		return player;
	}

	/**
	 * Sets the player associated with this session.
	 *
	 * @param player The new player.
	 * @throws IllegalStateException if there is already a player associated
	 *             with this session.
	 */
	public void setPlayer(SpoutPlayer player) {
		if (this.player != null) {
			throw new IllegalStateException();
		}

		this.player = player;
		PlayerLoginEvent event = EventFactory.onPlayerLogin(player);
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
			disconnect(event.getKickMessage(), true);
			return;
		}

		String message = EventFactory.onPlayerJoin(player).getJoinMessage();
		if (message != null) {
			server.broadcastMessage(message);
		}

		player.loadData();
		player.saveData();

		player.getWorld().getRawPlayers().add(player);

		Message userListMessage = new UserListItemMessage(player.getPlayerListName(), true, (short) timeoutCounter);
		for (Player sendPlayer : server.getOnlinePlayers()) {
			((SpoutPlayer) sendPlayer).getSession().send(userListMessage);
			send(new UserListItemMessage(sendPlayer.getPlayerListName(), true, (short) ((SpoutPlayer) sendPlayer).getSession().timeoutCounter));
		}
	}

	@SuppressWarnings("unchecked")
	public void pulse() {
		timeoutCounter++;

		Message message;
		while ((message = messageQueue.poll()) != null) {
			MessageHandler<Message> handler = (MessageHandler<Message>) HandlerLookupService.find(message.getClass());
			if (handler != null) {
				handler.handle(this, player, message);
			}
			timeoutCounter = 0;
		}

		if (timeoutCounter >= TIMEOUT_TICKS) {
			if (pingMessageId == 0) {
				pingMessageId = new Random().nextInt();
				send(new PingMessage(pingMessageId));
				timeoutCounter = 0;
			} else {
				disconnect("Timed out");
			}
		}
	}

	/**
	 * Sends a message to the client.
	 *
	 * @param message The message.
	 */
	public void send(Message message) {
		channel.write(message);
	}

	/**
	 * Disconnects the session with the specified reason. This causes a
	 * {@link KickMessage} to be sent. When it has been delivered, the channel
	 * is closed.
	 *
	 * @param reason The reason for disconnection.
	 */
	public void disconnect(String reason) {
		disconnect(reason, false);
	}

	/**
	 * Disconnects the session with the specified reason. This causes a
	 * {@link KickMessage} to be sent. When it has been delivered, the channel
	 * is closed.
	 *
	 * @param reason The reason for disconnection.
	 * @param overrideKick Whether to override the kick event.
	 */
	public void disconnect(String reason, boolean overrideKick) {
		if (player != null && !overrideKick) {
			PlayerKickEvent event = EventFactory.onPlayerKick(player, reason);
			if (event.isCancelled()) {
				return;
			}

			reason = event.getReason();

			if (event.getLeaveMessage() != null) {
				server.broadcastMessage(event.getLeaveMessage());
			}

			SpoutServer.logger.log(Level.INFO, "Player {0} kicked: {1}", new Object[] {player.getName(), reason});
			dispose(false);
		}

		channel.write(new KickMessage(reason)).addListener(ChannelFutureListener.CLOSE);
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
		return Session.class.getName() + " [address=" + channel.getRemoteAddress() + "]";
	}

	/**
	 * Adds a message to the unprocessed queue.
	 *
	 * @param message The message.
	 * @param <T> The type of message.
	 */
	<T extends Message> void messageReceived(T message) {
		messageQueue.add(message);
	}

	/**
	 * Disposes of this session by destroying the associated player, if there is
	 * one.
	 */
	void dispose(boolean broadcastQuit) {
		if (player != null) {
			player.remove();
			Message userListMessage = new UserListItemMessage(player.getPlayerListName(), false, (short) 0);
			for (Player player : server.getOnlinePlayers()) {
				((SpoutPlayer) player).getSession().send(userListMessage);
			}

			String text = EventFactory.onPlayerQuit(player).getQuitMessage();
			if (broadcastQuit && text != null) {
				server.broadcastMessage(text);
			}
			player = null; // in case we are disposed twice
		}
	}

	public String getSessionId() {
		return sessionId;
	}

	public int getPingMessageId() {
		return pingMessageId;
	}

	public void pong() {
		timeoutCounter = 0;
		pingMessageId = 0;
	}

	public BlockPlacementMessage getPreviousPlacement() {
		return previousPlacement;
	}

	public void setPreviousPlacement(BlockPlacementMessage message) {
		previousPlacement = message;
	}
}
