/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.entity;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Engine;
import org.spout.api.Platform;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.command.Command;
import org.spout.api.command.CommandArguments;
import org.spout.api.component.Component;
import org.spout.api.data.ValueHolder;
import org.spout.api.datatable.SerializableMap;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.entity.PlayerSnapshot;
import org.spout.api.entity.state.PlayerInputState;
import org.spout.api.event.entity.EntityHiddenEvent;
import org.spout.api.event.entity.EntityShownEvent;
import org.spout.api.event.server.PreCommandEvent;
import org.spout.api.event.server.RetrieveDataEvent;
import org.spout.api.event.server.permissions.PermissionGroupsEvent;
import org.spout.api.event.server.permissions.PermissionNodeEvent;
import org.spout.api.exception.CommandException;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.lang.Locale;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.util.access.BanType;
import org.spout.api.util.list.concurrent.ConcurrentList;
import org.spout.api.util.thread.annotation.DelayedWrite;
import org.spout.api.util.thread.annotation.SnapshotRead;
import org.spout.api.util.thread.annotation.Threadsafe;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.SpoutEngine;
import org.spout.engine.filesystem.versioned.PlayerFiles;
import org.spout.engine.protocol.SpoutSession;
import org.spout.engine.world.SpoutWorld;

public class SpoutPlayer extends SpoutEntity implements Player {
	private final AtomicReference<SpoutSession<?>> sessionLive = new AtomicReference<SpoutSession<?>>();
	private SpoutSession<?> session;
	private final String name;
	private final AtomicReference<String> displayName = new AtomicReference<String>();
	private final AtomicBoolean onlineLive = new AtomicBoolean(false);
	private boolean online;
	private final int hashcode;
	private PlayerInputState inputState = PlayerInputState.DEFAULT_STATE;
	private Locale preferredLocale = Locale.getByCode(SpoutConfiguration.DEFAULT_LANGUAGE.getString());
	private List<Entity> hiddenEntities = new ConcurrentList<Entity>();

	public SpoutPlayer(Engine engine, String name) {
		this(engine, name, null, SpoutConfiguration.VIEW_DISTANCE.getInt() * Chunk.BLOCKS.SIZE);
	}

	public SpoutPlayer(Engine engine, String name, Transform transform, int viewDistance) {
		this(engine, name, transform, viewDistance, null, true, (byte[])null, (Class<? extends Component>[]) null);
	}

	protected SpoutPlayer(Engine engine, String name, Transform transform, int viewDistance, UUID uid, boolean load, SerializableMap dataMap, Class<? extends Component>... components) {
		this(engine, name, transform, viewDistance, uid, load, (byte[])null, components);
		this.getData().putAll(dataMap);
	}

