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
package org.spout.api.util.config.migration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.Configuration;
import org.spout.api.util.config.ConfigurationNode;
import org.spout.api.util.config.FileConfiguration;

/**
 * A simple migrator for configurations that moves values from one key to another.
 * It can also convert values
 */
public abstract class ConfigurationMigrator {
    private final Configuration configuration;

    protected ConfigurationMigrator(Configuration configuration) {
		Validate.notNull(configuration);
        this.configuration = configuration;
    }

    protected abstract Map<String[], MigrationAction> getMigrationActions();

	public Configuration getConfiguration() {
		return configuration;
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
	 * If {@link #configuration} is a {@link org.spout.api.util.config.FileConfiguration}, the file the configuration vas previously stored in will be
	 * moved to (file name).old as a backup of the data before migration
	 *
	 * @throws MigrationException if the configuration could not be successfully migrated
	 */
    public void migrate() throws MigrationException {
        if (!shouldMigrate()) {
            return;
        }

		if (configuration instanceof FileConfiguration) {

			File oldFile = ((FileConfiguration) configuration).getFile();
			try {
				FileUtils.moveFile(oldFile, new File(oldFile.getAbsolutePath() + ".old"));
			} catch (IOException e) {
				throw new MigrationException(e);
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
			throw new MigrationException(e);
		}
    }

}
