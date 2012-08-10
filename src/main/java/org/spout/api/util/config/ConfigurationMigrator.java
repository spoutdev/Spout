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
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.spout.api.exception.ConfigurationException;

/**
 * A simple migrator for configurations that moves values from one key to another.
 * It can also convert values
 */
public abstract class ConfigurationMigrator {
    private final Configuration configuration;

    protected ConfigurationMigrator(Configuration configuration) {
        this.configuration = configuration;
    }

    protected abstract Map<String[], MigrationAction> getMigrationActions();

	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * This implementation of MigrationAction converts configuration keys based on predefined
	 * stringpath patterns where % is replaced by the old key. The stringpath pattern is split by the configuration's path separator
	 *
	 * @see org.spout.api.util.config.Configuration#getPathSeparator()
	 */
	public final class NewJoinedKey implements MigrationAction {
		private final String newKeyPattern;

		public NewJoinedKey(String newKeyPattern) {
			this.newKeyPattern = newKeyPattern;
		}

		public String[] convertKey(String[] key) {
			String oldKey = StringUtils.join(key, getConfiguration().getPathSeparator());
			return configuration.getPathSeparatorPattern().split(newKeyPattern.replaceAll("%", Matcher.quoteReplacement(oldKey)));
		}

		public Object convertValue(Object value) {
			return value;
		}
	}

	/**
	 * This implementation of MigrationAction changes the key of a configuration value to a predefined new key
	 */
	public static final class NewKey implements MigrationAction {
		private final String[] newKey;

		public NewKey(String... key) {
			this.newKey = key;
		}

		public String[] convertKey(String[] key) {
			return newKey;
		}

		public Object convertValue(Object value) {
			return value;
		}
	}

	/**
	 * Represents the two sides of migrating an existing configuration key:
	 * Converting the key and converting the value
	 */
	public static interface MigrationAction {
		/**
		 * This method converts the old configuration key to its migrated value.
		 *
		 * @param key The existing configuration key
		 * @return The key modified to its new value
		 */
		public String[] convertKey(String[] key);
		/**
		 * This method converts the old configuration value to its migrated value.
		 *
		 * @param value The existing configuration value
		 * @return The value modified to its new value
		 */
		public Object convertValue(Object value);
	}

	/**
	 * This method checks whether migration is needed on the {@link Configuration} this instance is constructed with
	 *
	 * @return Whether migration is needed
	 */
    protected abstract boolean shouldMigrate();

	/**
	 * Perform migration of the configuration this object was constructed with
	 * If migration was not necessary ({@link #shouldMigrate()} returned false), the method invocation will be considered successful.
	 * If {@link #configuration} is a {@link FileConfiguration}, the file the configuration vas previously stored in will be
	 * moved to (file name).old as a backup of the data before migration
	 *
	 * @return Null if successful, an error message if an error occurred.
	 */
    public String migrate() {
        if (!shouldMigrate()) {
            return null;
        }

		if (configuration instanceof FileConfiguration) {
			File oldFile = ((FileConfiguration) configuration).getFile();
        	if (!oldFile.renameTo(new File(oldFile.getAbsolutePath() + ".old"))) {
            	return "Unable to rename backup old configuration file!";
        	}
		}

        for (Map.Entry<String[], MigrationAction> entry : getMigrationActions().entrySet()) {
			final ConfigurationNode existingNode = configuration.getNode(entry.getKey());
            final Object existing = existingNode.getValue();
            existingNode.remove();
            if (existing == null || entry.getValue() == null) {
                continue;
            }
			final String[] newKey = entry.getValue().convertKey(entry.getKey());
			final Object newValue = entry.getValue().convertValue(existing);
            configuration.getNode(newKey).setValue(newValue);
        }
        try {
			configuration.save();
		} catch (ConfigurationException e) {
			return e.getMessage();
		}
		return null;
    }

}
