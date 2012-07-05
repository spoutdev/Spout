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

import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.geo.World;
import org.spout.api.util.SpoutToStringStyle;

/**
 * @author zml2008
 */
public class WorldChangeMessage extends DatatableMessage {
	private final UUID worldUuid;
	public WorldChangeMessage(World world, DatatableMap data) {
		super(data);
		this.worldUuid = world.getUID();
	}

	public WorldChangeMessage(UUID worldUuid, byte[] compressedData) {
		super(compressedData);
		this.worldUuid = worldUuid;
	}

	public UUID getWorldUUID() {
		return worldUuid;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, SpoutToStringStyle.INSTANCE)
				.appendSuper(super.toString())
				.append("worldUuid", worldUuid)
				.toString();
	}
}
