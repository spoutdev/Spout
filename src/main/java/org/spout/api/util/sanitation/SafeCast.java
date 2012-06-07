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
package org.spout.api.util.sanitation;

public class SafeCast {
	public static long toLong(Object o, long def) {
		if (o instanceof Long) {
			return (Long)o;
		} else {
			return def;
		}
	}
	
	public static int toInt(Object o, int def) {
		if (o instanceof Integer) {
			return (Integer)o;
		} else {
			return def;
		}
	}
	
	public static byte toByte(Object o, byte def) {
		if (o instanceof Byte) {
			return (Byte)o;
		} else {
			return def;
		}
	}
	
	public static float toFloat(Object o, float def) {
		if (o instanceof Float) {
			return (Float)o;
		} else {
			return def;
		}
	}
	
	public static byte[] toByteArray(Object o, byte[] def) {
		if (o instanceof byte[]) {
			return (byte[])o;
		} else {
			return def;
		}
	}
	
	public static short[] toShortArray(Object o, short[] def) {
		if (o instanceof short[]) {
			return (short[])o;
		} else {
			return def;
		}
	}
	
	public static String toString(Object o, String def) {
		if (o instanceof String) {
			return (String)o;
		} else {
			return def;
		}
	}

	public static <T, U extends T> T toGeneric(Object o, U def, Class<T> clazz) {
		if (o == null) {
			return def;
		}

		try {
			return clazz.cast(o);
		} catch (ClassCastException e) {
			return def;
		}
	}
}
