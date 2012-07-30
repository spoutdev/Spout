/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.protocol.builtin;

import org.spout.api.entity.Entity;
import org.spout.api.entity.component.controller.type.ControllerType;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.material.BlockMaterial;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.protocol.EntityProtocol;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.protocol.Session;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.builtin.message.BlockUpdateMessage;
import org.spout.api.protocol.builtin.message.ChunkDataMessage;
import org.spout.api.protocol.builtin.message.EntityPositionMessage;
import org.spout.api.protocol.builtin.message.WorldChangeMessage;

/**
 * @author zml2008
 */
public class SpoutNetworkSynchronizer extends NetworkSynchronizer {
	public SpoutNetworkSynchronizer(Session session) {
		super(session.getPlayer(), session, session.getPlayer().getEntity(), 3);
	}

	public void sendChunk(Chunk c) {
		session.send(false, new ChunkDataMessage(c.getSnapshot()));
	}


	protected void freeChunk(Point p) {
		session.send(false, new ChunkDataMessage(p.getBlockX(), p.getBlockY(), p.getBlockZ()));
	}

	protected void sendPosition(Point p, Quaternion rot) {
		session.send(false, new EntityPositionMessage(entity.getId(), new Transform(p, rot, Vector3.ONE)));
	}

	protected void worldChanged(World world) {
		//session.send(false, new WorldChangeMessage(world, world.getDataMap()));
	}

	public void updateBlock(Chunk chunk, int x, int y, int z, BlockMaterial material, short data) {
		session.send(false, new BlockUpdateMessage(chunk.getBlock(x, y, z, getOwner())));
	}

	private EntityProtocol getEntityProtocol(Entity entity) {
		ControllerType type = entity.getController().getType();
		EntityProtocol protocol = type.getEntityProtocol(SpoutProtocol.ENTITY_PROTOCOL_ID);
		if (protocol == null) {
			type.setEntityProtocol(SpoutProtocol.ENTITY_PROTOCOL_ID, SpoutEntityProtocol.INSTANCE);
			protocol = SpoutEntityProtocol.INSTANCE;
		}
		return protocol;
	}

	public void spawnEntity(Entity e) {
		EntityProtocol protocol = getEntityProtocol(e);
		for (Message m : protocol.getSpawnMessage(e)) {
			session.send(false, m);
		}
	}

	public void destroyEntity(Entity e) {
		EntityProtocol protocol = getEntityProtocol(e);
		for (Message m : protocol.getDestroyMessage(e)) {
			session.send(false, m);
		}
	}

	public void syncEntity(Entity e) {
		EntityProtocol protocol = getEntityProtocol(e);
		for (Message m : protocol.getUpdateMessage(e)) {
			session.send(false, m);
		}
	}
}
