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

		protected Map<String[], MigrationAction> getMigrationActions() {
			return migrationActions;
		}

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
