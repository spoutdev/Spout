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
package org.spout.api.util.config.serialization;

import java.util.HashMap;
import java.util.Map;

public class MapSerializer extends Serializer {
	@Override
	protected Object handleDeserialize(GenericType type, Object value) {
		Map<?, ?> raw = (Map<?, ?>) value;
		Map<Object, Object> values = new HashMap<Object, Object>();
		for (Map.Entry<?, ?> entry : raw.entrySet()) {
			values.put(Serialization.deserialize(type.getGenerics()[0], entry.getKey()),
					Serialization.deserialize(type.getGenerics()[1], entry.getValue()));
		}
		return values;
	}

	@Override
	public boolean isApplicable(GenericType type, Object value) {
		return Map.class.equals(type.getMainType()) && value instanceof Map;
	}

	@Override
	public Object serialize(GenericType type, Object value) {
		Map<?, ?> raw = (Map<?, ?>) value;
		Map<Object, Object> values = new HashMap<Object, Object>();
		for (Map.Entry<?, ?> entry : raw.entrySet()) {
			values.put(Serialization.serialize(type.getGenerics()[0], entry.getKey()),
					Serialization.serialize(type.getGenerics()[1], entry.getValue()));
		}
		return values;
	}

	@Override
	protected int getParametersRequired() {
		return 2;
	}
}
