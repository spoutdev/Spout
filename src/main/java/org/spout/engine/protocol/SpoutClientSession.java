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
package org.spout.engine.protocol;

import java.util.concurrent.atomic.AtomicReference;

import org.jboss.netty.channel.Channel;
import org.spout.api.chat.ChatArguments;
import org.spout.api.geo.World;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.PortBinding;
import org.spout.api.protocol.Protocol;
import org.spout.engine.SpoutClient;
import org.spout.engine.player.SpoutPlayer;
import org.spout.engine.world.SpoutWorld;

/**
 * Handle client-specific session tasks
 */
public class SpoutClientSession extends SpoutSession<SpoutClient> {
	private final AtomicReference<SpoutWorld> activeWorld = new AtomicReference<SpoutWorld>();
	/**
	 * Creates a new session.
	 *
	 * @param engine The server this session belongs to.
	 * @param channel The channel associated with this session.
	 */
	public SpoutClientSession(SpoutClient engine, Channel channel, Protocol bootstrapProtocol) {
		super(engine, channel, bootstrapProtocol);
	}

	@Override
	public void send(boolean upstream, boolean force, Message message) {
		if (!upstream) {
			getEngine().getLogger().warning("Attempt made to send packet to client");
		}
		super.send(upstream, force, message);
	}

	public void dispose() {
		activeWorld.set(null);
		SpoutPlayer player;
		if ((player = this.player.getAndSet(null)) != null) {
			player.disconnect();
		}
		getEngine().disconnected();
		channel.getCloseFuture().awaitUninterruptibly();
	}

	public World getActiveWorld() {
		return activeWorld.get();
	}

	public boolean disconnect(Object... reason) {
		return disconnect(true, reason);
	}

	public boolean disconnect(boolean kick, Object... reason) {
		SpoutPlayer player = getPlayer();
		if (player != null) {
			player.sendCommand("disconnect", new ChatArguments(reason));
			return true;
		}
		return false;
	}

	public PortBinding getActiveAddress() {
		return new PortBindingImpl(getProtocol(), channel.getRemoteAddress());
	}
}
