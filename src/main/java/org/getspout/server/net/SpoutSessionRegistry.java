package org.getspout.server.net;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.getspout.api.protocol.Session;
import org.getspout.api.protocol.SessionRegistry;

/**
 * A list of all the sessions which provides a convenient {@link #pulse()}
 * method to pulse every session in one operation.
 *
 * @author Graham Edgecombe
 */
public final class SpoutSessionRegistry implements SessionRegistry {
	/**
	 * A list of the sessions.
	 */
	private final ConcurrentMap<SpoutSession, Boolean> sessions = new ConcurrentHashMap<SpoutSession, Boolean>();

	/**
	 * Pulses all the sessions.
	 */
	public void pulse() {
		for (SpoutSession session : sessions.keySet()) {
			session.pulse();
		}
	}

	/**
	 * Adds a new session.
	 *
	 * @param session The session to add.
	 */
	public void add(Session session) {
		if (session instanceof SpoutSession) {
			sessions.put((SpoutSession) session, true);
		} else if (session != null) {
			throw new IllegalArgumentException("This session registry can only handle SpoutSessions: ");
		}
	}

	/**
	 * Removes a session.
	 *
	 * @param session The session to remove.
	 */
	public void remove(Session session) {
		if (session instanceof SpoutSession) {
			sessions.remove(session);
		} else if (session != null) {
			throw new IllegalArgumentException("This session registry can only handle SpoutSessions");
		}
	}
}
