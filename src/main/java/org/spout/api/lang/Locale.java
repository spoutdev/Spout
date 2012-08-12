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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class Locale {
	private String fullName;
	private String code;
	private static final LinkedHashMap<String, Locale> byCode = new LinkedHashMap<String, Locale>(5);
	private Class<? extends LocaleNumberHandler> numberHandler;
	
	public static final Locale ENGLISH_US = new Locale("English (USA)", "EN_US", DefaultNumberHandler.class);
	public static final Locale ENGLISH_UK = new Locale("English (United Kingdom)", "EN_UK", DefaultNumberHandler.class);
	public static final Locale GERMAN_DE = new Locale("Deutsch (Deutschland)", "DE_DE", DefaultNumberHandler.class);
	public static final Locale GERMAN_SW = new Locale("Deutsch (Schweiz)", "DE_SW", DefaultNumberHandler.class);
	public static final Locale GERMAN_AT = new Locale("Deutsch (Österreich)", "DE_AT", DefaultNumberHandler.class);
	public static final Locale FRENCH_FR = new Locale("Français (France)", "FR_FR", DefaultNumberHandler.class);
	
	/**
	 * Instead of using the constructor to create a locale,
	 * use {@link Locale.getByCode} so it returns already available instances for that language.
	 * @param fullName
	 * @param code
	 */
	public Locale(String fullName, String code, Class<? extends LocaleNumberHandler> numberHandler) {
		this.fullName = fullName;
		this.code = code;
		byCode.put(code, this);
		this.numberHandler = numberHandler;
	}

	/**
	 * @return the full name of the language in the target language
	 */
	public String getFullName() {
		return fullName;
	}
	
	/**
	 * @return the language code, i.e. "EN_US" (language_country)
	 */
	public String getCode() {
		return code;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.toLowerCase().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Locale other = (Locale) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

	public static Locale getByCode(String code) {
		if (byCode.containsKey(code)) {
			return byCode.get(code);
		} else {
			Locale l = new Locale("Unknown", code, DefaultNumberHandler.class);
			byCode.put(code, l);
			return l;
		}
	}
	
	public List<Locale> getLocales() {
		ArrayList<Locale> locales = new ArrayList<Locale>(byCode.size());
		for (Entry<String, Locale> l : byCode.entrySet()) {
			locales.add(l.getValue());
		}
		return locales;
	}
	
	public Class<? extends LocaleNumberHandler> getNumberHandler() {
		return numberHandler;
	}
}
