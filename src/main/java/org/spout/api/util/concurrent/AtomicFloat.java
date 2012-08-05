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
package org.spout.api.util.concurrent;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements an AtomicFloat class
 *
 * This does not exist in the standard java atomic package
 */
public class AtomicFloat extends Number {
	private static final long serialVersionUID = 1623478887667L;

	private AtomicInteger value = new AtomicInteger();

	public AtomicFloat(float initial) {
		set(initial);
	}

	public AtomicFloat() {
		set(0.0F);
	}

	public final float get() {
		return Float.intBitsToFloat(value.get());
	}

	public final void set(float value) {
		this.value.set(Float.floatToRawIntBits(value));
	}

	public final void lazySet(float value) {
		this.value.lazySet(Float.floatToRawIntBits(value));
	}

	public final float getAndSet(float value) {
		return Float.intBitsToFloat(this.value.getAndSet(Float.floatToRawIntBits(value)));
	}

	public final boolean compareAndSet(float expect, float newValue) {
		return value.compareAndSet(Float.floatToRawIntBits(expect), Float.floatToRawIntBits(newValue));
	}

	public final boolean weakCompareAndSet(float expect, float newValue) {
		return value.weakCompareAndSet(Float.floatToRawIntBits(expect), Float.floatToRawIntBits(newValue));
	}

	private final float add(float delta, boolean returnOldValue) {
		while (true) {
			int oldIntValue = value.get();
			float oldValue = Float.intBitsToFloat(oldIntValue);
			float newValue = oldValue + delta;
			if (value.compareAndSet(oldIntValue, Float.floatToRawIntBits(newValue))) {
				return returnOldValue ? oldValue : newValue;
			}
		}
	}

	public final float getAndAdd(float delta) {
		return add(delta, true);
	}

	public final float addAndGet(float delta) {
		return add(delta, false);
	}

	@Override
	public String toString() {
		return Float.toString(get());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AtomicFloat) {
			return ((AtomicFloat)obj).get() == get();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(get());
	}

	@Override
	public int intValue() {
		return (int) get();
	}

	@Override
	public long longValue() {
		return (long) get();
	}

	@Override
	public float floatValue() {
		return get();
	}

	@Override
	public double doubleValue() {
		return get();
	}
}
