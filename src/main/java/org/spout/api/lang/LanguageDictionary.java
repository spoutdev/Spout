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

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.nodes.Tag;

public class LanguageDictionary {
	private Locale locale;
	HashMap<Integer, Object> translations = new HashMap<Integer, Object>();
	
	public LanguageDictionary(Locale locale) {
		this.locale = locale;
	}
	
	public void setTranslation(int id, Object object) {
		translations.put(id, object);
	}
	
	public String getTranslation(int id) {
		return getTranslation(id, 0);
	}
	
	public String getTranslation(int id, Number number) {
		Object tr = translations.get(id);
		if (tr instanceof String) {
			return (String) tr;
		} else if (tr instanceof LocaleNumberHandler) {
			return ((LocaleNumberHandler) tr).getString(number);
		} else {
			return null;
		}
	}
	
	public Locale getLocale() {
		return locale;
	}

	public void save(FileWriter fileWriter) {
		Yaml yaml = new Yaml();
		LinkedHashMap<String, Object> dump = new LinkedHashMap<String, Object>();
		dump.put("locale", locale.getCode());
		LinkedHashMap<Integer, Object> tr = new LinkedHashMap<Integer, Object>();
		for (Entry<Integer, Object> e:translations.entrySet()) {
			if (e.getValue() instanceof String) {
				tr.put(e.getKey(), e.getValue());
			} else if (e.getValue() instanceof LocaleNumberHandler) {
				tr.put(e.getKey(), ((LocaleNumberHandler) e.getValue()).save());
			}
		}
		dump.put("strings", tr);
		String toWrite = yaml.dumpAs(dump, Tag.MAP, FlowStyle.BLOCK);
		try {
			fileWriter.write(toWrite);
			fileWriter.close();
		} catch (IOException e) {
		}
	}
}
