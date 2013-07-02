/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.util.SpoutToStringStyle;

public class BlockUpdateMessage extends SpoutMessage {
	private final int x, y, z;
	private final short type, data;

	public BlockUpdateMessage(Block block) {
		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
		this.type = block.getMaterial().getId();
		this.data = block.getBlockData();
	}

	public BlockUpdateMessage(int x, int y, int z, short type, short data) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
		this.data = data;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public short getType() {
		return type;
	}

	public short getData() {
		return data;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("x", x)
				.append("y", y)
				.append("z", z)
				.append("type", type)
				.append("data", data)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(45, 95)
				.append(x)
				.append(y)
				.append(z)
				.append(type)
				.append(data)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BlockUpdateMessage) {
			final BlockUpdateMessage other = (BlockUpdateMessage) obj;
			return new EqualsBuilder()
					.append(x, other.x)
					.append(y, other.y)
					.append(z, other.z)
					.append(type, other.type)
					.append(data, other.data)
					.isEquals();
		} else {
			return false;
		}
	}
}
