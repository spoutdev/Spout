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
package org.spout.engine.world.collision;

import org.spout.api.Spout;
import org.spout.api.collision.SpoutContactInfo;
import org.spout.api.component.Component;
import org.spout.api.component.entity.EntityComponent;
import org.spout.api.entity.Entity;
import org.spout.api.event.entity.EntityCollideBlockEvent;
import org.spout.api.event.entity.EntityCollideEntityEvent;
import org.spout.api.event.entity.EntityCollideEvent;
import org.spout.api.geo.cuboid.Block;

import org.spout.physics.body.CollisionBody;
import org.spout.physics.collision.CollisionListener;
import org.spout.physics.collision.ContactInfo;

public final class SpoutCollisionListener implements CollisionListener {
	@Override
	public boolean onCollide(CollisionBody body1, CollisionBody body2, ContactInfo contactInfo) {
		final Object user1 = body1.getUserPointer();
		final Object user2 = body2.getUserPointer();
		final SpoutContactInfo info = new SpoutContactInfo(contactInfo);

		EntityCollideEvent event = null;

		if (user1 instanceof Entity) {
			if (user2 instanceof Entity) {
				event = new EntityCollideEntityEvent((Entity) user1, (Entity) user2, info);
			} else {
				event = new EntityCollideBlockEvent((Entity) user1, (Block) user2, info);
			}
		} else if (user1 instanceof Block) {
			if (user2 instanceof Entity) {
				event = new EntityCollideBlockEvent((Entity) user2, (Block) user1, info);
			}
		}

		if (event != null && Spout.getEventManager().callEvent(event).isCancelled()) {
			return true;
		}

		if (user1 instanceof Entity) {
			for (Component component : ((Entity) user1).values()) {
				if (component instanceof EntityComponent) {
					((EntityComponent) component).onCollided(event);
				}
			}
		}

		if (user2 instanceof Entity) {
			for (Component component : ((Entity) user2).values()) {
				if (component instanceof EntityComponent) {
					((EntityComponent) component).onCollided(event);
				}
			}
		}
		//TODO Support collision groups
		return false;
	}
}
