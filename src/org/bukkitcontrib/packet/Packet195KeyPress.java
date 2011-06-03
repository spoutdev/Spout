package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkitcontrib.ContribNetServerHandler;
import org.bukkitcontrib.event.input.KeyPressedEvent;
import org.bukkitcontrib.event.input.KeyReleasedEvent;
import org.bukkitcontrib.player.ContribCraftPlayer;
import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet;

public class Packet195KeyPress extends Packet{
    public boolean pressDown;
    public int key;
    public short settingKeys[] = new short[10];
    public Packet195KeyPress()
    {
    }

    public Packet195KeyPress(int key, boolean pressDown)
    {
        this.key = key;
        this.pressDown = pressDown;
    }

    public void a(DataInputStream datainputstream) throws IOException
    {
        this.key = datainputstream.readInt();
        this.pressDown = datainputstream.readBoolean();
        for (int i = 0; i < 10; i++) {
            this.settingKeys[i] = datainputstream.readShort();
        }
    }

    public void a(DataOutputStream dataoutputstream) throws IOException
    {
        dataoutputstream.writeInt(this.key);
        dataoutputstream.writeBoolean(this.pressDown);
        for (int i = 0; i < 10; i++) {
            dataoutputstream.writeShort(this.settingKeys[i]);
        }
    }

    public void a(NetHandler nethandler)
    {
        if (nethandler instanceof ContribNetServerHandler) {
            ContribCraftPlayer ccp = (ContribCraftPlayer)((ContribNetServerHandler)nethandler).player.getBukkitEntity();
            ccp.updateKeys(settingKeys);
            if (pressDown) {
                Bukkit.getServer().getPluginManager().callEvent(new KeyPressedEvent(key, ccp));
            }
            else {
                Bukkit.getServer().getPluginManager().callEvent(new KeyReleasedEvent(key, ccp));
            }
        }
    }

    public int a()
    {
        return 4 + 1 + 20;
    }
    
    public static void addClassMapping() {
        try {
            Class<?>[] params = {int.class, boolean.class, boolean.class, Class.class};
            Method addClassMapping = Packet.class.getDeclaredMethod("a", params);
            addClassMapping.setAccessible(true);
            addClassMapping.invoke(null, 195, true, true, Packet195KeyPress.class);
            System.out.println("Added Packet 195");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
