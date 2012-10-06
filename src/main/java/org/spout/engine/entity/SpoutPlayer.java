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
package org.spout.engine.entity;

import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.ChatSection;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.Command;
import org.spout.api.command.RootCommand;
import org.spout.api.component.Component;
import org.spout.api.data.ValueHolder;
import org.spout.api.entity.Player;
import org.spout.api.entity.state.PlayerInputState;
import org.spout.api.event.server.RetrieveDataEvent;
import org.spout.api.event.server.permissions.PermissionGroupsEvent;
import org.spout.api.event.server.permissions.PermissionNodeEvent;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.lang.Locale;
import org.spout.api.plugin.Platform;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.util.access.BanType;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.SnapshotRead;
import org.spout.api.util.thread.Threadsafe;
import org.spout.engine.SpoutConfiguration;
import org.spout.engine.SpoutEngine;
import org.spout.engine.filesystem.WorldFiles;
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

	public SpoutPlayer(String name) {
		this(name, null, SpoutConfiguration.VIEW_DISTANCE.getInt() * Chunk.BLOCKS.SIZE);
	}

	public SpoutPlayer(String name, Transform transform, int viewDistance) {
		this(name, transform, viewDistance, null, true, null, (Class<? extends Component>[])null);
	}

	public SpoutPlayer(String name, Transform transform, int viewDistance, UUID uid, boolean load, byte[] dataMap, Class<? extends Component> ...components) {
		super(transform, viewDistance, uid, load, dataMap, components);
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
		SpoutSession<?> session = this.session;
		if (session == null) {
			throw new IllegalArgumentException("Session cannot be null!");
		}
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
		((SpoutWorld) getWorld()).removePlayer(this);
		onlineLive.set(false);
		//save player data on disconnect, probably should do this periodically as well...
		WorldFiles.savePlayerData(this);
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
		getTransform().setTransform(newTransform);
		setupInitialChunk(newTransform);
		sessionLive.set(session);
		copySnapshot();
		return true;
	}

	@Override
	public boolean sendMessage(Object... message) {
		return sendRawMessage(message);
	}

	@Override
	public void sendCommand(String commandName, ChatArguments arguments) {
		Command command = Spout.getEngine().getRootCommand().getChild(commandName);
		Message cmdMessage = getSession().getProtocol().getCommandMessage(command, arguments);
		if (cmdMessage == null) {
			return;
		}

		session.send(false, cmdMessage);
	}

	@Override
	public void processCommand(String command, ChatArguments arguments) {
		final RootCommand rootCmd = Spout.getEngine().getRootCommand();
		Command cmd = rootCmd.getChild(command);
		if (cmd == null) {
			sendMessage(rootCmd.getMissingChildException(rootCmd.getUsage(command,
					arguments.toSections(ChatSection.SplitType.WORD), -1)).getMessage());
		}  else {
			cmd.process(this, command, arguments, false);
		}
	}

	@Override
	public boolean sendMessage(ChatArguments message) {
		return sendRawMessage(message);
	}

	@Override
	public boolean sendRawMessage(Object... message) {
		return sendRawMessage(new ChatArguments(message));
	}

	@Override
	public boolean sendRawMessage(ChatArguments message) {
		sendCommand("say", message);
		return true;
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
		PermissionNodeEvent event = Spout.getEventManager().callEvent(new PermissionNodeEvent(world, this, node));
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
		PermissionGroupsEvent event = Spout.getEventManager().callEvent(new PermissionGroupsEvent(world, this));
		return event.getGroups();
	}

	@Override
	public ValueHolder getData(String node) {
		return getData(getWorld(), node);
	}

	@Override
	public ValueHolder getData(World world, String node) {
		RetrieveDataEvent event = Spout.getEngine().getEventManager().callEvent(new RetrieveDataEvent(world, this, node));
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
		kick((Object[])null);
	}

	@Override
	public void kick(Object... reason) {
		if (reason == null) {
			reason = new Object[] {ChatStyle.RED, "Kicked from server."};
		}
		session.disconnect(reason);
	}

	@Override
	public void ban() {
		ban(true);
	}

	@Override
	public void ban(boolean kick) {
		ban(kick, (Object[])null);
	}

	@Override
	public void ban(boolean kick, Object... reason) {
		if (Spout.getPlatform() != Platform.SERVER) {
			throw new IllegalStateException("Banning is only available in server mode.");
		}
		((Server)Spout.getEngine()).getAccessManager().ban(BanType.PLAYER, name, kick, reason);
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
		return false;
	}

	@Override
	public void teleport(Point loc) {
		getTransform().setPosition(loc);
		getNetworkSynchronizer().setPositionDirty();
	}
	
	@Override
	public void teleport(Transform transform) {
		getTransform().setTransform(transform);
		getNetworkSynchronizer().setPositionDirty();
	}

	@Override
	public void finalizeRun() {
		if (Spout.getEngine().getPlatform()!=Platform.CLIENT && !this.isOnlineLive()) {
			remove();
		}
		super.finalizeRun();
		if (this.isOnline()) {
			this.getNetworkSynchronizer().finalizeTick();
		}
		if (isRemoved()) {
			getNetworkSynchronizer().onRemoved();
			((SpoutEngine) Spout.getEngine()).removePlayer(this);
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

}
