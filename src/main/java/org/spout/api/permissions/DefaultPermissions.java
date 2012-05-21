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
package org.spout.api.permissions;

import org.spout.api.Spout;
import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.Result;
import org.spout.api.event.server.permissions.PermissionGetAllWithNodeEvent;
import org.spout.api.event.server.permissions.PermissionNodeEvent;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.ConfigurationHolder;
import org.spout.api.util.config.ConfigurationHolderConfiguration;
import org.spout.api.util.config.yaml.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Handle registering default permissions. Permissions registered here will be applied
 * to permissions events if no other plugin has changed the values.
 * Wildcards will be checked.
 */
public class DefaultPermissions extends ConfigurationHolderConfiguration implements Listener {
	private static final ConfigurationHolder ENABLED = new ConfigurationHolder(true, "enabled");
	private static final ConfigurationHolder DEFAULTS = new ConfigurationHolder(Collections.emptyList(), "defaults");
	private static final YamlConfiguration CONFIG = new YamlConfiguration(new File("permissions.yml"));

	private static final DefaultPermissions INSTANCE = new DefaultPermissions();
	private final Set<String> defaultPermissions = new HashSet<String>();
	private final Set<String> pluginDefaultPermissions = new HashSet<String>();

	private DefaultPermissions() {
		super(CONFIG);
		CONFIG.setHeader("This is the configuration file for default server permissions.",
				"If enabled is set to false, by default nobody will have default permissions.",
				"Plugins can set their own default permissions, and server admins can",
				"set default permissions under the defaults section of this file.");
		reload(this);
		Spout.getEngine().getEventManager().registerEvents(this, this);
	}

	private static void reload(DefaultPermissions instance) {
		try {
			instance.load();
			instance.save();
		} catch (ConfigurationException e) {
			Spout.getEngine().getLogger().log(Level.SEVERE, "Error loading permissions configuration!", e);
		}
		instance.defaultPermissions.clear();
		instance.defaultPermissions.addAll(DEFAULTS.getStringList());
	}

	/**
	 * Reloads DefaultPermissions
	 */
	public static void reload() {
		reload(INSTANCE);
	}

	@EventHandler(order = Order.LATEST)
	public void onPermissionNode(PermissionNodeEvent event) {
		if (!ENABLED.getBoolean()) {
			return;
		}
		if (event.getResult() == Result.DEFAULT) {
			for (String node : event.getNodes()) {
				if (defaultPermissions.contains(node) || pluginDefaultPermissions.contains(node)) {
					event.setResult(Result.ALLOW);
					break;
				}
			}
		}
	}

	@EventHandler(order = Order.LATEST)
	public void onGetAllWithNode(PermissionGetAllWithNodeEvent event) {
		if (!ENABLED.getBoolean()) {
			return;
		}
		boolean hasNode = false;
		for (String node : event.getNodes()) {
			if (defaultPermissions.contains(node) || pluginDefaultPermissions.contains(node)) {
				hasNode = true;
				break;
			}
		}
		if (hasNode) {
			for (Map.Entry<PermissionsSubject, Result> entry : event.getReceivers().entrySet()) {
				if (entry.getValue() == Result.DEFAULT) {
					entry.setValue(Result.ALLOW);
				}
			}
		}
	}

	/**
	 * Adds a default permission to be applied to PermissionSubjects
	 *
	 * @param node The node to add
	 */
	public static void addDefaultPermission(String node) {
		INSTANCE.pluginDefaultPermissions.add(node);
	}

	/**
	 * Get the current default permissions. The returned collection will be unmodifiable.
	 * If DefaultPermissions is not enabled, the returned set will be empty.
	 *
	 * @return The current default permissions
	 */
	public static Set<String> getDefaultPermissions() {
		if (!ENABLED.getBoolean()) {
			return Collections.emptySet();
		}
		HashSet<String> perms = new HashSet<String>(INSTANCE.defaultPermissions);
		perms.addAll(INSTANCE.pluginDefaultPermissions);
		return Collections.unmodifiableSet(perms);
	}

	/**
	 * Remove a permission from the set of default permissions.
	 * If the permission is not a default permission, nothing happens.
	 * This will remove default permissions from the user-defined list of permissions
	 * if they are not in the plugin-defined list.
	 *
	 * @param node The node to remove
	 */
	public static void removeDefaultPermission(String node) {
		if (!INSTANCE.pluginDefaultPermissions.remove(node)) {
			if (INSTANCE.defaultPermissions.contains(node)) {
				INSTANCE.defaultPermissions.remove(node);
				DEFAULTS.getList().remove(node);
				try {
					INSTANCE.save();
				} catch (ConfigurationException ignore) {}
			}
		}
	}
}
