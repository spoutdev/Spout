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
package org.spout.api.material.block;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spout.api.material.BlockMaterial;
import org.spout.api.util.StringUtil;

/**
 * Represents a {@link Block}'s ID and Data values, but contains no location-specific information.
 */
public class BlockFullState implements Cloneable {
	private final short id;
	private final short data;
	
	public BlockFullState(int idAndData) {
		id = (short)(idAndData >> 16);
		data = (short)(idAndData);
	}

	public BlockFullState(short id, short data) {
		this.id = id;
		this.data = data;
	}
	
	/**
	 * Id of the Block
	 * 
	 * @return id
	 */
	public final short getId() {
		return id;
	}

	/**
	 * Data value of the Block
	 * 
	 * @return data
	 */
	public final short getData() {
		return data;
	}
	
	/**
	 * Returns an Integer representation of the merged ID and data for this BlockFullState.<br/>
	 * The id will be contained in the upper 16-bits. The data will be contained in the lower 16-bits.<br/>
	 *  
	 * @param id
	 * @param data
	 * @return integer representation of ID and Data.
	 */
	public int getPacked() {
		return getPacked(id, data);
	}
	
	/**
	 * Returns an Integer representation of the merged ID and data.<br/>
	 * The id will be contained in the upper 16-bits. The data will be contained in the lower 16-bits.<br/>
	 *  
	 * @param id to pack.
	 * @param data to pack.
	 * @return integer representation of ID and Data.
	 */
	public static int getPacked(short id, short data) {
		return id << 16 | (data & 0xFFFF);
	}
	
	/**
	 * Returns an Integer representation of the ID and Data from a {@link BlockMaterial}.<br/>
	 * The id will be contained in the upper 16-bits. The data will be contained in the lower 16-bits.<br/>
	 * 
	 * @param m
	 * @return
	 */
	public static int getPacked(BlockMaterial m) {
		return getPacked(m.getId(), m.getData());
	}
	
	/**
	 * Unpacks the ID of a Material or Block from a packed integer.<br/>
	 * The integer being passed in must have the ID of the Material or Block contained in the upper 16-bits.<br/>
	 * 
	 * @param packed integer
	 * @return id of the material or block
	 */
	public static short getId(int packed) {
		return (short) (packed >> 16);
	}
	
	/**
	 * Unpacks the Data of a material or block from a packed integer.<br/>
	 * The integer being passed in must have the data of the Material or Block contained in the lower 16-bits.<br/>
	 * 
	 * @param packed integer
	 * @return data of the material or block.
	 */
	public static short getData(int packed) {
		return (short) packed;
	}
	
	/**
	 * Looks up the BlockMaterial from a packed integer.<br/>
	 * If the material does not exist in the {@link BlockMaterialRegistry} then {@link BasicAir} will be returned.
	 * If the material does exist, and it contains data, the Sub-Material will be returned.
	 * 
	 * @param packed
	 * @return the material found.
	 */
	public static BlockMaterial getMaterial(int packed) {
		short id = getId(packed);
		short data = getData(packed);
		BlockMaterial mat = BlockMaterial.get(id);
		if (mat == null) {
			return BlockMaterial.AIR;
		}
		return mat.getSubMaterial(data);
	}
	
	@Override
	public String toString() {
		return StringUtil.toNamedString(this, this.id, this.data);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(77, 81).append(id).append(data).toHashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof BlockFullState)) {
			return false;
		} else {
			BlockFullState fullState = (BlockFullState) o;

			return fullState.id == id && fullState.data == data;
		}
	}

	@Override
	public BlockFullState clone() {
		return new BlockFullState(id, data);
	}
}
