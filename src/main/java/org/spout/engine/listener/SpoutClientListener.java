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
package org.spout.engine.listener;

import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.player.ClientPlayerConnectedEvent;

import org.spout.engine.SpoutClient;
import org.spout.engine.protocol.SpoutSession;

/**
 * Listener for SpoutClient events
 */
public class SpoutClientListener implements Listener {
	private final SpoutClient client;

	public SpoutClientListener(SpoutClient client) {
		this.client = client;
	}

	@EventHandler
	public void onClientPlayerConnected(ClientPlayerConnectedEvent event) {
		client.getActivePlayer().setId(event.getServerPlayerId());
		client.getActivePlayer().connect((SpoutSession<?>) event.getSession(), null);
	}
}
