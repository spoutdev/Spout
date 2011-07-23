package org.bukkitcontrib.gui;

import java.util.HashMap;

public enum WidgetType {
	Label(0, GenericLabel.class),
	HealthBar(1, HealthBar.class),
	BubbleBar(2, BubbleBar.class),
	ChatBar(3, ChatBar.class),
	ChatTextBox(4, ChatTextBox.class),
	ArmorBar(5, ArmorBar.class),
	Texture(6, GenericTexture.class),
	PopupScreen(7, GenericPopup.class),
	InGameScreen(8, InGameScreen.class),
	ItemWidget(9, GenericItemWidget.class),
	
	;
	
	private final int id;
	private final Class<? extends Widget> widgetClass;
	private static final HashMap<Integer, WidgetType> lookupId = new HashMap<Integer, WidgetType>();
	WidgetType(final int id, final Class<? extends Widget> widgetClass) {
		this.id = id;
		this.widgetClass = widgetClass;
	}
	
	public int getId() {
		return id;
	}
	
	public Class<? extends Widget> getWidgetClass() {
		return widgetClass;
	}
	
	public static WidgetType getWidgetFromId(int id) {
		return lookupId.get(id);
	}
	
	static {
		for (WidgetType packet : values()) {
			lookupId.put(packet.getId(), packet);
		}
	}

}
