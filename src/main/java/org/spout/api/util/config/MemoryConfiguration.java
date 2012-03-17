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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.spout.api.math.MathHelper;

/**
 * Memory configuration used for memory storage of nodes through the 'root' map; provides options for the configuration.
 */
public class MemoryConfiguration {
	
	protected String pathSeperator = ".";
	protected Map<String, Object> root;
	protected Set<ConfigurationNode> nodes;
	
	/**
	 * Constructs a new configuration in memory.
	 * 
	 * @param root 
	 */
	public MemoryConfiguration(Map<String, Object> root, Set<ConfigurationNode> nodes) {
		this.root = root;
		this.nodes = nodes;
	}
	
	/**
	 * Gets the value at a given path
	 * 
	 * @param path
	 * @return the object from the path
	 */
	public Object getValue(String path) {
		if (!path.contains(pathSeperator)) {
			Object val = root.get(path);
			if (val == null) {
				return null;
			}
			return val;
		}

		String[] parts = path.split(pathSeperator);
		Map<String, Object> node = root;

		for (int i = 0; i < parts.length; i++) {
			Object o = node.get(parts[i]);

			if (o == null) {
				return null;
			}

			if (i == parts.length - 1) {
				return o;
			}

			try {
				node = (Map<String, Object>) o;
			} catch (ClassCastException e) {
				return null;
			}
		}

		return null;
	}
	
	/**
	 * Sets the object value at the given path.
	 * 
	 * @param path
	 * @param value 
	 */
	public void setValue(String path, Object value) {
		if (!path.contains(pathSeperator)) {
			root.put(path, value);
			return;
		}

		String[] parts = path.split(pathSeperator);
		Map<String, Object> node = root;

		for (int i = 0; i < parts.length; i++) {
			Object o = node.get(parts[i]);

			// Found our target!
			if (i == parts.length - 1) {
				node.put(parts[i], value);
				return;
			}

			if (o == null || !(o instanceof Map)) {
				// This will override existing configuration data!
				o = new HashMap<String, Object>();
				node.put(parts[i], o);
			}

			node = (Map<String, Object>) o;
		}
	}
	
	public void setPathSeperator(String pathSeperator) {
		this.pathSeperator = pathSeperator;
	}
	
	public String getPathSeperator(String pathSeperator) {
		return pathSeperator;
	}
	
	public Set<ConfigurationNode> getNodes() {
		return nodes;
	}
	
	/**
	 * Removes a node from memory.
	 * 
	 * @param path 
	 */
	public void removeNode(String path) {
		for (ConfigurationNode node :  nodes) {
			if (node.getPath().equalsIgnoreCase(path)) {
				nodes.remove(node);
			}
		}
	}
	
	/**
	 * Sets a node's state for this configuration.
	 * 
	 * @param node 
	 */
	public void addNode(ConfigurationNode node) {
		Object value = this.getValue(node.getPath());
		if (value == null) {
			this.setValue(node.getPath(), node.getValue());
		} else {
			node.setValue(value, false);
		}
		
		nodes.add(node);
		node.config = this;
	}
	
	/**
	 * Adds a node to the configuration given the path and value.
	 * 
	 * @param path
	 * @param value
	 * @return new node
	 */
	public ConfigurationNode addNode(String path, Object value) {
		ConfigurationNode node = new ConfigurationNode(path, value);
		node.setConfiguration(this);
		return node;
	}
	
	/**
	 * Adds multiple nodes to the configuration.
	 * 
	 * @param nodes 
	 */
	public void addNodes(ConfigurationNode... nodes) {
		for (ConfigurationNode node : nodes) {
			node.setConfiguration(this);
		}
	}
	
	/**
	 * Gets a node from the configuration, if the path is found, returns a node with the value of the path. 
	 * If it's null, it returns the default value given.
	 * 
	 * @param path
	 * @param def
	 * @return 
	 */
	public ConfigurationNode getNode(String path, Object def) {
		ConfigurationNode node = new ConfigurationNode(path, def);
		Object value = this.getValue(node.getPath());
		if (value == null) {
			value = def;
		}
		
		node.setValue(value, true);
		node.setConfiguration(this);
		return node;
	}
	
	/**
	 * Returns a string from the value, null if not a string.
	 * 
	 * @return string
	 */
	public String getString(String path) {
		return getString(path, null);
	}
	
