package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkitcontrib.packet.PacketUtil;

public class GenericLabel extends GenericWidget implements Label{
	protected String text = "";
	public GenericLabel(){
		
	}
	
	public GenericLabel(String text) {
		this.text = text;
	}
	
	@Override
	public void readData(DataInputStream input) throws IOException {
		upperRightX = input.readInt();
		upperRightY = input.readInt();
		width = input.readInt();
		height = input.readInt();
		this.setText(PacketUtil.readString(input));
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeInt(upperRightX);
		output.writeInt(upperRightY);
		output.writeInt(width);
		output.writeInt(height);
		PacketUtil.writeString(output, getText());
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public Label setText(String text) {
		this.text = text;
		return this;
	}
}
