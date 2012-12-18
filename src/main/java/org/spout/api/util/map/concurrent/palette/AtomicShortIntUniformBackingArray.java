/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.api.util.map.concurrent.palette;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicShortIntUniformBackingArray extends AtomicShortIntBackingArray {
	
	private final AtomicInteger store;

	public AtomicShortIntUniformBackingArray(int length) {
		this(length, (AtomicShortIntBackingArray) null);
	}
	public AtomicShortIntUniformBackingArray(AtomicShortIntBackingArray previous) {
		this(previous.length(), previous);
	}
	
	private  AtomicShortIntUniformBackingArray(int length, AtomicShortIntBackingArray previous) {
		super(length);
		if (previous == null) {
			store = new AtomicInteger(0);
		} else {
			store = new AtomicInteger(previous.get(0));
		}
		try {
			copyFromPrevious(previous);
		} catch (PaletteFullException e) {
			throw new IllegalStateException("Unable to create uniform block store");
		}
	}
	
	public AtomicShortIntUniformBackingArray(int length, int initial) {
		super(length);
		store = new AtomicInteger(initial);
	}

	@Override
	public int width() {
		return 0;
	}

	@Override
	public int getPaletteSize() {
		return 1;
	}

	@Override
	public int getPaletteUsage() {
		return 1;
	}

	@Override
	public int get(int i) {
		return store.get();
	}

	@Override
	public int set(int i, int newValue) throws PaletteFullException {
		if (!store.compareAndSet(newValue, newValue)) {
			throw paletteFull;
		}
		return newValue;
	}

	@Override
	public boolean compareAndSet(int i, int expect, int update) throws PaletteFullException {
		if (store.get() != expect) {
			return false;
		} else {
			if (expect != update) {
				throw paletteFull;
			}
			return store.compareAndSet(expect, update);
		}
	}

	@Override
	public boolean isPaletteMaxSize() {
		return false;
	}
	@Override
	public int[] getPalette() {
		return new int[] {store.get()};
	}
	@Override
	public int[] getBackingArray() {
		return new int[] {};
	}

}
