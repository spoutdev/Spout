package org.bukkitcontrib.gui;

import java.util.HashMap;
import java.util.Map;

public enum ScreenType {
	GAME_SCREEN(0),
	CHAT_SCREEN(1),
	CUSTOM_SCREEN(2),
	PLAYER_INVENTORY(3),
	CHEST_INVENTORY(4),
	DISPENSER_INVENTORY(5),
	FURNACE_INVENTORY(6),
	INGAME_MENU(7),
	OPTIONS_MENU(8),
	VIDEO_SETTINGS_MENU(9),
	CONTROLS_MENU(10),
	ACHIEVEMENTS_SCREEN(11),
	STATISTICS_SCREEN(12),
	WORKBENCH_INVENTORY(13),
	SIGN_SCREEN(14),
	GAME_OVER_SCREEN(15),
	SLEEP_SCREEN(16),
	UNKNOWN(-1);
	
	
	private final int code;
	private static Map<Integer, ScreenType> lookup = new HashMap<Integer, ScreenType>();
	private ScreenType(int code){
		this.code = code;
	}
	
	public int getCode(){
		return code;
	}
	
	public static ScreenType getType(int code){
		return lookup.get(code);
	}
	
	static {
		for(ScreenType type:values()){
			lookup.put(type.code, type);
		}
	}
}
