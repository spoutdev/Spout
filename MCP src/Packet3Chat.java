// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;
import net.minecraft.client.Minecraft; //BukkitContrib

import java.io.*;

// Referenced classes of package net.minecraft.src:
//            Packet, NetHandler

public class Packet3Chat extends Packet
{

    public Packet3Chat()
    {
    }

    public Packet3Chat(String s)
    {
        if(s.length() > 119)
        {
            s = s.substring(0, 119);
        }
        message = s;
    }

    public void readPacketData(DataInputStream datainputstream)
        throws IOException
    {
        message = func_27048_a(datainputstream, 119);
    }

    public void writePacketData(DataOutputStream dataoutputstream)
        throws IOException
    {
        func_27049_a(message, dataoutputstream);
    }

    public void processPacket(NetHandler nethandler)
    {
        //BukkitContrib Start
        boolean proc = false;
        if (!BukkitContrib.isEnabled() || BukkitContrib.getReloadPacket() != null) {
            String processed = BukkitContrib.colorToString(message);
            System.out.println(processed);
            if (processed.split("\\.").length == 3) {
                BukkitContrib.setVersion(processed);
                if (BukkitContrib.isEnabled()) {
                    proc = true;
                    System.out.println("BukkitContrib SP Enabled");
                    ((NetClientHandler)nethandler).addToSendQueue(new Packet3Chat("/" + BukkitContrib.getClientVersionString()));
                    //Let BukkitContrib know we just reloaded
                    if (BukkitContrib.getReloadPacket() != null) {
                        ((NetClientHandler)nethandler).addToSendQueue(new CustomPacket(BukkitContrib.getReloadPacket()));
                        BukkitContrib.setReloadPacket(null);
                    }
                    //Also need to send the render distance
                    Minecraft game = BukkitContrib.getGameInstance();
                    if (game != null && BukkitContrib.getVersion() > 5) {
                        final GameSettings settings = game.gameSettings;
                        ((NetClientHandler)nethandler).addToSendQueue(new CustomPacket(new PacketRenderDistance((byte)settings.renderDistance)));
                    }
                }
            }
        }
        if (!proc) {
            //Normal message handling
            nethandler.handleChat(this);
        }
        //BukkitContrib End
    }

    public int getPacketSize()
    {
        return message.length();
    }

    public String message;
}
