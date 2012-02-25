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

import java.util.ArrayList;
import java.util.List;
import org.spout.api.math.MathHelper;

public class ConfigurationNode {
	
	private final String path;
	private MemoryConfiguration config;
	private Object value;
	private Object def;

	public ConfigurationNode(String path, Object def) {
		this.path = path;
		this.def = def;
		this.value = def;
	}
	
	/**
	 * Gets the final path of the node.
	 * 
	 * @return 
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Gets the current value of the node.
	 * 
	 * @return the value of the node.
	 */
	public Object getValue() {
		if (value != null) {
			return value;
		}
		
		return def;
	}
	
	/**
	 * Sets the un-staged value of the node. The actually configuration will not be updated until 'MemoryConfiguration.setProperty()' 
	 * 
	 * @param value 
	 */
	public void setValue(Object value) {
		this.value = value;
		if (config != null && config.getValue(path) != value) {
			config.addNode(this);
		}
	}
	
	/**
	 * Sets the default value of the node.
	 * 
	 * @param default object
	 */
	public void setDefaultValue(Object def) {
		this.def = def;
		if (value == null) {
			this.value = def;
		}
	}
	
	/**
	 * Sets the configuration the node will be saved to.
	 * 
	 * @param config 
	 */
	public void setConfiguration(MemoryConfiguration config) {
		this.config = config;
		config.addNode(this);
	}
	
	/**
	 * Returns a string from the value, null if not a string.
	 * 
	 * @return string
	 */
	public String getString() {
		return getString(null);
	}
	
	/**
	 * Returns a string from the value, default value if not a string.
	 * 
	 * @return string
	 */
	public String getString(String def) {
		if (value instanceof String) {
			return (String) value;
		}
		
		return def;
	}
	
	/**
	 * Returns a integer from the value, null if not a integer.
	 * 
	 * @return integer
	 */
	public int getInteger() {
		return getInteger(0);
	}
	
	/**
	 * Returns a integer from the value, default value if not a integer.
	 * 
	 * @return integer
	 */
	public int getInteger(int def) {
		Integer i = MathHelper.castInt(value);
		if (i != null) {
			return i;
		}
		
		return def;
	}
	
	/**
	 * Returns a double from the value, null if not a double.
	 * 
	 * @return double
	 */
	public double getDouble() {
		return getDouble(0);
	}
	
	/**
	 * Returns a double from the value, default value if not a double.
	 * 
	 * @return double
	 */
	public double getDouble(double def) {
		Double d = MathHelper.castDouble(value);
		if (d != null) {
			return d;
		}
		
		return def;
	}
	
	/**
	 * Returns a boolean from the value, null if not a boolean.
	 * 
	 * @return boolean
	 */
	public boolean getBoolean() {
		return getBoolean(false);
	}
	
	/**
	 * Returns a boolean from the value, default value if not a boolean.
	 * 
	 * @return boolean
	 */
	public boolean getBoolean(boolean def) {
		Boolean b = MathHelper.castBoolean(value);
		if (b != null) {
			return b;
		}
		
		return def;
	}
	
	
	/**
	 * Returns a list from the value, null if not a list.
	 * 
	 * @return list
	 */
	public List<Object> getList() {
		return getList(null);
	}
	
	/**
	 * Returns a list from the value, default value if not a list.
	 * 
	 * @return list
	 */
	public List<Object> getList(List<Object> def) {
		if (value != null && value instanceof List) {
			return (List<Object>) value;
		}
		
		return def;
	}
	
	/**
	 * Returns a string list from the value, null if not a string list.
	 * 
	 * @return string list
	 */
	public List<String> getStringList() {
		return getStringList(null);
	}
	
	/**
	 * Returns a string list from the value, default value if not a string list.
	 * 
	 * @return string list
	 */
	public List<String> getStringList(List<String> def) {
		List<Object> raw = this.getList();
		if (raw != null) {
			List<String> list = new ArrayList<String>();
			for (Object obj : raw) {
				list.add(obj.toString());
			}
			
			return list;
		}
		return def;
	}
	
	/**
	 * Returns a integer list from the value, null if not a integer list.
	 * 
	 * @return integer list
	 */
	public List<Integer> getIntegerList() {
		return getIntegerList(null);
	}
	
	/**
	 * Returns a integer list from the value, default value if not a integer list.
	 * 
	 * @return integer list
	 */
	public List<Integer> getIntegerList(List<Integer> def) {
		List<Object> raw = this.getList();
		if (raw != null) {
			List<Integer> list = new ArrayList<Integer>();
			for (Object o : raw) {
				Integer i = MathHelper.castInt(o);
				if (i != null) {
					list.add(i);
				}
			}
			
			return list;
		}
		return def;
	}
	
	/**
	 * Returns a double list from the value, null if not a double list.
	 * 
	 * @return double list
	 */
	public List<Double> getDoubleList() {
		return getDoubleList(null);
	}
	
	/**
	 * Returns a double list from the value, default value if not a double list.
	 * 
	 * @return double list
	 */
	public List<Double> getDoubleList(List<Double> def) {
		List<Object> raw = this.getList();
		if (raw != null) {
			List<Double> list = new ArrayList<Double>();
			for (Object o : raw) {
				Double i = MathHelper.castDouble(o);
				if (i != null) {
					list.add(i);
				}
			}
			
			return list;
		}
		return def;
	}
	
	/**
	 * Returns a boolean list from the value, null if not a boolean list.
	 * 
	 * @return boolean list
	 */
	public List<Boolean> getBooleanList() {
		return getBooleanList(null);
	}
	
	/**
	 * Returns a boolean list from the value, default value if not a boolean list.
	 * 
	 * @return boolean list
	 */
	public List<Boolean> getBooleanList(List<Boolean> def) {
		List<Object> raw = this.getList();
		if (raw != null) {
			List<Boolean> list = new ArrayList<Boolean>();
			for (Object o : raw) {
				Boolean b = MathHelper.castBoolean(o);
				if (b != null) {
					list.add(b);
				}
			}
			
			return list;
		}
		return def;
	}
}