package org.bukkitcontrib.gui;

public enum ScreenType {
	GAME(0),
	CHAT(1),
	CUSTOM_SCREEN(2),
	PLAYER_INVENTORY(3),
	CHEST_INVENTORY(4),
	DISPENSER_INVENTORY(5),
	FURNACE_INVENTORY(6),
	MAIN_MENU(7),
	OPTIONS_MENU(8),
	VIDEO_SETTINGS_MENU(9),
	CONTROLS_MENU(10),
	ACHIEVEMENTS_MENU(11),
	STATISTICS_MENU(12),
	WORKBENCH_SCREEN(13),
	EDIT_SIGN(14),
	GAME_OVER(15),
	SLEEP_SCREEN(16);
	
	
	private final int code;
	private ScreenType(int code){
		this.code = code;
	}
	
	public int getCode(){
		return code;
	}
}
