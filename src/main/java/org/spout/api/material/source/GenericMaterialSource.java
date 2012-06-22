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
package org.spout.api.material.source;

import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;
import org.spout.api.util.LogicUtil;
import org.spout.api.util.hashing.ShortPairHashed;

public class GenericMaterialSource implements MaterialSource {
	private final BlockMaterial material;
	private final short data;

	public GenericMaterialSource(BlockMaterial material, short data) {
		this.material = material;
		this.data = data;
	}

	@Override
	public BlockMaterial getMaterial() {
		return this.material;
	}

	@Override
	public short getData() {
		return this.data;
	}

	@Override
	public String toString() {
		return "{material=" + this.material + ",data=" + this.data + "}";
	}

	@Override
	public int hashCode() {
		return ShortPairHashed.key(this.material.getId(), this.data);
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		} else if (other instanceof MaterialSource) {
			MaterialSource bs = (MaterialSource) other;
			return bs.getMaterial() == this.getMaterial() && bs.getData() == this.getData();
		} else {
			return false;
		}
	}

	@Override
	public Material getSubMaterial() {
		return this.getMaterial().getSubMaterial(this.getData());
	}

	@Override
	public boolean isMaterial(Material... materials) {
		return LogicUtil.equalsAny(this.material, materials);
	}
}
