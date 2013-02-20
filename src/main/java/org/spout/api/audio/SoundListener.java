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
package org.spout.api.audio;

import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Vector3;

public interface SoundListener {
	/**
	 * Sets the position of the SoundListener. The specified location will
	 * affect how this listener will hear sound relative to the existing
	 * sources.
	 *
	 * @param pos position of listener
	 */
	public void setPosition(Point pos);

	/**
	 * Returns the position of the SoundListener. The specified location will
	 * affect how this listener will hear sound relative to the existing
	 * sources.
	 *
	 * @return position of listener
	 */
	public Point getPosition();

	/**
	 * Sets the velocity of the listener. Used for doppler effects.
	 *
	 * @param vec velocity of listener
	 */
	public void setVelocity(Vector3 vec);

	/**
	 * Returns velocity of this listener. Used for doppler effects.
	 *
	 * @return velocity
	 */
	public Vector3 getVelocity();

	/**
	 * Returns the orientation of this listener.
	 *
	 * @param at the position of listening point relative to
	 * {@link #getPosition()}
	 * @param up
	 */
	public void setOrientation(Vector3 at, Vector3 up);

	/**
	 * Sets the at value of the listeners orientation.
	 *
	 * @param at value
	 */
	public void setOrientationAt(Vector3 at);

	/**
	 * Sets the up value of the listener orientation
	 *
	 * @param up value
	 */
	public void setOrientationUp(Vector3 up);

	/**
	 * Returns the 'at' value of the listener's orientation
	 *
	 * @return at value
	 */
	public Vector3 getOrientationAt();

	/**
	 * Returns the 'up' value of the listener's orientation.
	 *
	 * @return up value
	 */
	public Vector3 getOrientationUp();
}
