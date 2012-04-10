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
package org.spout.api.util.config;

import org.apache.commons.lang3.ArrayUtils;
import org.spout.api.math.MathHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zml2008
 */
public class ConfigurationNodeBase extends ConfigurationNode {
	private Object value;

	public ConfigurationNodeBase(Configuration config, Object value, String[] path) {
		super(config, path);
		if (value != null) {
			setValue(value);
		}
	}

	@Override
	public boolean getBoolean(boolean def) {
		Boolean val = MathHelper.castBoolean(getValue(def));
		return val == null ? def : val;
	}

	@Override
	public int getInt(int def) {
		final Integer val = MathHelper.castInt(getValue(def));
		return val == null ? def : val;
	}

	@Override
	public long getLong(long def) {
		final Long val = MathHelper.castLong(getValue(def));
		return val == null ? def : val;
	}

	@Override
	public double getDouble(double def) {
		final Double val = MathHelper.castDouble(getValue(def));
		return val == null ? def : val;
	}

	@Override
	public String getString(String def) {
		final Object val = getValue(def);
		return val == null ? def : val.toString();
	}

	@Override
	public Object getValue(Object def) {
		if (hasChildren()) {
			return getValues();
		} else {
			if (def != null && value == null && getConfiguration().doesWriteDefaults()) {
				setValue(def);
			}
			return value == null ? def : value;
		}
	}

	@Override
	public <T> T getTypedValue(Class<T> type, T def) {
		final Object val = getValue();
		return type.isInstance(val) ? type.cast(val) : def;
	}

	@Override
	public ConfigurationNode addChild(ConfigurationNode node) {
		checkAdded();
		return super.addChild(node);
	}

	@Override
	public Object setValue(Object value) {
		checkAdded();
		Object old = this.getValue();
		if (value instanceof Map<?, ?>) {
			this.value = null;
			removeChildren();
			for (Map.Entry<?, ?> entry : ((Map<?, ?>)value).entrySet()) {
				addChild(createConfigurationNode(ArrayUtils.add(getPathElements(), entry.getKey().toString()), entry.getValue()));
			}
		} else {
			if (value != null) {
				removeChildren();
			}
			this.value = value;
		}
		return old;
	}

	private void removeChildren() {
		for (ConfigurationNode node : children.values()) {
			detachChild(node);
		}
		children.clear();
	}

	protected void checkAdded() {
		if (!isAttached()) {
			getConfiguration().setNode(this);
			setAttached(true);
		}
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
	public List<String> getStringList(List<String> def) {
		List<?> val = getList(def);
		List<String> ret = new ArrayList<String>();
		for (Object item : val) {
			ret.add(item.toString());
		}
		return ret;
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
}
