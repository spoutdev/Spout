package org.getspout.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.getspout.gui.Widget;
import org.getspout.gui.Screen;

public class PacketControlAction implements BukkitContribPacket{
	protected UUID screen;
	protected UUID widget;
	protected float state;
	protected String data = "";
	public PacketControlAction() {
		
	}
	
	public PacketControlAction(Screen screen, Widget widget, float state) {
		this.screen = screen.getId();
		this.widget = widget.getId();
		this.state = state;
	}
	
	public PacketControlAction(Screen screen, Widget widget, String data, float position) {
		this.screen = screen.getId();
		this.widget = widget.getId();
		this.state = position;
		this.data = data;
	}

	@Override
	public int getNumBytes() {
		return 36 + PacketUtil.getNumBytes(data);
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		long msb = input.readLong();
		long lsb = input.readLong();
		this.screen = new UUID(msb, lsb);
		msb = input.readLong();
		lsb = input.readLong();
		this.widget = new UUID(msb, lsb);
		this.state = input.readFloat();
		this.data = PacketUtil.readString(input);
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeLong(screen.getMostSignificantBits());
		output.writeLong(screen.getLeastSignificantBits());
		output.writeLong(widget.getMostSignificantBits());
		output.writeLong(widget.getLeastSignificantBits());
		output.writeFloat(state);
		PacketUtil.writeString(output, data);
	}

	@Override
	public void run(int playerId) {
		//Nothing to do
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketControlAction;
	}

}
