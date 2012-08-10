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

import org.spout.api.tickable.TickPriority;
import org.spout.api.tickable.Tickable;

/**
 * Represents an attachment to a entity that can respond to Ticks.
 */
public interface Component<T extends ComponentHolder> extends Tickable, Comparable<Component<T>> {
	/**
	 * Attaches this component to a entity
	 * @param parent entity this component will be attached to.
	 */
	public void attachToController(T parent);

	/**
	 * Gets the parent entity associated with this component.
	 * @return the parent entity
	 */
	public T getParent();

	/**
	 * Called when this component is attached to a entity
	 */
	public abstract void onAttached();

	/**
	 * Called when this component is detached from a entity
	 */
	public void onDetached();

	/**
	 * Gets the priority this component will be ticked by.
	 * @return the priority the component will be ticked by
	 */
	public TickPriority getPriority();

	/**
	 * Sets the priority the component will be ticked by.
	 * @param priority the priority the component will be ticked by
	 */
	public void setPriority(TickPriority priority);

	/**
	 * Returns if this component will run once and remove itself.
	 * @return true if the component runs once, false if not
	 */
	public boolean runOnce();

	/**
	 * Sets whether this component will run once or not.
	 * @param runOnce true to run once, false to repeat
	 */
	public void setRunOnce(boolean runOnce);

	/**
	 * Gets the delay before this component is ticked.
	 * @return the delay
	 */
	public float getDelay();

	/**
	 * Sets the delay before this component is ticked.
	 * @param delay the delay before the component is ticked
	 */
	public void setDelay(float delay);

	/**
	 * Gets the max delay this component can delay before ticking
	 * @return the max delay
	 */
	public float getMaxDelay();

	/**
	 * Sets the max delay this component can delay before ticking
	 * @param maxDelay the max delay this component can delay
	 */
	public void setMaxDelay(float maxDelay);

	/**
	 * Ticks this component
	 * @param dt time since the last tick (delta time)
	 */
	public void tick(float dt);
}
