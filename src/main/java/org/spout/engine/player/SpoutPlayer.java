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
package org.spout.engine.player;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Spout;
import org.spout.api.data.ValueHolder;
import org.spout.api.entity.Entity;
import org.spout.api.event.Result;
import org.spout.api.event.player.PlayerChatEvent;
import org.spout.api.event.server.data.RetrieveDataEvent;
import org.spout.api.event.server.permissions.PermissionGetGroupsEvent;
import org.spout.api.event.server.permissions.PermissionGroupEvent;
import org.spout.api.event.server.permissions.PermissionNodeEvent;
import org.spout.api.geo.World;
import org.spout.api.player.Player;
import org.spout.api.player.PlayerInputState;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.protocol.Session;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.SnapshotRead;
import org.spout.api.util.thread.Threadsafe;

import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.protocol.SpoutSession;
import org.spout.engine.util.TextWrapper;

public class SpoutPlayer implements Player {
	private final AtomicReference<SpoutSession> sessionLive = new AtomicReference<SpoutSession>();
	private SpoutSession session;
	private final String name;
	private final AtomicReference<String> displayName = new AtomicReference<String>();
	private final AtomicReference<SpoutEntity> entityLive = new AtomicReference<SpoutEntity>();
	private Entity entity;
	private final AtomicBoolean onlineLive = new AtomicBoolean(false);
	private boolean online;
	private final int hashcode;
	private final PlayerInputState inputState = new PlayerInputState();

	public SpoutPlayer(String name) {
		this.name = name;
		displayName.set(name);
		hashcode = name.hashCode();
	}

	public SpoutPlayer(String name, SpoutEntity entity, SpoutSession session) {
		this(name);
		sessionLive.set(session);
		this.session = session;
		entityLive.set(entity);
		this.entity = entity;
		online = true;
		onlineLive.set(true);
	}

	@Override
	@Threadsafe
	public String getName() {
		return name;
	}

	@Override
	@Threadsafe
	public String getDisplayName() {
		return displayName.get();
	}

	@Override
	@Threadsafe
	public void setDisplayName(String name) {
		displayName.set(name);
	}

	@Override
	@SnapshotRead
	public Entity getEntity() {
		return entity;
	}

	@Override
	@SnapshotRead
	public SpoutSession getSession() {
		return session;
	}

	@Override
	@SnapshotRead
	public boolean isOnline() {
		return online;
	}

	public boolean isOnlineLive() {
		return onlineLive.get();
	}

	@Override
	@SnapshotRead
	public InetAddress getAddress() {
		if (session != null && session.getAddress() != null) {
			return session.getAddress().getAddress();
		}
		return null;
	}

	@DelayedWrite
	public boolean disconnect() {
		if (!onlineLive.compareAndSet(true, false)) {
			// player was already offline
			return false;
		}

		final SpoutEntity entity = entityLive.getAndSet(null);
		if (entity != null) {
			entity.kill();
		}
		sessionLive.set(null);
		return true;
	}

	@DelayedWrite
	public boolean connect(SpoutSession session, SpoutEntity entity) {
		if (!onlineLive.compareAndSet(false, true)) {
			// player was already online
			return false;
		}

		sessionLive.set(session);
		entityLive.set(entity);
		copyToSnapshot();
		return true;
	}

	@Override
	public void chat(final String message) {
		if (message.startsWith("/")) {
			Spout.getEngine().processCommand(this, message.substring(1));
		} else {
			PlayerChatEvent event = Spout.getEngine().getEventManager().callEvent(new PlayerChatEvent(this, message));
			if (event.isCancelled()) {
				return;
			}
			String formattedMessage;
			try {
				formattedMessage = String.format(event.getFormat(), getDisplayName(), event.getMessage());
			} catch (Throwable t) {
				return;
			}
			Spout.getEngine().broadcastMessage(formattedMessage);
		}
	}

	@Override
	public boolean sendMessage(String message) {
		boolean success = false;
		if (getEntity() != null) {
			for (String line : TextWrapper.wrapText(message)) {
				success |= sendRawMessage(line);
			}
		}
		return success;
	}

	@Override
	public boolean sendRawMessage(String message) {
		Message chatMessage = getNetworkSynchronizer().getChatMessage(message);
		if (message == null) {
			return false;
		}

		session.send(chatMessage);
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SpoutPlayer) {
			SpoutPlayer p = (SpoutPlayer) obj;
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
			world = entity.getWorld();
		}

		return hasPermission(world, node);
	}

	@Override
	public boolean hasPermission(World world, String node) {
		PermissionNodeEvent event = Spout.getEngine().getEventManager().callEvent(new PermissionNodeEvent(world, this, node));
		if (event.getResult() == Result.DEFAULT) {
			return false;
		}

		return event.getResult().getResult();
	}

	@Override
	public boolean isInGroup(String group) {
		World world = null;
		Entity entity = getEntity();
		if (entity != null) {
			world = entity.getWorld();
		}

		PermissionGroupEvent event = Spout.getEngine().getEventManager().callEvent(new PermissionGroupEvent(world, this, group));
		return event.getResult();
	}

	@Override
	public String[] getGroups() {
		World world = null;
		Entity entity = getEntity();
		if (entity != null) {
			world = entity.getWorld();
		}

		PermissionGetGroupsEvent event = Spout.getEngine().getEventManager().callEvent(new PermissionGetGroupsEvent(world, this));
		return event.getGroups();
	}

	@Override
	public boolean isGroup() {
		return false;
	}

	@Override
	public ValueHolder getData(String node) {
		RetrieveDataEvent event = Spout.getEngine().getEventManager().callEvent(new RetrieveDataEvent(this, node));
		return event.getResult();
	}

	@Override
	public void kick() {
		kick("Kicked");
	}

	@Override
	public void kick(String reason) {
		if (reason == null) {
			throw new IllegalArgumentException("reason cannot be null");
		}
		session.disconnect(reason);
	}

	@Override
	public NetworkSynchronizer getNetworkSynchronizer() {
		SpoutSession session = this.session;
		return session.getNetworkSynchronizer();
	}

	@Override
	public PlayerInputState input() {
		return inputState;
	}

}
