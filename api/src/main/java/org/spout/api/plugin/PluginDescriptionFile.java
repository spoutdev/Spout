/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.plugin;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.spout.api.Platform;
import org.spout.api.Spout;
import org.spout.api.exception.InvalidDescriptionFileException;
import org.spout.api.util.string.DamerauLevenshteinAlgorithm;
import org.spout.cereal.config.ConfigurationException;
import org.spout.cereal.config.ConfigurationNode;
import org.spout.cereal.config.yaml.YamlConfiguration;

public class PluginDescriptionFile {
	public static final List<String> RESTRICTED_NAMES = Collections.unmodifiableList(Arrays.asList(
			"org.spout",
			"org.getspout",
			"org.spoutcraft",
			"in.spout"));
	private static final DamerauLevenshteinAlgorithm dla = new DamerauLevenshteinAlgorithm(1, 1, 1, 1);
	private final ConfigurationProperty<Map<String, String>> data = new DataProperty("data");
	private final ConfigurationProperty<String> name = new BasicProperty<>("name", String.class);
	private final ConfigurationProperty<String> version = new BasicProperty<>("version", String.class);
	private final ConfigurationProperty<String> description = new BasicProperty<>("description", String.class);
	private final ConfigurationProperty<List<String>> authors = new RegexListProperty("author", "author(s)?", true);
	private final ConfigurationProperty<String> website = new BasicProperty<>("website", null, String.class);
	private final ConfigurationProperty<Boolean> reload = new BasicProperty<>("reload", false, Boolean.class);
	private final ConfigurationProperty<String> platform = new BasicProperty<>("platform", String.class);
	private final ConfigurationProperty<LoadOrder> load = new BasicProperty<>("load", LoadOrder.POSTWORLD, LoadOrder.class);
	private final ConfigurationProperty<String> main = new BasicProperty<>("main", String.class);
	private final ConfigurationProperty<List<String>> depends = new RegexListProperty("depends", "depend(s)?", false);
	private final ConfigurationProperty<List<String>> softdepends = new RegexListProperty("softdepends", "softdepend(s)?", false);
	private final ConfigurationProperty<Locale> codedLocale = new LocaleProperty("codedlocale", Locale.ENGLISH);
	private final ConfigurationProperty<Map<String, String>> components = new DataProperty("components");
	private final ConfigurationProperty<List<String>> protocols = new RegexListProperty("protools", "protocol(s)?", false);

	public PluginDescriptionFile(String name, String version, String main, String platform) {
		this.name.setValue(name);
		this.version.setValue(version);
		this.main.setValue(main);
		this.platform.setValue(platform);
	}

	public PluginDescriptionFile(InputStream stream) throws InvalidDescriptionFileException {
		this(new YamlConfiguration(stream));
	}

	public PluginDescriptionFile(Reader reader) throws InvalidDescriptionFileException {
		this(new YamlConfiguration(reader));
	}

	public PluginDescriptionFile(String raw) throws InvalidDescriptionFileException {
		this(new YamlConfiguration(raw));
	}

	private PluginDescriptionFile(YamlConfiguration yaml) throws InvalidDescriptionFileException {
		try {
			yaml.load();
		} catch (ConfigurationException e) {
			throw new InvalidDescriptionFileException(e);
		}
		load(yaml);
	}

