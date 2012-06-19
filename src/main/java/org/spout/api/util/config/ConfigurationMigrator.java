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
package org.spout.api.util.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.Configuration;
import org.spout.api.util.config.ConfigurationNode;

/**
 * A simple migrator for configurations that moves values from one key to another.
 * Values do not have their types converted.
 */
public abstract class ConfigurationMigrator {
    protected final Configuration config;
    protected final File oldFile;

    protected ConfigurationMigrator(File configFile, Configuration processor) {
        this.oldFile = configFile;
        this.config = processor;
    }

    protected Map<String[], String[]> getMigrationKeys() {
		Map<String, String> joined = getJoinedMigrationKeys();
		if (joined == null) {
			return null;
		}
		Map<String[], String[]> ret = new HashMap<String[], String[]>();
		for (Map.Entry<String, String> entry : joined.entrySet()) {
			String[] newKey = config.getPathSeparatorPattern().split(entry.getKey());
			String[] newValue = config.getPathSeparatorPattern().split(entry.getValue().replaceAll("%", Matcher.quoteReplacement(entry.getKey())));
			ret.put(newKey,  newValue);
		}
		return ret;
	}

	protected Map<String, String> getJoinedMigrationKeys() {
		return null;
	}

    protected abstract boolean shouldMigrate();

    public String migrate() {
        if (!shouldMigrate()) {
            return null;
        }

        if (!oldFile.renameTo(new File(oldFile.getAbsolutePath() + ".old"))) {
            return "Unable to rename backup old configuration file!";
        }
        for (Map.Entry<String[], String[]> entry : getMigrationKeys().entrySet()) {
			ConfigurationNode existingNode = config.getNode(entry.getKey());
            Object existing = existingNode.getValue();
            existingNode.remove();
            if (existing == null || entry.getValue() == null) {
                continue;
            }
            config.getNode(entry.getValue()).setValue(existing);
        }
        try {
			config.save();
		} catch (ConfigurationException e) {
			return e.getMessage();
		}
		return null;
    }

}
