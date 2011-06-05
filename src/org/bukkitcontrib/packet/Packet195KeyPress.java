package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkitcontrib.BukkitContrib;
import org.bukkitcontrib.ContribNetServerHandler;
import org.bukkitcontrib.event.input.KeyPressedEvent;
import org.bukkitcontrib.event.input.KeyReleasedEvent;
import org.bukkitcontrib.keyboard.Keyboard;
import org.bukkitcontrib.keyboard.SimpleKeyboardManager;
import org.bukkitcontrib.player.ContribCraftPlayer;
import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet;

public class Packet195KeyPress extends Packet{
    public boolean pressDown;
    public int key;
    public byte settingKeys[] = new byte[10];
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
            this.settingKeys[i] = datainputstream.readByte();
        }
    }

    public void a(DataOutputStream dataoutputstream) throws IOException
    {
        dataoutputstream.writeInt(this.key);
        dataoutputstream.writeBoolean(this.pressDown);
        for (int i = 0; i < 10; i++) {
            dataoutputstream.writeByte(this.settingKeys[i]);
        }
    }

    public void a(NetHandler nethandler)
    {
        if (nethandler instanceof ContribNetServerHandler) {
            ContribCraftPlayer ccp = (ContribCraftPlayer)((ContribNetServerHandler)nethandler).player.getBukkitEntity();
            ccp.updateKeys(settingKeys);
            Keyboard pressed = Keyboard.getKey(this.key);
            SimpleKeyboardManager manager = (SimpleKeyboardManager)BukkitContrib.getKeyboardManager();
            if (pressDown) {
                manager.onPreKeyPress(pressed, ccp);
                Bukkit.getServer().getPluginManager().callEvent(new KeyPressedEvent(this.key, ccp));
                manager.onPostKeyPress(pressed, ccp);
            }
            else {
                manager.onPreKeyRelease(pressed, ccp);
                Bukkit.getServer().getPluginManager().callEvent(new KeyReleasedEvent(this.key, ccp));
                manager.onPostKeyPress(pressed, ccp);
            }
        }
    }

    public int a()
    {
        return 4 + 1 + 10;
    }
    
    public static void addClassMapping() {
        try {
            Class<?>[] params = {int.class, boolean.class, boolean.class, Class.class};
            Method addClassMapping = Packet.class.getDeclaredMethod("a", params);
            addClassMapping.setAccessible(true);
            addClassMapping.invoke(null, 195, true, true, Packet195KeyPress.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("rawtypes")
    public static void removeClassMapping() {
        try {
            Field field = Packet.class.getDeclaredField("a");
            field.setAccessible(true);
            Map temp = (Map) field.get(null);
            temp.remove(195);
            field = Packet.class.getDeclaredField("b");
            field.setAccessible(true);
            temp = (Map) field.get(null);
            temp.remove(Packet195KeyPress.class);
        }
        catch (Exception e) {
            
        }
    }
}
