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

import java.io.File;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.*;

public class MemoryConfigurationTest {

	@Test
	public void testValue() {
		MemoryConfiguration config = new MemoryConfiguration(new HashMap<String, Object>(), new HashSet<ConfigurationNode>());
		config.setValue("foo.bar", "baz");
		String value = config.getString("foo.bar");
		config.setPathSeparator("/");
		String value1 = config.getString("foo/bar");
		assertEquals(value, value1);
	}
	
	@Test
	public void testNode() {
		File testFile = new File("test.yml");
		Configuration config = new Configuration(testFile);
		ConfigurationNode node = new ConfigurationNode("foo.bar", "baz");
		config.addNode(node);
		config.save();
		assertEquals(node, config.getNode("foo.bar"));
		testFile.delete();
	}
}
