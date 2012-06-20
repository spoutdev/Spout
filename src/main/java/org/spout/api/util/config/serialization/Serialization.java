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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Serialization {
	private static final Map<Type, Serializer> CACHED_SERIALIZERS = new HashMap<Type, Serializer>();
	private static final List<Serializer> SERIALIZERS = new ArrayList<Serializer>(
			Arrays.asList(new SameSerializer(),
					new StringSerializer(),
					new BooleanSerializer(),
					new NumberSerializer(),
					new EnumSerializer(),
					new ConfigurationBaseSerializer(),
					new SetSerializer(),
					new ListSerializer(),
					new MapSerializer()
			));

	public static Object deserialize(Type target, Object value) {
		return deserialize(new GenericType(target), value);
	}

	public static Object deserialize(GenericType type, Object value) {
		if (value == null) {
			return null;
		}

		Object ret = null;

		for (Serializer serializer : SERIALIZERS) {
			if ((ret = serializer.deserialize(type, value)) != null) {
				CACHED_SERIALIZERS.put(type.getRawType(), serializer);
				break;
			}
		}

		return ret;
	}

	public static Object serialize(GenericType type, Object obj) {
		Serializer serializer = CACHED_SERIALIZERS.get(type.getRawType());
		if (serializer != null) {
			return serializer.serialize(type, obj);
		}
		return obj;
	}

	public static Object serialize(Type type, Object obj) {
		Serializer serializer = CACHED_SERIALIZERS.get(type);
		if (serializer != null) {
			return serializer.serialize(new GenericType(type), obj);
		}
		return obj;
	}

	public static void registerSerializer(Serializer serializer) {
		SERIALIZERS.add(serializer);
	}
}
