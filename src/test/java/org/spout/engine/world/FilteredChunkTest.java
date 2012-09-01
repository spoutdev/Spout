/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.world;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.spout.api.geo.AreaBlockAccess;
import org.spout.api.geo.AreaBlockSource;

public class FilteredChunkTest {
	/**
	 * Contains a list of methods that are unrelated to the blockstore.
	 * Methods that interact with the blockstore should not be exempt!
	 */
	private static Set<String> exemptions = new HashSet<String>() {{
		add("getBlockController");
		add("setBlockController");
		add("getBiome");
		add("getBlock");
		add("containsBlock");
		//Handled by getBlockDataField
		add("isBlockDataBitSet");
		//Handled by setBlockDataFieldRaw
		add("setBlockData");
		add("setBlockDataField");
		add("setBlockDataBits");
		add("clearBlockDataBits");
		//Handled by addBlockDataFieldRaw
		add("addBlockData");
		add("addBlockDataField");
	}};

	@Test
	public void test() throws SecurityException {
		for (Method m : AreaBlockSource.class.getDeclaredMethods()) {
			if (!exemptions.contains(m.getName())) {
				if (!hasMethod(FilteredChunk.class, m.getName(), m.getParameterTypes())) {
					fail("FilteredChunk does not override " + m.getName() + " in AreaBlockSource");
				}
			}
		}
		for (Method m : AreaBlockAccess.class.getDeclaredMethods()) {
			if (!exemptions.contains(m.getName())) {
				if (!hasMethod(FilteredChunk.class, m.getName(), m.getParameterTypes())) {
					fail("FilteredChunk does not override " + m.getName() + " in AreaBlockAccess");
				}
			}
		}
	}

	private static boolean hasMethod(Class<?> clazz, String name, Class<?>[] params) {
		try {
			return clazz.getDeclaredMethod(name, params) != null;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}
}
