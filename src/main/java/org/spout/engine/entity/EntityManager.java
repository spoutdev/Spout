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
package org.spout.engine.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.Spout;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.math.MathHelper;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableHashMap;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutRegion;

/**
 * A class which manages all of the entities within a world.
 */
public class EntityManager {
	/**
	 * The snapshot manager
	 */
	protected final SnapshotManager snapshotManager = new SnapshotManager();
	/**
	 * A map of all the entity ids to the corresponding entities.
	 */
	private final SnapshotableHashMap<Integer, SpoutEntity> entities = new SnapshotableHashMap<Integer, SpoutEntity>(snapshotManager);

	/**
	 * The next id to check.
	 */
	private final static AtomicInteger nextId = new AtomicInteger(1);
	
	/**
	 * The region with entities this manager manages.
	 */
	private final SpoutRegion region;
	/**
	 * Player listings plus listings of sync'd entities per player
	 */
	private final SnapshotableHashMap<Player, ArrayList<SpoutEntity>> players = new SnapshotableHashMap<Player, ArrayList<SpoutEntity>>(snapshotManager);

	public EntityManager(SpoutRegion region) {
		if (region == null) {
			throw new NullPointerException("Region can not be null!");
		}
		this.region = region;
	}

	/**
	 * Gets all entities.
	 *
	 * @return A collection of entities.
	 */
	public Collection<SpoutEntity> getAll() {
		return entities.get().values();
	}
	
	/**
	 * Gets all the entities that are in a live state (not the snapshot).
	 * @return A collection of entities 
	 */
	public Collection<SpoutEntity> getAllLive() {
		return entities.getLive().values();
	}

	/**
	 * Gets all the players currently in the engine.
	 * @return The list of players.
	 */
	public List<Player> getPlayers() {
		return new ArrayList<Player>(players.get().keySet());
	}
	
	/**
	 * Gets an entity by its id.
	 *
	 * @param id The id.
	 * @return The entity, or {@code null} if it could not be found.
	 */
	public SpoutEntity getEntity(int id) {
		return entities.get().get(id);
	}

	/**
	 * Adds an entity to the manager.
	 * @param entity The entity
	 */
	public void addEntity(SpoutEntity entity) {
		int currentId = entity.getId();
		if (currentId == SpoutEntity.NOTSPAWNEDID) {
			currentId = getNextId();
			entity.setId(currentId);
		}
		entities.put(currentId, entity);
		if (entity instanceof Player) {
			players.put((Player) entity, new ArrayList<SpoutEntity>());
		}
	}
	
	private static int getNextId() {
		int id = nextId.getAndIncrement();
		if (id == -2) {
			throw new IllegalStateException("Entity id space exhausted");
		}
		return id;
	}

	public boolean isSpawnable(SpoutEntity entity) {
		if (entity.getId() == SpoutEntity.NOTSPAWNEDID) {
			return true;
		}
		return false;
	}
	
	/**
	 * Removes an entity from the manager.
	 * @param entity The entity
	 */
	public void removeEntity(SpoutEntity entity) {
		entities.remove(entity.getId());
		if (entity instanceof Player) {
			players.remove((Player) entity);
		}
	}

	/**
	 * Finalizes the manager at the FINALIZERUN tick stage
	 */
	public void finalizeRun() {
		for (SpoutEntity e : entities.get().values()) {
			e.finalizeRun();
			if (e.isRemoved()) {
				removeEntity(e);
			}
		}
	}

	/**
	 * Prepares the manager for a snapshot in the PRESNAPSHOT tickstage
	 */
	public void preSnapshotRun() {
		for (SpoutEntity e : entities.get().values()) {
			e.preSnapshotRun();
		}
	}
	
	/**
	 * Snapshots the manager and all the entities managed in the SNAPSHOT tickstage.
	 */
	public void copyAllSnapshots() {
		for (SpoutEntity e : entities.get().values()) {
			e.copySnapshot();
		}
		snapshotManager.copyAllSnapshots();
	}

	/**
	 * The region this entity manager oversees
	 *
	 * @return region
	 */
	public SpoutRegion getRegion() {
		return region;
	}

	/**
	 * Syncs all entities/observers in this region
	 */
	public void syncEntities() {
		for (Entity ent : getAll()) {
			//Do not sync entities with null chunks
			if (ent.getChunk() == null) {
				continue;
			}
			if (ent.getId() == SpoutEntity.NOTSPAWNEDID) {
				Spout.getLogger().info("Attempt to sync entity with the not spawned id");
				continue;
			}
			//Players observing the chunk this entity is in
			Set<? extends Entity> observers = ent.getChunk().getObservers();
			syncEntity(ent, observers, false);

			Set<? extends Entity> expiredObservers = ((SpoutChunk) ent.getChunk()).getExpiredObservers();
			syncEntity(ent, expiredObservers, true);
		}
	}
	
	private void syncEntity(Entity ent, Set<? extends Entity> observers, boolean forceDestroy) {
		for (Entity observer : observers) {
			//Don't sync ourselves to ourselves :p
			if (ent == observer) {
				continue;
			}
			//Non-players have no synchronizer, ignore
			if (!(observer instanceof Player)) {
				continue;
			}
			Player player = (Player) observer;
			//If the player is somehow still observing the chunk and offline, ignore
			if (!player.isOnline()) {
				continue;
			}
			//Grab the NetworkSynchronizer of the player
			NetworkSynchronizer network = player.getNetworkSynchronizer();
			//Grab player's view distance
			int view = player.getViewDistance();
			/*
			 * Just because a player can see a chunk doesn't mean the entity is within sync-range, do the math and sync based on the result.
			 *
			 * Following variables hold sync status
			 */
			boolean spawn, sync, destroy;
			spawn = sync = destroy = false;
			//Entity is out of range of the player's view distance, destroy
			if (forceDestroy || ent.isRemoved() || MathHelper.distance(ent.getTransform().getPosition(), player.getTransform().getPosition()) > view) {
				destroy = true;
			} else {
				if (!network.hasSpawned(ent)) {
					spawn = true;
				} else {
					sync = true;
				}
			}
			network.syncEntity(ent, spawn, destroy, sync);
		}
	}
}
