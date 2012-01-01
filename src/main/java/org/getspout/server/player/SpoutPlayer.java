package org.getspout.server.player;

import org.getspout.api.Spout;
import org.getspout.api.entity.Entity;
import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.player.Player;
import org.getspout.api.protocol.Session;
import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.SnapshotRead;
import org.getspout.api.util.thread.Threadsafe;
import org.getspout.server.util.TextWrapper;

public class SpoutPlayer implements Player {
	
	private Session session;
	private final String name;
	private Entity entity;
	private Transform transform;
	
	private volatile boolean update = false;
	private Session sessionNext;
	private Entity entityNext;
	private Transform transformNext;
	
	public SpoutPlayer(String name, Transform transform) {
		update = true;
		this.name = name;
		this.transformNext = transform;
		this.transform = transform;
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
	public Transform getTransform() {
		Entity entity = getEntity();
		if (entity == null) {
			return transform;
		} else {
			return entity.getTransform();
		}
		
	}

	@Override
	@SnapshotRead
	public Session getSession() {
		return session;
	}

	@Override
	@SnapshotRead
	public boolean isOnline() {
		return session != null && entity != null;
	}
	
	@DelayedWrite
	public void disconnect() {
		Entity entity = getEntity();
		if (entity == null) {
			throw new IllegalStateException("Attempting to disconnect an offline player");
		} else {
			update = true;
			this.transformNext = entity.getTransform();
			this.entityNext = null;
			this.sessionNext = null;
			// TODO - save on disconnect ?
		}
	}
	
	@DelayedWrite
	public void connect(Entity entity, Session session) {
		update = true;
		this.entityNext = entity;
		this.sessionNext = session;
	}
	
	public void copySnapshot() {
		if (update) {
			this.entity = entityNext;
			this.session = sessionNext;
			this.transform = transformNext;
		}
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
