package org.bukkitcontrib.event.screen;

import org.bukkitcontrib.gui.Screen;
import org.bukkitcontrib.gui.Slider;
import org.bukkitcontrib.player.ContribPlayer;

public class SliderDragEvent extends ScreenEvent {
	protected Slider slider;
	protected float position;
	protected float old;
	public SliderDragEvent(ContribPlayer player, Screen screen, Slider slider, float position) {
		super("SliderDragEvent", player, screen);
		this.slider = slider;
		this.position = position;
		this.old = slider.getSliderPosition();
	}
	
	public Slider getSlider() {
		return slider;
	}
	
	public float getOldPosition() {
		return old;
	}
	
	public float getNewPosition() {
		return position;
	}
	
	public void setNewPosition(float position) {
		this.position = position;
	}

}
