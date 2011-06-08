package net.minecraft.src;
//BukkitContrib
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketPluginReload implements BukkitContribPacket{
	public int activeInventoryX;
	public int activeInventoryY;
	public int activeInventoryZ;
	public String worldName;
	
	public PacketPluginReload() {
		
	}

	@Override
	public int getNumBytes() {
		return 9 + worldName.length();
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		activeInventoryX = input.readInt();
		activeInventoryY = input.readInt();
		activeInventoryZ = input.readInt();
		PacketUtil.readString(input, 64);
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeInt(activeInventoryX);
		output.writeInt(activeInventoryY);
		output.writeInt(activeInventoryZ);
		PacketUtil.writeString(output, worldName);
	}

	@Override
	public void run(int playerId) {
		BukkitContrib.setReloadPacket(this);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketPluginReload;
	}

}
