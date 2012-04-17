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

import org.junit.Test;
import org.spout.api.exception.ConfigurationException;

import static org.junit.Assert.*;

/**
 * @author zml2008
 */
public class ConfigurationHolderConfigurationTest {
	private static class TestConfig extends ConfigurationHolderConfiguration {
		public static final int TEST_ONE_VALUE = 42;
		public static final ConfigurationHolder TEST_ONE = new ConfigurationHolder(TEST_ONE_VALUE, "test", "one");
		public static final String TEST_TWO_VALUE = "richard";
		public static final ConfigurationHolder TEST_TWO = new ConfigurationHolder(TEST_TWO_VALUE, "test", "two");

		public TestConfig(Configuration base) {
			super(base);
		}
	}

	@Test
	public void testConfigSet() {
		TestConfig testConfig = new TestConfig(new MapConfiguration());
		assertEquals(testConfig.getConfiguration(), TestConfig.TEST_ONE.getConfiguration());
		assertEquals(testConfig.getConfiguration(), TestConfig.TEST_TWO.getConfiguration());
	}

	@Test
	public void testSetDefaultValues() throws ConfigurationException {
		TestConfig testConfig = new TestConfig(new MapConfiguration());
		testConfig.load();
		assertEquals(TestConfig.TEST_ONE_VALUE, testConfig.getConfiguration().getNode(TestConfig.TEST_ONE.getPathElements()).getValue());
		assertEquals(TestConfig.TEST_TWO_VALUE, testConfig.getConfiguration().getNode(TestConfig.TEST_TWO.getPathElements()).getValue());
	}
}
