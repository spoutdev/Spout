/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.event;

/**
 * Represents a cause of an event
 * 
 * @param <T> source of the cause
 */
public abstract class Cause<T> {
	private static final int MAX_CAUSES = 100;
	private final Cause<?> parent;
	/**
	 * Gets the source of the action
	 * @return
	 */
	public abstract T getSource();

	/**
	 * Constructs a cause with no parent cause
	 */
	public Cause() {
			this(null);
	}

	/**
	 * Constructs a cause with a parent cause that was directly responsible for the action
	 * 
	 * @param parent
	 */
	public Cause(Cause<?> parent) {
		this.parent = parent;
	}

	/**
	 * Gets the first cause in the parent-child series of causes that led to this.
	 * 
	 * <p>May terminate early to prevent infinite loops</p>
	 * 
	 * Note: Can be null if there is no parent
	 * 
	 * @return first cause or null if there is no parent
	 */
	public final Cause<?> getFirstCause() {
		int causes = 0;
		Cause<?> main = this;
		while(causes < MAX_CAUSES) {
			if (main.getParent() != null) {
				main = main.getParent();
			} else {
				break;
			}
		}
		return main;
	}

	/**
	 * Gets the parent cause of this cause.
	 * <br/><br/>
	 * Note: Can be null if there is no parent
	 * @return parent cause or null if there is no parent
	 */
	public final Cause<?> getParent() {
		return parent;
	}

}

