package org.bukkitcontrib.event.screen;

import org.bukkitcontrib.gui.Screen;
import org.bukkitcontrib.gui.TextField;

public class TextFieldChangeEvent extends ScreenEvent{

	protected TextField field;
	protected String oldVal;
	protected String newVal;
	public TextFieldChangeEvent(Screen screen, TextField field, String newVal) {
		super("TextFieldChangeEvent", screen);
		this.field = field;
		this.oldVal = field.getText();
		this.newVal = newVal;
	}
	
	public TextField getTextField() {
		return field;
	}
	
	public String getOldText() {
		return oldVal;
	}
	
	public String getNewText() {
		return newVal;
	}
	
	public void setNewText(String newVal) {
		if (newVal == null) newVal = "";
		this.newVal = newVal;
	}

}
