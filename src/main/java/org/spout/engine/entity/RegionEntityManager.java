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

	public void syncEntitiesFor() {

	}
}
