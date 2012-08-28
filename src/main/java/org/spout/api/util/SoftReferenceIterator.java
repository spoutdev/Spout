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
package org.spout.api.util;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This iterator can traverse a collection of soft references<br>
 * It automatically removes the empty (disposed content) soft references.
 */
public class SoftReferenceIterator <T> implements Iterator<T> {

	private final Iterator<SoftReference<T>> iterator;
	private T next;
	private boolean needGenNext;

	public SoftReferenceIterator(Collection<SoftReference<T>> collection) {
		this(collection.iterator());
	}

	public SoftReferenceIterator(Iterator<SoftReference<T>> iterator) {
		this.iterator = iterator;
		this.needGenNext = true;
	}

	private void genNext() {
		this.needGenNext = false;
		this.next = null;
		while (iterator.hasNext()) {
			this.next = iterator.next().get();
			if (this.next == null) {
				iterator.remove();
			} else {
				return;
			}
		}
	}

	@Override
	public boolean hasNext() {
		if (this.needGenNext) {
			this.genNext();
		}
		return this.next != null;
	}

	@Override
	public T next() {
		if (this.needGenNext) {
			this.genNext();
		}
		if (this.next == null) {
			throw new NoSuchElementException();
		}
		this.needGenNext = true;
		return this.next;
	}

	@Override
	public void remove() {
		if (this.next == null) {
			throw new IllegalStateException();
		}
		this.iterator.remove();
	}
}
