package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkitcontrib.packet.PacketUtil;

public abstract class GenericControl extends GenericWidget implements Control{

	protected boolean enabled = true;
	protected String hoverText = "";
	protected int color = 0xe0e0e0;
	protected int disabledColor = -0x5f5f60;
	public GenericControl() {
		
	}
	
	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 9 + PacketUtil.getNumBytes(getHoverText());
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		setEnabled(input.readBoolean());
		setHoverText(PacketUtil.readString(input));
		setColor(input.readInt());
		setDisabledColor(input.readInt());
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeBoolean(isEnabled());
		PacketUtil.writeString(output, getHoverText());
		output.writeInt(getColor());
		output.writeInt(getDisabledColor());
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

	@Override
	public int getColor() {
		return color;
	}

	@Override
	public Control setColor(int hexColor) {
		this.color = hexColor;
		return this;
	}

	@Override
	public int getDisabledColor() {
		return disabledColor;
	}

	@Override
	public Control setDisabledColor(int hexColor) {
		this.disabledColor = hexColor;
		return this;
	}

}
