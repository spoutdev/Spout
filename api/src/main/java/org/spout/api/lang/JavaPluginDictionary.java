/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
package org.spout.api.lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.plugin.Plugin;

public class JavaPluginDictionary extends CommonPluginDictionary {
	protected JavaPluginDictionary() {
		plugin = null;
	}

	public JavaPluginDictionary(Plugin plugin) {
		this.plugin = plugin;
		load();
	}

	protected String getJarBasePath() {
		return "lang/";
	}

	@Override
	@SuppressWarnings ("resource")
	protected InputStream openLangResource(String filename) {
		try {
			File inDataDir = new File(getLangDirectory(), filename);
			if (inDataDir.exists()) {
				return new FileInputStream(inDataDir);
			} else if (plugin != null) {
				JarFile jar = new JarFile(plugin.getFile());
				JarEntry keyMap = jar.getJarEntry(getJarBasePath() + filename);
				if (keyMap != null) {
					return jar.getInputStream(keyMap);
				}
			}
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	@SuppressWarnings ("resource")
	@Override
	protected void loadLanguages() {
		// Search for other languages
		try {
			Set<String> loaded = new HashSet<>();

			// Look in plugins datadir first
			File langDir = getLangDirectory();
			if (langDir.exists() && langDir.isDirectory()) {
				File[] files = langDir.listFiles();
				for (File file : files) {
					if (LANG_FILE_FILTER.matcher(file.getName()).matches()) {
						try {
							loadLanguage(new FileInputStream(file), file.getAbsolutePath());
							loaded.add(file.getName());
						} catch (Exception e) {
							Spout.getLogger().log(Level.SEVERE, "Error while loading a translation file for plugin " + plugin.getName(), e);
						}
					}
				}
			}

			if (plugin != null) {
				// Then look in plugins jar
				JarFile jar = new JarFile(plugin.getFile());
				if (jar.getEntry(getJarBasePath()) == null) {
					// Skip plugins without language files
					return;
				}
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					if (entry.getName().startsWith(getJarBasePath())) {
						String file = entry.getName().replaceFirst(getJarBasePath(), "");
						if (LANG_FILE_FILTER.matcher(file).matches() && !loaded.contains(file)) {
							try {
								loadLanguage(jar.getInputStream(entry), plugin.getFile().getAbsolutePath() + "/" + entry.getName());
								loaded.add(file);
							} catch (Exception e) {
								Spout.getLogger().log(Level.SEVERE, "Error while loading a translation file for plugin " + plugin.getName(), e);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			Spout.getLogger().log(Level.SEVERE, "Error while loading translation files for plugin " + plugin.getName(), e);
		}
	}
}
