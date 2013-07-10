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

import java.io.IOException;
import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.entity.Entity;
import org.spout.api.protocol.MessageHandler;
import org.spout.api.protocol.ClientSession;
import org.spout.engine.protocol.builtin.message.EntityDatatableMessage;

public class EntityDatatableMessageHandler extends MessageHandler<EntityDatatableMessage> {
	@Override
	public void handleClient(ClientSession session, EntityDatatableMessage message) {
		if(!session.hasPlayer()) {
			throw new IllegalStateException("Message sent when session has no player");
		}
		Entity entity;
		if (message.getEntityId() == session.getPlayer().getId()) {
			entity = session.getPlayer();
		} else {
			entity = session.getEngine().getDefaultWorld().getEntity(message.getEntityId());
		}
		// TODO: why doesn't this work!?
		//Entity entity = session.getPlayer().getWorld().getEntity(message.getEntityId());
		try {
			System.out.println("Received datatable message for " + entity.toString());
			entity.getData().deserialize(message.getCompressedData(), true);
		} catch (IOException e) {
			Spout.getLogger().log(Level.SEVERE, "Exception deserializing compressed datatable", e);
		}
	}
}
