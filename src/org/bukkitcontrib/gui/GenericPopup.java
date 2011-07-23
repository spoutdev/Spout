package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkitcontrib.packet.PacketUtil;

public class GenericPopup extends GenericScreen implements PopupScreen{
	protected boolean transparent = false;
	protected String url = "";
	protected int color = -1;
	public GenericPopup() {
		
	}
	
	@Override
	public int getNumBytes() {
		return super.getNumBytes() + PacketUtil.getNumBytes(url) + 5;
	}
	
	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		this.setTransparent(input.readBoolean());
		this.setBackgroundSolidColor(input.readInt());
		this.setBackgroundTextureUrl(PacketUtil.readString(input, 255));
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeBoolean(isTransparent());
		output.writeInt(getBackgroundSolidColor());
		PacketUtil.writeString(output, getBackgroundTextureUrl());
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
	public String getBackgroundTextureUrl() {
		return url;
	}

	@Override
	public PopupScreen setBackgroundTextureUrl(String url) {
		this.url = url;
		return this;
	}

	@Override
	public int getBackgroundSolidColor() {
		return color;
	}

	@Override
	public PopupScreen setBackgroundSolidColor(int hexCode) {
		this.color = hexCode;
		return this;
	}
	
	@Override
	public WidgetType getType() {
		return WidgetType.PopupScreen;
	}

	@Override
	public void render() {}

}
