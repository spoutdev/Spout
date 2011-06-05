package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet;

public class Packet197SkinURL extends Packet{
    
    public Packet197SkinURL() {
        
    }
    
    public Packet197SkinURL(int id, String skinURL, String cloakURL) {
        this.entityId = id;
        this.skinURL = skinURL;
        this.cloakURL = cloakURL;
    }
    
    public Packet197SkinURL(int id, String skinURL) {
        this.entityId = id;
        this.skinURL = skinURL;
        this.cloakURL = "none";
    }
    
    public Packet197SkinURL(String cloakURL, int id) {
        this.entityId = id;
        this.skinURL = "none";
        this.cloakURL = cloakURL;
    }
    public int entityId;
    public String skinURL;
    public String cloakURL;

    @Override
    public int a() {
        return 4 + skinURL.length() + cloakURL.length();
    }

    @Override
    public void a(DataInputStream input) throws IOException {
        entityId = input.readInt();
        skinURL = Packet.a(input, 256);
        cloakURL = Packet.a(input, 256);
    }

    @Override
    public void a(DataOutputStream output) throws IOException {
        output.writeInt(entityId);
        Packet.a(skinURL, output);
        Packet.a(cloakURL, output);
    }

    @Override
    public void a(NetHandler netHandler) {

    }
    
   public static void addClassMapping() {
        try {
            Class<?>[] params = {int.class, boolean.class, boolean.class, Class.class};
            Method addClassMapping = Packet.class.getDeclaredMethod("a", params);
            addClassMapping.setAccessible(true);
            addClassMapping.invoke(null, 197, true, true, Packet197SkinURL.class);
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
            temp.remove(197);
            field = Packet.class.getDeclaredField("b");
            field.setAccessible(true);
            temp = (Map) field.get(null);
            temp.remove(Packet197SkinURL.class);
        }
        catch (Exception e) {
            
        }
    }

}
