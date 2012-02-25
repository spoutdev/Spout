/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.util.map.concurrent;

import org.spout.api.geo.cuboid.Chunk;

/**
 * An interface for class which can be linked to a particular block
 */
public interface BlockLinkable {
	/**
	 * Links the object to a block. The sequenceNumber should be stored as an
	 * AtomicInteger.<br>
	 * <br>
	 * The sequence number for a block should be read before and after any read.
	 * If either sequence number is DataTableSequenceNumber.UNSTABLE, then the
	 * read is unsafe. Otherwise, if both sequence numbers are the same, then
	 * the object can be considered read correctly.
	 *
	 * @param chunk the chunk containing the block
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @param sequenceNumber the sequence number
	 */
	public void linkToBlock(Chunk chunk, int x, int y, int z, int sequenceNumber);
}
