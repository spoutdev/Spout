package org.getspout.gui;

public interface Slider extends Control{
	
	/**
	 * Gets the slider position (between 0.0f and 1.0f)
	 * @return slider position
	 */
	public float getSliderPosition();
	
	/**
	 * Sets the slider position. Values below 0.0f are rounded to 0, and values above 1.0f are rounded to 1
	 * @param value to set
	 * @return slider
	 */
	public Slider setSliderPosition(float value);
}
