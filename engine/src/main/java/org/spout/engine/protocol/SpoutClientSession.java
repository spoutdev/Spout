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
package org.spout.engine.protocol;

import java.util.concurrent.atomic.AtomicReference;

import io.netty.channel.Channel;

import org.spout.api.geo.World;
import org.spout.api.protocol.ClientSession;
import org.spout.api.protocol.PortBinding;
import org.spout.api.protocol.Protocol;
import org.spout.engine.SpoutClient;
import org.spout.engine.entity.SpoutClientPlayer;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.world.SpoutWorld;

/**
 * Handle client-specific session tasks
 */
public class SpoutClientSession extends SpoutSession<SpoutClient> implements ClientSession {
	private final AtomicReference<SpoutWorld> activeWorld = new AtomicReference<>();

	/**
	 * Creates a new session.
	 *
	 * @param engine The client this session belongs to.
	 * @param channel The channel associated with this session.
	 */
	public SpoutClientSession(SpoutClient engine, Channel channel, Protocol bootstrapProtocol) {
		super(engine, channel, bootstrapProtocol);
	}

	@Override
	public boolean disconnect(String reason) {
		if (isDisconnected()) {
			throw new IllegalStateException("Tried to disconnect a session that has already been disconnected!");
		}
		isDisconnected = true;
		activeWorld.set(null);
		SpoutPlayer player = getPlayer();
		if (player == null) {
			throw new IllegalStateException("Tried to disconnect a session with a null player!");
		}
		player.disconnect(false);
		getEngine().stop(reason);
		return true;
	}

	public PortBinding getActiveAddress() {
		return new PortBindingImpl(getProtocol(), getChannel().remoteAddress());
	}

	@Override
	public SpoutClientPlayer getPlayer() {
		return (SpoutClientPlayer) super.getPlayer();
	}

	@Override
	public void setPlayer(SpoutPlayer player) {
		if (!(player instanceof SpoutClientPlayer)) {
			// Force player to be a SpoutClientPlayer
			throw new IllegalArgumentException("Player in SpoutClientSession MUST be a SpoutClientPlayer");
		}
		super.setPlayer(player);
	}

	@Override
	public void dispose() {
		getEngine().stop("Client has been disconnected from the Server");
	}
}
