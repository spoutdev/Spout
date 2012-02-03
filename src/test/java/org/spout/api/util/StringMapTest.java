/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNull;

import org.spout.api.io.store.simple.MemoryStore;
import org.junit.Before;
import org.junit.Test;

public class StringMapTest {
	private StringMap subject;
	private MemoryStore<Integer> store;
	private final String firstKey = "firstKey";
	private final String lastKey = "lastKey";
	private final int minValue = 0;
	private final int maxValue = 100;

	@Before
	public void setUp() {
		store = new MemoryStore<Integer>();
		subject = new StringMap(null, store, minValue, maxValue);

		subject.register(firstKey);
		for (int i = 0; i < (maxValue - 2); i++) {
			subject.register("middle" + i);
		}
		subject.register(lastKey);
	}

	@Test
	public void getNonexistingReturnsNull() {
		assertNull(store.get("unusedKey"));
	}

	@Test
	public void firstKeyReturnsMinValue() {
		assertThat(store.get(firstKey), is(minValue));
	}

	@Test
	public void lastKeyReturnsMaxValue() {
		assertThat(store.get(lastKey), is(maxValue - 1));
	}

	//TODO: @raphfrk Please write some tests here in the same format for moving between maps.
	//Stuff like convertTo, convertFrom, and parent map functionality should be in unit tests
	//Then we will notice immediately should they ever break.
}
