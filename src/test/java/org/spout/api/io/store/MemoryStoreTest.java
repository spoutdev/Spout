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
package org.spout.api.io.store;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.io.store.simple.SimpleStore;

import com.google.common.collect.Lists;

public class MemoryStoreTest {
	SimpleStore<Integer> subject = new MemoryStore<Integer>();
	String key = "key";
	int value = 1;

	@Test(expected = NullPointerException.class)
	public void notAllowNullKeys() {
		subject.set(null, value);
	}

	@Test(expected = NullPointerException.class)
	public void notAllowNullValues() {
		subject.set(key, null);
	}

	@Test
	public void getKeysOnEmptyIsEmpty() {
		assertThat(subject.getKeys(), hasSize(0));
	}

	@Test
	public void getEntrySetOnEmptyIsEmpty() {
		assertThat(subject.getEntrySet(), hasSize(0));
	}

	@Test
	public void emptyStoreHasZeroSize() {
		assertThat(subject.getSize(), is(0));
	}

	@Test
	public void clearedEmptyStoreHasZeroSize() {
		assertTrue(subject.clear());
		assertThat(subject.getSize(), is(0));
	}

	@Test
	public void getNonexistingReturnsNull() {
		assertNull(subject.get(key));
	}

	@Test
	public void reverseGetNonexistingReturnsNull() {
		assertNull(subject.reverseGet(value));
	}

	@Test
	public void defaultForNonExistingWorks() {
		assertThat(subject.get(key, value), is(value));
	}

	@Test
	public void addValue() {
		assertNull(subject.set(key, value));
	}

	@Test
	public void removingAddedValue() {
		subject.set(key, value);
		assertThat(subject.remove(key), is(value));
	}

	@Test
	public void getAddedValue() {
		subject.set(key, value);
		assertThat(subject.get(key), is(value));
	}

	@Test
	public void getValueWithDefault() {
		assertThat(subject.get(key, value), is(value));
	}

	@Test
	public void reverseGetAdded() {
		subject.set(key, value);
		assertThat(subject.reverseGet(value), is(key));
	}

	@Test
	public void setReturnsReplacedValue() {
		subject.set(key, value);
		assertThat(subject.set(key, value), is(value));
		assertThat(subject.get(key), is(value));
	}

	@Test
	public void updateValue() {
		subject.set(key, value);
		assertThat(subject.get(key), is(value));

		int newValue = 2;
		subject.set(key, newValue);
		assertThat(subject.get(key), is(newValue));
	}

	@Test
	public void clearEmptiesStore() {
		subject.set(key, value);

		assertThat(subject.getKeys(), hasSize(1));
		assertThat(subject.getEntrySet(), hasSize(1));

		subject.clear();

		assertThat(subject.getKeys(), hasSize(0));
		assertThat(subject.getEntrySet(), hasSize(0));
	}

	@Test
	public void extendedUse() {
		List<String> keys = Lists.newArrayList("a", "b", "c", "d", "e");
		List<Integer> values = Lists.newArrayList(1, 2, 3, 4, 5);

		for (int i = 0; i < keys.size(); i++) {
			key = keys.get(i);
			value = values.get(i);

			assertNull(subject.set(key, value));
			assertThat(subject.set(key, value), is(value));
			assertThat(subject.get(key), is(value));

			assertThat(subject.getKeys(), hasSize(i + 1));
			assertThat(subject.getEntrySet(), hasSize(i + 1));
		}

		for (int i = 0; i < 2; i++) {
			key = keys.get(i);
			subject.remove(key);

			assertNull(subject.get(key));
			assertThat(subject.getKeys(), hasSize(keys.size() - 1 - i));
			assertThat(subject.getEntrySet(), hasSize(keys.size() - 1 - i));
		}

		subject.clear();
		assertThat(subject.getKeys(), hasSize(0));
		assertThat(subject.getEntrySet(), hasSize(0));

        for (String key1 : keys) {
            assertNull(subject.get(key1));
        }
	}
}
