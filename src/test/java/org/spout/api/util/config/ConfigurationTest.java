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
import org.spout.api.exception.ConfigurationException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author zml2008
 */
public class ConfigurationTest {
	private Configuration config;

	@Before
	public void setUp() throws ConfigurationException {
		config = createConfiguration();
		config.load();
	}

	public Configuration createConfiguration() {
		Map<Object, Object> newData = new HashMap<Object, Object>();
		newData.put("string-type", "someString");
		newData.put("int-type", 45);
		Map<Object, Object> testNested = new HashMap<Object, Object>();
		testNested.put("bar", "baz");
		newData.put("foo", testNested);
		return new MapConfiguration(newData);
	}

	@Test
	public void testGetNode() {
		ConfigurationNode node = config.getNode("string-type");
		assertEquals("someString", node.getValue());
		node = config.getNode("foo.bar");
		assertEquals("baz", node.getValue());
	}

	@Test
	public void testPathSeparator() {
		String value = config.getNode("foo.bar").getString();
		assertEquals("baz", value);
		config.setPathSeparator("/");
		value = config.getNode("foo/bar").getString();
		assertEquals("baz", value);
	}
}
