package org.bukkitcontrib.gui;

public interface InGameHUD extends Screen{
	
	public ArmorBar getArmorBar();
	
	public ChatTextBox getChatTextBox();
	
	public ChatBar getChatBar();
	
	public BubbleBar getBubbleBar();
	
	public HealthBar getHealthBar();
	
	public boolean canAttachWidget(Widget widget);
	
	public boolean attachPopupScreen(PopupScreen screen);
	
	public PopupScreen getActivePopup();
	
	public boolean closePopup();

}
