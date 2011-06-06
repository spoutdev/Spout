// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.io.*;

// Referenced classes of package net.minecraft.src:
//            Packet, NetHandler

public class Packet1Login extends Packet
{

    public Packet1Login()
    {
    }

    public Packet1Login(String s, int i)
    {
        username = s;
        protocolVersion = i;
    }

    public void readPacketData(DataInputStream datainputstream)
        throws IOException
    {
        protocolVersion = datainputstream.readInt();
        username = func_27048_a(datainputstream, 16);
        mapSeed = datainputstream.readLong();
        dimension = datainputstream.readByte();
    }

    public void writePacketData(DataOutputStream dataoutputstream)
        throws IOException
    {
        dataoutputstream.writeInt(protocolVersion);
        func_27049_a(username, dataoutputstream);
        dataoutputstream.writeLong(mapSeed);
        dataoutputstream.writeByte(dimension);
    }

    public void processPacket(NetHandler nethandler)
    {
        //System.out.println("Resetting BukkitContrib " + BukkitContrib.getBukkitContribLoginCounter());
        BukkitContrib.resetBukkitContrib(); //BukkitContrib
        //System.out.println("Reset BukkitContrib " + BukkitContrib.getBukkitContribLoginCounter());
        nethandler.handleLogin(this);
    }

    public int getPacketSize()
    {
        return 4 + username.length() + 4 + 5;
    }

    public int protocolVersion;
    public String username;
    public long mapSeed;
    public byte dimension;
}
