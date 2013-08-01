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
package org.spout.engine.component.entity;

import java.util.ArrayList;
import java.util.List;
import org.spout.api.Spout;
import org.spout.api.component.entity.PlayerNetworkComponent;
import org.spout.api.entity.Entity;
import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Vector3;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.event.ChunkDatatableSendEvent;
import org.spout.api.protocol.event.ChunkFreeEvent;
import org.spout.api.protocol.event.ChunkSendEvent;
import org.spout.api.protocol.event.PositionSendEvent;
import org.spout.api.protocol.event.UpdateBlockEvent;
import org.spout.api.protocol.event.UpdateEntityEvent;
import org.spout.api.protocol.event.WorldChangeProtocolEvent;
import org.spout.engine.protocol.builtin.message.BlockUpdateMessage;
import org.spout.engine.protocol.builtin.message.ChunkDataMessage;
import org.spout.engine.protocol.builtin.message.ChunkDatatableMessage;
import org.spout.engine.protocol.builtin.message.EntityDatatableMessage;
import org.spout.engine.protocol.builtin.message.UpdateEntityMessage;
import org.spout.engine.protocol.builtin.message.WorldChangeMessage;
import org.spout.engine.world.SpoutChunk;

public abstract class SpoutPlayerNetworkComponent extends PlayerNetworkComponent implements Listener {

	@Override
	public void onAttached() {
		super.onAttached();
		Spout.getEventManager().registerEvents(this, Spout.getEngine());
	}

	@EventHandler
	public void onChunkSend(ChunkSendEvent event) {
		event.getMessages().add(new ChunkDataMessage(event.getChunk().getSnapshot(ChunkSnapshot.SnapshotType.BOTH, ChunkSnapshot.EntityType.NO_ENTITIES, ChunkSnapshot.ExtraData.BIOME_DATA)));
	}

	@EventHandler
	public void onChunkFree(ChunkFreeEvent event) {
		event.getMessages().add(new ChunkDataMessage(event.getPoint().getChunkX(), event.getPoint().getChunkY(), event.getPoint().getChunkZ()));
	}

	@EventHandler
	public void onPositionSend(PositionSendEvent event) {
		event.getMessages().add(new UpdateEntityMessage(event.getPlayerId(), new Transform(event.getPoint(), event.getRotation(), Vector3.ONE), UpdateEntityEvent.UpdateAction.TRANSFORM, getRepositionManager()));
	}

	@EventHandler
	public void onWorldChange(WorldChangeProtocolEvent event) {
		event.getMessages().add(new WorldChangeMessage(event.getWorld(), getOwner().getPhysics().getTransform(), event.getWorld().getData()));
	}
	
	@EventHandler
	public void onBlockUpdate(UpdateBlockEvent event) {
		event.getMessages().add(new BlockUpdateMessage(event.getChunk().getBlock(event.getX(), event.getY(), event.getZ())));
	}

	@EventHandler
	public void onChunkDatatableSend(ChunkDatatableSendEvent event) {
		event.getMessages().add(new ChunkDatatableMessage(((SpoutChunk) event.getChunk())));
	}

	@EventHandler
	public void onUpdateEntity(UpdateEntityEvent event) {
		event.getMessages().add(new UpdateEntityMessage(event.getEntityId(), event.getTransform(), event.getAction(), event.getRepositionManager()));
	}

	@Override
	// TODO move to ServerNetworkSynchronizer?
	public void syncEntity(Entity e, Transform liveTransform, boolean spawn, boolean destroy, boolean update) {
		super.syncEntity(e, liveTransform, spawn, destroy, update);
		List<Message> messages = new ArrayList<>(3);
		if (destroy) {
			messages.add(new UpdateEntityMessage(e.getId(), null, UpdateEntityEvent.UpdateAction.REMOVE, null));
		}
		if (spawn) {
			messages.add(new UpdateEntityMessage(e.getId(), e.getPhysics().getTransform(), UpdateEntityEvent.UpdateAction.ADD, getRepositionManager()));
		}
		if (update) {
			// TODO - might be worth adding force support
			boolean force = false;
			if (force || e.getPhysics().isTransformDirty()) {
				messages.add(new UpdateEntityMessage(e.getId(), liveTransform, UpdateEntityEvent.UpdateAction.TRANSFORM, getRepositionManager()));
			}
			if (!e.getData().getDeltaMap().isEmpty()) {
				messages.add(new EntityDatatableMessage(e.getId(), e.getData().getDeltaMap()));
				e.getData().resetDelta();
			}
		}
		for (Message message : messages) {
			getSession().send(message);
		}
	}
}
