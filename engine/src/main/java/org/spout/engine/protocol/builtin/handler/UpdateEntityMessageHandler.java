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
package org.spout.engine.protocol.builtin.handler;

import org.spout.api.entity.Entity;
import org.spout.api.protocol.ClientSession;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.protocol.ServerSession;
import org.spout.api.protocol.reposition.RepositionManager;
import org.spout.engine.component.entity.MovementValidatorComponent;
import org.spout.engine.protocol.builtin.message.UpdateEntityMessage;
import org.spout.engine.world.SpoutWorld;

import static org.spout.api.protocol.event.UpdateEntityEvent.UpdateAction.ADD;
import static org.spout.api.protocol.event.UpdateEntityEvent.UpdateAction.TRANSFORM;

public class UpdateEntityMessageHandler extends MessageHandler<UpdateEntityMessage> {
	@Override
	public void handleClient(ClientSession session, UpdateEntityMessage message) {
		RepositionManager rmInverse = session.getPlayer().getNetwork().getRepositionManager().getInverse();

		// Add is a special case because the player is already spawned
		if (message.getAction() == ADD) {
			Entity entity = session.getEngine().getDefaultWorld().createEntity(rmInverse.convert(message.getTransform().getPosition()));
			entity.getPhysics().setTransform(rmInverse.convert(message.getTransform()));
			((SpoutWorld) session.getEngine().getDefaultWorld()).spawnEntity(entity, message.getEntityId());
		} else {
			Entity entity;
			if (message.getEntityId() == session.getPlayer().getId()) {
				entity = session.getPlayer();
			} else {
				entity = session.getEngine().getDefaultWorld().getEntity(message.getEntityId());
				if (entity == null) {
					// We only want to null-check non-player entities. The player should never be null.
					return;
				}
			}

			switch (message.getAction()) {
				case TRANSFORM:
					entity.getPhysics().setTransform(rmInverse.convert(message.getTransform()));
					break;
				case POSITION:
					throw new UnsupportedOperationException("UpdateAction.POSITION not implemented yet.");
				case REMOVE:
					entity.remove();
			}
		}
	}

	@Override
	public void handleServer(ServerSession session, UpdateEntityMessage message) {
		RepositionManager rmInverse = session.getNetworkSynchronizer().getRepositionManager().getInverse();

		if (message.getAction() == TRANSFORM) {
			if (message.getEntityId() == session.getPlayer().getId()) {
				session.getDataMap().put(MovementValidatorComponent.RECEIVED_TRANSFORM, rmInverse.convert(message.getTransform()));
				return;
			} else {
				// TODO: protocol - please fix this sequence
				//throw new IllegalStateException("Server can not receive non-player transforms.");
				return;
			}
		}
		throw new UnsupportedOperationException("Not allowed to perform the following UpdateAction on the server: " + message.getAction());
	}
}
