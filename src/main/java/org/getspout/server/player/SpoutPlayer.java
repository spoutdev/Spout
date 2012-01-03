package org.getspout.server.player;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.getspout.api.Spout;
import org.getspout.api.entity.Entity;
import org.getspout.api.player.Player;
import org.getspout.api.protocol.Session;
import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.SnapshotRead;
import org.getspout.api.util.thread.Threadsafe;
import org.getspout.server.util.TextWrapper;

public class SpoutPlayer implements Player {
	
	private final AtomicReference<Session> sessionLive = new AtomicReference<Session>();
	private Session session;
	private final String name;
	private final AtomicReference<Entity> entityLive = new AtomicReference<Entity>();
	private Entity entity;
	private final AtomicBoolean onlineLive = new AtomicBoolean(false);
	private boolean online;
	
	public SpoutPlayer(String name, Entity entity, Session session) {
		this.name = name;
		this.sessionLive.set(session);
		this.session = session;
		this.entityLive.set(entity);
		this.entity = entity;
	}

	@Override
	@Threadsafe
	public String getName() {
		return name;
	}

	@Override
	@SnapshotRead
	public Entity getEntity() {
		return entity;
	}

	@Override
	@SnapshotRead
	public Session getSession() {
		return session;
	}

	@Override
	@SnapshotRead
	public boolean isOnline() {
		return online;
	}
	
	@DelayedWrite
	public void disconnect() {
		if (entity == null) {
			throw new IllegalStateException("Attempting to disconnect an offline player");
		} else {
			// TODO - actually handle disconnect properly
			onlineLive.set(false);
			sessionLive.set(null);
			entityLive.set(null);
		}
	}
	
	@DelayedWrite
	public void connect(Session session, Entity entity) {
		// TODO, do all 3 atomically .. or remove this method and make everything final?
		onlineLive.set(true);
		sessionLive.set(session);
		entityLive.set(entity);
	}
	
	@Override
	public void chat(String message) {
		if (message.startsWith("/")) {
			Spout.getGame().processCommand(this, message.substring(1));
		} else {
			for (Player player : Spout.getGame().getOnlinePlayers()) {
				if (player != this) player.sendMessage(message);
			}
		}
	}

	@Override
	public boolean sendMessage(String message) {
		boolean success = false;
		if (getEntity() == null)
		for (String line : TextWrapper.wrapText(message)) {
			success |= sendRawMessage(line);
		}
		return success;
	}

	@Override
	public boolean sendRawMessage(String message) {
		return false;
	}
}
