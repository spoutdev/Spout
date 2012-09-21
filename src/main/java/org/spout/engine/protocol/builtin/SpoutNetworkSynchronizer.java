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
package org.spout.engine.protocol.builtin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.spout.api.entity.Entity;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.material.BlockMaterial;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.protocol.EntityProtocol;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.protocol.Session;
import org.spout.engine.protocol.builtin.message.BlockUpdateMessage;
import org.spout.engine.protocol.builtin.message.ChunkDataMessage;
import org.spout.engine.protocol.builtin.message.EntityPositionMessage;
import org.spout.engine.protocol.builtin.message.WorldChangeMessage;

public class SpoutNetworkSynchronizer extends NetworkSynchronizer {
	public SpoutNetworkSynchronizer(Session session) {
		super(session, 3);
	}

	@Override
	public Collection<Chunk> sendChunk(Chunk c) {
		session.send(false, new ChunkDataMessage(c.getSnapshot()));
		return null;
	}

	@Override
	protected void freeChunk(Point p) {
		session.send(false, new ChunkDataMessage(p.getBlockX(), p.getBlockY(), p.getBlockZ()));
	}

	@Override
	protected void sendPosition(Point p, Quaternion rot) {
		session.send(false, new EntityPositionMessage(player.getId(), new Transform(p, rot, Vector3.ONE)));
	}

	@Override
	protected void worldChanged(World world) {
		session.send(false, new WorldChangeMessage(world, world.getComponentHolder().getData()));
	}

	@Override
	public void updateBlock(Chunk chunk, int x, int y, int z, BlockMaterial material, short data) {
		session.send(false, new BlockUpdateMessage(chunk.getBlock(x, y, z, player)));
	}

	private EntityProtocol getEntityProtocol(Entity entity) {
		EntityProtocol protocol = entity.getNetwork().getEntityProtocol(SpoutProtocol.ENTITY_PROTOCOL_ID);
		if (protocol == null) {
			entity.getNetwork().setEntityProtocol(SpoutProtocol.ENTITY_PROTOCOL_ID, SpoutEntityProtocol.INSTANCE);
			protocol = SpoutEntityProtocol.INSTANCE;
		}
		return protocol;
	}

	@Override
	public void syncEntity(Entity e, boolean spawn, boolean destroy, boolean update) {
		EntityProtocol protocol = getEntityProtocol(e);
		List<Message> messages = new ArrayList<Message>(3);
		if (destroy) {
			messages.addAll(protocol.getDestroyMessages(e));
		}
		if (spawn) {
			messages.addAll(protocol.getSpawnMessages(e));
		}
		if (update) {
			messages.addAll(protocol.getUpdateMessages(e));
		}
		for (Message message : messages) {
			this.session.send(false, message);
		}
	}
}