	private List<ConfigurationProperty<?>> getConfigurationProperties() {
		Field[] fields = PluginDescriptionFile.class.getDeclaredFields();
		List<ConfigurationProperty<?>> properties = new ArrayList<>();
		for (Field f : fields) {
			if (!Modifier.isStatic(f.getModifiers())) {
				if (f.getType().isAssignableFrom(ConfigurationProperty.class)) {
					f.setAccessible(true);
					try {
						properties.add((ConfigurationProperty<?>) f.get(this));
					} catch (			IllegalArgumentException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return Collections.unmodifiableList(properties);
	}

	private void load(YamlConfiguration yaml) throws InvalidDescriptionFileException {
		Map<String, ConfigurationNode> children = yaml.getChildren();
		List<ConfigurationProperty<?>> properties = getConfigurationProperties();
		List<String> unmatchedProperties = new LinkedList<>();
		for (Entry<String, ConfigurationNode> e : children.entrySet()) {
			final String entry = e.getKey();

			boolean success = false;
			for (ConfigurationProperty<?> property : properties) {
				if (property.matches(entry)) {
					property.setValue(entry, e.getValue());
					success = true;
					break;
				}
			}

			if (!success) {
				unmatchedProperties.add(entry);
			}
		}

		//Check for required properties
		for (ConfigurationProperty<?> property : properties) {
			if (property.isRequired()) {
				if (property.getValue() == null) {
					throw new InvalidDescriptionFileException("The field '" + property.name() + "' is not present in the properties.yml!");
				}
			}
		}

		//Validate name
		if (!name.getValue().matches("^[A-Za-z0-9 _.-]+$")) {
			throw new InvalidDescriptionFileException("The field 'name' in properties.yml contains invalid characters.");
		}
		if (name.getValue().toLowerCase().contains("spout")) {
			throw new InvalidDescriptionFileException("The plugin '" + name + "' has Spout in the name. This is not allowed.");
		}

		//Validate main
		if (!isOfficialPlugin(main.getValue())) {
			for (String namespace : RESTRICTED_NAMES) {
				if (main.getValue().startsWith(namespace)) {
					throw new InvalidDescriptionFileException("The use of the namespace '" + namespace + "' is not permitted.");
				}
			}
		}

		//Try and be helpful, check if they misspelled an unmatched property
		if (Spout.getLogger() != null) {
			for (String key : unmatchedProperties) {
				for (ConfigurationProperty<?> property : properties) {
					if (!property.beenUpdated()) {
						if (dla.execute(key, property.name()) < 4) {
							Spout.getLogger().info("Unused plugin.yml for " + name.getValue() + ", property [" + key + "]. Did you mean [" + property.name() + "]?");
						}
					}
				}
			}
		}
	}

	/**
	 * Returns true if the plugin is an Official Spout Plugin
	 *
	 * @param namespace The plugin's main class namespace
	 * @return true if an official plugin
	 */
	private boolean isOfficialPlugin(String namespace) {
		return (namespace.equalsIgnoreCase("org.spout.vanilla.VanillaPlugin")
				|| namespace.equalsIgnoreCase("org.spout.bridge.VanillaBridgePlugin")
				|| namespace.equalsIgnoreCase("org.spout.infobjects.InfObjectsPlugin")
				|| namespace.startsWith("org.spout.droplet"));
	}

	/**
	 * Returns the plugin's name
	 *
	 * @return name
	 */
	public String getName() {
		return name.getValue();
	}

	/**
	 * Returns the plugin's version
	 *
	 * @return version
	 */
	public String getVersion() {
		return version.getValue();
	}

	/**
	 * Returns the plugin's description
	 *
	 * @return description
	 */
	public String getDescription() {
		return description.getValue();
	}

	/**
	 * Returns the plugin's authors
	 *
	 * @return authors
	 */
	public List<String> getAuthors() {
		return authors.getValue();
	}

	/**
	 * Returns the plugin's website
	 *
	 * @return website
	 */
	public String getWebsite() {
		return website.getValue();
	}

	/**
	 * Returns false if the plugin wants to be exempt from a reload
	 *
	 * @return reload
	 */
	public boolean allowsReload() {
		return reload.getValue();
	}

	/**
	 * Returns the plugin's platform
	 *
	 * @return platform
	 */
	public boolean isValidPlatform(Platform platform) {
		if (platform.name().equalsIgnoreCase(this.platform.getValue())) {
			return true;
		}
		if (this.platform.getValue().equalsIgnoreCase("all") || this.platform.getValue().equalsIgnoreCase("both")) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the plugin's load order
	 *
	 * @return load
	 */
	public LoadOrder getLoad() {
		return load.getValue();
	}

	/**
	 * Returns the path the plugins main class
	 *
	 * @return main
	 */
	public String getMain() {
		return main.getValue();
	}

	/**
	 * Returns the plugin's dependencies
	 *
	 * @return depends
	 */
	public List<String> getDepends() {
		return depends.getValue();
	}

	/**
	 * Returns the plugin's soft dependencies
	 *
	 * @return softdepends
	 */
	public List<String> getSoftDepends() {
		return softdepends.getValue();
	}

	/**
	 * Returns the plugin's fullname The fullname is formatted as follows: [name] v[version]
	 *
	 * @return The full name of the plugin
	 */
	public String getFullName() {
		return getName() + " v" + getVersion();
	}

	/**
	 * Returns the locale the strings in the plugin are coded in. Will be read from the plugins properties.yml from the field "codedlocale"
	 *
	 * @return the locale the plugin is coded in
	 */
	public Locale getCodedLocale() {
		return codedLocale.getValue();
	}

	public String getData(String key) {
		return data.getValue().get(key);
	}

	public Map<String, String> getComponentRemapping() {
		return Collections.unmodifiableMap(components.getValue());
	}

	public List<String> getProtocols() {
		return Collections.unmodifiableList(protocols.getValue());
	}

	private static interface ConfigurationProperty<T> {
		public String name();

		public boolean matches(String key);

		public T getValue();

		public void setValue(T value);

		public void setValue(String key, ConfigurationNode node);

		public boolean isRequired();

		public boolean beenUpdated();
	}

	private static abstract class AbstractProperty<T> implements ConfigurationProperty<T> {
		private final String name;
		private T value;
		private boolean updated = false;

		public AbstractProperty(String name, T def) {
			this.name = name;
			this.value = def;
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public boolean matches(String key) {
			return name.equals(key);
		}

		@Override
		public T getValue() {
			return value;
		}

		@Override
		public void setValue(T value) {
			this.value = value;
			this.updated = true;
		}

		@Override
		public boolean isRequired() {
			return false;
		}

		@Override
		public boolean beenUpdated() {
			return updated;
		}
	}

	private static class BasicProperty<T> extends AbstractProperty<T> implements ConfigurationProperty<T> {
		private final Class<T> clazz;
		private final boolean required;

		public BasicProperty(String name, Class<T> clazz) {
			super(name, null);
			this.clazz = clazz;
			this.required = true;
		}

		public BasicProperty(String name, T def, Class<T> clazz) {
			super(name, def);
			this.clazz = clazz;
			this.required = false;
		}

		@Override
		public void setValue(String key, ConfigurationNode node) {
			super.setValue((T) node.getTypedValue(clazz));
		}

		@Override
		public boolean isRequired() {
			return required;
		}
	}

	private static class RegexListProperty extends AbstractProperty<List<String>> {
		private final boolean required;
		private final Pattern pattern;

		public RegexListProperty(String name, String regex, boolean required) {
			super(name, new ArrayList<String>());
			this.pattern = Pattern.compile(regex);
			this.required = required;
		}

		@Override
		public boolean matches(String key) {
			return pattern.matcher(key).matches();
		}

		@SuppressWarnings ({"unchecked", "rawtypes"})
		@Override
		public void setValue(String key, ConfigurationNode node) {
			List list = node.getTypedValue(List.class);
			if (list != null) {
				((List) this.getValue()).addAll(list);
			} else {
				((List) this.getValue()).add(node.getTypedValue(String.class));
			}
		}

		@Override
		public boolean isRequired() {
			return required;
		}
	}

	private static class LocaleProperty extends AbstractProperty<Locale> {
		public LocaleProperty(String name, Locale def) {
			super(name, def);
		}

		@Override
		public void setValue(String key, ConfigurationNode node) {
			Locale[] locales = Locale.getAvailableLocales();
			final Locale locale = new Locale(node.getString());
			for (Locale l : locales) {
				if (l.getLanguage().equals(locale.getLanguage())) {
					setValue(l);
				}
			}
		}
	}

	private static class DataProperty extends AbstractProperty<Map<String, String>> {
		public DataProperty(String name) {
			super(name, new HashMap<String, String>());
		}

		@Override
		public void setValue(String k, ConfigurationNode node) {
			Map<String, ConfigurationNode> data = node.getChildren();
			for (Map.Entry<String, ConfigurationNode> entry : data.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue().getString();
				this.getValue().put(key, value);
			}
		}
	}
}
