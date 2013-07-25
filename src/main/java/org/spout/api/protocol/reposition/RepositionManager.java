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
package org.spout.api.protocol.reposition;

import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Vector3;

public interface RepositionManager {

	/**
	 * Gets the converted Chunk x value for the given Chunk x value
	 * 
	 * @param cX the chunk x value
	 * @return
	 */
	public int convertChunkX(int x);
	
	/**
	 * Gets the converted Chunk y value for the given Chunk y value
	 * 
	 * @param cY the server-side chunk y value
	 * @return
	 */
	public int convertChunkY(int y);
	
	/**
	 * Gets the converted Chunk z value for the given Chunk z value
	 * 
	 * @param cZ the server-side chunk z value
	 * @return
	 */
	public int convertChunkZ(int z);
	
	/**
	 * Gets the converted x value for the given x value.  The change must be exactly an integer number of chunks.
	 * 
	 * @param x the x value
	 * @return
	 */
	public int convertX(int x);
	
	/**
	 * Gets the converted y value for the given y value.  The change must be exactly an integer number of chunks.
	 * 
	 * @param y the y value
	 * @return
	 */
	public int convertY(int y);
	
	/**
	 * Gets the converted z value for the given z value.  The change must be exactly an integer number of chunks.
	 * 
	 * @param z the z value
	 * @return
	 */
	public int convertZ(int z);
	
	/**
	 * Gets the converted x value for the given x value.  The change must be exactly an integer number of chunks.
	 * 
	 * @param x the x value
	 * @return
	 */
	public float convertX(float x);
	
	/**
	 * Gets the converted y value for the given y value.  The change must be exactly an integer number of chunks.
	 * 
	 * @param y the y value
	 * @return
	 */
	public float convertY(float y);
	
	/**
	 * Gets the converted z value for the given y value.  The change must be exactly an integer number of chunks.
	 * 
	 * @param z the z value
	 * @return
	 */
	public float convertZ(float z);
	
	/**
	 * Gets the converted x value for the given x value.  The change must be exactly an integer number of chunks.
	 * 
	 * @param x the x value
	 * @return
	 */
	public double convertX(double x);
	
	/**
	 * Gets converted y value for the given y value.  The change must be exactly an integer number of chunks.
	 * 
	 * @param y the y value
	 * @return
	 */
	public double convertY(double y);
	
	/**
	 * Gets the converted z value for the given z value.  The change must be exactly an integer number of chunks.
	 * 
	 * @param z the z value
	 * @return
	 */
	public double convertZ(double z);
	
	/**
	 * Gets the converted Transform for the given Transform.  The change must be exactly an integer number of chunks in each dimension.
	 * 
	 * @param t the transform
	 * @return
	 */
	public Transform convert(Transform t);
	
	/**
	 * Gets the converted Point for the given Point.  The change must be exactly an integer number of chunks in each dimension.
	 * 
	 * @param p the point
	 * @return
	 */
	public Point convert(Point p);
	
	/**
	 * Gets the converted Vector3 for the given Vector3.  The change must be exactly an integer number of chunks in each dimension.
	 * 
	 * @param p the point
	 * @return
	 */
	public Vector3 convert(Vector3 p);

	/**
	 * Gets the inverse RepositionManager that reverses changes made by this manager.
	 * 
	 * @return
	 */
	public RepositionManager getInverse();
	
}
