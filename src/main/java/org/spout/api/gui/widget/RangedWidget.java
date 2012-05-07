package org.spout.api.gui.widget;

import org.spout.api.gui.Widget;

public interface RangedWidget<T extends Number> extends Widget {
	/**
	 * Sets the range
	 * @param min the minimum value of the range
	 * @param max the maximum value of the range
	 * @return the instance
	 * @throws java.lang.InvalidStateException when min >= max
	 */
	public RangedWidget<T> setRange(T min, T max);
	
	/**
	 * Sets the value
	 * @param value the value. Must be inside the range
	 * @return the instance
	 * @throws java.lang.InvalidStateException when value outside the range
	 */
	public RangedWidget<T> setValue(T value);
	
	/**
	 * Gets the value
	 * @return the value
	 */
	public T getValue();
	
	/**
	 * Gets the min part of the range
	 * @return the minimum value
	 */
	public T getRangeMin();
	
	/**
	 * Gets the max part of the range
	 * @return the maximum value
	 */
	public T getRangeMax();
}
