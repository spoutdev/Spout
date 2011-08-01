package org.getspout.Spout.gui;

import java.util.UUID;

public class HealthBar extends GenericWidget{

	public HealthBar() {
		
	}
	
	@Override
	public WidgetType getType() {
		return WidgetType.HealthBar;
	}
	
	public UUID getId() {
		return new UUID(0, 4);
	}
	
	public void render() {
		//TODO send health packet
	}

}
