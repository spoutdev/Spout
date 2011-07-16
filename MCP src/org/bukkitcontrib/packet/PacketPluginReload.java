package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.src.*;

public class PacketPluginReload implements BukkitContribPacket{
	 public int activeInventoryX;
	 public int activeInventoryY;
	 public int activeInventoryZ;
	 public String worldName;
	 
	 public PacketPluginReload() {
		  
	 }

	 @Override
	 public int getNumBytes() {
        return 12 + PacketUtil.getNumBytes(worldName);
	 }

	 @Override
	 public void readData(DataInputStream input) throws IOException {
		  activeInventoryX = input.readInt();
		  activeInventoryY = input.readInt();
		  activeInventoryZ = input.readInt();
		  worldName = PacketUtil.readString(input, 64);
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
