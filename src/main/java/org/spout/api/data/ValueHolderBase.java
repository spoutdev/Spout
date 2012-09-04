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
package org.spout.api.data;

import org.spout.api.math.MathHelper;
import org.spout.api.util.config.serialization.Serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ValueHolderBase implements ValueHolder {
	private final ValueHolder actualValue;

	public ValueHolderBase() {
		this(null);
	}

	public ValueHolderBase(ValueHolder actualValue) {
		this.actualValue = actualValue;
	}

	@Override
	public boolean getBoolean() {
		return getBoolean(false);
	}

	@Override
	public boolean getBoolean(boolean def) {
		final Boolean val = MathHelper.castBoolean(getValue(def));
		return val == null ? def : val;
	}

	@Override
	public byte getByte() {
		return getByte((byte) 0);
	}

	@Override
	public byte getByte(byte def) {
		final Byte val = MathHelper.castByte(getValue(def));
		return val == null ? def : val;
	}

	@Override
	public float getFloat() {
		return getFloat(0f);
	}

	@Override
	public float getFloat(float def) {
		final Float val = MathHelper.castFloat(getValue(def));
		return val == null ? def : val;
	}

	@Override
	public short getShort() {
		return getShort((short) 0);
	}

	@Override
	public short getShort(short def) {
		final Short val = MathHelper.castShort(getValue(def));
		return val == null ? def : val;
	}

	@Override
	public int getInt() {
		return getInt(0);
	}

	@Override
	public int getInt(int def) {
		final Integer val = MathHelper.castInt(getValue(def));
		return val == null ? def : val;
	}

	@Override
	public long getLong() {
		return getLong(0);
	}

	@Override
	public long getLong(long def) {
		final Long val = MathHelper.castLong(getValue(def));
		return val == null ? def : val;
	}

	@Override
	public double getDouble() {
		return getDouble(0);
	}

	@Override
	public double getDouble(double def) {
		final Double val = MathHelper.castDouble(getValue(def));
		return val == null ? def : val;
	}

	@Override
	public String getString() {
		return getString(null);
	}

	@Override
	public String getString(String def) {
		final Object val = getValue(def);
		return val == null ? def : val.toString();
	}

	@Override
	public Object getValue() {
		if (actualValue == null) {
			throw new UnsupportedOperationException("ValueHolderBase must have a reference to another ValueHolder or override getValue");
		}
		return actualValue.getValue();
	}

	@Override
	public Object getValue(Object def) {
		if (actualValue == null) {
			throw new UnsupportedOperationException("ValueHolderBase must have a reference to another ValueHolder or override getValue");
		}
		return actualValue.getValue(def);
	}

	@Override
	public <T> T getTypedValue(Class<T> type) {
		return getTypedValue(type, null);
	}

	@Override
	public <T> T getTypedValue(Class<T> type, T def) {
		final Object val = Serialization.deserialize(type, getValue());
		return type.isInstance(val) ? type.cast(val) : def;
	}

	@Override
	public Object getTypedValue(Type type) {
		return getTypedValue(type, null);
	}

	@Override
	public Object getTypedValue(Type type, Object def) {
		Object val = Serialization.deserialize(type, getValue());
		if (val == null) {
			val = def;
		}
		return val;
	}

	@Override
	public List<?> getList() {
		return getList(null);
	}

	@Override
	public List<?> getList(List<?> def) {
		Object val = getValue();
		if (val instanceof List<?>) {
			return (List<?>) val;
		} else if (val instanceof Collection<?>) {
			return new ArrayList<Object>((Collection<?>) val);
		} else {
			return def == null ? Collections.emptyList() : def;
		}
	}

	@Override
	public List<String> getStringList() {
		return getStringList(null);
	}

	@Override
	public List<String> getStringList(List<String> def) {
		List<?> val = getList(def);
		List<String> ret = new ArrayList<String>();
		for (Object item : val) {
			ret.add(item == null ? null : item.toString());
		}
		return ret;
	}

	@Override
	public List<Integer> getIntegerList() {
		return getIntegerList(null);
	}

	@Override
	public List<Integer> getIntegerList(List<Integer> def) {
		List<?> val = getList(def);
		List<Integer> ret = new ArrayList<Integer>();
		for (Object item : val) {
			Integer asInt = MathHelper.castInt(item);
			if (asInt == null) {
				return def == null ? Collections.<Integer>emptyList() : def;
			}
			ret.add(asInt);
		}
		return ret;
	}

	@Override
	public List<Double> getDoubleList() {
		return getDoubleList(null);
	}

	@Override
	public List<Double> getDoubleList(List<Double> def) {
		List<?> val = getList(def);
		List<Double> ret = new ArrayList<Double>();
		for (Object item : val) {
			Double asDouble = MathHelper.castDouble(item);
			if (asDouble == null) {
				return def == null ? Collections.<Double>emptyList() : def;
			}
			ret.add(asDouble);
		}
		return ret;
	}

	@Override
	public List<Boolean> getBooleanList() {
		return getBooleanList(null);
	}

	@Override
	public List<Boolean> getBooleanList(List<Boolean> def) {
		List<?> val = getList(def);
		List<Boolean> ret = new ArrayList<Boolean>();
		for (Object item : val) {
			Boolean asBoolean = MathHelper.castBoolean(item);
			if (asBoolean == null) {
				return def == null ? Collections.<Boolean>emptyList() : def;
			}
			ret.add(asBoolean);
		}
		return ret;
	}

	public static class NullHolder extends ValueHolderBase {
		@Override
		public Object getValue() {
			return null;
		}

		@Override
		public Object getValue(Object def) {
			return def;
		}
	}
}
