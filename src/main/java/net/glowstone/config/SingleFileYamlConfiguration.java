package net.glowstone.config;

import net.glowstone.GlowServer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

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
            GlowServer.logger.log(Level.SEVERE, "Cannot load " + configFile, ex);
        } catch (InvalidConfigurationException ex) {
            if (ex.getCause() instanceof YAMLException) {
                GlowServer.logger.severe("Config file " + configFile + " isn't valid! " + ex.getCause());
            } else if ((ex.getCause() == null) || (ex.getCause() instanceof ClassCastException)) {
                GlowServer.logger.severe("Config file " + configFile + " isn't valid!");
            } else {
                GlowServer.logger.log(Level.SEVERE, "Cannot load " + configFile + ": " + ex.getCause().getClass(), ex);
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
            GlowServer.logger.log(Level.SEVERE, "Cannot save " + configFile, ex);
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
