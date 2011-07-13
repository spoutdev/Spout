package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.*;

public class PacketClipboardText implements BukkitContribPacket{
	
	public PacketClipboardText() {
		
	}
	
	public PacketClipboardText(String text) {
		this.text = text;
	}
	protected String text;
	@Override
	public int getNumBytes() {
        return PacketUtil.getNumBytes(text);
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		text = PacketUtil.readString(input);
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		if (text.length() > PacketUtil.maxString) {
			text = text.substring(0, PacketUtil.maxString - 1);
		}
		PacketUtil.writeString(output, text);
	}

	@Override
	public void run(int playerId) {
		BukkitContrib.setClipboardText(text);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketClipboardText;
	}

}
