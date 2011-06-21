package org.bukkitcontrib.gui;

public interface Screen {
	
	public Widget[] getAttachedWidgets();
	
	public Screen attachWidget(Widget widget);
	
	public Screen removeWidget(Widget widget);

}
