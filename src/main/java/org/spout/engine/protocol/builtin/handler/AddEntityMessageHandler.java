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
package org.spout.engine.protocol.builtin.handler;

import org.spout.api.Client;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.protocol.Session;
import org.spout.engine.protocol.builtin.message.AddEntityMessage;
import org.spout.engine.protocol.builtin.SpoutProtocol;

public class AddEntityMessageHandler extends MessageHandler<AddEntityMessage> {
	@Override
	public void handleClient(Session session, AddEntityMessage message) {
		if(!session.hasPlayer()) {
			return;
		}
		System.out.println("Ading entity with id " + message.getEntityId());

		Player player = session.getPlayer();
		Entity newEntity;
		if (message.getEntityId() == session.getDataMap().get(SpoutProtocol.PLAYER_ENTITY_ID)) {
			newEntity = player;
		} else {
			newEntity = session.getEngine().getDefaultWorld().createEntity(message.getTransform().getPosition(), null);
		}

		newEntity.getTransform().setTransform(message.getTransform());
		//newEntity.setId(message.getEntityId()); // TODO: Allow providing an entity ID to use
		((Client) session.getEngine()).getDefaultWorld().spawnEntity(newEntity);
	}
}
