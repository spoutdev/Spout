package net.minecraft.src;
//BukkitContrib
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketEntityTitle implements BukkitContribPacket{
    public String title;
    public int entityId;
    public PacketEntityTitle() {
        
    }
    
    public PacketEntityTitle(int entityId, String title) {
        this.entityId = entityId;
        this.title = title;
    }
    @Override
    public int getNumBytes() {
        return 4 + title.length();
    }

    @Override
    public void readData(DataInputStream input) throws IOException {
        entityId = input.readInt();
        title = PacketUtil.readString(input, 32);
    }

    @Override
    public void writeData(DataOutputStream output) throws IOException {
        output.writeInt(entityId);
        PacketUtil.writeString(output, title);
    }

    @Override
    public void run(int id) {
        if (title.equals("reset")) {
			BukkitContrib.entityLabel.remove(entityId);
		}
		else {
			BukkitContrib.entityLabel.put(entityId, title);
		}
    }
    
	@Override
	public PacketType getPacketType() {
		return PacketType.PacketEntityTitle;
	}
}
