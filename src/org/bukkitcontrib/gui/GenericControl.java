package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkitcontrib.packet.PacketUtil;

public abstract class GenericControl extends GenericWidget implements Control{

	protected boolean enabled = true;
	protected String hoverText = "";
	public GenericControl() {
		
	}
	
	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 1 + PacketUtil.getNumBytes(getHoverText());
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		setEnabled(input.readBoolean());
		setHoverText(PacketUtil.readString(input));
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeBoolean(isEnabled());
		PacketUtil.writeString(output, getHoverText());
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public Control setEnabled(boolean enable) {
		enabled = enable;
		return this;
	}

	@Override
	public String getHoverText() {
		return hoverText;
	}

	@Override
	public Control setHoverText(String text) {
		hoverText = text;
		return this;
	}

}
