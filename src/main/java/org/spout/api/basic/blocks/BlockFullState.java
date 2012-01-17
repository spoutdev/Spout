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
package org.spout.api.basic.blocks;

public class BlockFullState<T> implements Cloneable {
	private short id;
	private short data;
	private T auxData;

	public BlockFullState() {
	}

	public BlockFullState(short id, short data, T auxData) {
		this.id = id;
		this.data = data;
		this.auxData = auxData;
	}

	public final short getId() {
		return id;
	}

	public final void setId(short id) {
		this.id = id;
	}

	public final short getData() {
		return data;
	}

	public final void setData(short data) {
		this.data = data;
	}

	public final T getAuxData() {
		return auxData;
	}

	public final void setAuxData(T auxData) {
		this.auxData = auxData;
	}

	public String toString() {
		return this.getClass().getSimpleName() + "{" + id + ", " + data + ", " + auxData + "}";
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof BlockFullState)) {
			return false;
		} else {
			@SuppressWarnings("unchecked")
			BlockFullState<T> fullState = (BlockFullState<T>)o;

			return (fullState.id == id && fullState.data == data && fullState.auxData.equals(auxData));
		}
	}

	/**
	 * Gets a shallow copy of the BlockFullState, the auxiliary data is not cloned.
	 *
	 * @return the shallow clone
	 */
	public BlockFullState<T> shallowClone() {
		return new BlockFullState<T>(id, data, auxData);
	}
}
