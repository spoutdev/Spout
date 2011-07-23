package org.bukkitcontrib.packet;

public enum ScreenAction {
	ScreenOpen(0),
	ScreenClose(1),
	;
	
	private final byte id;
	ScreenAction(int id) {
		this.id = (byte)id;
	}
	
	public int getId() {
		return id;
	}
	
	public static ScreenAction getScreenActionFromId(int id) {
		for (ScreenAction action : values()) {
			if (action.getId() == id) {
				return action;
			}
		}
		return null;
	}
}
