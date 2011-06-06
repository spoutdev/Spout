//BukkitContrib
package net.minecraft.src;

import java.io.*;
import java.lang.reflect.Method;

public class Packet197SkinURL extends Packet
{
    
    public Packet197SkinURL()
    {
    
    }

    public void readPacketData(DataInputStream input) throws IOException
    {
        entityId = input.readInt();
        skinURL = func_27048_a(input, 256);
        cloakURL = func_27048_a(input, 256);
    }

    public void writePacketData(DataOutputStream output) throws IOException
    {
        output.writeInt(entityId);
        func_27049_a(skinURL, output);
        func_27049_a(cloakURL, output);
    }

    public void processPacket(NetHandler nethandler)
    {
        Class<?>[] params = {int.class};
        Entity e = null;
        try {
            Method m = NetClientHandler.class.getDeclaredMethod("a", params); //getEntityByID
            m.setAccessible(true);
            e = (Entity)m.invoke(nethandler, entityId);
        }
        catch (Exception e1) {}
        if (e != null && e instanceof EntityOtherPlayerMP) {
            if (!this.skinURL.equals("none")) {
                e.skinUrl = this.skinURL;
            }
            if (!this.cloakURL.equals("none")) {
                e.cloakUrl = this.cloakURL;
                ((EntityPlayer)e).playerCloakUrl = this.cloakURL;
            }
            BukkitContrib.getGameInstance().theWorld.obtainEntitySkin(e);
        }
    }

    public int getPacketSize()
    {
        return 4 + skinURL.length() + cloakURL.length(); 
    }

    public int entityId;
    public String skinURL;
    public String cloakURL;
    static 
    {
        addIdClassMapping(197, true, true, net.minecraft.src.Packet197SkinURL.class);
    }
}
