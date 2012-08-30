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
package org.spout.api.plugin;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.spout.api.datatable.DatatableTuple;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.datatable.value.DatatableBool;
import org.spout.api.datatable.value.DatatableFloat;
import org.spout.api.datatable.value.DatatableInt;
import org.spout.api.datatable.value.DatatableSerializable;
import org.spout.api.exception.InvalidDescriptionFileException;
import org.spout.api.util.config.serialization.Serialization;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

public class PluginDescriptionFile implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Yaml yaml = new Yaml(new SafeConstructor());
	public static final List<String> RESTRICTED_NAMES = Collections.unmodifiableList(Arrays.asList(
			"org.spout",
			"org.getspout",
			"org.spoutcraft",
			"in.spout"));

	private String name;
	private String version;
	private String description;
	private List<String> authors = new ArrayList<String>();
	private String website;
	private boolean reload;
	private Platform platform;
	private LoadOrder load;
	private String main;
	private List<String> depends;
	private List<String> softdepends;
	private String fullname;
	private final GenericDatatableMap datatableMap = new GenericDatatableMap();
	private Locale codedLocale = Locale.ENGLISH;

	public PluginDescriptionFile(String name, String version, String main, Platform platform) {
		this.name = name;
		this.version = version;
		this.main = main;
		this.platform = platform;
		fullname = name + " v" + version;
	}

	public PluginDescriptionFile(InputStream stream) throws InvalidDescriptionFileException {
		load((Map<?, ?>) yaml.load(stream));
	}

	public PluginDescriptionFile(Reader reader) throws InvalidDescriptionFileException {
		load((Map<?, ?>) yaml.load(reader));
	}

	public PluginDescriptionFile(String raw) throws InvalidDescriptionFileException {
		load((Map<?, ?>) yaml.load(raw));
	}

	@SuppressWarnings("unchecked")
	private void load(Map<?, ?> map) throws InvalidDescriptionFileException {
		name = getEntry("name", String.class, map);
		if (!name.matches("^[A-Za-z0-9 _.-]+$")) {
			throw new InvalidDescriptionFileException("The field 'name' in properties.yml contains invalid characters.");
		}
		if (name.toLowerCase().contains("spout")) {
			throw new InvalidDescriptionFileException("The plugin '" + name + "' has Spout in the name. This is not allowed.");
		}

		main = getEntry("main", String.class, map);
		if (!isOfficialPlugin(main)) {
			for (String namespace : RESTRICTED_NAMES) {
				if (main.startsWith(namespace)) {
					throw new InvalidDescriptionFileException("The use of the namespace '" + namespace + "' is not permitted.");
				}
			}
		}

		version = getEntry("version", String.class, map);
		platform = getEntry("platform", Platform.class, map);
		fullname = name + " v" + version;

		if (map.containsKey("author")) {
			authors.add(getEntry("author", String.class, map));
		}

		if (map.containsKey("authors")) {
			authors.addAll(getEntry("authors", List.class, map));
		}

		if (map.containsKey("depends")) {
			depends = getEntry("depends", List.class, map);
		}

		if (map.containsKey("softdepends")) {
			softdepends = getEntry("softdepends", List.class, map);
		}

		if (map.containsKey("description")) {
			description = getEntry("description", String.class, map);
		}

		if (map.containsKey("load")) {
			load = getEntry("load", LoadOrder.class, map);
		}

		if (map.containsKey("reload")) {
			reload = getEntry("reload", Boolean.class, map);
		}

		if (map.containsKey("website")) {
			website = getEntry("website", String.class, map);
		}
		
		if (map.containsKey("codedlocale")) {
			Locale[] locales = Locale.getAvailableLocales();
			for (Locale l:locales) {
				if (l.getLanguage().equals((new Locale((String) map.get("codedlocale"))).getLanguage())) {
					codedLocale = l;
				}
			}
		}

		if (map.containsKey("data")) {
			Map<?, ?> data = getEntry("data", Map.class, map);
			for (Map.Entry<?, ?> entry : data.entrySet()) {
				String key = entry.getKey().toString();
				if (entry.getValue() instanceof Boolean) {
					setData(key, ((Boolean) entry.getValue()).booleanValue());
				} else if (entry.getValue() instanceof Float || entry.getValue() instanceof Double) {
					setData(key, ((Number) entry.getValue()).floatValue());
				} else if (entry.getValue() instanceof Integer) {
					setData(key, ((Number) entry.getValue()).intValue());
				} else if (entry.getValue() instanceof Serializable) {
					setData(key, (Serializable) entry.getValue());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getEntry(Object key, Class<T> type, Map<?, ?> values) throws InvalidDescriptionFileException {
		Object value = values.get(key);
		if (value == null) {
			throw new InvalidDescriptionFileException("The field '" + key + "' is not present in the properties.yml!");
		}

		return (T) Serialization.deserialize(type, value);
	}

	/**
	 * Returns true if the plugin is an Official Spout Plugin
	 *
	 * @param namespace The plugin's main class namespace
	 * @return true if an official plugin
	 */
	private boolean isOfficialPlugin(String namespace) {
		return (namespace.equalsIgnoreCase("org.spout.vanilla.VanillaPlugin")
				|| namespace.equalsIgnoreCase("org.spout.bukkit.BukkitBridge")
				|| namespace.startsWith("org.spout.droplet"));
	}

	/**
	 * Returns the plugin's name
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the plugin's version
	 *
	 * @return version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Returns the plugin's description
	 *
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the plugin's authors
	 *
	 * @return authors
	 */
	public List<String> getAuthors() {
		return authors;
	}

	/**
	 * Returns the plugin's website
	 *
	 * @return website
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * Returns false if the plugin wants to be exempt from a reload
	 *
	 * @return reload
	 */
	public boolean allowsReload() {
		return reload;
	}

	/**
	 * Returns the plugin's platform
	 *
	 * @return platform
	 */
	public Platform getPlatform() {
		return platform;
	}

	/**
	 * Returns the plugin's load order
	 *
	 * @return load
	 */
	public LoadOrder getLoad() {
		return load;
	}

	/**
	 * Returns the path the plugins main class
	 *
	 * @return main
	 */
	public String getMain() {
		return main;
	}

	/**
	 * Returns the plugin's dependencies
	 *
	 * @return depends
	 */
	public List<String> getDepends() {
		return depends;
	}

	/**
	 * Returns the plugin's soft dependencies
	 *
	 * @return softdepends
	 */
	public List<String> getSoftDepends() {
		return softdepends;
	}

	/**
	 * Returns the plugin's fullname The fullname is formatted as follows:
	 * [name] v[version]
	 *
	 * @return The full name of the plugin
	 */
	public String getFullName() {
		return fullname;
	}
	
	/**
	 * Returns the locale the strings in the plugin are coded in.
	 * Will be read from the plugins properties.yml from the field "codedlocale"
	 * 
	 * @return the locale the plugin is coded in
	 */
	public Locale getCodedLocale() {
		return codedLocale;
	}

	public void setData(String key, int value) {
		int ikey = datatableMap.getIntKey(key);
		datatableMap.set(ikey, new DatatableInt(ikey, value));
	}

	public void setData(String key, float value) {
		int ikey = datatableMap.getIntKey(key);
		datatableMap.set(ikey, new DatatableFloat(ikey, value));
	}

	public void setData(String key, boolean value) {
		int ikey = datatableMap.getIntKey(key);
		datatableMap.set(ikey, new DatatableBool(ikey, value));
	}

	public void setData(String key, Serializable value) {
		int ikey = datatableMap.getIntKey(key);
		datatableMap.set(ikey, new DatatableSerializable(ikey, value));
	}

	public DatatableTuple getData(String key) {
		return datatableMap.get(key);
	}

	public boolean hasData(String key) {
		return datatableMap.contains(key);
	}
}
