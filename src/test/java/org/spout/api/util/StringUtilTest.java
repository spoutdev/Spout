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
package org.spout.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class StringUtilTest {

	private static class NameTest implements Named {
		private final String name;
		public NameTest(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	private List<NameTest> values = new ArrayList<NameTest>();

	@Before
	public void setUp() {
		values.add(new NameTest("on"));
		values.add(new NameTest("dummyvalue1"));
		values.add(new NameTest("dummyvalue2"));
		values.add(new NameTest("one"));
		values.add(new NameTest("onetwo"));
		values.add(new NameTest("two"));
		values.add(new NameTest("three"));
		values.add(new NameTest("THREE"));
	}

	@After
	public void cleanUp() {
		values.clear();
	}

	private void assertValuesEquals(Collection<NameTest> values, String... names) {
		assertEquals(names.length, values.size());
		for (String name : names) {
			boolean found = false;
			for (NameTest value : values) {
				if (value.getName().equals(name)) {
					found = true;
					break;
				}
			}
			assertTrue("Value " + name + " not found", found);
		}
		for (NameTest value : values) {
			boolean found = false;
			for (String name : names) {
				if (value.getName().equals(name)) {
					found = true;
					break;
				}
			}
			assertTrue("Value " + value.getName() + " not expected", found);
		}
	}

	@Test
	public void testMatching() {
		assertValuesEquals(StringUtil.matchName(values, "dummyvalue"), "dummyvalue1", "dummyvalue2");
		assertValuesEquals(StringUtil.matchName(values, "one"), "one", "onetwo");
		assertValuesEquals(StringUtil.matchName(values, "two"), "two");
		assertValuesEquals(StringUtil.matchName(values, "three"), "three", "THREE");
		assertEquals(StringUtil.getShortest(values).getName(), "on");
	}
}
