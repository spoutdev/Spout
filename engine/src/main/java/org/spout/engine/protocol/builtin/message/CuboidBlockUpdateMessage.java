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
package org.spout.engine.protocol.builtin.message;

import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import org.spout.math.vector.Vector3f;
import org.spout.api.util.SpoutToStringStyle;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;

public class CuboidBlockUpdateMessage extends SpoutMessage {
	private UUID world;
	// TODO: protocol - why do we need a blockLight AND skyLight?
	private final byte[] blockLight, skyLight;
	private CuboidBlockMaterialBuffer cuboid;

	public CuboidBlockUpdateMessage(UUID world, CuboidBlockMaterialBuffer cuboid, byte[] blockLight, byte[] skyLight) {
		this.world = world;
		this.cuboid = cuboid;
		this.blockLight = blockLight;
		this.skyLight = skyLight;
	}

	public CuboidBlockUpdateMessage(UUID world, Vector3f min, Vector3f max, short[] blockTypes, short[] blockData, byte[] blockLight, byte[] skyLight) {
		this(world, min.getFloorX(), min.getFloorY(), min.getFloorZ(),
				max.getFloorX(), max.getFloorY(), max.getFloorZ(),
				blockTypes, blockData, blockLight, skyLight);
	}

	public CuboidBlockUpdateMessage(UUID world, int minX, int minY, int minZ, int sizeX, int sizeY, int sizeZ, short[] blockTypes, short[] blockData, byte[] blockLight, byte[] skyLight) {
		this.world = world;
		cuboid = new CuboidBlockMaterialBuffer(minX, minY, minZ, sizeX, sizeY, sizeZ, blockTypes, blockData, true);
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

		this.blockLight = blockLight;
		this.skyLight = skyLight;
	}

	public int getMinX() {
		return cuboid.getBase().getFloorX();
	}

	public int getMinY() {
		return cuboid.getBase().getFloorY();
	}

	public int getMinZ() {
		return cuboid.getBase().getFloorZ();
	}

	public short[] getBlockTypes() {
		return cuboid.getRawId();
	}

	public short[] getBlockData() {
		return cuboid.getRawData();
	}

	public byte[] getBlockLight() {
		return blockLight;
	}

	public byte[] getSkyLight() {
		return skyLight;
	}

	public int getSizeX() {
		return cuboid.getSize().getFloorX();
	}

	public int getSizeY() {
		return cuboid.getSize().getFloorY();
	}

	public int getSizeZ() {
		return cuboid.getSize().getFloorZ();
	}

	public UUID getWorldUUID() {
		return world;
	}

	public CuboidBlockMaterialBuffer getCuboid() {
		return cuboid;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("cuboid", cuboid)
				.append("blockLight", blockLight)
				.append("skyLight", skyLight)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(39, 67)
				.append(getMinX())
				.append(getMinY())
				.append(getMinZ())
				.append(getSizeX())
				.append(getSizeY())
				.append(getSizeZ())
				.append(getBlockTypes())
				.append(getBlockData())
				.append(blockLight)
				.append(skyLight)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CuboidBlockUpdateMessage) {
			final CuboidBlockUpdateMessage other = (CuboidBlockUpdateMessage) obj;
			return new EqualsBuilder()
					.append(getMinX(), other.getMinX())
					.append(getMinY(), other.getMinY())
					.append(getMinZ(), other.getMinZ())
					.append(getSizeX(), other.getSizeX())
					.append(getSizeY(), other.getSizeY())
					.append(getSizeZ(), other.getSizeZ())
					.append(getBlockTypes(), other.getBlockTypes())
					.append(getBlockData(), other.getBlockData())
					.append(blockLight, other.blockLight)
					.append(skyLight, other.skyLight)
					.isEquals();
		} else {
			return false;
		}
	}
}
