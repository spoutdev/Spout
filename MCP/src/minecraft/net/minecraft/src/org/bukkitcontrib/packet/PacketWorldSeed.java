package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.*;

public class PacketWorldSeed implements BukkitContribPacket{
	public long newSeed;
	
	public PacketWorldSeed() {
	}
	
	public PacketWorldSeed(long newSeed) {
		this.newSeed = newSeed;
	}

	@Override
	public int getNumBytes() {
		return 8;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		this.newSeed = input.readLong();
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeLong(this.newSeed);
	}

	@Override
	public void run(int id) {
		BukkitContrib.getGameInstance().theWorld.getWorldInfo().setNewSeed(newSeed);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketWorldSeed;
	}
}
