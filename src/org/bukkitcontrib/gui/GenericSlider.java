package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
public class GenericSlider extends GenericControl implements Slider {

	protected float slider = 0.5f;
	public GenericSlider() {
		
	}
	
	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 4;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		setSliderPosition(input.readFloat());
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeFloat(getSliderPosition());
	}

	@Override
	public float getSliderPosition() {
		return slider;
	}

	@Override
	public Slider setSliderPosition(float value) {
		if (value > 1f) {
			value = 1f;
		}
		else if (value < 0f) {
			value = 0f;
		}
		slider = value;
		return this;
	}
	
	@Override
	public WidgetType getType() {
		return WidgetType.Slider;
	}

	@Override
	public void render() {

	}

}
