package org.bukkitcontrib.gui;

import java.util.UUID;

public interface Screen {
	
	public Widget[] getAttachedWidgets();
	
	public Screen attachWidget(Widget widget);
	
	public Screen removeWidget(Widget widget);
	
	public boolean containsWidget(Widget widget);
	
	public boolean updateWidget(Widget widget);
	
	public UUID getId();
	
	public void onTick();

}
