package org.getspout.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.GuiButton;
import net.minecraft.src.BukkitContrib;
public class GenericSlider extends GenericControl implements Slider {

	protected float slider = 0.5f;
	private CustomGuiSlider sliderControl = null;
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
	
	protected void setup(int x, int y) {
		this.x = x;
		this.y = y;
	}
	private int x;
	private int y;

	@Override
	public void render() {
		if (sliderControl == null) {
			boolean success = false;
			if (BukkitContrib.getGameInstance().currentScreen instanceof CustomScreen) {
				CustomScreen popup = (CustomScreen)BukkitContrib.getGameInstance().currentScreen;
				for (GuiButton control : popup.getControlList()) {
					if (control instanceof CustomGuiSlider) {
						if (control.equals(this)) {
							sliderControl = (CustomGuiSlider)control;
							sliderControl.updateWidget(this);
							success = true;
							break;
						}
					}
				}
				if (!success) {
					sliderControl = new CustomGuiSlider(getScreen(), this);
					popup.getControlList().add(sliderControl);
				}
			}
		}
		sliderControl.drawButton(BukkitContrib.getGameInstance(), x, y);
	}

}
