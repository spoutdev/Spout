package org.getspout.api.util.concurrent;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements an AtomicFloat class
 * 
 * This does not exist in the standard java atomic package
 */
public class AtomicFloat extends Number implements Serializable{

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
		return this.value.compareAndSet(Float.floatToRawIntBits(expect), Float.floatToRawIntBits(newValue));
	}

	public final boolean weakCompareAndSet(float expect, float newValue) {
		return this.value.weakCompareAndSet(Float.floatToRawIntBits(expect), Float.floatToRawIntBits(newValue));
	}

	private final float add(float delta, boolean returnOldValue) {
		while (true) {
			int oldIntValue = this.value.get();
			float oldValue = Float.intBitsToFloat(oldIntValue);
			float newValue = oldValue + delta;
			if (this.value.compareAndSet(oldIntValue, Float.floatToRawIntBits(newValue))) {
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

	public String toString() {
		return Float.toString(get());
	}

	@Override
	public int intValue() {
		return (int)get();
	}

	@Override
	public long longValue() {
		return (long)get();
	}

	@Override
	public float floatValue() {
		return get();
	}

	@Override
	public double doubleValue() {
		return (double)get();
	}

}
