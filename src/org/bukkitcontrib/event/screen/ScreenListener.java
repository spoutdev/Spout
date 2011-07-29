package org.bukkitcontrib.event.screen;

import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public class ScreenListener extends CustomEventListener implements Listener{
	
	public void onScreenOpen(ScreenOpenEvent event) {
		
	}
	
	public void onButtonClick(ButtonClickEvent event) {
		
	}
	
	public void onSliderDrag(SliderDragEvent event) {
		
	}
	
	public void onTextFieldChange(TextFieldChangeEvent event) {
		
	}
	
	public void onScreenClose(ScreenCloseEvent event) {
		
	}
	
	@Override
	public void onCustomEvent(Event event) {
		if (event instanceof ScreenOpenEvent) {
			onScreenOpen((ScreenOpenEvent)event);
		}
		else if (event instanceof ButtonClickEvent) {
			onButtonClick((ButtonClickEvent)event);
		}
		else if (event instanceof SliderDragEvent) {
			onSliderDrag((SliderDragEvent)event);
		}
		else if (event instanceof TextFieldChangeEvent) {
			onTextFieldChange((TextFieldChangeEvent)event);
		}
		else if (event instanceof ScreenCloseEvent) {
			onScreenClose((ScreenCloseEvent)event);
		}
	}

}
