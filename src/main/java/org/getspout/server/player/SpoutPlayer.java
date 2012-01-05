package org.getspout.server.player;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;

import org.getspout.api.Spout;
import org.getspout.api.entity.Entity;
import org.getspout.api.event.player.PlayerChatEvent;
import org.getspout.api.player.Player;
import org.getspout.api.protocol.Message;
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
	private final int hashcode;
	
	private static AtomicInteger uidCounter = new AtomicInteger(1);

	public SpoutPlayer(String name) {
		this.name = name;
		hashcode = name.hashCode();
	}

	public SpoutPlayer(String name, Entity entity, Session session) {
		this(name);
		this.sessionLive.set(session);
		this.session = session;
		this.entityLive.set(entity);
		this.entity = entity;
		this.online = true;
		this.onlineLive.set(true);
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

	@Override
	@SnapshotRead
	public InetAddress getAddress() {
		return session.getAddress().getAddress();
	}

	@DelayedWrite
	public boolean disconnect() {
		if (onlineLive.compareAndSet(true, false)) {
			entityLive.get().kill();
			sessionLive.set(null);
			entityLive.set(null);
			return true;
		} else {
			return false;
		}
	}

	@DelayedWrite
	public boolean connect(Session session, Entity entity) {
		if (onlineLive.compareAndSet(false, true)) {
			sessionLive.set(session);
			entityLive.set(entity);
			return true;
		} else {
			return true;
		}
	}

	@Override
	public void chat(String message) {
		PlayerChatEvent event = Spout.getGame().getEventManager().callEvent(new PlayerChatEvent(this, message));
		message = event.getMessage();
		if (event.isCancelled()) return;
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
		Message chatMessage = getSession().getPlayerProtocol().getChatMessage(message);
		if (message != null) {
			session.send(chatMessage);
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SpoutPlayer) {
			SpoutPlayer p = (SpoutPlayer)obj;
			if (p.hashCode() != hashCode()) {
				return false;
			} else if (p == this) {
				return true;
			} else {
				return name.equals(p.name);
			} 
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return hashcode;
	}
	
	public void copyToSnapshot() {
		session = sessionLive.get();
		online = onlineLive.get();
		entity = entityLive.get();
	}
	
}
