//BukkitContrib
package net.minecraft.src;

import java.io.*;

public class Packet196AirTime extends Packet
{
    
    public Packet196AirTime()
    {
    }

    public void readPacketData(DataInputStream datainputstream) throws IOException
    {
        this.airTime = datainputstream.readInt();
        this.air = datainputstream.readInt();
    }

    public void writePacketData(DataOutputStream dataoutputstream) throws IOException
    {
        dataoutputstream.writeInt(this.airTime);
        dataoutputstream.writeInt(this.air);
    }

    public void processPacket(NetHandler nethandler)
    {
        try {
            NetClientHandler handler = (NetClientHandler)nethandler;
            BukkitContrib.getGameInstance().thePlayer.maxAir = airTime;
            BukkitContrib.getGameInstance().thePlayer.air = air;
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public int getPacketSize()
    {
        return 4 + 4; 
    }

    public int airTime;
    public int air;
    static 
    {
        addIdClassMapping(196, true, true, net.minecraft.src.Packet196AirTime.class);
    }
}
