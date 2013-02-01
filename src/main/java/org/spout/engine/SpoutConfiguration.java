/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spout.engine;

import java.io.File;

import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.ConfigurationHolder;
import org.spout.api.util.config.ConfigurationHolderConfiguration;
import org.spout.api.util.config.yaml.YamlConfiguration;

import org.spout.engine.filesystem.SharedFileSystem;

public class SpoutConfiguration extends ConfigurationHolderConfiguration {
	// General
	public static final ConfigurationHolder MAXIMUM_PLAYERS = new ConfigurationHolder(20, "general", "maximum-players");
	public static final ConfigurationHolder DEFAULT_WORLD = new ConfigurationHolder("world", "general", "default-world");
	public static final ConfigurationHolder WHITELIST_ENABLED = new ConfigurationHolder(false, "general", "whitelist-enabled");
	public static final ConfigurationHolder VIEW_DISTANCE = new ConfigurationHolder(10, "general", "view-distance");
	public static final ConfigurationHolder RECLAIM_MEMORY = new ConfigurationHolder(true, "general", "reclaim-memory");
	public static final ConfigurationHolder AUTOSAVE_INTERVAL = new ConfigurationHolder(60000, "general", "autosave-interval");
	public static final ConfigurationHolder CONSOLE_TYPE = new ConfigurationHolder("jline", "general", "console");
	// Lightning
	public static final ConfigurationHolder LIGHTING_ENABLED = new ConfigurationHolder(true, "lightning", "enabled");
	public static final ConfigurationHolder LIVE_LIGHTING = new ConfigurationHolder(false, "lightning", "live-lighting");
	// Chunks
	public static final ConfigurationHolder CHUNK_REAP_DELAY = new ConfigurationHolder(1, "chunks", "reap-delay");
	public static final ConfigurationHolder REAP_CHUNKS_PER_TICK = new ConfigurationHolder(50, "chunks", "reap-per-tick");
	public static final ConfigurationHolder UNLOAD_CHUNKS_PER_TICK = new ConfigurationHolder(50, "chunks", "unload-per-tick");
	public static final ConfigurationHolder DYNAMIC_BLOCKS = new ConfigurationHolder(true, "chunks", "dynamic-blocks");
	public static final ConfigurationHolder BLOCK_PHYSICS = new ConfigurationHolder(true, "chunks", "block-physics");	
	// Messages
	public static final ConfigurationHolder DEFAULT_LANGUAGE = new ConfigurationHolder("EN_US", "messages", "default-language");
	// Network
	public static final ConfigurationHolder UPNP = new ConfigurationHolder(true, "network", "upnp");
	public static final ConfigurationHolder BONJOUR = new ConfigurationHolder(false, "network", "bonjour");
	public static final ConfigurationHolder SHOW_CONNECTIONS = new ConfigurationHolder(false, "network", "show-connections");
	public static final ConfigurationHolder SEND_LATENCY = new ConfigurationHolder(0L, "network", "send_latency");
	public static final ConfigurationHolder SEND_SPIKE_LATENCY = new ConfigurationHolder(0L, "network", "send_spike_latency");
	public static final ConfigurationHolder SEND_SPIKE_CHANCE = new ConfigurationHolder(0F, "network", "send_spikes_per_second");
	public static final ConfigurationHolder RECV_SPIKE_LATENCY = new ConfigurationHolder(0L, "network", "recv_spike_latency");
	public static final ConfigurationHolder RECV_SPIKE_CHANCE = new ConfigurationHolder(0F, "network", "recv_spikes_per_second");

	public SpoutConfiguration() {
		super(new YamlConfiguration(new File(SharedFileSystem.getConfigDirectory(), "spout.yml")));
	}

	@Override
	public void load() throws ConfigurationException {
		super.load();
		super.save();
	}
}
