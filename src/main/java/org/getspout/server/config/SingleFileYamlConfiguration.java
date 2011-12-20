package org.getspout.server.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.yaml.snakeyaml.error.YAMLException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import org.getspout.server.SpoutServer;

public class SingleFileYamlConfiguration extends YamlConfiguration {
	private final File configFile;

	public SingleFileYamlConfiguration(File configFile) {
		this.configFile = configFile;
	}

	public boolean load() {
		try {
			checkAndCreateFile();
			super.load(configFile);
			return true;
		} catch (IOException ex) {
			SpoutServer.logger.log(Level.SEVERE, "Cannot load " + configFile, ex);
		} catch (InvalidConfigurationException ex) {
			if (ex.getCause() instanceof YAMLException) {
				SpoutServer.logger.severe("Config file " + configFile + " isn't valid! " + ex.getCause());
			} else if (ex.getCause() == null || ex.getCause() instanceof ClassCastException) {
				SpoutServer.logger.severe("Config file " + configFile + " isn't valid!");
			} else {
				SpoutServer.logger.log(Level.SEVERE, "Cannot load " + configFile + ": " + ex.getCause().getClass(), ex);
			}
		}
		return false;
	}

	public boolean save() {
		try {
			checkAndCreateFile();
			super.save(configFile);
			return true;
		} catch (IOException ex) {
			SpoutServer.logger.log(Level.SEVERE, "Cannot save " + configFile, ex);
		}
		return false;
	}

	private void checkAndCreateFile() throws IOException {
		File configDir = configFile.getParentFile();
		if (!configDir.exists() || !configDir.isDirectory()) {
			configDir.mkdirs();
		}
		if (!configFile.exists()) {
			configFile.createNewFile();
		}
	}
}
