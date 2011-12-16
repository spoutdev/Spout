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

import org.getspout.api.plugin.InvalidDescriptionException;
import org.getspout.api.plugin.PluginLoadOrder;
import org.getspout.api.plugin.Plugin.Mode;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

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

	@SuppressWarnings({ "unchecked" })
	public PluginDescriptionFile(InputStream stream) throws InvalidDescriptionException {
		loadMap((Map<String, Object>) yaml.load(stream));
	}

	@SuppressWarnings({ "unchecked" })
	public PluginDescriptionFile(Reader reader) throws InvalidDescriptionException {
		loadMap((Map<String, Object>) yaml.load(reader));
	}

	public PluginDescriptionFile(String addonName, String addonVersion, String mainClass) {
		this.name = addonName;
		this.version = addonVersion;
		this.main = mainClass;
	}

	public void save(Writer writer) {
		yaml.dump(saveMap(), writer);
	}

	public String getName() {
		return this.name;
	}

	public String getVersion() {
		return this.version;
	}

	public String getFullName() {
		return this.name + " v" + this.version;
	}

	public String getMain() {
		return this.main;
	}
	
	public PluginLoadOrder getLoad() {
		return this.load;
	}

	public Object getCommands() {
		return this.commands;
	}

	public Object getDepend() {
		return this.depend;
	}

	public Object getSoftDepend() {
		return this.softDepend;
	}

	public String getDescription() {
		return this.description;
	}

	public ArrayList<String> getAuthors() {
		return this.authors;
	}

	public String getWebsite() {
		return this.website;
	}

	@SuppressWarnings("unchecked")
	private void loadMap(Map<String, Object> map) throws InvalidDescriptionException {
		try {
			this.name = map.get("name").toString();

			if (!this.name.matches("^[A-Za-z0-9 _.-]+$"))
				throw new InvalidDescriptionException("name '" + this.name + "' contains invalid characters.");
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "name is not defined");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "name is of wrong type");
		}

		try {
			this.version = map.get("version").toString();
		} catch (NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "version is not defined");
		} catch (ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "version is of wrong type");
		}

		try {
			this.main = map.get("main").toString();
			if (this.main.startsWith("org.bukkit."))
				throw new InvalidDescriptionException("main may not be within the org.bukkit namespace");
			if (this.main.startsWith("org.getspout."))
				throw new InvalidDescriptionException("main may not be within the org.getspout namespace");
			if (this.main.startsWith("org.spoutcraft."))
				throw new InvalidDescriptionException("main may not be within the org.spoutcraft namespace");
			if (this.main.startsWith("in.spout."))
				throw new InvalidDescriptionException("main may not be within the in.spout namespace");
			if (this.main.startsWith("net.minecraft."))
				throw new InvalidDescriptionException("main may not be within the net.minecraft namespace");
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
				this.commands = map.get("load");
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "load is of wrong type");
			}
		}

		if (map.containsKey("commands")) {
			try {
				this.commands = map.get("commands");
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "commands are of wrong type");
			}
		}

		if (map.containsKey("depend")) {
			try {
				this.depend = ((ArrayList<String>) map.get("depend"));
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "depend is of wrong type");
			}
		}

		if (map.containsKey("softdepend")) {
			try {
				this.softDepend = ((ArrayList<String>) map.get("softdepend"));
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "softdepend is of wrong type");
			}
		}

		if (map.containsKey("website")) {
			try {
				this.website = ((String) map.get("website"));
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "website is of wrong type");
			}
		}

		if (map.containsKey("description")) {
			try {
				this.description = ((String) map.get("description"));
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "description is of wrong type");
			}
		}

		if (map.containsKey("author")) {
			try {
				String extra = (String) map.get("author");

				this.authors.add(extra);
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "author is of wrong type");
			}
		}

		if (map.containsKey("authors")) {
			try {
				ArrayList<String> extra = (ArrayList<String>) map.get("authors");

				this.authors.addAll(extra);
			} catch (ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "authors are of wrong type");
			}
		}
	}
	
	private Map<String, Object> saveMap() {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("name", this.name);
		map.put("main", this.main);
		map.put("version", this.version);

		if (this.commands != null) {
			map.put("command", this.commands);
		}
		if (this.depend != null) {
			map.put("depend", this.depend);
		}
		if (this.softDepend != null) {
			map.put("softdepend", this.softDepend);
		}
		if (this.website != null) {
			map.put("website", this.website);
		}
		if (this.description != null) {
			map.put("description", this.description);
		}
		if (this.authors.size() == 1)
			map.put("author", this.authors.get(0));
		else if (this.authors.size() > 1) {
			map.put("authors", this.authors);
		}
		return map;
	}

}
