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
package org.spout.api.component;

import org.spout.api.component.components.DatatableComponent;
import org.spout.api.tickable.Tickable;

import com.alta189.annotations.RequireDefault;

public abstract class Component implements Tickable {
	private ComponentHolder holder;

	public Component() {
	}

	/**
	 * Attaches to a component holder.
	 * @param holder the componet holder to attach to
	 * @return true if successful
	 */
	public boolean attachTo(ComponentHolder holder) {
		this.holder = holder;
		return true;
	}

	/**
	 * Gets the component holder that is holding this component.
	 * @return the component holder
	 */
	public ComponentHolder getHolder() {
		return holder;
	}

	/**
	 * Called when this component is attached to a holder.
	 */
	public void onAttached() {
	}

	/**
	 * Called when this component is detached from a holder.
	 */
	public void onDetached() {
	}

	/**
	 * Specifies whether or not this component can be detached,
	 * after it has already been attached to a holder.
	 * @return true if it can be detached
	 */
	public boolean isDetachable() {
		return true;
	}

	/**
	 * Called when the holder is set to be synchronized.
	 * <p/>
	 * This method is READ ONLY. You cannot update in this method.
	 */
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

	/**
	 * Gets the datatable component attached to the holder.
	 * This component exists in every holder.
	 * @return the datatable component
	 */
	public final DatatableComponent getData() {
		return getHolder().getData();
	}
}
