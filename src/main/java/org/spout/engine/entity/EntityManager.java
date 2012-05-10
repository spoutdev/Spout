/*
 * This file is part of Spout (http://www.spout.org/).
 *
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.entity.Controller;
import org.spout.api.entity.PlayerController;
import org.spout.api.player.Player;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.util.StringMap;

import org.spout.engine.player.SpoutPlayer;
import org.spout.engine.util.thread.snapshotable.SnapshotManager;
import org.spout.engine.util.thread.snapshotable.SnapshotableHashMap;
import org.spout.engine.util.thread.snapshotable.SnapshotableHashSet;
import org.spout.engine.world.SpoutRegion;

/**
 * A class which manages all of the entities within a world.
 */
public final class EntityManager implements Iterable<SpoutEntity> {
	/**
	 * The snapshot manager
	 */
	private final SnapshotManager snapshotManager = new SnapshotManager();
	/**
	 * A map of all the entity ids to the corresponding entities.
	 */
	private final SnapshotableHashMap<Integer, SpoutEntity> entities = new SnapshotableHashMap<Integer, SpoutEntity>(snapshotManager);
	/**
	 * A map of entity types to a set containing all entities of that type.
	 */
	private final ConcurrentHashMap<Class<? extends Controller>, SnapshotableHashSet<SpoutEntity>> groupedEntities = new ConcurrentHashMap<Class<? extends Controller>, SnapshotableHashSet<SpoutEntity>>();
	/**
	 * The next id to check.
	 */
	private final static AtomicInteger nextId = new AtomicInteger(1);
	private final StringMap entityMap = GenericDatatableMap.getStringMap();

	private SnapshotableHashSet<SpoutEntity> getRawAll(Class<? extends Controller> type) {
		SnapshotableHashSet<SpoutEntity> set = groupedEntities.get(type);
		if (set == null) {
			set = new SnapshotableHashSet<SpoutEntity>(snapshotManager);
			SnapshotableHashSet<SpoutEntity> currentSet = groupedEntities.putIfAbsent(type, set);
			if (currentSet != null) {
				set = currentSet;
			}
		}
		return set;
	}

	/**
	 * Gets all entities with the specified type from the live map.
	 * @param type The {@link Class} for the type.
	 * @return A set of entities with the specified type.
	 */
	public Set<SpoutEntity> getLiveAll(Class<? extends Controller> type) {
		return getRawAll(type).getLive();
	}

	/**
	 * Gets all entities with the specified type.
	 * @param type The {@link Class} for the type.
	 * @return A set of entities with the specified type.
	 */
	public Set<SpoutEntity> getAll(Class<? extends Controller> type) {
		return getRawAll(type).get();
	}

	/**
	 * Gets all entities.
	 * @return A set of entities.
	 */
	public Set<SpoutEntity> getAll() {
		Collection<SpoutEntity> collection = entities.get().values();
		HashSet<SpoutEntity> set = new HashSet<SpoutEntity>(collection.size());
		for (SpoutEntity entity : collection) {
			set.add(entity);
		}
		return set;
	}

	/**
	 * Gets an entity by its id.
	 * @param id The id.
	 * @return The entity, or {@code null} if it could not be found.
	 */
	public SpoutEntity getEntity(int id) {
		return entities.get().get(id);
	}

	/**
	 * Allocates the id for an entity.
	 * @param entity The entity.
	 * @return The id.
	 */
	public int allocate(SpoutEntity entity) {
		return allocate(entity, null);
	}
	
	/**
	 * Allocates the id for an entity.
	 * @param entity The entity.
	 * @param region to allocate the entity for
	 * @return The id.
	 */
	public int allocate(SpoutEntity entity, SpoutRegion region) {
		int currentId = entity.getId();
		SpoutRegion entityRegion = region == null ? ((SpoutRegion) entity.getRegionLive()) : region;
		if (currentId != SpoutEntity.NOTSPAWNEDID) {
			entities.put(currentId, entity);
			getRawAll(entity.getController().getClass()).add(entity);
			entity.setOwningThread(entityRegion.getExceutionThread());
			return currentId;
		} else {
			int id = nextId.getAndIncrement();
			if (id == -2) {
				throw new IllegalStateException("No new entity ids left");
			}
			entities.put(id, entity);
			entity.setId(id);
			Controller controller = entity.getController();
			if (controller != null) {
				getRawAll(controller.getClass()).add(entity);
			}
			
			entity.setOwningThread(entityRegion.getExceutionThread());

			return id;
		}
	}

	/**
	 * Deallocates the id for an entity.
	 * @param entity The entity.
	 */
	public void deallocate(SpoutEntity entity) {
		entities.remove(entity.getId());
		Controller controller = entity.getController();
		if (controller != null) {
			getRawAll(entity.getController().getClass()).remove(entity);
		}
	}

	@Override
	public Iterator<SpoutEntity> iterator() {
		return entities.get().values().iterator();
	}

	public void finalizeRun() {
		// Entity removal and additions happen here
		for (SpoutEntity e : entities.get().values()) {
			e.finalizeRun();
			Controller controller = e.getController();
			if (controller == null) {
				continue;
			}

			controller.finalizeTick();

			if (!(controller instanceof PlayerController)) {
				continue;
			}

			Player p = ((PlayerController) controller).getPlayer();
			NetworkSynchronizer n = ((SpoutPlayer) p).getNetworkSynchronizer();
			if (n != null) {
				n.finalizeTick();
			}
		}
	}

	public void preSnapshotRun() throws InterruptedException {
		for (SpoutEntity e : entities.get().values()) {
			Controller controller = e.getController();
			if (controller != null) {
				controller.preSnapshot();
				if (controller instanceof PlayerController) {
					Player p = ((PlayerController) controller).getPlayer();
					NetworkSynchronizer n = p.getNetworkSynchronizer();
					if (n != null) {
						n.preSnapshot();
					}
				}
			}
		}
	}

	/**
	 * Updates the snapshot for all entities
	 */
	public void copyAllSnapshots() {
		for (SpoutEntity e : entities.get().values()) {
			e.copyToSnapshot();
		}
		snapshotManager.copyAllSnapshots();
	}

	/**
	 * Gets the string map associated with this entity manager
	 * @return
	 */
	public StringMap getStringMap() {
		return entityMap;
	}
}
