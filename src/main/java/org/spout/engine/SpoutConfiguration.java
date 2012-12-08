/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.engine;

import java.io.File;

import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.ConfigurationHolder;
import org.spout.api.util.config.ConfigurationHolderConfiguration;
import org.spout.api.util.config.yaml.YamlConfiguration;

public class SpoutConfiguration extends ConfigurationHolderConfiguration {
	
	// General
	public static final ConfigurationHolder MAXIMUM_PLAYERS = new ConfigurationHolder(20, "general", "maximum-players");
	public static final ConfigurationHolder DEFAULT_WORLD = new ConfigurationHolder("world", "general", "default-world");
	public static final ConfigurationHolder WHITELIST_ENABLED = new ConfigurationHolder(false, "general", "whitelist-enabled");
	public static final ConfigurationHolder VIEW_DISTANCE = new ConfigurationHolder(10, "general", "view-distance");
	public static final ConfigurationHolder RECLAIM_MEMORY = new ConfigurationHolder(true, "general", "reclaim-memory");
	public static final ConfigurationHolder AUTOSAVE_INTERVAL = new ConfigurationHolder(60000, "general", "autosave-interval");
	public static final ConfigurationHolder CONSOLE_TYPE = new ConfigurationHolder("jline", "general","console");
	
	// Lightning
	public static final ConfigurationHolder LIGHTING_ENABLED = new ConfigurationHolder(true, "lightning", "enabled");
	public static final ConfigurationHolder LIVE_LIGHTING = new ConfigurationHolder(false, "lightning", "live-lighting");
	
	// Chunks
	public static final ConfigurationHolder CHUNK_REAP_DELAY = new ConfigurationHolder(10000, "chunks", "reap-delay");
	public static final ConfigurationHolder REAP_CHUNKS_PER_TICK = new ConfigurationHolder(5, "chunks", "reap-per-tick");
	public static final ConfigurationHolder UNLOAD_CHUNKS_PER_TICK = new ConfigurationHolder(3, "chunks", "unload-per-tick");
	
	// Messages
	public static final ConfigurationHolder DEFAULT_LANGUAGE = new ConfigurationHolder("EN_US", "messages", "default-language");
	public static final ConfigurationHolder DEFAULT_CHATCHANNEL_NAME = new ConfigurationHolder("", "messages", "default-chatchannel-name");
	public static final ConfigurationHolder DEFAULT_CHATCHANNEL_FORMAT = new ConfigurationHolder("{{GREY}}<{{player-name}}> {{message}}", "messages", "default-chatchannel-format");
	public static final ConfigurationHolder CONSOLE_CHATCHANNEL_NAME = new ConfigurationHolder("Console ", "messages", "console-chatchannel-name");
	public static final ConfigurationHolder CONSOLE_CHATCHANNEL_FORMAT = new ConfigurationHolder("{{GREY}}<{{channel-name}}> {{message}}", "messages", "console-chatchannel-format");

	// Network
	public static final ConfigurationHolder UPNP = new ConfigurationHolder(true, "network", "upnp");
	public static final ConfigurationHolder BONJOUR = new ConfigurationHolder(true, "network", "bonjour");
	
	public SpoutConfiguration() {
		super(new YamlConfiguration(new File("config", "spout.yml")));
	}

	@Override
	public void load() throws ConfigurationException {
		super.load();
		super.save();
	}
}
