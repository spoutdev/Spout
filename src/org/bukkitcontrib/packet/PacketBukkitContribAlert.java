package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketBukkitContribAlert implements BukkitContribPacket{
    
    public PacketBukkitContribAlert() {
        
    }
    
    public PacketBukkitContribAlert(String title, String message, int itemId) {
        this.title = title;
        this.message = message;
        this.itemId = itemId;
    }

    @Override
    public int getNumBytes() {
        return 4 + PacketUtil.getNumBytes(title) + PacketUtil.getNumBytes(message);
    }

    @Override
    public void readData(DataInputStream input) throws IOException {
        title = PacketUtil.readString(input, 26);
        message = PacketUtil.readString(input, 26);
        itemId = input.readInt();
    }

    @Override
    public void writeData(DataOutputStream output) throws IOException {
        PacketUtil.writeString(output, title);
        PacketUtil.writeString(output, message);
        output.writeInt(itemId);
    }

    @Override
    public void run(int PlayerId) {
        
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.PacketBukkitContribAlert;
    }

    String message;
    String title;
    int itemId;
}
