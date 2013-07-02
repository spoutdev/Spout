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
package org.spout.engine.protocol.builtin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spout.api.entity.Entity;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.protocol.EntityProtocol;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.reposition.RepositionManager;
import org.spout.engine.protocol.builtin.message.AddEntityMessage;
import org.spout.engine.protocol.builtin.message.EntityTransformMessage;
import org.spout.engine.protocol.builtin.message.RemoveEntityMessage;

/**
 * EntityProtocol for the SpoutClient protocol
 */
public class SpoutEntityProtocol implements EntityProtocol {
	public static final SpoutEntityProtocol INSTANCE = new SpoutEntityProtocol();
	protected SpoutEntityProtocol() {
		super();
	}

	@Override
	public List<Message> getSpawnMessages(Entity entity, RepositionManager rm) {
		return Arrays.<Message>asList(new AddEntityMessage(entity.getId(), entity.getWorld().getUID(), entity.getScene().getTransform(), rm));
	}

	@Override
	public List<Message> getDestroyMessages(Entity entity) {
		return Arrays.<Message>asList(new RemoveEntityMessage(entity.getId()));
	}

	@Override
	public List<Message> getUpdateMessages(Entity entity, Transform liveTransform, RepositionManager rm, boolean force) {
		List<Message> messages = new ArrayList<Message>(2);
		if (force || entity.getScene().isTransformDirty()) {
			messages.add(new EntityTransformMessage(entity.getId(), liveTransform, rm));
		}
		//TODO Need to send datatable here if dirty...I believe
		return messages;
	}
}
