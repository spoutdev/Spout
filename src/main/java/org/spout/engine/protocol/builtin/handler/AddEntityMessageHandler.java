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
package org.spout.engine.protocol.builtin.handler;

import org.spout.api.Client;
import org.spout.api.component.entity.CameraComponent;
import org.spout.api.entity.Entity;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.reposition.RepositionManager;

import org.spout.engine.SpoutConfiguration;
import org.spout.engine.entity.SpoutClientPlayer;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.protocol.builtin.SpoutProtocol;
import org.spout.engine.protocol.builtin.message.AddEntityMessage;

public class AddEntityMessageHandler extends MessageHandler<AddEntityMessage> {
	@Override
	public void handleClient(Session session, AddEntityMessage message) {
		RepositionManager rmInverse = session.getNetworkSynchronizer().getRepositionManager().getInverse();
		Entity entity;
		//Spawning a player
		if (message.getEntityId() == session.getDataMap().get(SpoutProtocol.PLAYER_ENTITY_ID)) {
			//The client has no client player
			if (((Client) session.getEngine()).getPlayer() == null) {
				//TODO How do we get the name of the player? Tie it to session?
				entity = new SpoutClientPlayer(session.getEngine(), "Spouty", message.getTransform(), SpoutConfiguration.VIEW_DISTANCE.getInt() * Chunk.BLOCKS.SIZE);
			} else {
				entity = session.getPlayer();
			}
		} else {
			entity = session.getEngine().getDefaultWorld().createEntity(rmInverse.convert(message.getTransform().getPosition()));
		}
		((SpoutEntity) entity).setId(message.getEntityId());
		entity.getScene().setTransform(rmInverse.convert(message.getTransform()));
		session.getEngine().getDefaultWorld().spawnEntity(entity);
	}
}
