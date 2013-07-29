package org.spout.api.component.entity;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Client;
import org.spout.api.Server;
import org.spout.api.entity.Player;
import org.spout.api.event.ProtocolEvent;
import org.spout.api.geo.discrete.Point;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.protocol.ClientSession;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.ServerSession;
import org.spout.api.protocol.Session;
import org.spout.api.util.SyncedStringMap;

/**
 * The networking behind {@link org.spout.api.entity.Player}s. This component holds the {@link Session} which is the connection
 * the Player has to the server.
 */
public abstract class PlayerNetworkComponent extends NetworkComponent {
	private static final SyncedStringMap protocolMap = SyncedStringMap.create(null, new MemoryStore<Integer>(), 0, 256, "componentProtocols");
	private AtomicReference<Session> session = new AtomicReference<>(null);

	@Override
	public void onAttached() {
		if (!(getOwner() instanceof Player)) {
			throw new IllegalStateException("The PlayerNetworkComponent may only be given to Players");
		}
		setObserver(true);
	}

	/**
	 * Returns the {@link Session} representing the connection to the server.
	 * @return The session
	 */
	public final Session getSession() {
		return session.get();
	}

	/**
	 * Sets the session this Player has to the server.
	 * @param session The session to the server
	 */
	public final void setSession(Session session) {
		if (getEngine() instanceof Client && !(session instanceof ClientSession)) {
			throw new IllegalStateException("The client may only have a ClientSession");
		}

		if (getEngine() instanceof Server && !(session instanceof ServerSession)) {
			throw new IllegalStateException("The server may only have a ServerSession");
		}

		if (!this.session.compareAndSet(null, session)) {
			throw new IllegalStateException("Once set, the session may not be re-set until a new connection is made");
		}
	}

	/**
	 * Calls a {@link ProtocolEvent} for all {@link Player}s within sync distance of the owning Player.
	 * <p/>
	 * This method also sends the protocol event to the owning Player as well.
	 *
	 * @param event to send
	 */
	public final void callProtocolEvent(final ProtocolEvent event) {
		callProtocolEvent(event, false);
	}

	/**
	 * Calls a {@link ProtocolEvent} for all {@link Player}s within sync distance of the owning Player.
	 *
	 * @param event to send
	 * @param ignoreOwner True to ignore the owning Player, false to include
	 */
	public final void callProtocolEvent(final ProtocolEvent event, final boolean ignoreOwner) {
		final List<Player> players = getOwner().getWorld().getPlayers();
		final Point position = getOwner().getPhysics().getPosition();
		final List<Message> messages = getEngine().getEventManager().callEvent(event).getMessages();

		for (final Player player : players) {
			if (ignoreOwner && getOwner() == player) {
				continue;
			}
			final Point otherPosition = player.getPhysics().getPosition();
			//TODO: Verify this math
			if (position.subtract(otherPosition).fastLength() > getOwner().getNetwork().getSyncDistance()) {
				continue;
			}
			for (final Message message : messages) {
				player.getNetwork().getSession().send(false, message);
			}
		}
	}

	/**
	 * Calls a {@link ProtocolEvent} for all {@link Player}s provided.
	 *
	 * @param event to send
	 * @param players to send to
	 */
	public final void callProtocolEvent(final ProtocolEvent event, final Player... players) {
		final List<Message> messages = getEngine().getEventManager().callEvent(event).getMessages();
		for (final Player player : players) {
			for (final Message message : messages) {
				player.getNetwork().getSession().send(false, message);
			}
		}
	}

	/**
	 * Registers the protocol name and gets the id assigned.
	 *
	 * @param protocolName The name of the protocol class to get an id for
	 * @return The id for the specified protocol class
	 */
	public static int getProtocolId(String protocolName) {
		return protocolMap.register(protocolName);
	}
}
