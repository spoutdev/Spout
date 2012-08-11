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

import java.util.LinkedHashMap;

/**
 * Implements LocaleNumberHandler for the most languages<br/>
 * <strong>Behaviour</strong><br/>
 * Returns the base string for singular when number == 1, else returns the plural
 */
public class DefaultNumberHandler extends LocaleNumberHandler {
	private String singular, plural;
	
	public DefaultNumberHandler() {
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Object yaml) {
		if (yaml instanceof LinkedHashMap<?, ?>) {
			LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) yaml;
			singular = map.get("singular");
			plural = map.get("plural");
		} else {
			throw new IllegalArgumentException("The YAML for this key must contain a map with the 2 keys 'singular' and 'plural'");
		}
	}

	@Override
	public String getString(Number number) {
		String use = plural;
		if (isDiscreteNumber(number)) {
			if (number.intValue() == 1) {
				use = singular;
			}
		}
		return use.replaceAll("%n", number.toString());
	}

	@Override
	public Object save() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("singular", singular);
		map.put("plural", plural);
		return map;
	}

	@Override
	public void init(String placeholder) {
		singular = plural = placeholder;
	}
}
