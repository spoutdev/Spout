/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.server.util.config;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.spout.api.util.config.Configuration;
import org.spout.api.util.config.ConfigurationNode;

public class SpoutConfiguration extends Configuration {
	
	private static final String[] whitelist = {"Notch", "ez", "jeb"};
	private static final String[] banlist = {"satan"};
	public static final ConfigurationNode WORLDS = new ConfigurationNode("worlds", "default");
	public static final ConfigurationNode WHITELIST = new ConfigurationNode("whitelist", Arrays.asList(whitelist));
	public static final ConfigurationNode BANLIST = new ConfigurationNode("banlist", banlist);
	public static final ConfigurationNode ALLOW_FLIGHT = new ConfigurationNode("allowflight", false);
	public static final ConfigurationNode USE_WHITELIST = new ConfigurationNode("usewhitelist", false);
	public static final ConfigurationNode WORLD_CONTAINER = new ConfigurationNode("worldcontainer", ".");
	public static final ConfigurationNode ADDRESS = new ConfigurationNode("address", "0.0.0.0:25565");
	
	public SpoutConfiguration() {
		super(new File("config/config.yml"));
	}
	
	@Override
	public void load() {
		super.load();
		for (Field field : SpoutConfiguration.class.getFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				try {
					Object f = field.get(null);
					if (f instanceof ConfigurationNode) {
						this.addNode((ConfigurationNode) f);
					}
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}
		
		this.save();
	}
}
