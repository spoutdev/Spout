package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkitcontrib.packet.PacketUtil;

public class GenericTexture extends GenericWidget implements Texture {
	protected String Url = null;
	public GenericTexture() {
		
	}
	
	public GenericTexture(String Url) {
		this.Url = Url;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.Texture;
	}
	
	@Override
	public int getNumBytes() {
		return super.getNumBytes() + getUrl().length();
	}
	
	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		this.setUrl(PacketUtil.readString(input));
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		PacketUtil.writeString(output, getUrl());
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUrl() {
		return Url;
	}

	@Override
	public Texture setUrl(String Url) {
		this.Url = Url;
		return this;
	}

}
