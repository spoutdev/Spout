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

import java.util.List;
import java.util.Map;

import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.entity.controller.PlayerController;
import org.spout.api.protocol.NetworkSynchronizer;

import org.spout.engine.util.thread.snapshotable.SnapshotableArrayList;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutRegion;

public final class RegionEntityManager extends EntityManager {
	private final SpoutRegion region;
	public RegionEntityManager(SpoutRegion region) {
		super();
		if (region == null) {
			throw new NullPointerException("Region can not be null!");
		}
		this.region = region;
	}
	
	@Override
	public int allocate(SpoutEntity entity) {
		return allocate(entity, region);
	}
	
	/**
	 * The region this entity manager oversees
	 * 
	 * @return region
	 */
	@Override
	public final SpoutRegion getRegion() {
		return region;
	}

	public void syncEntities(SpoutChunk chunk) {
		SnapshotableArrayList<SpoutEntity> observers = getObserversFor(chunk);
		SnapshotableArrayList<SpoutEntity> entities = getEntitiesFor(chunk);
		List<SpoutEntity> observerSnapshot = observers.get();
		List<SpoutEntity> observerLive = observers.getLive();
		List<SpoutEntity> changedObservers = observers.getDirtyList();

		List<SpoutEntity> entitiesSnapshot = entities.get();
		List<SpoutEntity> changedEntities = entities.getDirtyList();

		if (entitiesSnapshot.size() > 0) {
			for (Entity p : changedObservers) {
				Integer playerDistanceOld = observerSnapshot.get(p);
				if (playerDistanceOld == null) {
					playerDistanceOld = Integer.MAX_VALUE;
				}
				Integer playerDistanceNew = observerLive.get(p);
				if (playerDistanceNew == null) {
					playerDistanceNew = Integer.MAX_VALUE;
				}
				// Player Network sync
				if (p instanceof Player) {
					Player player = (Player) p;

					NetworkSynchronizer n = player.getNetworkSynchronizer();
					for (Entity e : entitiesSnapshot) {
						if (player.equals(e)) {
							continue;
						}
						int entityViewDistanceOld = ((SpoutEntity) e).getPrevViewDistance();
						int entityViewDistanceNew = e.getViewDistance();

						if (playerDistanceOld <= entityViewDistanceOld && playerDistanceNew > entityViewDistanceNew) {
							n.destroyEntity(e);
						} else if (playerDistanceNew <= entityViewDistanceNew && playerDistanceOld > entityViewDistanceOld) {
							n.spawnEntity(e);
						}
					}
				}
			}
		}

		for (Entity e : changedEntities) {
			SpoutChunk oldChunk = (SpoutChunk) e.getChunk();
			if (((SpoutEntity) e).justSpawned()) {
				oldChunk = null;
			}
			SpoutChunk newChunk = (SpoutChunk) ((SpoutEntity) e).getChunkLive();
			if (!(oldChunk != null && oldChunk.equals(this)) && !((SpoutEntity) e).justSpawned()) {
				continue;
			}
			for (Entity p : observerLive.keySet()) {
				if (p == null || p.equals(e)) {
					continue;
				}
				if (p.getController() instanceof PlayerController) {
					Integer playerDistanceOld;
					if (oldChunk == null) {
						playerDistanceOld = Integer.MAX_VALUE;
					} else {
						playerDistanceOld = oldChunk.observers.getLive().get(p);
						if (playerDistanceOld == null) {
							playerDistanceOld = Integer.MAX_VALUE;
						}
					}
					Integer playerDistanceNew;
					if (newChunk == null) {
						playerDistanceNew = Integer.MAX_VALUE;
					} else {
						playerDistanceNew = newChunk.observers.getLive().get(p);
						if (playerDistanceNew == null) {
							playerDistanceNew = Integer.MAX_VALUE;
						}
					}
					int entityViewDistanceOld = ((SpoutEntity) e).getPrevViewDistance();
					int entityViewDistanceNew = e.getViewDistance();

					Player player = (Player) p.getController().getParent();

					if (!player.isOnline()) {
						continue;
					}
					NetworkSynchronizer n = player.getNetworkSynchronizer();
					if (playerDistanceOld <= entityViewDistanceOld && playerDistanceNew > entityViewDistanceNew) {
						n.destroyEntity(e);
					} else if (playerDistanceNew <= entityViewDistanceNew && playerDistanceOld > entityViewDistanceOld) {
						n.spawnEntity(e);
					}
				}
			}
		}

		// Update all entities that are in the chunk
		// TODO - should have sorting based on view distance
		for (Map.Entry<Entity, Integer> entry : observerLive.entrySet()) {
			Entity p = entry.getKey();
			if (p.getController() instanceof PlayerController) {
				Player player = (Player) p.getController().getParent();
				if (player.isOnline()) {
					NetworkSynchronizer n = player.getNetworkSynchronizer();
					int playerDistance = entry.getValue();
					Entity playerEntity = p;
					for (Entity e : entitiesSnapshot) {
						if (playerEntity != e) {
							if (playerDistance <= e.getViewDistance()) {
								if (((SpoutEntity) e).getPrevController() != e.getController()) {
									n.destroyEntity(e);
									n.spawnEntity(e);
								}
								n.syncEntity(e);
							}
						}
					}
					for (Entity e : changedEntities) {
						if (entitiesSnapshot.contains(e)) {
							continue;
						} else if (((SpoutEntity) e).justSpawned()) {
							if (playerEntity != e) {
								if (playerDistance <= e.getViewDistance()) {
									if (((SpoutEntity) e).getPrevController() != e.getController()) {
										n.destroyEntity(e);
										n.spawnEntity(e);
									}
									n.syncEntity(e);
								}
							}
						}
					}
				}
			}
		}
	}
}
