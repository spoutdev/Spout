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
package org.spout.api.entity;

import org.spout.api.datatable.Datatable;
import org.spout.api.entity.components.DatatableComponent;
import org.spout.api.entity.components.TransformComponent;
import org.spout.api.geo.discrete.Transform;

public abstract class BaseComponent implements Component {
	private Entity parent;

	@Override
	public final void attachToEntity(Entity parent) {
		this.parent = parent;
	}

	@Override
	public Entity getParent() {
		return parent;
	}
	
	@Override
	public void onAttached() {
	}

	@Override
	public void onDetached() {
	}

	@Override
	public void onSpawned() {
	}

	@Override
	public void onRemoved() {
	}

	@Override
	public void onObserved() {
	}

	@Override
	public void onUnObserved() {
	}

	@Override
	public void onSync() {
	}	

	@Override
	public boolean canTick() {
		return true;
	}

	@Override
	public final void tick(float dt) {
		if (canTick()) {
			onTick(dt);
		}
	}
	
	@Override
	public void onTick(float dt) {
	}
	
	@Override
	public final DatatableComponent getDatatable() {
		return getParent().getDatatable();
	}
	
	@Override
	public final TransformComponent getTransform() {
		return getParent().getTransform();
	}
}
