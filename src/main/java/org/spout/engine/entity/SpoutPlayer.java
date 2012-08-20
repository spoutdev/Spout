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
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.ChatSection;
import org.spout.api.command.Command;
import org.spout.api.command.RootCommand;
import org.spout.api.data.ValueHolder;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Player;
import org.spout.api.entity.controller.PlayerController;
import org.spout.api.entity.state.PlayerInputState;
import org.spout.api.event.Result;
import org.spout.api.event.server.data.RetrieveDataEvent;
import org.spout.api.event.server.permissions.PermissionGetGroupsEvent;
import org.spout.api.event.server.permissions.PermissionGroupEvent;
import org.spout.api.event.server.permissions.PermissionNodeEvent;
import org.spout.api.exception.InvalidControllerException;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.lang.Locale;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.protocol.Protocol;
import org.spout.api.protocol.SendMode;
import org.spout.api.util.thread.DelayedWrite;
import org.spout.api.util.thread.SnapshotRead;
import org.spout.api.util.thread.Threadsafe;

import org.spout.engine.SpoutConfiguration;
import org.spout.engine.SpoutEngine;
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
	private PriorityQueue<PlayerInputState> inputQueue = new PriorityQueue<PlayerInputState>();
	private Locale preferredLocale = Locale.getByCode(SpoutConfiguration.DEFAULT_LANGUAGE.getString());

	public SpoutPlayer(String name, SpoutEngine engine) {
		this(name, null, null, engine, SpoutConfiguration.VIEW_DISTANCE.getInt() * Chunk.BLOCKS.SIZE);
	}

	public SpoutPlayer(String name, Transform transform, SpoutSession<?> session, SpoutEngine engine, int viewDistance) {
		super(engine, transform, null, viewDistance);
		this.name = name;
		displayName.set(name);
		hashcode = name.hashCode();
		connect(session, transform);
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
	public boolean disconnect() {
		if (!onlineLive.compareAndSet(true, false)) {
			// player was already offline
			return false;
		}
		sessionLive.set(null);
		return true;
	}

	@Override
	public boolean kill()  {
		boolean success = super.kill();
		if (success) {
			((SpoutWorld) getWorld()).removePlayer(this);
		}
		return success;
	}

	@DelayedWrite
	public boolean connect(SpoutSession<?> session, Transform newPosition) {
		if (!onlineLive.compareAndSet(false, true)) {
			// player was already online
			return false;
		}

		if (newPosition != null) {
			setTransform(newPosition);
		}
		final Transform transform = getTransform();
		if (newPosition != null && transform != null && !this.isSpawned()) {
			setupInitialChunk(transform);
		}

		sessionLive.set(session);
		copySnapshot();
		justSpawned = true;
		return true;
	}

	@Override
	public boolean sendMessage(Object... message) {
		return sendRawMessage(message);
	}

	public void sendCommand(String commandName, ChatArguments arguments) {
		Command command = Spout.getEngine().getRootCommand().getChild(commandName);
		Message cmdMessage = getSession().getProtocol().getCommandMessage(command, arguments);
		if (cmdMessage == null) {
			return;
		}

		session.send(false, cmdMessage);
	}

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

	public boolean sendMessage(ChatArguments message) {
		return sendRawMessage(message);
	}

	@Override
	public boolean sendRawMessage(Object... message) {
		return sendRawMessage(new ChatArguments(message));
	}

	public boolean sendRawMessage(ChatArguments message) {
		sendCommand("say", message);
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
		PermissionNodeEvent event = Spout.getEngine().getEventManager().callEvent(new PermissionNodeEvent(world, this, node));
		if (event.getResult() == Result.DEFAULT) {
			return false;
		}

		return event.getResult().getResult();
	}

	@Override
	public boolean isInGroup(String group) {
		PermissionGroupEvent event = Spout.getEngine().getEventManager().callEvent(new PermissionGroupEvent(getWorld(), this, group));
		return event.getResult();
	}

	@Override
	public String[] getGroups() {
		PermissionGetGroupsEvent event = Spout.getEngine().getEventManager().callEvent(new PermissionGetGroupsEvent(getWorld(), this));
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
	public void kick(Object... reason) {
		if (reason == null) {
			throw new IllegalArgumentException("reason cannot be null");
		}
		session.disconnect(reason);
	}

	@Override
	public NetworkSynchronizer getNetworkSynchronizer() {
		SpoutSession<?> session = this.session;
		return session == null ? null : session.getNetworkSynchronizer();
	}

	@Override
	public PlayerInputState input() {
		return inputQueue.poll();
	}

	@Override
	public void processInput(PlayerInputState state) {
		inputQueue.add(state);

	}

	@Override
	public void setController(Controller controller, Source source) {
		if (controller == null) {
			return;
		}
		if (!(controller instanceof PlayerController)) {
			throw new InvalidControllerException(controller.getType() + " is not a valid controller for a Player entity!");
		}
		super.setController(controller,  source);
	}

	public Locale getPreferredLocale() {
		return preferredLocale;
	}

	@Override
	protected void removeObserver() {
		getNetworkSynchronizer().onDeath();
	}
	
	@Override
	protected void updateObserver() {
		return;
	}

	@Override
	public void sendMessage(SendMode sendMode, Protocol protocol, Message... messages) {
		if (sendMode.canSendToSelf() && this.getSession().getProtocol().equals(protocol)) {
			getSession().sendAll(false, messages);
		}
	}
}
