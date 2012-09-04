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
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.migration.ConfigurationMigrator;
import org.spout.api.util.config.ini.IniConfiguration;
import org.spout.api.util.config.migration.MigrationAction;
import org.spout.api.util.config.migration.MigrationException;
import org.spout.api.util.config.migration.NewJoinedKey;
import org.spout.api.util.config.migration.NewKey;

import static org.junit.Assert.*;

public class ConfigurationMigratorTest {
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private class TestConfigurationMigrator extends ConfigurationMigrator {
		private final Map<String[], MigrationAction> migrationActions;

		protected TestConfigurationMigrator(Configuration configuration, Map<String[], MigrationAction> migrationActions) {
			super(configuration);
			this.migrationActions = migrationActions;
		}

		@Override
		protected Map<String[], MigrationAction> getMigrationActions() {
			return migrationActions;
		}

		@Override
		protected boolean shouldMigrate() {
			return true; // Always migrate so we can test easily
		}
	}
	@Test
	public void testNewJoinedKeyMigrationAction() {
		ConfigurationMigrator migrator = new TestConfigurationMigrator(new MapConfiguration(),
				Collections.<String[], MigrationAction>emptyMap());
		NewJoinedKey action = new NewJoinedKey("now.before.%", migrator.getConfiguration());
		assertArrayEquals(new String[] {"now", "before", "input", "key"}, action.convertKey(new String[] {"input", "key"}));
	}

	@Test
	public void testConfigurationFileMoved() throws IOException, ConfigurationException, MigrationException {
		File testFile = folder.newFile("test.ini");
		IniConfiguration testConfig = new IniConfiguration(testFile);
		testConfig.getNode("test", "node").setValue("node-value");
		testConfig.getNode("test", "node2").setValue("node2-value");
		testConfig.getNode("test2", "node").setValue("node-value");
		testConfig.getNode("test2", "node2").setValue("node2-value");
		testConfig.save();
		ConfigurationMigrator migrator = new TestConfigurationMigrator(testConfig, Collections.<String[], MigrationAction>emptyMap());
		migrator.migrate();
		assertTrue(new File(testFile.getAbsolutePath() + ".old").isFile());

	}

	@Test(expected = MigrationException.class)
	public void testErrorOnOldFileExisting() throws IOException, ConfigurationException, MigrationException {
		File testNewFile = folder.newFile("testOldFileExisting.ini"), testOldFile = folder.newFile("testOldFileExisting.ini.old");
		IniConfiguration config = new IniConfiguration(testNewFile);
		config.getNode("test", "node").setValue("test value");
		config.save();

		ConfigurationMigrator migrator = new TestConfigurationMigrator(config, Collections.<String[], MigrationAction>emptyMap());
		migrator.migrate();
	}

	@Test
	public void testConfigurationKeyMove() throws MigrationException {
		Configuration testConfig = new MapConfiguration();
		ConfigurationMigrator testMigrator = new TestConfigurationMigrator(testConfig,
				Collections.<String[], MigrationAction>singletonMap(new String[] {"test", "key"}, new NewKey("test2", "key2")));
		final Object obj = new Object();
		testConfig.getNode("test", "key").setValue(obj);
		testMigrator.migrate();

		assertEquals(obj, testConfig.getNode("test2", "key2").getValue());
	}
}
