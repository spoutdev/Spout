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
package org.spout.api.protocol.builtin.message;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.entity.component.controller.type.ControllerType;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.protocol.Message;
import org.spout.api.util.SpoutToStringStyle;

public class AddEntityMessage extends Message {
	private final int entityId;
	private final ControllerType type;
	private final Transform position;

	public AddEntityMessage(int entityId, ControllerType type, Transform position) {
		this.entityId = entityId;
		this.type = type;
		this.position = position;
	}

	public int getEntityId() {
		return entityId;
	}

	public ControllerType getType() {
		return type;
	}

	public Transform getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.append("entityId", entityId)
				.append("type", type)
				.append("position", position)
				.toString();
	}
}
