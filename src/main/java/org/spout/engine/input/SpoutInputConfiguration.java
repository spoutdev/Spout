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
package org.spout.engine.input;

import java.io.File;

import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.ConfigurationHolder;
import org.spout.api.util.config.ConfigurationHolderConfiguration;
import org.spout.api.util.config.yaml.YamlConfiguration;

public class SpoutInputConfiguration extends ConfigurationHolderConfiguration{
	public static final ConfigurationHolder FORWARD = new ConfigurationHolder("W", "forward");
	public static final ConfigurationHolder BACKWARD = new ConfigurationHolder("S", "backward");
	public static final ConfigurationHolder LEFT = new ConfigurationHolder("A", "left");
	public static final ConfigurationHolder RIGHT = new ConfigurationHolder("D", "right");
	public static final ConfigurationHolder UP = new ConfigurationHolder("SPACE", "up");
	public static final ConfigurationHolder DOWN = new ConfigurationHolder("LSHIFT", "down");
	
	public SpoutInputConfiguration() {
		super(new YamlConfiguration(new File("config", "controls.yml")));
	}

	@Override
	public void load() throws ConfigurationException {
		super.load();
		super.save();
	}
}
