package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GenericPopup extends GenericScreen implements PopupScreen{
	protected boolean transparent = false;
	public GenericPopup() {
		
	}
	
	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 1;
	}
	
	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		this.setTransparent(input.readBoolean());
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeBoolean(isTransparent());
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
	public WidgetType getType() {
		return WidgetType.PopupScreen;
	}
	
	@Override
	public boolean close() {
		if (getScreen() != null) {
			if (getScreen() instanceof InGameScreen) {
				return ((InGameScreen)getScreen()).closePopup();
			}
		}
		return false;
	}
}
