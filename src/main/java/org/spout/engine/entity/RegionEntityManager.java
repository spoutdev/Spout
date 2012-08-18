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

import com.google.common.collect.Lists;

import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.math.MathHelper;
import org.spout.api.protocol.NetworkSynchronizer;

import org.spout.engine.util.thread.snapshotable.SnapshotableHashMap;
import org.spout.engine.world.SpoutRegion;

public final class RegionEntityManager extends EntityManager {
	private final SpoutRegion region;
	/**
	 * Player listings plus listings of sync'd entities per player
	 */
	private final SnapshotableHashMap<Player, List<SpoutEntity>> players = new SnapshotableHashMap<Player, List<SpoutEntity>>(snapshotManager);

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

	@Override
	public void addEntity(SpoutEntity entity, SpoutRegion region) {
		super.addEntity(entity, region);
		//Handle players
		if (entity instanceof Player) {
			if (entity.getController() != null) {
				players.putIfAbsent((Player) entity, new ArrayList<SpoutEntity>());
			}
		}
	}

	@Override
	public void removeEntity(SpoutEntity entity) {
		super.removeEntity(entity);
		if (entity instanceof Player) {
			players.remove((Player) entity);
		}
	}

	public List<Player> getPlayers() {
		Map map = players.get();
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
			for (SpoutEntity entity : getAll()) {
				if (entity.equals(player)) {
					continue;
				}
				boolean contains = entitiesPerPlayer.contains(entity);
				if (MathHelper.distance(player.getPosition(), entity.getPosition()) <= playerViewDistance) {
					if (!contains) {
						entitiesPerPlayer.add(entity);
						net.spawnEntity(entity);
					} else if (entity.isDead()) {
						boolean remove = entitiesPerPlayer.remove(entity);
						if (remove) {
							net.destroyEntity(entity);
						}
					} else if (!entity.getController().equals(entity.getPrevController())) {
						net.destroyEntity(entity);
						net.spawnEntity(entity);
					} else {
						net.syncEntity(entity);
					}
				} else {
					boolean remove = entitiesPerPlayer.remove(entity);
					if (remove) {
						net.destroyEntity(entity);
					}
				}
			}
			players.put(player, entitiesPerPlayer);
		}
	}
}
