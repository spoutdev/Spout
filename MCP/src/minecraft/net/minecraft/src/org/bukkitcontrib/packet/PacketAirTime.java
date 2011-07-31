package org.getspout.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.*;

public class PacketAirTime implements BukkitContribPacket{
	public int airTime;
	public int air;
	
	public PacketAirTime() {
		
	}
	
	public PacketAirTime(int maxTime, int time) {
		this.airTime = maxTime;
		this.air = time;
	}

	@Override
	public int getNumBytes() {
		return 8;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		this.airTime = input.readInt();
		this.air = input.readInt();
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeInt(this.airTime);
		output.writeInt(this.air);
	}

	@Override
	public void run(int id) {
		EntityUtil.setMaximumAir(BukkitContrib.getGameInstance().thePlayer, airTime);
		BukkitContrib.getGameInstance().thePlayer.air = air;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketAirTime;
	}
}
