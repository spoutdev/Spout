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
package org.spout.api.permissions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.spout.api.Engine;
import org.spout.api.event.EventHandler;
import org.spout.api.event.Listener;
import org.spout.api.event.Order;
import org.spout.api.event.Result;
import org.spout.api.event.server.permissions.PermissionGetAllWithNodeEvent;
import org.spout.api.event.server.permissions.PermissionNodeEvent;
import org.spout.cereal.config.Configuration;
import org.spout.cereal.config.ConfigurationException;
import org.spout.cereal.config.ConfigurationHolder;
import org.spout.cereal.config.ConfigurationHolderConfiguration;
import org.spout.cereal.config.yaml.YamlConfiguration;

/**
 * Handle registering default permissions. Permissions registered here will be applied to permissions events if no other plugin has changed the values. Wildcards will be checked.
 */
public class DefaultPermissions extends ConfigurationHolderConfiguration implements Listener {
	private final ConfigurationHolder ENABLED = new ConfigurationHolder(true, "enabled");
	private final ConfigurationHolder DEFAULTS = new ConfigurationHolder(new ArrayList<>(Arrays.asList("spout.*")), "defaults");
	private final Engine engine;
	private final YamlConfiguration config;
	private final Set<String> defaultPermissions = new HashSet<>();
	private final Set<String> pluginDefaultPermissions = new HashSet<>();

	public DefaultPermissions(Engine engine, File configFile) {
		super(null);
		this.engine = engine;

		config = new YamlConfiguration(configFile);
		config.setHeader("This is the configuration file for default server permissions.",
				"If enabled is set to false, by default nobody will have default permissions.",
				"Plugins can set their own default permissions, and server admins can",
				"set default permissions under the defaults section of this file.");
		reload();

		engine.getEventManager().registerEvents(this, this);
	}

	@Override
	public Configuration getConfiguration() {
		return config;
	}

	/**
	 * Reload the user-defined default permissions
	 */
	public void reload() {
		try {
			load();
			save();
		} catch (ConfigurationException e) {
			engine.getLogger().log(Level.SEVERE, "Error loading permissions configuration!", e);
		}
		defaultPermissions.clear();
		defaultPermissions.addAll(DEFAULTS.getStringList());
	}

	@EventHandler (order = Order.LATEST)
	protected void onPermissionNode(PermissionNodeEvent event) {
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

	@EventHandler (order = Order.LATEST)
	protected void onGetAllWithNode(PermissionGetAllWithNodeEvent event) {
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
	public void addDefaultPermission(String node) {
		pluginDefaultPermissions.add(node);
	}

	/**
	 * Get the current default permissions. The returned collection will be unmodifiable. If DefaultPermissions is not enabled, the returned set will be empty.
	 *
	 * @return The current default permissions
	 */
	public Set<String> getDefaultPermissions() {
		if (!ENABLED.getBoolean()) {
			return Collections.emptySet();
		}
		HashSet<String> perms = new HashSet<>(defaultPermissions);
		perms.addAll(pluginDefaultPermissions);
		return Collections.unmodifiableSet(perms);
	}

	/**
	 * Remove a permission from the set of default permissions. If the permission is not a default permission, nothing happens. This will remove default permissions from the user-defined list of
	 * permissions if they are not in the plugin-defined list.
	 *
	 * @param node The node to remove
	 */
	public void removeDefaultPermission(String node) {
		if (!pluginDefaultPermissions.remove(node)) {
			if (defaultPermissions.contains(node)) {
				defaultPermissions.remove(node);
				DEFAULTS.getList().remove(node);
				try {
					save();
				} catch (ConfigurationException ignore) {
				}
			}
		}
	}
}
