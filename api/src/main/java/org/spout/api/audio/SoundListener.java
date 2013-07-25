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
package org.spout.api.audio;

import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Vector3;

/**
 * Listens for Sounds.
 */
public interface SoundListener {
	/**
	 * Sets the position of this SoundListener. The specified location will affect how this listener will hear sound relative to the existing sources.
	 *
	 * @param pos position of listener
	 * @see #getPosition
	 */
	public void setPosition(Point pos);

	/**
	 * Returns the position of this SoundListener. The specified location will affect how this listener will hear sound relative to the existing sources.
	 *
	 * @return position of listener
	 * @see #setPosition
	 */
	public Point getPosition();

	/**
	 * Sets the velocity of this SoundListener. Used for doppler effects.
	 *
	 * @param vec velocity of listener
	 * @see #getVelocity
	 */
	public void setVelocity(Vector3 vec);

	/**
	 * Returns velocity of this SoundListener. Used for doppler effects.
	 *
	 * @return velocity
	 * @see #setVelocity
	 */
	public Vector3 getVelocity();

	/**
	 * Sets the orientation of this SoundListener.
	 *
	 * @param at position of the listening point relative to {@link #getPosition()}
	 * @param up up value
	 */
	public void setOrientation(Vector3 at, Vector3 up);

	/**
	 * Sets the at value of this SoundListener's orientation.
	 *
	 * @param at position of the listening point relative to {@link #getPosition()}
	 * @see #getOrientationAt
	 */
	public void setOrientationAt(Vector3 at);

	/**
	 * Sets the up value of this SoundListener's orientation.
	 *
	 * @param up up value
	 * @see #getOrientationUp
	 */
	public void setOrientationUp(Vector3 up);

	/**
	 * Returns the at value of this SoundListener's orientation.
	 *
	 * @return position of the listening point relative to {@link #getPosition()}
	 * @see #setOrientationAt
	 */
	public Vector3 getOrientationAt();

	/**
	 * Returns the up value of the SoundListener's orientation.
	 *
	 * @return up value
	 * @see #setOrientationUp
	 */
	public Vector3 getOrientationUp();
}
