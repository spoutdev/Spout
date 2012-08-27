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
package org.spout.api.io.store;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import org.spout.api.io.store.simple.BinaryFileStore;
import org.spout.api.io.store.simple.SimpleStore;

public class BinaryFileStoreTest {
	File file = new File("test.dat");

	SimpleStore<Integer> subject = new BinaryFileStore(file);

	String[] keys = new String[] {"key1", "key2", "key3", "key4"};
	int[] ids = new int[] {1, 2, -1, 1000, 77};

	@Test
	public void basicCheck() {
		set();
		check();
	}

	@Test
	public void saveReload() {
		file.delete();
		set();
		subject.save();
		subject = new BinaryFileStore(file);
		subject.load();
		check();
		file.delete();
	}

	private void set() {
		for (int i = 0; i < keys.length; i++) {
			subject.set(keys[i], ids[i]);
		}
	}

	private void check() {
		for (int i = 0; i < keys.length; i++) {
			assertTrue("Check mismatch", subject.get(keys[i]).equals(ids[i]) );
		}
	}
}
