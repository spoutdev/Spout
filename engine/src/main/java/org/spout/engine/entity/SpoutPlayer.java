/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
import org.spout.api.component.entity.PlayerNetworkComponent;
import org.spout.api.data.ValueHolder;
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
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.lang.Locale;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.Session;
import org.spout.api.util.access.BanType;
import org.spout.api.util.list.concurrent.ConcurrentList;
import org.spout.api.util.thread.annotation.DelayedWrite;
import org.spout.api.util.thread.annotation.SnapshotRead;
import org.spout.api.util.thread.annotation.Threadsafe;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.SpoutServer;
import org.spout.engine.component.entity.MovementValidatorComponent;
import org.spout.engine.component.entity.SpoutPhysicsComponent;
import org.spout.engine.filesystem.versioned.PlayerFiles;
import org.spout.engine.protocol.SpoutSession;
import org.spout.engine.world.SpoutServerWorld;

public class SpoutPlayer extends SpoutEntity implements Player {
	private final AtomicReference<String> displayName = new AtomicReference<>();
	private final AtomicReference<String> name = new AtomicReference<>();
	private final AtomicBoolean onlineLive = new AtomicBoolean(true);
	private boolean online;
	private final int hashcode;
	private PlayerInputState inputState = PlayerInputState.DEFAULT_STATE;
	private Locale preferredLocale = Locale.getByCode(SpoutConfiguration.DEFAULT_LANGUAGE.getString());
	private List<Entity> hiddenEntities = new ConcurrentList<>();

	public SpoutPlayer(Engine engine, Class<? extends PlayerNetworkComponent> network, SpoutPlayerSnapshot snapshot) {
		super(engine, snapshot);
		this.network = add(network);
		this.name.set(snapshot.getName());
		this.displayName.set(snapshot.getName());
		this.hashcode = name.hashCode();
		this.online = true;
		if (Spout.getPlatform() == Platform.SERVER) {
			add(MovementValidatorComponent.class);
		}
		copySnapshot();
	}

	public SpoutPlayer(Engine engine, Class<? extends PlayerNetworkComponent> network, String name, Transform transform) {
		this(engine, network, name, transform, null, (byte[]) null, (Class<? extends Component>[]) null);
	}

	public SpoutPlayer(Engine engine, Class<? extends PlayerNetworkComponent> network, String name, Transform transform, UUID uid, byte[] dataMap, Class<? extends Component>... components) {
		super(engine, transform, uid, dataMap, components);
		this.network = add(network);
		this.name.set(name);
		this.displayName.set(name);
		this.hashcode = name.hashCode();
		if (Spout.getPlatform() == Platform.SERVER) {
			add(MovementValidatorComponent.class);
		}
		copySnapshot();
	}

	@Override
	@Threadsafe
	public String getName() {
		return name.get();
	}

	public void setName(final String name) {
		this.name.set(name);
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
	public boolean isOnline() {
		return online;
	}

	public boolean isOnlineLive() {
		return onlineLive.get();
	}

	@DelayedWrite
	public boolean disconnect(boolean async) {
		if (Spout.getPlatform() == Platform.SERVER) {
			((SpoutServerWorld) getWorld()).removePlayer(this);
			//save player data on disconnect, probably should do this periodically as well...
			PlayerFiles.savePlayerData(this, async);
		}
		onlineLive.set(false);
		return true;
	}

	@Override
	public void sendMessage(String message) {
		sendCommand("say", message.split(" "));
	}

	@Override
	public void sendCommand(String command, String... args) {
		final Command cmd = Spout.getCommandManager().getCommand(command, false);
		final Session session = getNetwork().getSession();
		final Message msg = session.getProtocol().getCommandMessage(cmd, new CommandArguments(cmd.getName(), args));
		if (msg == null) {
			return;
		}
		session.send(msg);
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
			cmd.process(this, arguments);
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

		((SpoutSession) getNetwork().getSession()).disconnect(!stop, stop, reason);
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
		((Server) getEngine()).getAccessManager().ban(BanType.PLAYER, name.get(), kick, reason);
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
	public void finalizeRun() {
		if (getEngine().getPlatform() != Platform.CLIENT && !this.isOnlineLive()) {
			remove();
		}

		if (isRemoved()) {
			if (getEngine().getPlatform() == Platform.SERVER) {
				((SpoutServer) getEngine()).removePlayer(this);
			}
			// TODO stop client?
			return;
		}
		super.finalizeRun();
	}

	@Override
	public void preSnapshotRun() {
		super.preSnapshotRun();
		if (this.isOnline()) {
			this.getNetwork().preSnapshot(((SpoutPhysicsComponent) getPhysics()).getTransformLive().copy());
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
		return new ArrayList<>(hiddenEntities);
	}

	@Override
	public boolean isInvisible(Entity entity) {
		return hiddenEntities.contains(entity);
	}

	@Override
	public PlayerSnapshot snapshot() {
		return new SpoutPlayerSnapshot(this);
	}

	@Override
	public PlayerNetworkComponent getNetwork() {
		return (PlayerNetworkComponent) network;
	}
}
