package org.bukkitcontrib.gui;

import java.util.HashMap;
import java.util.Map;

public enum ScreenType {
	/**
	 * The Game (without any overlays open)
	 */
	GAME(0),
	/**
	 * The Chat overlay
	 */
	CHAT(1),
	/**
	 * A PopupScreen or other Screens made by Spout
	 */
	CUSTOM_SCREEN(2),
	/**
	 * The Screen with the player's inventory
	 */
	PLAYER_INVENTORY(3),
	/**
	 * The Chest inventory Window
	 */
	CHEST_INVENTORY(4),
	/**
	 * The Dispenser inventory Window
	 */
	DISPENSER_INVENTORY(5),
	/**
	 * The Furnace inventory Window
	 */
	FURNACE_INVENTORY(6),
	/**
	 * The Main Menu which appears when you press ESC
	 */
	MAIN_MENU(7),
	/**
	 * The Options main menu
	 */
	OPTIONS_MENU(8),
	/**
	 * The Video settings menu
	 */
	VIDEO_SETTINGS_MENU(9),
	/**
	 * The Controls settings menu
	 */
	CONTROLS_MENU(10),
	/**
	 * The Achievements screen
	 */
	ACHIEVEMENTS_MENU(11),
	/**
	 * The statistics screen
	 */
	STATISTICS_MENU(12),
	/**
	 * The workbench inventory screen
	 */
	WORKBENCH_SCREEN(13),
	/**
	 * The Screen that appears when you edit a sign
	 */
	EDIT_SIGN(14),
	/**
	 * The Screen that appears when you die.
	 */
	GAME_OVER(15),
	/**
	 * The Screen that appears when you go to bed.
	 */
	SLEEP_SCREEN(16);
	
	private final int code;
	private static final Map<Integer, ScreenType> values = new HashMap<Integer, ScreenType>();
	private ScreenType(int code){
		this.code = code;
	}
	
	public int getCode(){
		return code;
	}
	
	public static ScreenType getType(int code){
		return values.get(code);
	}
	
	static {
		for(ScreenType type:values()){
			values.put(type.code, type);
		}
	}
}
