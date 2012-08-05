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

import java.util.HashMap;

public class Locale {
	private String fullName;
	private String code;
	private static final HashMap<String, Locale> byCode = new HashMap<String, Locale>(5);
	
	public static final Locale ENGLISH_US = new Locale("English (USA)", "EN_US");
	public static final Locale ENGLISH_UK = new Locale("English (United Kingdom)", "EN_UK");
	public static final Locale GERMAN_DE = new Locale("Deutsch (Deutschland)", "DE_DE");
	public static final Locale GERMAN_SW = new Locale("Deutsch (Schweiz)", "DE_SW");
	public static final Locale GERMAN_AT = new Locale("Deutsch (Österreich)", "DE_AT");
	
	
	public Locale(String fullName, String code) {
		this.fullName = fullName;
		this.code = code;
		byCode.put(code, this);
	}

	public String getFullName() {
		return fullName;
	}
	
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
			Locale l = new Locale("Unknown", code);
			byCode.put(code, l);
			return l;
		}
	}
}
