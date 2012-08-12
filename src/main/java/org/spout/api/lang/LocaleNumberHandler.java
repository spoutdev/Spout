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

import java.math.BigInteger;

public abstract class LocaleNumberHandler {
	/**
	 * Initializes the object from the given yaml
	 * @param locale
	 * @param yaml
	 */
	public abstract void init(Object yaml);
	
	/**
	 * Initializes the object with the placeholder
	 * All alternatives should have the placeholder
	 * @param placeholder the placeholder to set to all alternative strings
	 */
	public abstract void init(String placeholder);
	
	/**
	 * Dumps the contents to an object that SnakeYAML can dump
	 * @return
	 */
	public abstract Object save();
	
	/**
	 * @param number
	 * @return the string corresponding to the given number (i.e. plural, singular, etc)
	 */
	public abstract String getString(Number number);
	
	public static boolean isDiscreteNumber(Number num) {
		return num instanceof Integer || num instanceof Byte || num instanceof Short || num instanceof Long || num instanceof BigInteger;
	}
}
