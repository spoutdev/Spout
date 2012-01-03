package org.getspout.server.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.getspout.api.entity.Controller;
import org.getspout.server.util.thread.snapshotable.SnapshotManager;
import org.getspout.server.util.thread.snapshotable.SnapshotableConcurrentHashMap;
import org.getspout.server.util.thread.snapshotable.SnapshotableConcurrentHashSet;

/**
 * A class which manages all of the entities within a world.
 *
 * @author Graham Edgecombe
 */
public final class EntityManager implements Iterable<SpoutEntity> {
	
	/**
	 * The snapshot manager
	 */
	private final SnapshotManager snapshotManager = new SnapshotManager();
	
	/**
	 * A map of all the entity ids to the corresponding entities.
	 */
	private final SnapshotableConcurrentHashMap<Integer, SpoutEntity> entities = new SnapshotableConcurrentHashMap<Integer, SpoutEntity>(snapshotManager, null);

	/**
	 * A map of entity types to a set containing all entities of that type.
	 */
	private final ConcurrentHashMap<Class<? extends Controller>, SnapshotableConcurrentHashSet<SpoutEntity>> groupedEntities = new ConcurrentHashMap<Class<? extends Controller>, SnapshotableConcurrentHashSet<SpoutEntity>>();

	/**
	 * The next id to check.
	 */
	private int nextId = 1;
	
	private SnapshotableConcurrentHashSet<SpoutEntity> getRawAll(Class<? extends Controller> type) {
		SnapshotableConcurrentHashSet<SpoutEntity> set = groupedEntities.get(type);
		if (set == null) {
			set = new SnapshotableConcurrentHashSet<SpoutEntity>(snapshotManager);
			SnapshotableConcurrentHashSet<SpoutEntity> currentSet = groupedEntities.putIfAbsent(type, set);
			if (currentSet != null) {
				set = currentSet;
			}
		}
		return set;
	}
	
	/**
	 * Gets all entities with the specified type from the live map.
	 *
	 * @param type The {@link Class} for the type.
	 * @param <T> The type of entity.
	 * @return A collection of entities with the specified type.
	 */
	public Collection<SpoutEntity> getLiveAll(Class<? extends Controller> type) {
		return getRawAll(type).getLive();
	}

	/**
	 * Gets all entities with the specified type.
	 *
	 * @param type The {@link Class} for the type.
	 * @param <T> The type of entity.
	 * @return A collection of entities with the specified type.
	 */
	public Collection<SpoutEntity> getAll(Class<? extends Controller> type) {
		return getRawAll(type).get();
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
	 * @param currentId the entity's current id, or null
	 * @return The id.
	 */
	// TODO - should be a global server based id register, not regional
	public int allocate(SpoutEntity entity) {
		int currentId = entity.getId();
		if (currentId != SpoutEntity.NOTSPAWNEDID) {
			entities.put(currentId, entity);
			getRawAll(entity.getController().getClass()).add(entity);
			return currentId;
		} else {
			for (int id = nextId; id < Integer.MAX_VALUE; id++) {
				if (!entities.getLive().containsKey(id)) {
					entities.put(id, entity);
					entity.setId(id);
					Controller controller = entity.getController();
					if (controller != null) {
						getRawAll(controller.getClass()).add(entity);
					}
					nextId = id + 1;
					return id;
				}
			}

			for (int id = Integer.MIN_VALUE; id < -1; id++) { // as -1 is used as a special value
				if (!entities.getLive().containsKey(id)) {
					entities.put(id, entity);
					entity.setId(id);
					Controller controller = entity.getController();
					if (controller != null) {
						getRawAll(controller.getClass()).add(entity);
					}
					nextId = id + 1;
					return id;
				}
			}
		}

		throw new IllegalStateException("No free entity ids");
	}

	/**
	 * Deallocates the id for an entity.
	 *
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
	
	public void preSnapshot() {
		// Entity removal and additions happen here
		for (SpoutEntity e : entities.get().values()) {
			e.preSnapshot();
		}		
	}
	
	/**
	 * Updates the snapshot for all entities
	 */
	public void copyAllSnapshots() {
		for (SpoutEntity e : entities.get().values()) {
			Controller controller = e.getController();
			if (controller != null) {
				controller.snapshotStart();
			}
		}
		for (SpoutEntity e : entities.get().values()) {
			e.copyToSnapshot();
		}
		snapshotManager.copyAllSnapshots();
	}
}
