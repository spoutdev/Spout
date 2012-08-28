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
package org.spout.api.protocol;

/**
 * The Sending mode to use to send Messages an Entity
 */
public enum SendMode {
	/**
	 * The Messages are only sent to itself
	 */
	SELF(true, false),
	/**
	 * The Messages are only sent to the observers
	 */
	OBSERVERS(false, true),
	/**
	 * The messages are sent to the observers and to itself
	 */
	ALL(true, true);

	private final boolean self, observers;

	private SendMode(boolean self, boolean observers) {
		this.self = self;
		this.observers = observers;
	}

	/**
	 * Whether this Send mode permits sending to the observers
	 * 
	 * @return True or False
	 */
	public boolean canSendToObservers() {
		return this.observers;
	}

	/**
	 * Whether this Send mode permits sending to itself (if self is a Message receiver)
	 * 
	 * @return True or False
	 */
	public boolean canSendToSelf() {
		return this.self;
	}
}
