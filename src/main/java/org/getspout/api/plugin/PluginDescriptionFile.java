/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.api.plugin;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import org.getspout.api.plugin.Plugin.Mode;

public class PluginDescriptionFile {
	private static final Yaml yaml = new Yaml(new SafeConstructor());
	private String name = null;
	private String main = null;
	private PluginLoadOrder load = PluginLoadOrder.POSTWORLD;
	private ArrayList<String> depend = null;
	private ArrayList<String> softDepend = null;
	private String version = null;
	public Mode mode = null;
	private Object commands = null;
	private String description = null;
	private ArrayList<String> authors = new ArrayList<String>();
	private String website = null;

	@SuppressWarnings({"unchecked"})
	public PluginDescriptionFile(InputStream stream) throws InvalidDescriptionException {
		loadMap((Map<String, Object>) yaml.load(stream));
	}

	@SuppressWarnings({"unchecked"})
	public PluginDescriptionFile(Reader reader) throws InvalidDescriptionException {
		loadMap((Map<String, Object>) yaml.load(reader));
	}

	public PluginDescriptionFile(String addonName, String addonVersion, String mainClass) {
		name = addonName;
		version = addonVersion;
		main = mainClass;
	}

	public void save(Writer writer) {
		yaml.dump(saveMap(), writer);
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getFullName() {
		return name + " v" + version;
	}

	public String getMain() {
		return main;
	}

	public PluginLoadOrder getLoad() {
		return load;
	}

	public Object getCommands() {
		return commands;
	}

	public Object getDepend() {
		return depend;
	}

	public Object getSoftDepend() {
		return softDepend;
	}

	public String getDescription() {
		return description;
	}

	public ArrayList<String> getAuthors() {
		return authors;
	}

	public String getWebsite() {
		return website;
	}

	@SuppressWarnings("unchecked")
	private void loadMap(Map<String, Object> map) throws InvalidDescriptionException {
		try {
			name = map.get("name").toString();

			if (!name.matches("^[A-Za-z0-9 _.-]+$")) {
				throw new InvalidDescriptionException("name '" + name + "' contains invalid characters.");
			}
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "name is not defined");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "name is of wrong type");
		}

		try {
			version = map.get("version").toString();
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "version is not defined");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "version is of wrong type");
		}

		try {
			main = map.get("main").toString();
			if (main.startsWith("org.bukkit.")) {
				throw new InvalidDescriptionException("main may not be within the org.bukkit namespace");
			}
			if (main.startsWith("org.getspout.")) {
				throw new InvalidDescriptionException("main may not be within the org.getspout namespace");
			}
			if (main.startsWith("org.spoutcraft.")) {
				throw new InvalidDescriptionException("main may not be within the org.spoutcraft namespace");
			}
			if (main.startsWith("in.spout.")) {
				throw new InvalidDescriptionException("main may not be within the in.spout namespace");
			}
			if (main.startsWith("net.minecraft.")) {
				throw new InvalidDescriptionException("main may not be within the net.minecraft namespace");
			}
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "main is not defined");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "main is of wrong type");
		}

		try {
			String mode = map.get("mode").toString();

			this.mode = Mode.valueOf(mode);

			if (this.mode == null) {
				if (map.containsKey("mode")) {
					throw new InvalidDescriptionException(null, "mode is of wrong type");
				} else {
					throw new InvalidDescriptionException("mode is not defined");
				}
			}

		} catch (NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "mode is not defined");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "mode is of wrong type");
		}

		if (map.containsKey("load")) {
			try {
				commands = map.get("load");
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "load is of wrong type");
			}
		}

		if (map.containsKey("commands")) {
			try {
				commands = map.get("commands");
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "commands are of wrong type");
			}
		}

		if (map.containsKey("depend")) {
			try {
				depend = (ArrayList<String>) map.get("depend");
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "depend is of wrong type");
			}
		}

		if (map.containsKey("softdepend")) {
			try {
				softDepend = (ArrayList<String>) map.get("softdepend");
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "softdepend is of wrong type");
			}
		}

		if (map.containsKey("website")) {
			try {
				website = (String) map.get("website");
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "website is of wrong type");
			}
		}

		if (map.containsKey("description")) {
			try {
				description = (String) map.get("description");
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "description is of wrong type");
			}
		}

		if (map.containsKey("author")) {
			try {
				String extra = (String) map.get("author");

				authors.add(extra);
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "author is of wrong type");
			}
		}

		if (map.containsKey("authors")) {
			try {
				ArrayList<String> extra = (ArrayList<String>) map.get("authors");

				authors.addAll(extra);
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "authors are of wrong type");
			}
		}
	}

	private Map<String, Object> saveMap() {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("name", name);
		map.put("main", main);
		map.put("version", version);

		if (commands != null) {
			map.put("command", commands);
		}
		if (depend != null) {
			map.put("depend", depend);
		}
		if (softDepend != null) {
			map.put("softdepend", softDepend);
		}
		if (website != null) {
			map.put("website", website);
		}
		if (description != null) {
			map.put("description", description);
		}
		if (authors.size() == 1) {
			map.put("author", authors.get(0));
		} else if (authors.size() > 1) {
			map.put("authors", authors);
		}
		return map;
	}

}
