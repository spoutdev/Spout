/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.protocol.builtin.message;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.util.SpoutToStringStyle;

public class EntityDatatableMessage extends DatatableMessage {
	private final int entityId;

	public EntityDatatableMessage(int entityId, DatatableMap data) {
		super(data);
		this.entityId = entityId;
	}

	public EntityDatatableMessage(int entityId, byte[] data) {
		super(data);
		this.entityId = entityId;
	}

	public int getEntityId() {
		return entityId;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.appendSuper(super.toString())
				.append("entityId", entityId)
				.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(3, 7)
				.append(getCompressedData())
				.append(entityId)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EntityDatatableMessage) {
			final EntityDatatableMessage other = (EntityDatatableMessage) obj;
			return new EqualsBuilder()
					.append(getCompressedData(), other.getCompressedData())
					.append(entityId, other.entityId)
					.isEquals();
		} else {
			return false;
		}
	}
}
