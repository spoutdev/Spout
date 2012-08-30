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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.component.components.BlockComponent;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;
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
	 * The map of entities to Vector3s(BlockControllers)
	 */
	private final Map<Vector3, Entity> blockEntities = new HashMap<Vector3, Entity>();

	private final SpoutRegion region;
	/**
	 * Player listings plus listings of sync'd entities per player
	 */
	private final SnapshotableHashMap<Player, List<SpoutEntity>> players = new SnapshotableHashMap<Player, List<SpoutEntity>>(snapshotManager);

	public EntityManager(SpoutRegion region) {
		if (region == null) {
			throw new NullPointerException("Region can not be null!");
		}
		this.region = region;
	}

	/**
	 * Gets all entities.
	 *
	 * @return A set of entities.
	 */
	public Collection<SpoutEntity> getAll() {
		Collection<SpoutEntity> all = entities.get().values();
		if (all == null) {
			return Collections.emptyList();
		}
		return all;
	}

	public Collection<SpoutEntity> getAllLive() {
		Collection<SpoutEntity> all = entities.getLive().values();
		if (all == null) {
			return Collections.emptyList();
		}
		return all;
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
	 *
	 * @param entity The entity.
	 */
	public void deallocate(SpoutEntity entity) {
		entities.remove(entity.getId());

		//Players are never removed (offline concept), instead set their ID back to -1 to be reallocated.
		if (entity instanceof Player) {
			entity.setId(SpoutEntity.NOTSPAWNEDID);
		}
	}

	public void addEntity(SpoutEntity entity) {
		allocate(entity);
		
		if (entity.hasComponent(BlockComponent.class)) {
			Vector3 pos = entity.getTransform().getPosition().floor();
			Entity old = blockEntities.put(pos, entity);
			if (old != null) {
				old.remove();
			}
		}

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

	public void removeEntity(SpoutEntity entity) {
		deallocate(entity);

		if (entity.hasComponent(BlockComponent.class)) {
			Vector3 pos = entity.getTransform().getPosition().floor();
			Entity be = blockEntities.get(pos);
			if (be == entity) {
				blockEntities.remove(pos);
			}
		}

		if (entity instanceof Player) {
			players.remove((Player) entity);
		}
	}

	public void finalizeRun() {
		for (SpoutEntity e : entities.get().values()) {
			if (e.isRemoved()) {
				removeEntity(e);
				continue;
			}
			e.finalizeRun();

			if (e instanceof Player) {
				Player p = (Player) e;
				if (p.isOnline()) {
					p.getNetworkSynchronizer().finalizeTick();
				}
			}
		}
	}

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
	 * Updates the snapshot for all entities
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

	public Map<Vector3, Entity> getBlockEntities() {
		return Collections.unmodifiableMap(blockEntities);
	}


	public List<Player> getPlayers() {
		Map<?, ?> map = players.get();
		if (map == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(new ArrayList<Player>(players.get().keySet()));
	}

	/**
	 * Syncs all entities/observers in this region
	 */
	public void syncEntities() {
		Map<Player, List<SpoutEntity>> toSync = players.get();
		for (Player player : toSync.keySet()) {
			/*
			 * Offline players have no network synchronizer, skip them
			 */
			if (!player.isOnline()) {
				continue;
			}
			Integer playerViewDistance = player.getViewDistance();
			NetworkSynchronizer net = player.getNetworkSynchronizer();
			List<SpoutEntity> entitiesPerPlayer = toSync.get(player);
			if (entitiesPerPlayer == null) {
				entitiesPerPlayer = new ArrayList<SpoutEntity>();
			}
			boolean spawn, destroy, update;
			for (SpoutEntity entity : getAll()) {
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
			players.put(player, entitiesPerPlayer);
		}
	}
}