	public SpoutPlayer(Engine engine, String name, Transform transform, int viewDistance, UUID uid, boolean load, byte[] dataMap, Class<? extends Component>... components) {
		super(engine, transform, viewDistance, uid, load, dataMap, components);
		this.name = name;
		displayName.set(name);
		hashcode = name.hashCode();
		this.setObserver(true);
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
	public SpoutSession<?> getSession() {
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
	public boolean disconnect(boolean async) {
		((SpoutWorld) getWorld()).removePlayer(this);
		onlineLive.set(false);
		//save player data on disconnect, probably should do this periodically as well...
		PlayerFiles.savePlayerData(this, async);
		return true;
	}

	@DelayedWrite
	public boolean connect(SpoutSession<?> session, Transform newTransform) {
		if (!onlineLive.compareAndSet(false, true)) {
			// player was already online
			return false;
		}
		//Disallow null transforms or transforms with null worlds
		if (newTransform == null || newTransform.getPosition().getWorld() == null) {
			return false;
		}
		getScene().setTransform(newTransform);
		if (getEngine().getPlatform() == Platform.SERVER) {
			setupInitialChunk(newTransform, LoadOption.LOAD_GEN);
		}
		sessionLive.set(session);
		copySnapshot();
		return true;
	}

	@Override
	public void sendMessage(String message) {
		sendCommand("say", message.split(" "));
	}

	@Override
	public void sendCommand(String command, String... args) {
		Command cmd = Spout.getCommandManager().getCommand(command, false);
		Message msg = session.getProtocol().getCommandMessage(cmd, new CommandArguments(args));
		if (msg == null) {
			return;
		}
		session.send(false, msg);
	}

	@Override
	public void processCommand(String command, String... args) {
		// call the event
		PreCommandEvent event = getEngine().getEventManager().callEvent(new PreCommandEvent(this, command, args));
		if (event.isCancelled()) {
			return;
		}
		command = event.getCommand();
		CommandArguments arguments = event.getArguments();

		// get the command
		Command cmd = getEngine().getCommandManager().getCommand(command, false);
		if (cmd == null) {
			sendMessage("Unknown command: " + command);
			return;
		}

		// try to execute and send any exceptions to the player
		try {
			cmd.execute(this, arguments);
		} catch (CommandException e) {
			sendMessage(e.getMessage());
		}
	}

	@Override
	public int hashCode() {
		return hashcode;
	}

	@Override
	public void copySnapshot() {
		super.copySnapshot();
		session = sessionLive.get();
		online = onlineLive.get();
	}

	@Override
	public boolean hasPermission(String node) {
		return hasPermission(getWorld(), node);
	}

	@Override
	public boolean hasPermission(World world, String node) {
		PermissionNodeEvent event = getEngine().getEventManager().callEvent(new PermissionNodeEvent(world, this, node));
		return event.getResult().getResult();
	}

	@Override
	public boolean isInGroup(String group) {
		return isInGroup(getWorld(), group);
	}

	@Override
	public boolean isInGroup(World world, String group) {
		for (String g : getGroups(world)) {
			if (g.equalsIgnoreCase(group)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String[] getGroups() {
		return getGroups(getWorld());
	}

	@Override
	public String[] getGroups(World world) {
		PermissionGroupsEvent event = getEngine().getEventManager().callEvent(new PermissionGroupsEvent(world, this));
		return event.getGroups();
	}

	@Override
	public ValueHolder getData(String node) {
		return getData(getWorld(), node);
	}

	@Override
	public ValueHolder getData(World world, String node) {
		RetrieveDataEvent event = getEngine().getEventManager().callEvent(new RetrieveDataEvent(world, this, node));
		return event.getResult();
	}

	@Override
	public boolean hasData(String node) {
		return hasData(getWorld(), node);
	}

	@Override
	public boolean hasData(World world, String node) {
		return getData(world, node) != null;
	}

	@Override
	public void kick() {
		kick(null);
	}

	@Override
	public void kick(String reason) {
		kick(false, reason);
	}

	public void kick(boolean stop, String reason) {
		if (reason == null) {
			reason = "Kicked from server.";
		}
		//If we are stopping, it's not really a kick (it's a friendly disconnect)
		//If we aren't stopping, it really is a kick
		session.disconnect(!stop, stop, reason);
	}

	@Override
	public void ban() {
		ban(true);
	}

	@Override
	public void ban(boolean kick) {
		ban(kick, null);
	}

	@Override
	public void ban(boolean kick, String reason) {
		if (getEngine().getPlatform() != Platform.SERVER) {
			throw new IllegalStateException("Banning is only available in server mode.");
		}
		((Server) getEngine()).getAccessManager().ban(BanType.PLAYER, name, kick, reason);
	}

	@Override
	public NetworkSynchronizer getNetworkSynchronizer() {
		SpoutSession<?> session = this.session;
		return session == null ? null : session.getNetworkSynchronizer();
	}

	@Override
	public PlayerInputState input() {
		return inputState;
	}

	@Override
	public void processInput(PlayerInputState state) {
		if (state == null) {
			throw new IllegalArgumentException("PlayerInputState cannot be null!");
		}
		inputState = state;
	}

	@Override
	public Locale getPreferredLocale() {
		return preferredLocale;
	}

	@Override
	public boolean save() {
		PlayerFiles.savePlayerData(this, true);
		return true;
	}

	@Override
	public void teleport(Point loc) {
		getScene().setPosition(loc);
		getNetworkSynchronizer().setPositionDirty();
	}

	@Override
	public void teleport(Transform transform) {
		getScene().setTransform(transform);
		getNetworkSynchronizer().setPositionDirty();
	}

	@Override
	public void finalizeRun() {
		if (getEngine().getPlatform() != Platform.CLIENT && !this.isOnlineLive()) {
			remove();
		}
		super.finalizeRun();
		if (this.isOnline()) {
			this.getNetworkSynchronizer().finalizeTick();
		}
		if (isRemoved()) {
			getNetworkSynchronizer().onRemoved();
			((SpoutEngine) getEngine()).removePlayer(this);
			sessionLive.set(null);
		}
	}

	@Override
	public void preSnapshotRun() {
		super.preSnapshotRun();
		if (this.isOnline()) {
			this.getNetworkSynchronizer().preSnapshot();
		}
	}

	@Override
	public void setVisible(Entity entity, boolean visible) {
		if (visible) {
			hiddenEntities.remove(entity);
			getEngine().getEventManager().callEvent(new EntityShownEvent(entity, this));
		} else {
			hiddenEntities.add(entity);
			getEngine().getEventManager().callEvent(new EntityHiddenEvent(entity, this));
		}
	}

	@Override
	public List<Entity> getInvisibleEntities() {
		return new ArrayList<Entity>(hiddenEntities);
	}

	@Override
	public boolean isInvisible(Entity entity) {
		return hiddenEntities.contains(entity);
	}

	@Override
	public PlayerSnapshot snapshot() {
		return new SpoutPlayerSnapshot(this);
	}
}
