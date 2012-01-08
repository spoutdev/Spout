package org.getspout.server.player;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.getspout.api.Spout;
import org.getspout.api.entity.Entity;
import org.getspout.api.event.Result;
import org.getspout.api.event.player.PlayerChatEvent;
import org.getspout.api.event.server.data.RetrieveIntDataEvent;
import org.getspout.api.event.server.data.RetrieveObjectDataEvent;
import org.getspout.api.event.server.data.RetrieveStringDataEvent;
import org.getspout.api.event.server.permissions.PermissionGetGroupsEvent;
import org.getspout.api.event.server.permissions.PermissionGroupEvent;
import org.getspout.api.event.server.permissions.PermissionNodeEvent;
import org.getspout.api.geo.World;
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
			String formattedMessage;
			try {
				formattedMessage = String.format(event.getFormat(), getName(), message);
			} catch (Throwable t) {
				return;
			}
			Spout.getGame().broadcastMessage(formattedMessage);
		}
	}

	@Override
	public boolean sendMessage(String message) {
		boolean success = false;
		if (getEntity() != null)
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

	@Override
	public boolean hasPermission(String node) {
		World world = null;
		Entity entity = getEntity();
		if (entity != null) {
			world = entity.getChunk().getWorld();
		}

		return hasPermission(world, node);
	}

	@Override
	public boolean hasPermission(World world, String node) {
		PermissionNodeEvent event = session.getGame().getEventManager()
				.callEvent(new PermissionNodeEvent(world, this, node));
		if (event.getResult() == Result.DEFAULT) {
			return false;
		}

		return event.getResult().getResult();
	}

	@Override
	public boolean isInGroup(String group) {
		PermissionGroupEvent event = session.getGame().getEventManager()
				.callEvent(new PermissionGroupEvent(this.entity.getChunk().getWorld(), this, group));
		return event.getResult();
	}

	@Override
	public String[] getGroups() {
		PermissionGetGroupsEvent event = session.getGame().getEventManager()
				.callEvent(new PermissionGetGroupsEvent(this.entity.getChunk().getWorld(), this));
		return event.getGroups();
	}

	@Override
	public boolean isGroup() {
		return false;
	}

	public Object getData(String node) {
		return getData(node, null);
	}
	
	public Object getData(String node, Object defaultValue) {
		World world = null;
		Entity entity = getEntity();
		if (entity != null) {
			world = entity.getChunk().getWorld();
		}
		return getData(world, node, defaultValue);
	}
	
	public Object getData(World world, String node) {
		return getData(world, node, null);
	}
	
	public Object getData(World world, String node, Object defaultValue) {
		RetrieveObjectDataEvent event = session.getGame().getEventManager()
				.callEvent(new RetrieveObjectDataEvent(world, this, node));
		Object res = event.getResult();
		if (res == null) {
			return defaultValue;
		}
		return res;
	}

	@Override
	public int getInt(String node) {
		return getInt(node, RetrieveIntDataEvent.DEFAULT_VALUE);
	}

	@Override
	public int getInt(String node, int defaultValue) {
		World world = null;
		Entity entity = getEntity();
		if (entity != null) {
			world = entity.getChunk().getWorld();
		}
		return getInt(world, node, defaultValue);
	}

	@Override
	public int getInt(World world, String node) {
		return getInt(world, node, RetrieveIntDataEvent.DEFAULT_VALUE);
	}

	@Override
	public int getInt(World world, String node, int defaultValue) {
		RetrieveIntDataEvent event = session.getGame().getEventManager()
				.callEvent(new RetrieveIntDataEvent(world, this, node));
		int res = event.getResult();
		if (res == RetrieveIntDataEvent.DEFAULT_VALUE) {
			return defaultValue;
		}
		return res;
	}

	public String getString(String node) {
		return getString(node, null);
	}

	public String getString(String node, String defaultValue) {
		World world = null;
		Entity entity = getEntity();
		if (entity != null) {
			world = entity.getChunk().getWorld();
		}
		return getString(world, node, defaultValue);
	}

	public String getString(World world, String node) {
		return getString(world, node, null);
	}

	public String getString(World world, String node, String defaultValue) {
		RetrieveStringDataEvent event = session.getGame().getEventManager()
				.callEvent(new RetrieveStringDataEvent(world, this, node));
		String res = event.getResult();
		if (res == null) {
			return defaultValue;
		}
		return res;
	}

	@Override
	public void kick() {
		kick("");
	}

	@Override
	public void kick(String reason) {
		session.disconnect(reason);
	}
}
