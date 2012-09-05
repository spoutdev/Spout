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
import java.util.Collections;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.entity.Player;
import org.spout.api.math.MathHelper;
import org.spout.api.protocol.NetworkSynchronizer;

import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableHashMap;
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
		Collection<SpoutEntity> all = entities.get().values();
		if (all == null) {
			return Collections.emptyList();
		}
		return all;
	}
	
	/**
	 * Gets all the entities that are in a live state (not the snapshot).
	 * @return A collection of entities 
	 */
	public Collection<SpoutEntity> getAllLive() {
		Collection<SpoutEntity> all = entities.getLive().values();
		if (all == null) {
			return Collections.emptyList();
		}
		return all;
	}

	/**
	 * Gets all the players currently in the engine.
	 * @return The list of players.
	 */
	public List<Player> getPlayers() {
		Map<?, ?> map = players.get();
		if (map == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(new ArrayList<Player>(players.get().keySet()));
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
	 * Allocates the id for an entity.
	 *
	 * @param entity The entity.
	 * @return The id.
	 */
	public int allocate(SpoutEntity entity) {
		int currentId = entity.getId();
		if (currentId == SpoutEntity.NOTSPAWNEDID) {
			currentId = nextId.getAndIncrement();
			if (currentId == -2) {
				throw new IllegalStateException("No new entity ids left");
			}
			entity.setId(currentId);
		}
		entities.put(currentId, entity);
		return currentId;
	}

	/**
	 * Deallocates the id for an entity.
	 * @param entity The entity.
	 */
	public void deallocate(SpoutEntity entity) {
		entities.remove(entity.getId());

		//Players are never removed (offline concept), instead set their ID back to -1 to be reallocated.
		if (entity instanceof Player) {
			entity.setId(SpoutEntity.NOTSPAWNEDID);
		}
	}
	
	/**
	 * Adds an entity to the manager.
	 * @param entity The entity
	 */
	public void addEntity(SpoutEntity entity) {
		allocate(entity);
		if (entity instanceof Player) {
			players.putIfAbsent((Player) entity, new ArrayList<SpoutEntity>());
		}
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
		deallocate(entity);
		if (entity instanceof Player) {
			NetworkSynchronizer synchronizer = ((Player) entity).getNetworkSynchronizer();
			if (synchronizer != null) {
				synchronizer.onRemoved();
			}
			players.remove((Player) entity);
		}
	}

	/**
	 * Finalizes the manager at the FINALIZERUN tick stage
	 */
	public void finalizeRun() {
		for (SpoutEntity e : entities.get().values()) {
			e.finalizeRun();
			if (e instanceof Player) {
				Player p = (Player) e;
				if (p.isOnline()) {
					p.getNetworkSynchronizer().finalizeTick();
				}
			}			
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
			if (e instanceof Player) {
				Player p = (Player) e;
				if (p.isOnline()) {
					p.getNetworkSynchronizer().preSnapshot();
				}
			}
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
		Map<Player, ArrayList<SpoutEntity>> toSync = players.get();
		Collection<SpoutEntity> allEntities = getAll();
		for (Player player : toSync.keySet()) {
			/*
			 * Offline players have no network synchronizer, skip them
			 */
			if (!player.isOnline()) {
				continue;
			}
			Integer playerViewDistance = player.getViewDistance();
			NetworkSynchronizer net = player.getNetworkSynchronizer();
			ArrayList<SpoutEntity> entitiesPerPlayer = toSync.get(player);
			boolean spawn, destroy, update;
			for (SpoutEntity entity : allEntities) {
				if (entity.equals(player)) {
					continue;
				}
				boolean contains = entitiesPerPlayer.contains(entity);
				spawn = destroy = update = false;
				if (MathHelper.distance(player.getTransform().getPosition(), entity.getTransform().getPosition()) <= playerViewDistance) {
					if (!contains) {
						entitiesPerPlayer.add(entity);
						spawn = true; // Spawn
					} else if (entity.isRemoved()) {
						destroy = entitiesPerPlayer.remove(entity); // Destroy if not already destroyed
					} else {
						update = true; // Update otherwise
					}
				} else {
					destroy = entitiesPerPlayer.remove(entity); // Destroy if not already destroyed
				}
				net.syncEntity(entity, spawn, destroy, update);
			}
		}
	}
}
