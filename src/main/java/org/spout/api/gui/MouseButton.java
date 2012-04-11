package org.spout.api.gui;

import java.util.HashMap;

public class MouseButton {
	private int buttonId;
	private String buttonName;
	private static HashMap<Integer, MouseButton> buttonsById;
	
	public static final MouseButton LEFT_BUTTON = new MouseButton(0, "Left");
	public static final MouseButton RIGHT_BUTTON = new MouseButton(1, "Right");
	public static final MouseButton MIDDLE_BUTTON = new MouseButton(2, "Middle");
	
	public static MouseButton getButtonById(int id) {
		MouseButton button = buttonsById.get(id);
		if(button == null) {
			button = new MouseButton(id, "Unknown "+id);
			buttonsById.put(id, button);
		}
		return button;
	}
	
	public MouseButton(int buttonId, String buttonName) {
		this.buttonId = buttonId;
		this.buttonName = buttonName;
		buttonsById.put(buttonId, this);
	}
	
	public int getButtonId() {
		return buttonId;
	}
	public String getButtonName() {
		return buttonName;
	}
}
