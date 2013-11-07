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
package org.spout.api.protocol.reposition;

import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.math.vector.Vector3f;

public interface RepositionManager {
	/**
	 * Gets the converted Chunk x value for the given Chunk x value
	 *
	 * @param cX the chunk x value
	 */
	public int convertChunkX(int x);

	/**
	 * Gets the converted Chunk y value for the given Chunk y value
	 *
	 * @param cY the server-side chunk y value
	 */
	public int convertChunkY(int y);

	/**
	 * Gets the converted Chunk z value for the given Chunk z value
	 *
	 * @param cZ the server-side chunk z value
	 */
	public int convertChunkZ(int z);

	/**
	 * Gets the converted x value for the given x value.  The change must be exactly an integer number of chunks.
	 *
	 * @param x the x value
	 */
	public int convertX(int x);

	/**
	 * Gets the converted y value for the given y value.  The change must be exactly an integer number of chunks.
	 *
	 * @param y the y value
	 */
	public int convertY(int y);

	/**
	 * Gets the converted z value for the given z value.  The change must be exactly an integer number of chunks.
	 *
	 * @param z the z value
	 */
	public int convertZ(int z);

	/**
	 * Gets the converted x value for the given x value.  The change must be exactly an integer number of chunks.
	 *
	 * @param x the x value
	 */
	public float convertX(float x);

	/**
	 * Gets the converted y value for the given y value.  The change must be exactly an integer number of chunks.
	 *
	 * @param y the y value
	 */
	public float convertY(float y);

	/**
	 * Gets the converted z value for the given y value.  The change must be exactly an integer number of chunks.
	 *
	 * @param z the z value
	 */
	public float convertZ(float z);

	/**
	 * Gets the converted x value for the given x value.  The change must be exactly an integer number of chunks.
	 *
	 * @param x the x value
	 */
	public double convertX(double x);

	/**
	 * Gets converted y value for the given y value.  The change must be exactly an integer number of chunks.
	 *
	 * @param y the y value
	 */
	public double convertY(double y);

	/**
	 * Gets the converted z value for the given z value.  The change must be exactly an integer number of chunks.
	 *
	 * @param z the z value
	 */
	public double convertZ(double z);

	/**
	 * Gets the converted Transform for the given Transform.  The change must be exactly an integer number of chunks in each dimension.
	 *
	 * @param t the transform
	 */
	public Transform convert(Transform t);

	/**
	 * Gets the converted Point for the given Point.  The change must be exactly an integer number of chunks in each dimension.
	 *
	 * @param p the point
	 */
	public Point convert(Point p);

	/**
	 * Gets the converted Vector3 for the given Vector3.  The change must be exactly an integer number of chunks in each dimension.
	 *
	 * @param p the point
	 */
	public Vector3f convert(Vector3f p);

	/**
	 * Gets the inverse RepositionManager that reverses changes made by this manager.
	 */
	public RepositionManager getInverse();
}
