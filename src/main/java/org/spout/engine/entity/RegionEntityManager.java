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

import java.util.Collection;

import org.spout.api.entity.Player;
import org.spout.api.math.MathHelper;
import org.spout.api.protocol.NetworkSynchronizer;

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

	/**
	 * Syncs all entities/observers in this region
	 */
	public void syncEntities() {
		/*
		 * The list of entities that have snapshot values.
		 */
		Collection<SpoutEntity> snapshots = getRegion().getEntityManager().getAll();
		/*
		 * The list of entities that have live values.
		 */
		Collection<SpoutEntity> lives = getRegion().getEntityManager().getAllLive();
		if (snapshots.isEmpty() || lives.isEmpty()) {
			return;
		}
		for (Player player : getRegion().getPlayers()) {
			/*
			 * Offline players have no network synchronizer, skip them
			 */
			if (!player.isOnline()) {
				continue;
			}
			Integer playerViewDistance = player.getViewDistance();
			NetworkSynchronizer net = player.getNetworkSynchronizer();
			for (SpoutEntity snapshot : snapshots) {
				if (snapshot.equals(player)) {
					continue;
				}
				/*
				 * We have four scenarios when sync'ing entities to a NetworkSynchronizer
				 * - Spawning the entity if in range and is new.
				 * - Destroying the entity if out of range and isn't new.
				 * - Syncing the entity if in range and has changed.
				 * - Destroy/spawn an entity if isn't new and changed controllers.
				 */
				/*
				 * If the live entities' position is in range of the player's view distance + position
				 */
				if (!snapshot.isDead() && MathHelper.distance(player.getPosition(), snapshot.getPosition()) <= playerViewDistance) {
					/*
					 * If the entity has just spawned, spawn it to the synchronizer!
					 */
					if (snapshot.justSpawned()) {
						net.spawnEntity(snapshot);
						/*
						 * If the entity is in the dirty list and snapshots list then it changed this tick, so sync it!
 						 */
					} else if (lives.contains(snapshot)) {
						/*
						 * The entity is in a snapshot and a dirty list for updates but swapped controllers. Destroy and spawn it!
						 */
						if (!snapshot.getController().equals(snapshot.getPrevController())) {
							net.destroyEntity(snapshot);
							net.spawnEntity(snapshot);
						} else {
							/*
							 * Controller change didn't occur, so sync it!
							 */
							net.syncEntity(snapshot);
						}
					}
					/*
					 * The entity is out of range or is dead, destroy it!
					 */
				} else {
					net.destroyEntity(snapshot);
				}
			}
		}
	}
}
