package org.getspout.Spout.gui;

import java.util.UUID;

public interface Screen extends Widget{
	
	public Widget[] getAttachedWidgets();
	
	public Screen attachWidget(Widget widget);
	
	public Screen removeWidget(Widget widget);
	
	public boolean containsWidget(Widget widget);
	
	public boolean containsWidget(UUID id);
	
	public Widget getWidget(UUID id);
	
	public boolean updateWidget(Widget widget);
	
	public UUID getId();
	
	public void onTick();

}
