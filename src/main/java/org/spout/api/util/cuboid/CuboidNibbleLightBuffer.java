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
package org.spout.api.util.cuboid;

import java.util.Arrays;

import org.spout.api.lighting.Modifiable;


public class CuboidNibbleLightBuffer extends CuboidLightBuffer {
	protected final byte[] lightData;
	private CuboidNibbleLightBuffer source = null;
	
	protected CuboidNibbleLightBuffer(CuboidNibbleLightBuffer buffer) {
		this(buffer.holder, buffer.getManagerId(), buffer.baseX, buffer.baseY, buffer.baseZ, buffer.sizeX, buffer.sizeY, buffer.sizeZ, buffer.lightData);
	}
	
	protected CuboidNibbleLightBuffer(Modifiable holder, int id, int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ) {
		this(holder, id, baseX, baseY, baseZ, sizeX, sizeY, sizeZ, null);
	}
	
	protected CuboidNibbleLightBuffer(Modifiable holder, int id, int baseX, int baseY, int baseZ, int sizeX, int sizeY, int sizeZ, byte[] data) {
		super(holder, id, baseX, baseY, baseZ, sizeX, sizeY, sizeZ);
		int volume = getVolume();
		if ((volume | 1) == volume) {
			throw new IllegalArgumentException("Buffer volume must be an even number, " + volume);
		}
		int arrayLength = volume >> 1;
		if (data != null) {
			if (data.length != arrayLength) {
				throw new IllegalArgumentException("The length of the given array is invalid, " + data.length + ", expected length, " + arrayLength);
			}
			this.lightData = Arrays.copyOf(data, data.length);;
		} else {
			this.lightData = new byte[arrayLength];
		}
	}
	
	@Override
	public void setSource(CuboidBuffer source) {
		if (source instanceof CuboidNibbleLightBuffer) {
			this.source = (CuboidNibbleLightBuffer) source;
		} else {
			throw new IllegalArgumentException("Only CuboidNibbleLightBuffer may be used as the data source when copying to a CuboidNibbleLightBuffer");
		}
	}

	@Override
	public void copyElement(int thisIndex, int sourceIndex, int runLength) {
		holder.setModified();
		if (!isEven(thisIndex + sourceIndex)) {
			// means one is even and one is odd
			for (int i = 0; i < runLength; i++) {
				set(thisIndex++, source.get(sourceIndex++));
			}
		} else { // both even or odd (means can copy the underlying arrays)
			// If both even add copy the first elements slowly
			if (!isEven(thisIndex)) {
				set(thisIndex++, source.get(sourceIndex++));
				runLength--;
			}
			// If the remaining elements are odd, copy the last element slowly
			if (!isEven(runLength)) {
				set(thisIndex + runLength - 1, source.get(sourceIndex + runLength - 1));
			}
			// Copy the remaining elements with direct array to array copy
			runLength >>= 1;
			thisIndex >>= 1;
			sourceIndex >>= 1;
			for (int i = 0; i < runLength; i++) {
				lightData[thisIndex++] = source.lightData[sourceIndex++];
			}
		}
	}
	
	public void set(int x, int y, int z, byte value) {
		set(getIndex(x, y, z), value);
	}
	
	public byte get(int x, int y, int z) {
		return get(getIndex(x, y, z));
	}
	
	public void set(int index, byte value) {
		holder.setModified();
		if (isEven(index)) {
			index >>= 1;
			lightData[index] = (byte) ((lightData[index] & 0xF0) | (value & 0x0F));
		} else {
			index >>= 1;
			lightData[index] = (byte) ((lightData[index] & 0x0F) | (value << 4));
		}
	}
	
	public byte get(int index) {
		if (isEven(index)) {
			index >>= 1;
			return (byte) (lightData[index] & 0x0F);
		} else {
			index >>= 1;
			return (byte) ((lightData[index] >> 4) & 0x0F);
		}		
	}
	
	public CuboidNibbleLightBuffer copy() {
		return new CuboidNibbleLightBuffer(this);
	}
	
	public void copyToArray(byte[] target, int start) {
		System.arraycopy(lightData, 0, target, start, lightData.length);
	}
	
	public byte[] serialize() {
		return Arrays.copyOf(lightData, lightData.length);
	}
	
	private static boolean isEven(int i) {
		return (i | 1) != i;
	}

}
