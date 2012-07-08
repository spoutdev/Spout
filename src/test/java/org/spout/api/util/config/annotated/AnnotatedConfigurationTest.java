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
package org.spout.api.util.config.annotated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.Configuration;
import org.spout.api.util.config.MapConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for AnnotatedConfiguration
 */
public class AnnotatedConfigurationTest {
	public static enum TestEnum {
		ONE, TWO,
	}

	// Constants for easier accurate results
	private static final String BOOLEAN_KEY = "boolean-setting";
	private static final boolean BOOLEAN_VALUE = true;

	private static final String INT_KEY = "int-setting";
	private static final int INT_VALUE = 42;

	private static final String[] NESTED_STRING_KEY = new String[]{"nested", "key"};
	private static final String NESTED_STRING_VALUE = "cute asian cadvahns";

	private static final String[] MAP_STRING_STRING_KEY = new String[]{"map", "string-string"};
	private static final Map<String, String> MAP_STRING_STRING_VALUE = createMapStringString();

	private static final String SET_INTEGER_KEY = "int-set";
	private static final Set<Integer> SET_INTEGER_VALUE = new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5));

	private static final String SET_ENUM_KEY = "enum-set";
	private static final Set<TestEnum> SET_ENUM_VALUE = new HashSet<TestEnum>(Arrays.asList(TestEnum.values()));

	private static final String[] NESTED_MAP_KEY = new String[]{"map", "nested"};
	private static final Map<String, Map<?, ?>> NESTED_MAP_VALUE = createNestedMap();

	private static final String ENUM_KEY = "enum";
	private static final TestEnum ENUM_VALUE = TestEnum.TWO;

	private static final String CONFIG_BASE_KEY = "configbase";

	private static final String DEFAULT_KEY = "dead";
	private static final String DEFAULT_VALUE = "parrot";

	private static class LocalConfiguration extends AnnotatedConfiguration {
		@Setting(BOOLEAN_KEY) public boolean booleanSetting;
		@Setting(INT_KEY) public int intSetting;
		@Setting({"nested", "key"}) public String nestedStringSetting;
		@Setting({"map", "string-string"}) public Map<String, String> mapStringStringSetting;
		@Setting(SET_INTEGER_KEY) public Set<Integer> setIntegerSetting;
		@Setting(SET_ENUM_KEY) public Set<TestEnum> setEnumSetting;
		@Setting({"map", "nested"}) public Map<String, Map<String, Object>> nestedMapSetting;
		@Setting(ENUM_KEY) public TestEnum enumSetting;
		@Setting(CONFIG_BASE_KEY) public SubConfiguration subConfigSetting;
		@Setting(DEFAULT_KEY) public String defaultSetting = DEFAULT_VALUE;

		public LocalConfiguration(Configuration baseConfig) {
			super(baseConfig);
		}
	}

	public static class SubConfiguration extends AnnotatedConfiguration {

		public SubConfiguration(Configuration baseConfig) {
			super(baseConfig);
		}

		@Setting("a") public String aSetting;
		@Setting("b") public String bSetting;
	}

	private static Map<String, String> createMapStringString() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("hello", "world");
		result.put("command", "book");
		return result;
	}

	private static Map<String, Map<?, ?>> createNestedMap() {
		Map<String, Map<?, ?>> result = new HashMap<String, Map<?, ?>>();
		result.put("one", (Map<?, ?>) createMapStringString());
		Map<String, Object> two = new HashMap<String, Object>();
		two.put("something", "else");
		two.put("andnowforsomething", "completelydifferent");
		result.put("two", two);
		return result;
	}

	// The real tests!

	protected Configuration config;
	protected LocalConfiguration annotatedConfig;

	@Before
	public void setUp() throws ConfigurationException {
		config = new MapConfiguration();
		config.getNode(BOOLEAN_KEY).setValue(BOOLEAN_VALUE);
		config.getNode(INT_KEY).setValue(INT_VALUE);
		config.getNode(NESTED_STRING_KEY).setValue(NESTED_STRING_VALUE);
		config.getNode(MAP_STRING_STRING_KEY).setValue(MAP_STRING_STRING_VALUE);
		List<Integer> sortedIntList = new ArrayList<Integer>(SET_INTEGER_VALUE);
		Collections.sort(sortedIntList);
		config.getNode(SET_INTEGER_KEY).setValue(sortedIntList);

		List<String> enumNames = new ArrayList<String>(SET_ENUM_VALUE.size());
		for (TestEnum val : SET_ENUM_VALUE) {
			enumNames.add(val.name());
		}
		Collections.sort(enumNames);
		config.getNode(SET_ENUM_KEY).setValue(enumNames);
		config.getNode(NESTED_MAP_KEY).setValue(NESTED_MAP_VALUE);
		config.getNode(ENUM_KEY).setValue(ENUM_VALUE.name());
		config.save();
		annotatedConfig = new LocalConfiguration(config);
		annotatedConfig.load();
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testSaving() throws ConfigurationException {
		MapConfiguration saveConfig = new MapConfiguration();
		annotatedConfig.save(saveConfig);
		Map<String, Object> values = config.getValues();
		SubConfiguration subConfig = new SubConfiguration(new MapConfiguration());
		subConfig.save();
		Map<String, Object> saveValues = saveConfig.getValues();
		Collections.sort((List) saveValues.get(SET_ENUM_KEY));
		Collections.sort((List) saveValues.get(SET_INTEGER_KEY));
		values.put(CONFIG_BASE_KEY, subConfig.getValues());
		assertEquals(values, saveValues);
	}

	@Test
	public void testBooleanValue() {
		assertEquals(BOOLEAN_VALUE, annotatedConfig.booleanSetting);
	}

	@Test
	public void testIntValue() {
		assertEquals(INT_VALUE, annotatedConfig.intSetting);
	}

	@Test
	public void testNestedStringValue() {
		assertEquals(NESTED_STRING_VALUE, annotatedConfig.nestedStringSetting);
	}

	@Test
	public void testMapStringStringValue() {
		assertEquals(MAP_STRING_STRING_VALUE, annotatedConfig.mapStringStringSetting);
	}

	@Test
	public void testSetIntegerValue() {
		assertEquals(SET_INTEGER_VALUE, annotatedConfig.setIntegerSetting);
	}

	@Test
	public void testNestedMapValue() {
		assertEquals(NESTED_MAP_VALUE, annotatedConfig.nestedMapSetting);
	}

	@Test
	public void testEnumValue() {
		assertEquals(ENUM_VALUE, annotatedConfig.enumSetting);
	}

	@Test
	public void testDefaultValue() {
		assertEquals(DEFAULT_VALUE, annotatedConfig.defaultSetting);
	}

	@Test
	public void testSetEnumValue() {
		assertEquals(SET_ENUM_VALUE, annotatedConfig.setEnumSetting);
	}

	@Test
	public void testAnnotatedConfigurationValue() throws ConfigurationException {
		assertNotNull(annotatedConfig.subConfigSetting);
		SubConfiguration second = new SubConfiguration(new MapConfiguration());
		second.load();
		annotatedConfig.save();
		assertEquals(second.getValues(), annotatedConfig.getNode(CONFIG_BASE_KEY).getValues());
	}

}
