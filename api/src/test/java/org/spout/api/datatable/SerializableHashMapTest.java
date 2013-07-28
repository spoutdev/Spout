/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.datatable;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import org.spout.api.inventory.Inventory;
import org.spout.api.map.DefaultedKey;
import org.spout.api.map.DefaultedKeyImpl;


public class SerializableHashMapTest {

    private static final int RANDOM_SEED = 37;
    String intString = "Int Value";
    int intValue = 1;
    String floatString = "Float Value";
    float floatValue = 4.5F;
    String boolString = "Bool Value";
    boolean boolValue = true;
    String randomString = "Random Value";
    Serializable randomValue = new Random(RANDOM_SEED);
    String invString = "Inventory Value";
    Inventory invValue = new Inventory(5);
    String defaultedKeyString = "Defaulted Key";
    Integer defaultedValue = 123;
    Integer defaultedPutValue = 234;

    @Test
    public void test() throws IOException {
        SerializableHashMap test = new SerializableHashMap();

        assertTrue("Map returned incorrect value for isEmpty()", test.isEmpty());

        test.put(intString, intValue);
        assertTrue("Value from map key is incorrect.", (Integer) test.get(intString) == 1);

        test.put(boolString, boolValue);
        assertTrue("Value from map key is incorrect.", (Boolean) test.get(boolString) == boolValue);

        test.put(floatString, floatValue);
        assertTrue("Value from map key is incorrect.", (Float) test.get(floatString) == floatValue);

        test.put(invString, invValue);
        assertTrue("Value from map key is incorrect.", test.get(invString) == invValue);

        test.put(randomString, randomValue);

        DefaultedKey<Integer> defaultedKey = new DefaultedKeyImpl<>(defaultedKeyString, defaultedValue);

        Integer getValue = test.get(defaultedKey);

        assertTrue("Map did not return default value when new key was added", getValue.equals(defaultedValue));

        Integer getValue2 = test.get(defaultedKey);

        assertTrue("Map did not return default value when new key was added", getValue2.equals(defaultedValue));

        Integer putReturnValue = test.put(defaultedKey, defaultedPutValue);

        assertTrue("Defaulted key did not return default value", defaultedValue.equals(putReturnValue));

        assertTrue("Map did not return the same instance for the same key, checking both gets", getValue == getValue2);

        assertTrue("Map did not return the same instance for the same key, checking get and put", getValue == putReturnValue);

        Integer getValue3 = test.get(defaultedKey);

        assertTrue("Map did not return the correct value after put", defaultedPutValue.equals(getValue3));

        Random r = new Random(RANDOM_SEED);
        Random old = (Random) test.get(randomString);

        assertTrue("Randoms did not generate the same value", r.nextDouble() == old.nextDouble());

        assertTrue("Map size is incorrect", test.size() == 6);

        assertTrue("containsKey returned the incorrect response", test.containsKey(intString));

        assertTrue("containsValue returned the incorrect response", test.containsValue(floatValue));

        assertTrue("Map did not remove and return removed value correctly", (Integer) test.remove(intString) == intValue);

        assertTrue("Map size is incorrect", test.size() == 5);

        assertTrue("containsKey returned the incorrect response", !test.containsKey(intString));

        testMapContents(test, true);

        byte[] compressed = test.serialize();
        SerializableHashMap map = new SerializableHashMap();
        map.deserialize(compressed);

        test = map;

        assertTrue("Map size is incorrect", test.size() == 5);

        testMapContents(test, false);

        Serializable removedValue = test.remove(defaultedKey);
        assertTrue("Defaulted key did not remove correctly, got " + removedValue + " instead of " + defaultedPutValue, removedValue.equals(defaultedPutValue));

        removedValue = test.remove(defaultedKey);
        assertTrue("Not present defaulted key did not remove correctly, got " + removedValue + " instead of null", test.remove(defaultedKey) == null);

        removedValue = test.remove(invString);
        assertTrue("String key did not remove correctly, got " + removedValue + " instead of " + invValue, removedValue.equals(invValue));

        removedValue = test.remove(floatString);
        assertTrue("String key did not remove correctly, got " + removedValue + " instead of " + floatValue, removedValue.equals(floatValue));

        removedValue = test.remove(floatString);
        assertTrue("Not present string key did not remove correctly, got " + removedValue + " instead of null", test.remove(defaultedKey) == null);

        test.clear();

        assertTrue("Map size is incorrect", test.isEmpty());
        assertTrue("Key set size is incorrect. Was " + test.keySet().size() + " but 0 was expected.", test.keySet().isEmpty());
        assertTrue("Value collection size is incorrect", test.values().isEmpty());
        assertTrue("Entry set size is incorrect", test.entrySet().isEmpty());
    }

    private void testMapContents(SerializableHashMap test, boolean matchRandom) {
        Set<String> keySet = test.keySet();

        assertTrue("Key set size is incorrect", keySet.size() == 5);

        for (String key : keySet) {
            assertTrue("Unknown key, [" + key + "]", key.equals(defaultedKeyString) || key.equals(floatString) || key.equals(boolString) || key.equals(randomString) || key.equals(invString));
        }

        Collection<Serializable> values = test.values();

        assertTrue("Value collection size is incorrect", values.size() == 5);

        for (Serializable value : values) {
            if (value instanceof Random && !matchRandom) {
                continue;
            }
            System.out.println(invValue);
            assertTrue("Unknown value, [" + value + "]", value.equals(defaultedPutValue) || value.equals(floatValue) || value.equals(boolValue) || value.equals(randomValue) || value.equals(invValue));
        }

        assertTrue("Entry set size is incorrect", test.entrySet().size() == 5);

        Serializable[] valueArray = new Serializable[test.size()];
        String[] keyArray = new String[test.size()];

        int i = 0;

        for (Map.Entry<String, Serializable> e : test.entrySet()) {
            String key = e.getKey();
            Serializable value = e.getValue();
            valueArray[i] = value;
            keyArray[i++] = key;
            assertTrue("Unknown key, [" + key + "]", key.equals(defaultedKeyString) || key.equals(floatString) || key.equals(boolString) || key.equals(randomString) || key.equals(invString));
            if (value instanceof Random && !matchRandom) {
                continue;
            }
            assertTrue("Unknown value, [" + value + "]", value.equals(defaultedPutValue) || value.equals(floatValue) || value.equals(boolValue) || value.equals(randomValue) || value.equals(invValue));
        }

        for (i = 0; i < keyArray.length; i++) {
            assertTrue("Entry set error, " + keyArray[i] + " linked with " + valueArray[i] + " instead of " + test.get(keyArray[i], (Serializable) null), test.get(keyArray[i], (Serializable) null).equals(valueArray[i]));
        }
    }
}
