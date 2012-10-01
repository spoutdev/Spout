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
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.spout.api.Spout;
import org.spout.api.command.CommandSource;
import org.spout.api.plugin.Plugin;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

public abstract class CommonPluginDictionary implements PluginDictionary {

	public static final int NO_ID = -1;

	protected abstract InputStream openLangResource(String filename);

	protected static final Pattern LANG_FILE_FILTER = Pattern.compile("lang-[a-zA-Z_]{2,5}.yml");
	protected Plugin plugin;
	private final TIntObjectHashMap<LanguageDictionary> languageDictionaries = new TIntObjectHashMap<LanguageDictionary>();
	private int nextId = 0;
	private final HashMap<String, HashMap<String, Integer>> classes = new HashMap<String, HashMap<String,Integer>>(10);
	private final LinkedList<Integer> idList = new LinkedList<Integer>();
	private final LanguageDictionary codedLanguage = new LanguageDictionary(null);

	public CommonPluginDictionary() {
		super();
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
		} catch (IOException e) {
			Spout.getLogger().log(Level.SEVERE, "unable to save dictionary", e);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	
	protected abstract void loadLanguages();

	protected File getLangDirectory() {
		return new File(plugin.getDataFolder(), "lang");
	}

	@SuppressWarnings("unchecked")
	protected void loadLanguage(InputStream in, String fileName) {
		Yaml yaml = new Yaml();
		Map<String, Object> dump = (Map<String, Object>) yaml.load(in);
		Locale locale = null;
		if (dump.containsKey("locale")) {
			locale = Locale.getByCode((String) dump.get("locale"));
		}
		if (locale == null) {
			throw new IllegalStateException("No locale was set in the file " + fileName);
		}
		LanguageDictionary dict = new LanguageDictionary(locale);
		setDictionary(locale, dict);
		if (dump.containsKey("strings")) {
			Map<Integer, Object> strings = (Map<Integer, Object>) dump.get("strings");
			for (Entry<Integer, Object> e : strings.entrySet()) {
				if (e.getValue() instanceof String) {
					dict.setTranslation(e.getKey(), e.getValue());
				} else {
					try {
						LocaleNumberHandler handler = locale.getNumberHandler().newInstance();
						handler.init(e.getValue());
						dict.setTranslation(e.getKey(), handler);
					} catch (IllegalArgumentException e1) {
						throw new RuntimeException("Could not construct a LocaleNumberHandler, check if you used correct syntax (in file " + fileName + ")");
					} catch (SecurityException e1) {
						Spout.getLogger().log(Level.SEVERE, "Failed to construct LocaleNumberHandler", e1);
					} catch (InstantiationException e1) {
						Spout.getLogger().log(Level.SEVERE, "Failed to construct LocaleNumberHandler", e1);
					} catch (IllegalAccessException e1) {
						Spout.getLogger().log(Level.SEVERE, "Failed to construct LocaleNumberHandler", e1);
					}
					
				}
			}
		}
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
		Number num = 0;
		if (args.length >= 1 && args[0] instanceof Number) {
			num = (Number) args[0];
		}
		if (dict != null) {
			int key = getKey(source, foundClass);
			if (key != NO_ID) {
				String translation = dict.getTranslation(key, num);
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
		synchronized (classes) {
			HashMap<String, Integer> idmap = classes.get(clazz);
			if (idmap == null) {
				idmap = new HashMap<String, Integer>();
				classes.put(clazz, idmap);
			}
			idmap.put(source, id);
		}
		synchronized (idList) {
			idList.add(id);
		}
		synchronized (codedLanguage) {
			codedLanguage.setTranslation(id, source);
		}
	}

	public int getKey(String source, String clazz) {
		synchronized (classes) {
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
		synchronized (idList) {
			return Collections.unmodifiableList(idList);
		}
	}

	public void setDictionary(Locale locale, LanguageDictionary dictionary) {
		languageDictionaries.put(locale.hashCode(), dictionary);
	}

	public String getCodedSource(int id) {
		synchronized (codedLanguage) {
			return codedLanguage.getTranslation(id);
		}
	}

}