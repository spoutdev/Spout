package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GenericPopup extends GenericScreen implements PopupScreen{
	protected boolean transparent = false;
	protected boolean focus = true;
	public GenericPopup() {
		
	}
	
	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 2;
	}
	
	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		this.setTransparent(input.readBoolean());
		this.setFocus(input.readBoolean());
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeBoolean(isTransparent());
		output.writeBoolean(isFocus());
	}
	
	@Override
	public boolean isTransparent() {
		return transparent;
	}

	@Override
	public PopupScreen setTransparent(boolean value) {
		this.transparent = value;
		return this;
	}
	
	@Override
	public boolean isFocus() {
		return focus;
	}
	
	@Override
	public PopupScreen setFocus(boolean focus) {
		this.focus = focus;
		return this;
	}
	
	@Override
	public WidgetType getType() {
		return WidgetType.PopupScreen;
	}
	
	@Override
	public boolean close() {
		if (getScreen() != null) {
			if (getScreen() instanceof InGameScreen) {
				((InGameScreen)getScreen()).clearPopup();
			}
		}
		return false;
	}
}
