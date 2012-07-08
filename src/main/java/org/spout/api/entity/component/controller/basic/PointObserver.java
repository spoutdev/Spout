/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
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
package org.spout.api.entity.component.controller.basic;

import org.spout.api.Spout;
import org.spout.api.entity.component.Controller;
import org.spout.api.entity.component.controller.type.ControllerType;
import org.spout.api.entity.component.controller.type.EmptyConstructorControllerType;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;

/**
 * Controller that observes chunks around a point. It will always keep these chunks in memory.
 */
public class PointObserver extends Controller {
	public static final int CHUNK_VIEW_DISTANCE = 4;
	public static final ControllerType TYPE = new EmptyConstructorControllerType(PointObserver.class, "Point Observer");
	private Point currPoint;

	private final int viewDistance;
	
	public PointObserver(int viewDistance) {
		super(TYPE);
		this.viewDistance = viewDistance;
	}
	
	public PointObserver() {
		this(CHUNK_VIEW_DISTANCE);
	}

	@Override
	public boolean isSavable() {
		return false;
	}

	@Override
	public void onTick(float dt) {
		//Lets make sure the point observer never goes haywire...
		if (!getParent().getPosition().equals(currPoint)) {
			if (Spout.debugMode()) {
				Spout.getLogger().severe("Point observer is moving! " + getParent().getPosition().toString());
			}
			getParent().setPosition(currPoint);
		}
	}

	@Override
	public void onAttached() {
		getParent().setCollision(null);
		getParent().setObserver(true);
		getParent().setViewDistance(viewDistance << Chunk.BLOCKS.BITS);
		currPoint = getParent().getPosition();
	}
	
	public boolean isImportant() {
		return true;
	}
}
