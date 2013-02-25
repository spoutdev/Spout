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

import org.spout.api.component.Component;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.reposition.RepositionManager;
import org.spout.engine.protocol.builtin.SpoutProtocol;
import org.spout.engine.protocol.builtin.message.AddEntityMessage;

public class AddEntityMessageHandler extends MessageHandler<AddEntityMessage> {
	@SuppressWarnings("unchecked")
	@Override
	public void handleClient(Session session, AddEntityMessage message) {
		if(!session.hasPlayer()) {
			return;
		}
		System.out.println("Ading entity with id " + message.getEntityId());

		Player player = session.getPlayer();
		RepositionManager rmInverse = player.getNetworkSynchronizer().getRepositionManager().getInverse();
		Entity newEntity;
		if (message.getEntityId() == session.getDataMap().get(SpoutProtocol.PLAYER_ENTITY_ID)) {
			newEntity = player;
		} else {
			newEntity = session.getEngine().getDefaultWorld().createEntity(rmInverse.convert(message.getTransform().getPosition()), (Class<? extends Component>)null);
		}

		newEntity.getScene().setTransform(rmInverse.convert(message.getTransform()));
		//newEntity.setId(message.getEntityId()); // TODO: Allow providing an entity ID to use
		session.getEngine().getDefaultWorld().spawnEntity(newEntity);
	}
}
