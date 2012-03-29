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
package org.spout.api.data;

/**
 * An Object wrapper to provide an extra level of abstraction to DataSubjects.
 */
public class DataValue {
	private final Object value;
	
	public DataValue(Object value) {
		this.value = value;
	}

	/**
	 * Returns the raw value initialized during instantiation.
	 *
	 * @return raw value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns a integer from the parsed value, default value if no integer value.
	 *
	 * @param def
	 * @return parsed value
	 */
	public int toInteger(int def) {
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		
		return def;
	}

	/**
	 * Returns a integer from the parsed value, -1 if no integer value.
	 *
	 * @return parsed value
	 */
	public int toInteger() {
		return toInteger(-1);
	}

	/**
	 * Returns a long from the parsed value, default value if no long value.
	 *
	 * @param def
	 * @return parsed value
	 */
	public long toLong(long def) {
		if (value instanceof Number) {
			return ((Number) value).longValue();
		}

		return def;
	}

	/**
	 * Returns a long from the parsed value, -1 if no long value.
	 *
	 * @return parsed value
	 */
	public long toLong() {
		return toLong(-1);
	}

	/**
	 * Returns a float from the parsed value, default value if no float value.
	 *
	 * @param def
	 * @return parsed value
	 */
	public float toFloat(float def) {
		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}

		return def;
	}

	/**
	 * Returns a float from the parsed value, -1 if no float value.
	 *
	 * @return parsed value
	 */
	public float toFloat() {
		return toFloat(-1);
	}

	/**
	 * Returns a double from the parsed value, default value if no double value.
	 *
	 * @param def
	 * @return parsed value
	 */
	public double toDouble(double def) {
		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		return def;
	}

	/**
	 * Returns a double from the parsed value, -1 if no double value.
	 *
	 * @return parsed value
	 */
	public double toDouble() {
		return toDouble(-1);
	}

	/**
	 * Returns a boolean from the parsed value, default value if no boolean value.
	 *
	 * @param def
	 * @return parsed value
	 */
	public boolean toBoolean(boolean def) {
		if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		}

		return def;
	}

	/**
	 * Returns a boolean from the parsed value, false if no boolean value.
	 *
	 * @return
	 */
	public boolean toBoolean() {
		return toBoolean(false);
	}

	/**
	 * Returns a string from the parsed value, default value if no string value.
	 *
	 * @param def
	 * @return parsed value
	 */
	public String toString(String def) {
		if (value instanceof String) {
			return ((String) value);
		}
		
		return def;
	}

	@Override
	public String toString() {
		return toString(null);
	}
}
