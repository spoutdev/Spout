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
package org.spout.api.lang;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.spout.api.command.CommandSource;
import org.spout.api.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.nodes.Tag;

public class PluginDictionary {
	public static final int NO_ID = -1;
	private static final Pattern LANG_FILE_FILTER = Pattern.compile("lang-[a-zA-Z_]{2,5}.yml");
	private Plugin plugin;
	private TIntObjectHashMap<LanguageDictionary> languageDictionaries = new TIntObjectHashMap<LanguageDictionary>();
	private int nextId = 0;
	//              Classname  ->   Source->key
	private HashMap<String, HashMap<String, Integer>> classes = new HashMap<String, HashMap<String,Integer>>(10);
	private LinkedList<Integer> idList = new LinkedList<Integer>();
	private LanguageDictionary codedLanguage = new LanguageDictionary(null);

	protected PluginDictionary() {
		plugin = null;
	}
	
	public PluginDictionary(Plugin plugin) {
		this.plugin = plugin;
		load();
	}
	
	@SuppressWarnings("unchecked")
	protected void load() {
		Yaml yaml = new Yaml();
		
		// Load keymap
		InputStream in = openLangResource("keymap.yml");
		if (in == null) {
			return;
		}
		try {
			Map<String, Object> dump = (Map<String, Object>) yaml.load(in);
			if (dump.containsKey("nextId")) {
				nextId = (Integer) dump.get("nextId");
			}
			if (dump.containsKey("ids")) {
				Map<Integer, Map<String, String>> idmap = (Map<Integer, Map<String, String>>) dump.get("ids");
				for (Entry<Integer, Map<String, String>> e1 : idmap.entrySet()) {
					Integer id = e1.getKey();
					Map<String, String> contents = e1.getValue();
					String clazz, source;
					clazz = contents.get("class");
					source = contents.get("string");
					if (id != null && clazz != null && source != null) {
						setKey(source, clazz, id);
					}
				}
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		
		loadLanguages();
	}
	
	public void save(Writer writer) {
		Yaml yaml = new Yaml();
		LinkedHashMap<String, Object> dump = new LinkedHashMap<String, Object>();
		dump.put("nextId", nextId);
		LinkedHashMap<Integer, LinkedHashMap<String, String>> ids = new LinkedHashMap<Integer, LinkedHashMap<String,String>>();
		for (Entry<String, HashMap<String, Integer>> e1 : classes.entrySet()) {
			for (Entry<String, Integer> e2 : e1.getValue().entrySet()) {
				String clazz = e1.getKey();
				String source = e2.getKey();
				int key = e2.getValue();
				LinkedHashMap<String, String> v = new LinkedHashMap<String, String>(2);
				v.put("class", clazz);
				v.put("string", source);
				ids.put(key, v);
			}
		}
		dump.put("ids", ids);
		
		String toWrite = yaml.dumpAs(dump, Tag.MAP, FlowStyle.BLOCK);
		try {
			writer.write(toWrite);
			writer.close();
		} catch (IOException e) {}
	}

	protected void loadLanguages() {
		// Search for other languages
		try {
			Set<String> loaded = new HashSet<String>();
			
			// Look in plugins datadir first
			File langDir = getLangDirectory();
			if (langDir.exists() && langDir.isDirectory()) {
				File[] files = langDir.listFiles();
				for (File file:files) {
					if (LANG_FILE_FILTER.matcher(file.getName()).matches()) {
						loadLanguage(new FileInputStream(file));
						loaded.add(file.getName());
					}
				}
			}
			
			if (plugin != null) {
				// Then look in plugins jar
				JarFile jar = new JarFile(plugin.getFile());
				if (jar.getEntry(getJarBasePath()) == null) { // Skip plugins without language files
					return;
				}
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					if (entry.getName().startsWith(getJarBasePath())) {
						String file = entry.getName().replaceFirst(getJarBasePath(), "");
						if (LANG_FILE_FILTER.matcher(file).matches() && !loaded.contains(file)) {
							loadLanguage(jar.getInputStream(entry));
							loaded.add(file);
						}
					}
				}
			}
		} catch (IOException e) {}
	}
	
	protected File getLangDirectory() {
		return new File(plugin.getDataFolder(), "lang");
	}
	
	@SuppressWarnings("unchecked")
	private void loadLanguage(InputStream in) {
//		System.out.println("Loading lang file");
		Yaml yaml = new Yaml();
		Map<String, Object> dump = (Map<String, Object>) yaml.load(in);
		Locale locale = null;
		if (dump.containsKey("locale")) {
			locale = Locale.getByCode((String) dump.get("locale"));
		}
		if (locale == null) {
			return;
		}
		LanguageDictionary dict = new LanguageDictionary(locale);
		setDictionary(locale, dict);
		if (dump.containsKey("strings")) {
			Map<Integer, String> strings = (Map<Integer, String>) dump.get("strings");
			for (Entry<Integer, String> e : strings.entrySet()) {
				dict.setTranslation(e.getKey(), e.getValue());
			}
		}
	}

	protected InputStream openLangResource(String filename) {
		try {
			File inDataDir = new File(getLangDirectory(), filename);
			if (inDataDir.exists()) {
				return new FileInputStream(inDataDir);
			} else if(plugin != null) {
				JarFile jar = new JarFile(plugin.getFile());
				JarEntry keyMap = jar.getJarEntry(getJarBasePath()+filename);
				if (keyMap != null) {
					return jar.getInputStream(keyMap);
				}
			}
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	protected String getJarBasePath() {
		return "lang/";
	}

	/**
	 * Returns the translation of source into the receivers preferred language
	 * 
	 * @param source
	 *            the string to translate
	 * @param receiver
	 *            the receiver who will see the message
	 * @param args
	 *            any object given will be inserted into the target string for
	 *            each %0, %1 asf
	 * @param foundClass
	 *            the class that called the translation (used for determining
	 *            which translation is correct)
	 * @return the translation
	 */
	public String tr(String source, CommandSource receiver, String foundClass, Object[] args) {
		String use = source;
		Locale preferred = receiver.getPreferredLocale();

		// Search for translation
		LanguageDictionary dict = getDictionary(preferred);
		if (dict != null) {
			int key = getKey(source, foundClass);
			if (key != NO_ID) {
				String translation = dict.getTranslation(key);
				if (translation != null) {
					use = translation;
				}
			}
		}

		use = replacePlaceholders(use, args);
		return use;
	}

	protected String replacePlaceholders(String source, Object... args) {
		// Replace placeholders
		int i = 0;
		for (Object arg : args) {
			source = source.replaceAll("%" + i, arg.toString());
			i++;
		}
		return source;
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public LanguageDictionary getDictionary(Locale locale) {
		return languageDictionaries.get(locale.hashCode());
	}
	
	public void setKey(String source, String clazz, int id) {
		HashMap<String, Integer> idmap = classes.get(clazz);
		if (idmap == null) {
			idmap = new HashMap<String, Integer>();
			classes.put(clazz, idmap);
		}
		idmap.put(source, id);
		idList.add(id);
		codedLanguage.setTranslation(id, source);
	}

	public int getKey(String source, String clazz) {
		HashMap<String, Integer> idmap = classes.get(clazz);
		if (idmap == null) {
			return NO_ID;
		} else {
			Integer id = idmap.get(source);
			if (id != null) {
				return id;
			}
		}
		return NO_ID;
	}

	public void broadcast(String source, CommandSource[] receivers, String clazz, Object[] args) {
		int key = getKey(source, clazz);
		for (CommandSource receiver:receivers) {
			String use = source;
			LanguageDictionary dict = getDictionary(receiver.getPreferredLocale());
			if (dict != null) {
				String translation = dict.getTranslation(key);
				if (translation != null) {
					use = translation;
				}
			}
			use = replacePlaceholders(use, args);
			receiver.sendMessage(use);
		}
	}
	
	public int getNextKey() {
		return nextId++;
	}
	
	public List<Integer> getIdList() {
		return Collections.unmodifiableList(idList);
	}

	public void setDictionary(Locale locale, LanguageDictionary dictionary) {
		languageDictionaries.put(locale.hashCode(), dictionary);
	}
	
	public String getCodedSource(int id) {
		return codedLanguage.getTranslation(id);
	}
}
