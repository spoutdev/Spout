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
package org.spout.api.util.config;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 * @author zml2008
 */
public class ConfigurationNodeTest {
	private Configuration base;
	@Before
	public void setUp() {
		base = new MapConfiguration();
	}

	@Test
	public void testGetPath() {
		ConfigurationNode config = new ConfigurationNode(base, new String[] {"a", "b", "c"}, null);
		assertEquals("a.b.c", config.getPath());
	}

	@Test
	public void testGetKeys() {
		ConfigurationNode parent = base.getChild("a");
		parent.getChild("a1", true).setValue("b1");
		parent.getChild("a2", true).setValue("b2");
		parent.getChild("a3", true).setValue("b3");
		parent.getChild("a3").getChild("c3", true);
		assertEquals(new HashSet<String>(Arrays.asList("a1", "a2", "a3")), parent.getKeys(false));
		assertEquals(Arrays.asList("a"), base.getKeys(false));
		assertEquals(new HashSet<String>(Arrays.asList("a", "a.a1", "a.a2", "a.a3", "a.a3.c3")), base.getKeys(true));
	}
	@Test
	public void testGetValues() {
		ConfigurationNode parent = base.getChild("a");
		Map<String, Object> vals = new HashMap<String, Object>();
		for (int i = 1; i <= 3; ++i) {
			parent.getChild("a" + i).setValue("b" + i);
			vals.put("a" + i, "b" + i);
		}
		assertEquals(vals, parent.getValues());
	}
}
