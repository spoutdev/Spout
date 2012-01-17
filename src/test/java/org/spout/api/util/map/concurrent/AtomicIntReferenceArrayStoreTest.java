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
package org.spout.api.util.map.concurrent;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class AtomicIntReferenceArrayStoreTest {

	private final static int MAX_SIZE = 4096;

	private AtomicIntReferenceArrayStore<Integer> store = new AtomicIntReferenceArrayStore<Integer>(MAX_SIZE, 0.5, 0);

	private short[] ids = new short[MAX_SIZE];
	private short[] data = new short[MAX_SIZE];;
	private Integer[] auxData = new Integer[MAX_SIZE];
	private int[] index = new int[MAX_SIZE];
	@Before
	public void setUp() {
		Random rand = new Random();

		for (int i = 0; i < MAX_SIZE; i++) {
			ids[i] = (short)rand.nextInt();
			data[i] = (short)rand.nextInt();
			auxData[i] = rand.nextInt();
			index[i] = -1;
		}
	}

	@Test
	public void testArray() {

		for (int i = 0; i < MAX_SIZE; i++) {
			index[i] = store.add(ids[i], data[i], auxData[i]);
		}

		for (int i = 0; i < MAX_SIZE; i++) {
			int currentIndex = index[i];
			int initialSequence = store.getSequence(currentIndex);
			assertTrue("Record read at index has wrong short data", store.getData(currentIndex) == data[i]);
			assertTrue("Record read at index has wrong short is", store.getId(currentIndex) == ids[i]);
			assertTrue("Record read at index has wrong aux data", store.getAuxData(currentIndex) == auxData[i]);
			assertTrue("Record sequence number changed without write", store.getSequence(currentIndex) == initialSequence);
			store.remove(currentIndex);
			assertTrue("Record sequence number didn't change after a removal", store.getSequence(currentIndex) != initialSequence);
		}

	}
}
