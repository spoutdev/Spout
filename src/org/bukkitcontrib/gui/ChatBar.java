package org.bukkitcontrib.gui;

import java.util.UUID;

public class ChatBar extends GenericWidget implements Widget{
	public ChatBar() {
		
	}
	
	@Override
	public WidgetType getType() {
		return WidgetType.ChatBar;
	}
	
	public UUID getId() {
		return new UUID(0, 2);
	}
	
	public void render() {
		
	}

}
