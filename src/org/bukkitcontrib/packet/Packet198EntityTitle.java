package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet;

public class Packet198EntityTitle extends Packet{
    public String title;
    public int entityId;
    public Packet198EntityTitle() {
        
    }
    
    public Packet198EntityTitle(int entityId, String title) {
        this.entityId = entityId;
        this.title = title;
    }
    @Override
    public int a() {
        return 4 + title.length();
    }

    @Override
    public void a(DataInputStream input) throws IOException {
        entityId = input.readInt();
        title = Packet.a(input, 32);
    }

    @Override
    public void a(DataOutputStream output) throws IOException {
        output.writeInt(entityId);
        Packet.a(title, output);
    }

    @Override
    public void a(NetHandler arg0) {
        
    }

   public static void addClassMapping() {
        try {
            Class<?>[] params = {int.class, boolean.class, boolean.class, Class.class};
            Method addClassMapping = Packet.class.getDeclaredMethod("a", params);
            addClassMapping.setAccessible(true);
            addClassMapping.invoke(null, 198, true, true, Packet198EntityTitle.class);
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
            temp.remove(198);
            field = Packet.class.getDeclaredField("b");
            field.setAccessible(true);
            temp = (Map) field.get(null);
            temp.remove(Packet198EntityTitle.class);
        }
        catch (Exception e) {
            
        }
    }
}
