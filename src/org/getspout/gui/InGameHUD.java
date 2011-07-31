package org.getspout.gui;

public interface InGameHUD extends Screen{
	
	/**
	 * Gets the armor bar associated with this HUD
	 * @return armor bar
	 */
	public ArmorBar getArmorBar();
	
	/**
	 * Gets the chat text box associated with this HUD
	 * @return chat text box
	 */
	public ChatTextBox getChatTextBox();
	
	/**
	 * Gets the chat text bar associated with this HUD
	 * @return chat bar
	 */
	public ChatBar getChatBar();
	
	/**
	 * Gets the underwater bubble bar associated with this HUD
	 * @return bubble bar
	 */
	public BubbleBar getBubbleBar();
	
	/**
	 * Gets the health bar associated with this HUD
	 * @return health bar
	 */
	public HealthBar getHealthBar();
	
	/**
	 * Is true if the widget can be attached to the screen.
	 * Primary controls, like the health bar can not be attached twice.
	 * Control widgets that require input from the mouse or keyboard can not be attached
	 * @param widget
	 * @return true if the widge can be attached
	 */
	public boolean canAttachWidget(Widget widget);
	
	/**
	 * Attachs a popup screen and brings it to the front of the screen
	 * @param screen to pop up
	 * @return true if the popup screen was attached, false if there was already a popup launched
	 */
	public boolean attachPopupScreen(PopupScreen screen);
	
	/**
	 * Gets the active popup screen for this player, or null if none available
	 * @return the active popup
	 */
	public PopupScreen getActivePopup();
	
	/**
	 * Closes the popup screen, or returns false on failure
	 * @return true if a popup screen was closed
	 */
	public boolean closePopup();

}
