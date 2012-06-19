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

public class NumberSerializer extends Serializer {
	@Override
	protected Object handleDeserialize(GenericType type, Object rawVal) {
		Class<?> target = type.getMainType();
		Number value = (Number)rawVal;
		// Wrapper classes are evil!
		if (target.equals(Number.class)) {
			return value;
		} else if (target.equals(int.class) || target.equals(Integer.class)) {
			if (value instanceof Integer) {
				return value;
			} else {
				return value.intValue();
			}
		} else if (target.equals(byte.class) || target.equals(Byte.class)) {
			if (value instanceof Byte) {
				return value;
			} else {
				return value.byteValue();
			}
		} else if (target.equals(long.class) || target.equals(Long.class)) {
			if (value instanceof Long) {
				return value;
			} else {
				return value.longValue();
			}
		} else if (target.equals(double.class) || target.equals(Double.class)) {
			if (value instanceof Double) {
				return value;
			} else {
				return value.doubleValue();
			}
		} else if (target.equals(float.class) || target.equals(Float.class)) {
			if (value instanceof Float) {
				return value;
			} else {
				return value.floatValue();
			}
		} else if (target.equals(short.class) || target.equals(Short.class)) {
			if (value instanceof Short) {
				return value;
			} else {
				return value.shortValue();
			}
		}
		return null;
	}

	@Override
	public boolean isApplicable(GenericType type, Object value) {
		Class<?> target = type.getMainType();
		return target != null && (target.isPrimitive() || Number.class.isAssignableFrom(target)) && !boolean.class.isAssignableFrom(target);
	}

	@Override
	public int getParametersRequired() {
		return 0;
	}
}
