/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.component;

import org.spout.api.datatable.SerializableMap;
import org.spout.api.tickable.Tickable;

public abstract class Component implements Tickable {
	private ComponentOwner owner;

	public Component() {
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

	/**
	 * Attaches to a component owner.
	 *
	 * @param owner the component owner to attach to
	 * @return true if successful
	 */
	public boolean attachTo(ComponentOwner owner) {
		this.owner = owner;
		return true;
	}

	/**
	 * Gets the component owner that owns this component.
	 *
	 * @return the component owner
	 */
	public ComponentOwner getOwner() {
		if (owner == null) {
			throw new IllegalStateException("Trying to access the owner of this component before it was attached");
		}
		return owner;
	}

	/**
	 * Called when this component is attached to a owner.
	 */
	public void onAttached() {
	}

	/**
	 * Called when this component is detached from a owner.
	 */
	public void onDetached() {
	}

	/**
	 * Specifies whether or not this component can be detached, after it has already been attached to an owner..
	 *
	 * @return true if it can be detached
	 */
	public boolean isDetachable() {
		return true;
	}

	/**
	 * Called when the owner is set to be synchronized. <p> This method is READ ONLY. You cannot update in this method.
	 */
	public void onSync() {
	}

	/**
	 * Gets the {@link SerializableMap} which a ComponentOwner always has <p> This is merely a convenience method.
	 *
	 * @return SerializableMap of the owner
	 */
	public final SerializableMap getData() {
		return getOwner().getData();
	}
}
