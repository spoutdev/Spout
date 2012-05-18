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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.spout.api.io.store.simple.FlatFileStore;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.io.store.simple.SimpleStore;

public class StringMapTest {
	private StringMap serverMap;
	private SimpleStore<Integer> serverStore;
	
	private StringMap world1Map;
	private SimpleStore<Integer> world1Store;
	
	private StringMap world2Map;
	private SimpleStore<Integer> world2Store;
	
	private File world1File = new File("world1.dat");
	private File world2File = new File("world2.dat");
	
	private HashMap<String, Integer> serverCache = new HashMap<String, Integer>();
	private HashMap<String, Integer> world1Cache = new HashMap<String, Integer>();
	private HashMap<String, Integer> world2Cache = new HashMap<String, Integer>();
	
	private final String firstKey = "firstKey";
	private final String lastKey = "lastKey";
	private final int minValue = 100;
	private final int maxValue = 200;
	private final int numKeys = maxValue - minValue;
	
	@Before
	public void setUp() {
		
		world1File.delete();
		world2File.delete();
		
		serverStore = new MemoryStore<Integer>();
		serverMap = new StringMap(null, serverStore, minValue, maxValue);
		
		readWorldFiles();
		
	}

	@Test
	public void getNonexistingReturnsNull() {
		fillServerMap();
		assertNull(serverStore.get("unusedKey"));
	}

	@Test
	public void firstKeyReturnsMinValue() {
		fillServerMap();
		assertThat(serverStore.get(firstKey), is(minValue));
	}

	@Test
	public void lastKeyReturnsMaxValue() {
		fillServerMap();
		assertThat(serverStore.get(lastKey), is(maxValue - 1));
	}
	
	@Test(expected=IllegalStateException.class)
	public void mapOverflow() {
		fillServerMap();
		serverMap.register("overflowKey");
	}
	
	@Test
	public void consistentRegistration() {
		fillServerMap();
		fillServerMap();
	}
	
	@Test 
	public void worldToServerSameKeys() {
		
		int numKeys = this.numKeys / 4;
		
		for (int i = 0; i < numKeys; i++) {
			registerWithWorld1Map("key" + i);
			registerWithWorld2Map("key" + i);
		}
		
		checkWorldToServer(numKeys, "key", world1Map);
		checkWorldToServer(numKeys, "key", world2Map);
		
	}
	
	@Test 
	public void worldToServerDiffKeys() {
		
		int numKeys = this.numKeys / 4;
		
		for (int i = 0; i < numKeys; i++) {
			registerWithWorld1Map("key1" + i);
			registerWithWorld2Map("key2" + i);
		}
		
		checkWorldToServer(numKeys, "key1", world1Map);
		checkWorldToServer(numKeys, "key2", world2Map);
		
	}
	
	@Test 
	public void worldToWorld() {
		
		int numKeys = this.numKeys / 4;
		
		for (int i = 0; i < numKeys; i++) {
			registerWithWorld1Map("key" + i);
		}
		
		checkWorldToServer(numKeys, "key", world1Map);
		checkServerToWorld(numKeys, "key", world2Map);
		checkWorldToServer(numKeys, "key", world2Map);
		
	}
	
	@Test
	public void persistTest() {
		int numKeys = this.numKeys / 4;
		
		for (int i = 0; i < numKeys; i++) {
			registerWithWorld1Map("key1" + i);
			registerWithWorld2Map("key2" + i);
		}
		
		world1Map.save();
		world2Map.save();
		
		readWorldFiles();
		
		for (int i = numKeys - 1; i >= 0; i--) {
			registerWithWorld1Map("key1" + i);
			registerWithWorld2Map("key2" + i);
		}
		
		world1File.delete();
		world2File.delete();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ManualRegisterOutOfMinRange() {
		// only ids *below* minValue can be directly set
		world1Map.register("some.block.name1", minValue + 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ManualRegisterOutOfMaxRange() {
		world1Map.register("some.block.name2", maxValue + 1);
	}
	
	public void ManualRegister() {
		assertTrue("Unable to manually register key/value pair", world1Map.register("some.block.name3", 10));
		assertFalse("Registering key twice didn't fail", world1Map.register("some.block.name3", 11));
		assertFalse("Registering id twice didn't fail", world1Map.register("some.block.name4", 10));
		assertTrue("Wrong id for registered key", world1Map.register("some.block.name3") == 10);
		assertTrue("Wrong key for registered id", world1Map.getString(10).equals("some.block.name3"));
	}
	
	private void checkWorldToServer(int numKeys, String prefix, StringMap map) {
		for (int i = 0; i < numKeys; i++) {
			String key = prefix + i;
			Integer worldId = map.register(key);
			assertNotNull(worldId);
			Integer serverId = map.convertTo(serverMap, worldId);
			assertNotNull(serverId);
			Integer getId = registerWithServerMap(key);
			assertThat(serverId, is(getId));
		}
	}
	
	private void checkServerToWorld(int numKeys, String prefix, StringMap map) {
		for (int i = 0; i < numKeys; i++) {
			String key = prefix + i;
			Integer serverId = registerWithServerMap(key);
			assertNotNull(serverId);
			Integer worldId = map.convertFrom(serverMap, serverId);
			assertNotNull(worldId);
			Integer getId = map.register(key);
			assertThat(serverId, is(getId));
		}
	}
	
	private void readWorldFiles() {
		world1Store = new FlatFileStore<Integer>(world1File, Integer.class);
		world1Store.load();
		world1Map = new StringMap(serverMap, world1Store, minValue, maxValue);
		
		world2Store = new FlatFileStore<Integer>(world2File, Integer.class);
		world2Store.load();
		world2Map = new StringMap(serverMap, world2Store, minValue, maxValue);
	}
	
	private void fillServerMap() {
		registerWithServerMap(firstKey);
		for (int i = 0; i < (numKeys - 2); i++) {
			registerWithServerMap("middle" + i);
		}
		registerWithServerMap(lastKey);
		assertThat(serverStore.get(lastKey), is(maxValue - 1));
	}
	
	private int registerWithServerMap(String key) {
		Integer newId = serverMap.register(key);
		Integer oldId = serverCache.put(key, newId);
		if (oldId != null && !oldId.equals(newId)) {
			throw new IllegalStateException("Registering the same key (" + key + ") with the server map gave 2 different results, " + oldId + " -> " + newId);
		}
		return newId;
	}
	
	private int registerWithWorld1Map(String key) {
		Integer newId = world1Map.register(key);
		Integer oldId = world1Cache.put(key, newId);
		if (oldId != null && !oldId.equals(newId)) {
			throw new IllegalStateException("Registering the same key (" + key + ") with the server map gave 2 different results, " + oldId + " -> " + newId);
		}
		return newId;
	}

	private int registerWithWorld2Map(String key) {
		Integer newId = world2Map.register(key);
		Integer oldId = world2Cache.put(key, newId);
		if (oldId != null && !oldId.equals(newId)) {
			throw new IllegalStateException("Registering the same key (" + key + ") with the server map gave 2 different results, " + oldId + " -> " + newId);
		}
		return newId;
	}
}
