/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 * 
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.datatable;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

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
		
		Random r = new Random(RANDOM_SEED);
		Random old = (Random)test.get(serialString);
		
		assertTrue("Randoms did not generate the same value", r.nextDouble() == old.nextDouble());
		
		assertTrue("Map size is incorrect", test.size() == 4);
		
		assertTrue("containsKey returned the incorrect response", test.containsKey(intString));
		
		assertTrue("containsValue returned the incorrect response", test.containsValue(floatValue));
		
		assertTrue("Map did not remove and return removed value correctly", (Integer)test.remove(intString) == intValue);
		
		assertTrue("Map size is incorrect", test.size() == 3);
		
		assertTrue("containsKey returned the incorrect response", !test.containsKey(intString));
		
		testMapContents(test);
		
		byte[] compressed = map.compress();
		map = new GenericDatatableMap();
		map.decompress(compressed);
		
		test = new DataMap(map);
		
		assertTrue("Map size is incorrect", test.size() == 3);
		
		testMapContents(test);
		
		test.clear();
		
		assertTrue("Map size is incorrect", test.size() == 0);
		assertTrue("Key set size is incorrect", test.keySet().size() == 0);
		assertTrue("Value collection size is incorrect", test.values().size() == 0);
		assertTrue("Entry set size is incorrect", test.entrySet().size() == 0);
	}
	
	private void testMapContents(DataMap test) {
		Set<String> keySet = test.keySet();
		
		assertTrue("Key set size is incorrect", keySet.size() == 3);
		
		for (String key : keySet) {
			assertTrue("Unknown key, [" + key + "]", key.equals(floatString) || key.equals(boolString) || key.equals(serialString));
		}
		
		Collection<Serializable> values = test.values();
		
		assertTrue("Value collection size is incorrect", values.size() == 3);
		
		for (Serializable value : values) {
			assertTrue("Unknown value, [" + value + "]", value.equals(floatValue) || value.equals(boolValue) || value.equals(serialValue));
		}
		
		assertTrue("Entry set size is incorrect", test.entrySet().size() == 3);
		for (Map.Entry<String, Serializable> e : test.entrySet()) {
			String key = e.getKey();
			Serializable value = e.getValue();
			assertTrue("Unknown key, [" + key + "]", key.equals(floatString) || key.equals(boolString) || key.equals(serialString));
			assertTrue("Unknown value, [" + value + "]", value.equals(floatValue) || value.equals(boolValue) || value.equals(serialValue));
		}
	}

}