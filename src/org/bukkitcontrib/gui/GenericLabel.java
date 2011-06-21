package org.bukkitcontrib.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkitcontrib.packet.PacketUtil;

public abstract class GenericLabel extends GenericWidget implements Label{
	public GenericLabel(){
		
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
}
