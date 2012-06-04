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
package org.spout.api.datatable;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.spout.api.map.DefaultedKeyImpl;
import org.spout.api.map.DefaultedKey;

public class DataMapTest {
	private static final int RANDOM_SEED = 37;

	String intString = "Int Value";
	int intValue = 1;

	String floatString = "Float Value";
	float floatValue = 4.5F;

	String boolString = "Bool Value";
	boolean boolValue = true;

	String serialString = "Serial Value";
	Serializable serialValue = new Random(RANDOM_SEED);
	
	String defaultedKeyString = "Defaulted Key";
	Integer defaultedValue = 123;
	Integer defaultedPutValue = 234;

	@Test
	public void test() {
		DatatableMap map = new GenericDatatableMap();
		DataMap test = new DataMap(map);

		assertTrue("Map returned incorrect value for isEmpty()", test.isEmpty());

		test.put(intString, intValue);

		assertTrue("Value from map key is incorrect.", (Integer)test.get(intString) == 1);

		test.put(boolString, boolValue);

		assertTrue("Value from map key is incorrect.", (Boolean)test.get(boolString) == boolValue);

		test.put(floatString, floatValue);

		assertTrue("Value from map key is incorrect.", (Float)test.get(floatString) == floatValue);

		test.put(serialString, serialValue);
		
		DefaultedKey<Integer> defaultedKey = new DefaultedKeyImpl<Integer>(defaultedKeyString, defaultedValue);
		
		assertTrue("Defaulted key did not return default value", defaultedValue.equals(test.get(defaultedKey)));
		
		Integer oldValue = test.put(defaultedKey, defaultedPutValue);
		
		assertTrue("Map did not return null for old value when new key was added", oldValue == null);

		Random r = new Random(RANDOM_SEED);
		Random old = (Random)test.get(serialString);

		assertTrue("Randoms did not generate the same value", r.nextDouble() == old.nextDouble());

		assertTrue("Map size is incorrect", test.size() == 5);

		assertTrue("containsKey returned the incorrect response", test.containsKey(intString));

		assertTrue("containsValue returned the incorrect response", test.containsValue(floatValue));

		assertTrue("Map did not remove and return removed value correctly", (Integer)test.remove(intString) == intValue);

		assertTrue("Map size is incorrect", test.size() == 4);

		assertTrue("containsKey returned the incorrect response", !test.containsKey(intString));

		testMapContents(test, true);

		byte[] compressed = map.compress();
		map = new GenericDatatableMap();
		map.decompress(compressed);

		test = new DataMap(map);

		assertTrue("Map size is incorrect", test.size() == 4);

		testMapContents(test, false);
		
		Serializable removedValue = test.remove(defaultedKey);
		assertTrue("Defaulted key did not remove correctly, got " + removedValue + " instead of " + defaultedPutValue, removedValue.equals(defaultedPutValue));

		removedValue = test.remove(defaultedKey);
		assertTrue("Not present defaulted key did not remove correctly, got " + removedValue + " instead of null", test.remove(defaultedKey) == null);

		removedValue = test.remove(floatString);
		assertTrue("String key did not remove correctly, got " + removedValue + " instead of " + floatValue, removedValue.equals(floatValue));

		removedValue = test.remove(floatString);
		assertTrue("Not present string key did not remove correctly, got " + removedValue + " instead of null", test.remove(defaultedKey) == null);
		
		test.clear();

		assertTrue("Map size is incorrect", test.size() == 0);
		assertTrue("Key set size is incorrect", test.keySet().size() == 0);
		assertTrue("Value collection size is incorrect", test.values().size() == 0);
		assertTrue("Entry set size is incorrect", test.entrySet().size() == 0);
	}

	private void testMapContents(DataMap test, boolean matchRandom) {
		Set<String> keySet = test.keySet();

		assertTrue("Key set size is incorrect", keySet.size() == 4);

		for (String key : keySet) {
			assertTrue("Unknown key, [" + key + "]", key.equals(defaultedKeyString) || key.equals(floatString) || key.equals(boolString) || key.equals(serialString));
		}

		Collection<Serializable> values = test.values();

		assertTrue("Value collection size is incorrect", values.size() == 4);

		for (Serializable value : values) {
			if (value instanceof Random && !matchRandom) {
				continue;
			}
			assertTrue("Unknown value, [" + value + "]", value.equals(defaultedPutValue) || value.equals(floatValue) || value.equals(boolValue) || value.equals(serialValue));
		}

		assertTrue("Entry set size is incorrect", test.entrySet().size() == 4);
		
		Serializable[] valueArray = new Serializable[test.size()];
		String[] keyArray = new String[test.size()];
		
		int i = 0;
		
		for (Map.Entry<String, Serializable> e : test.entrySet()) {
			String key = e.getKey();
			Serializable value = e.getValue();
			valueArray[i] = value;
			keyArray[i++] = key;
			assertTrue("Unknown key, [" + key + "]", key.equals(defaultedKeyString) || key.equals(floatString) || key.equals(boolString) || key.equals(serialString));
			if (value instanceof Random && !matchRandom) {
				continue;
			}
			assertTrue("Unknown value, [" + value + "]", value.equals(defaultedPutValue) || value.equals(floatValue) || value.equals(boolValue) || value.equals(serialValue));
		}
		
		for (i = 0; i < keyArray.length; i++) {
			assertTrue("Entry set error, " + keyArray[i] + " linked with " + valueArray[i] + " instead of " + test.get(keyArray[i], null), test.get(keyArray[i], null).equals(valueArray[i]));
		}
		
	}
}
