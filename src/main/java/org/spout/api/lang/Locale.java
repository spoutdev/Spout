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
	private static final LinkedHashMap<String, Locale> BY_CODE = new LinkedHashMap<String, Locale>(5);
	public static final Locale ENGLISH_US = new Locale(java.util.Locale.US, DefaultNumberHandler.class);
	public static final Locale ENGLISH_UK = new Locale(java.util.Locale.UK, DefaultNumberHandler.class);
	public static final Locale GERMAN_DE = new Locale(java.util.Locale.GERMANY, DefaultNumberHandler.class);
	public static final Locale GERMAN_SW = new Locale(new java.util.Locale("de", "sw"), DefaultNumberHandler.class);
	public static final Locale GERMAN_AT = new Locale(new java.util.Locale("de", "at") , DefaultNumberHandler.class);
	public static final Locale FRENCH_FR = new Locale(java.util.Locale.FRENCH, DefaultNumberHandler.class);

	private final java.util.Locale baseLocale;
	private final Class<? extends LocaleNumberHandler> numberHandler;

	/**
	 * Instead of using the constructor to create a locale,
	 * use {@link #getByCode} so it returns already available instances for that language.
	 * @param baseLocale The base locale for the language
	 */
	public Locale(java.util.Locale baseLocale, Class<? extends LocaleNumberHandler> numberHandler) {
		BY_CODE.put(baseLocale.toString().toLowerCase(), this);
		this.baseLocale = baseLocale;
		this.numberHandler = numberHandler;
	}

	/**
	 * @return the full name of the language in the target language
	 */
	public String getFullName() {
		return baseLocale.getDisplayName();
	}

	/**
	 * @return the language code, i.e. "EN_US" (language_country)
	 */
	public String getCode() {
		return baseLocale.toString();
	}

	public java.util.Locale getBaseLocale() {
		return baseLocale;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + baseLocale.hashCode();
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
		if (baseLocale == null) {
			if (other.baseLocale != null)
				return false;
		} else if (!baseLocale.equals(other.baseLocale))
			return false;
		return true;
	}

	public static Locale getByCode(String code) {
		String lower = code.toLowerCase();
		if (BY_CODE.containsKey(lower)) {
			return BY_CODE.get(lower);
		} else {
			Locale l = new Locale(new java.util.Locale(code), DefaultNumberHandler.class);
			BY_CODE.put(code, l);
			return l;
		}
	}

	public static Locale getByLocale(java.util.Locale locale) {
		String localeStr = locale.toString().toLowerCase();
		if (BY_CODE.containsKey(localeStr)) {
			return BY_CODE.get(localeStr);
		} else {
			Locale l = new Locale(locale, DefaultNumberHandler.class);
			return l;
		}
	}

	public List<Locale> getLocales() {
		ArrayList<Locale> locales = new ArrayList<Locale>(BY_CODE.size());
		for (Entry<String, Locale> l : BY_CODE.entrySet()) {
			locales.add(l.getValue());
		}
		return locales;
	}

	public Class<? extends LocaleNumberHandler> getNumberHandler() {
		return numberHandler;
	}
}
