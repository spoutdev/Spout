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
package org.spout.engine.listener;

import org.spout.api.entity.Entity;
import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.entity.EntityDespawnEvent;
import org.spout.api.event.entity.EntitySpawnEvent;
import org.spout.api.event.player.ClientPlayerConnectedEvent;

import org.spout.engine.SpoutClient;
import org.spout.engine.entity.component.EntityRendererComponent;

public class SpoutClientListener implements Listener {
	private final SpoutClient client;

	public SpoutClientListener(SpoutClient client) {
		this.client = client;
	}

	//TODO Switch these to use sync events

	@EventHandler(order = Order.MONITOR)
	public void onEntitySpawn(EntitySpawnEvent event) {
		final Entity entity = event.getEntity();
		if (client.getPlayer().equals(entity)) {
			return;
		}
		final EntityRendererComponent renderer = entity.get(EntityRendererComponent.class);
		if (renderer != null) {
			client.getRenderer().getEntityRenderer().add(renderer);
		}
	}

	@EventHandler(order = Order.MONITOR)
	public void onEntityDespawn(EntityDespawnEvent event){
		final Entity entity = event.getEntity();
		if (client.getPlayer().equals(entity)) {
			return;
		}
		final EntityRendererComponent renderer = entity.get(EntityRendererComponent.class);
		if (renderer != null) {
			client.getRenderer().getEntityRenderer().remove(renderer);
		}
	}

	@EventHandler
	public void onClientPlayerConnected(ClientPlayerConnectedEvent event) {
		client.getPlayer().setId(event.getServerPlayerId());
	}
}
