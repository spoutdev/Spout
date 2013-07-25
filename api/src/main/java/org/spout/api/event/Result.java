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
package org.spout.api.event;

/**
 * Represents an event's result.
 */
public enum Result {
	/**
	 * Deny the event. Depending on the event, the action indicated by the event will either not take place or will be reverted. Some actions may not be denied.
	 */
	DENY(false),
	/**
	 * Neither deny nor allow the event. The server will proceed with its normal handling.
	 */
	DEFAULT(false),
	/**
	 * Allow / Force the event. The action indicated by the event will take place if possible, even if the server would not normally allow the action. Some actions may not be allowed.
	 */
	ALLOW(true);
	private boolean result;

	private Result(boolean result) {
		this.result = result;
	}

	/**
	 * True if the event is allowed, and is taking normal operation. False if the event is denied. Null if neither allowed, nor denied. The server will continue to proceed with its normal handling.
	 *
	 * @return the event's resolution.
	 */
	public boolean getResult() {
		return result;
	}
}
