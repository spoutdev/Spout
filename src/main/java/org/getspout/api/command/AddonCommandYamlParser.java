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
package org.getspout.api.command;


import java.util.Set;

import org.getspout.api.command.Command;
import org.getspout.api.plugin.Plugin;

public class AddonCommandYamlParser implements CommandRegistrationsFactory<Plugin> {

	public Set<Command> create(Plugin plugin) {
		/*List<Command> pluginCmds = new ArrayList<Command>();
		Object object = plugin.getDescription().getCommands();

		if (object == null) {
			return pluginCmds;
		}

		Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) object;

		if (map != null) {
			for (Entry<String, Map<String, Object>> entry : map.entrySet()) {
				Command newCmd = new AddonCommand(entry.getKey(), plugin);
				Object description = entry.getValue().get("description");
				Object usage = entry.getValue().get("usage");
				Object aliases = entry.getValue().get("aliases");

				if (description != null) {
					newCmd.setDescription(description.toString());
				}

				if (usage != null) {
					newCmd.setUsage(usage.toString());
				}

				if (aliases != null) {
					List<String> aliasList = new ArrayList<String>();

					if (aliases instanceof List) {
						for (Object o : (List<Object>) aliases) {
							aliasList.add(o.toString());
						}
					} else {
						aliasList.add(aliases.toString());
					}

					newCmd.setAliases(aliasList);
				}

				pluginCmds.add(newCmd);
			}
		}
		return pluginCmds;
		*/
		return null;
	}
}
