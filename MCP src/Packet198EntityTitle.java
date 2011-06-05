//BukkitContrib
package net.minecraft.src;

import java.io.*;

public class Packet198EntityTitle extends Packet
{
	
    public Packet198EntityTitle()
    {
	
    }

    public void readPacketData(DataInputStream input) throws IOException
    {
		entityId = input.readInt();
		title = func_27048_a(input, 32);
    }

    public void writePacketData(DataOutputStream output) throws IOException
    {
		output.writeInt(entityId);
		func_27049_a(title, output);
    }

    public void processPacket(NetHandler nethandler)
    {
		if (title.equals("reset")) {
			BukkitContrib.entityLabel.remove(entityId);
		}
		else {
			BukkitContrib.entityLabel.put(entityId, title);
		}
    }

    public int getPacketSize()
    {
        return 4 + title.length();
    }

	public String title;
	public int entityId;
	static 
    {
        addIdClassMapping(198, true, true, net.minecraft.src.Packet198EntityTitle.class);
    }
}