	/**
	 * Returns a string from the value, default value if not a string.
	 * 
	 * @return string
	 */
	public String getString(String path, String def) {
		Object value = getValue(path);
		if (value instanceof String) {
			return (String) value;
		}

		if(value == null) {
			this.setValue(path, def);
		}
		
		return def;
	}
	
	/**
	 * Returns a integer from the value, null if not a integer.
	 * 
	 * @return integer
	 */
	public int getInteger(String path) {
		return getInteger(path, 0);
	}
	
	/**
	 * Returns a integer from the value, default value if not a integer.
	 * 
	 * @return integer
	 */
	public int getInteger(String path, int def) {
		Object value = getValue(path);
		Integer i = MathHelper.castInt(value);
		if (i != null) {
			return i;
		}

		if(value == null) {
			this.setValue(path, def);
		}
		
		return def;
	}
	
	/**
	 * Returns a double from the value, null if not a double.
	 * 
	 * @return double
	 */
	public double getDouble(String path) {
		return getDouble(path, 0);
	}
	
	/**
	 * Returns a double from the value, default value if not a double.
	 * 
	 * @return double
	 */
	public double getDouble(String path, double def) {
		Object value = getValue(path);
		Double d = MathHelper.castDouble(value);
		if (d != null) {
			return d;
		}

		if(value == null) {
			this.setValue(path, def);
		}
		
		return def;
	}
	
	/**
	 * Returns a boolean from the value, null if not a boolean.
	 * 
	 * @return boolean
	 */
	public boolean getBoolean(String path) {
		return getBoolean(path, false);
	}
	
	/**
	 * Returns a boolean from the value, default value if not a boolean.
	 * 
	 * @return boolean
	 */
	public boolean getBoolean(String path, boolean def) {
		Object value = getValue(path);
		Boolean b = MathHelper.castBoolean(value);
		if (b != null) {
			return b;
		}

		if(value == null) {
			this.setValue(path, def);
		}
		
		return def;
	}
	
	
	/**
	 * Returns a list from the value, null if not a list.
	 * 
	 * @return list
	 */
	public List<Object> getList(String path) {
		return getList(path, null);
	}
	
	/**
	 * Returns a list from the value, default value if not a list.
	 * 
	 * @return list
	 */
	public List<Object> getList(String path, List<Object> def) {
		Object value = getValue(path);
		if (value != null && value instanceof List) {
			return (List<Object>) value;
		}

		if(value == null) {
			this.setValue(path, def);
		}
		
		return def;
	}
	
	/**
	 * Returns a string list from the value, null if not a string list.
	 * 
	 * @return string list
	 */
	public List<String> getStringList(String path) {
		return getStringList(path, null);
	}
	
	/**
	 * Returns a string list from the value, default value if not a string list.
	 * 
	 * @return string list
	 */
	public List<String> getStringList(String path, List<String> def) {
		List<Object> raw = this.getList(path);
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
	public List<Integer> getIntegerList(String path) {
		return getIntegerList(path, null);
	}
	
	/**
	 * Returns a integer list from the value, default value if not a integer list.
	 * 
	 * @return integer list
	 */
	public List<Integer> getIntegerList(String path, List<Integer> def) {
		List<Object> raw = this.getList(path);
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
	public List<Double> getDoubleList(String path) {
		return getDoubleList(path, null);
	}
	
	/**
	 * Returns a double list from the value, default value if not a double list.
	 * 
	 * @return double list
	 */
	public List<Double> getDoubleList(String path, List<Double> def) {
		List<Object> raw = this.getList(path);
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
	public List<Boolean> getBooleanList(String path) {
		return getBooleanList(path, null);
	}
	
	/**
	 * Returns a boolean list from the value, default value if not a boolean list.
	 * 
	 * @return boolean list
	 */
	public List<Boolean> getBooleanList(String path, List<Boolean> def) {
		List<Object> raw = this.getList(path);
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
	/**
	 * Returns a Set of the keys under that parent
	 * @param path
	 * @return Set String
	 */
	public Set<String> getKeys(String path) {
		//TODO Add "deep" option in the future potentially.
		Set<String> keys = new HashSet<String>();
		String[] sections = path.split(pathSeperator);
		Map<String, Object> section = this.root;
		for(int i = 0; i < sections.length && section != null; i++) {
			String sec = sections[i];
			try {
				section = (Map<String, Object>) section.get(sec);
			} catch (Exception e) {
				System.err.println("[MemoryConfiguration] Invalid path!");
				e.printStackTrace();
			}
		}
		// Only if it's not null
		if(section != null) {
			keys = section.keySet();
		}
		return keys;
	}
}
