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

import java.io.IOException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.datatable.SerializableMap;
import org.spout.api.util.SpoutToStringStyle;

public abstract class DatatableMessage extends SpoutMessage {

	private final byte[] compressedData;

	public DatatableMessage(byte[] compressedData) {
		this.compressedData = compressedData;
	}

	public byte[] getCompressedData() {
		return compressedData;
	}

	public void decompressTo(SerializableMap data) throws IOException {
		data.deserialize(compressedData);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("compressedData", compressedData)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(27, 59)
				.append(compressedData)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DatatableMessage) {
			final DatatableMessage other = (DatatableMessage) obj;
			return new EqualsBuilder()
					.append(compressedData, other.compressedData)
					.isEquals();
		} else {
			return false;
		}
	}
}
