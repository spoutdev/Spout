package org.bukkitcontrib.gui;

public enum HoverState {
	
	Locked(0),
	Unpressed(1),
	Pressed(2),
	;
	
	private final int id;
	HoverState(final int id) {
		this.id = id;
	}
	
	public final int getId() {
		return id;
	}
	
	public static HoverState getHoverStateFromId(int id) {
		for (HoverState hs : values()) {
			if (hs.getId() == id) {
				return hs;
			}
		}
		return null;
	}

}
