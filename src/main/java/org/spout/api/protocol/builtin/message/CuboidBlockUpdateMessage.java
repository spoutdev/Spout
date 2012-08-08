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
package org.spout.api.protocol.builtin.message;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.math.Vector3;
import org.spout.api.protocol.Message;
import org.spout.api.util.SpoutToStringStyle;

public class CuboidBlockUpdateMessage implements Message {
	private final int minX, minY, minZ;
	private final int maxX, maxY, maxZ;
	// These fields aren't sent across the network - just for reference
	private transient final int sizeX, sizeY, sizeZ;
	private final short[] blockTypes, blockData;
	private final byte[] blockLight, skyLight;

	public CuboidBlockUpdateMessage(Vector3 min, Vector3 max, short[] blockTypes, short[] blockData, byte[] blockLight, byte[] skyLight) {
		this(min.getFloorX(), min.getFloorY(), min.getFloorZ(),
				max.getFloorX(), max.getFloorY(), max.getFloorZ(),
				blockTypes, blockData, blockLight, skyLight);
	}

	public CuboidBlockUpdateMessage(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, short[] blockTypes, short[] blockData, byte[] blockLight, byte[] skyLight) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		this.sizeX = Math.abs(maxX - minX);
		this.sizeY = Math.abs(maxY - minY);
		this.sizeZ = Math.abs(maxZ - minZ);
		if (blockTypes.length != sizeX * sizeY * sizeZ) {
			throw new IllegalArgumentException(String.format("blockTypes is not of expected size (%d instead of %d)",
					blockTypes.length, sizeX * sizeY * sizeZ));
		}

		if (blockData.length != sizeX * sizeY * sizeZ) {
			throw new IllegalArgumentException(String.format("blockData is not of expected size (%d instead of %d)",
					blockData.length, sizeX * sizeY * sizeZ));
		}

		if (blockLight.length != sizeX * sizeY * sizeZ / 2) {
			throw new IllegalArgumentException(String.format("blockLight is not of expected size (%d instead of %d)",
					blockLight.length, sizeX * sizeY * sizeZ / 2));
		}

		if (skyLight.length != sizeX * sizeY * sizeZ / 2) {
			throw new IllegalArgumentException(String.format("skyLight is not of expected size (%d instead of %d)",
					skyLight.length, sizeX * sizeY * sizeZ / 2));
		}

		this.blockTypes = blockTypes;
		this.blockData = blockData;
		this.blockLight = blockLight;
		this.skyLight = skyLight;
	}

	public int getMinX() {
		return minX;
	}

	public int getMinY() {
		return minY;
	}

	public int getMinZ() {
		return minZ;
	}

	public int getMaxX() {
		return maxX;
	}

	public int getMaxY() {
		return maxY;
	}

	public int getMaxZ() {
		return maxZ;
	}


	public short[] getBlockTypes() {
		return blockTypes;
	}

	public short[] getBlockData() {
		return blockData;
	}

	public byte[] getBlockLight() {
		return blockLight;
	}

	public byte[] getSkyLight() {
		return skyLight;
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public int getSizeZ() {
		return sizeZ;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("minX", minX)
				.append("minY", minY)
				.append("minZ", minZ)
				.append("maxX", maxX)
				.append("maxY", maxY)
				.append("maxZ", maxZ)
				.append("blockTypes", blockTypes)
				.append("blockData", blockData)
				.append("blockLight", blockLight)
				.append("skyLight", skyLight)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(39, 67)
				.append(minX)
				.append(minY)
				.append(minZ)
				.append(maxX)
				.append(maxY)
				.append(maxZ)
				.append(sizeX)
				.append(sizeY)
				.append(sizeZ)
				.append(blockTypes)
				.append(blockData)
				.append(blockLight)
				.append(skyLight)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CuboidBlockUpdateMessage) {
			final CuboidBlockUpdateMessage other = (CuboidBlockUpdateMessage) obj;
			return new EqualsBuilder()
					.append(minX, other.minX)
					.append(minY, other.minY)
					.append(minZ, other.minZ)
					.append(maxX, other.maxX)
					.append(maxY, other.maxY)
					.append(maxZ, other.maxZ)
					.append(sizeX, other.sizeX)
					.append(sizeY, other.sizeY)
					.append(sizeZ, other.sizeZ)
					.append(blockTypes, other.blockTypes)
					.append(blockData, other.blockData)
					.append(blockLight, other.blockLight)
					.append(skyLight, other.skyLight)
					.isEquals();
		} else {
			return false;
		}
	}
}
