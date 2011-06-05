package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet;

public class Packet196AirTime extends Packet{
    public int airTime;
    public int air;
    
    public Packet196AirTime() {
        
    }
    
    public Packet196AirTime(int maxTime, int time) {
        this.airTime = maxTime;
        this.air = time;
    }

    @Override
    public int a() {
        return 4 + 4;
    }

    @Override
    public void a(DataInputStream input) throws IOException {
        this.airTime = input.readInt();
        this.air = input.readInt();
    }

    @Override
    public void a(DataOutputStream output) throws IOException {
        output.writeInt(this.airTime);
        output.writeInt(this.air);
    }

    @Override
    public void a(NetHandler arg0) {
        
    }
    
    public static void addClassMapping() {
        try {
            Class<?>[] params = {int.class, boolean.class, boolean.class, Class.class};
            Method addClassMapping = Packet.class.getDeclaredMethod("a", params);
            addClassMapping.setAccessible(true);
            addClassMapping.invoke(null, 196, true, true, Packet196AirTime.class);
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
            temp.remove(196);
            field = Packet.class.getDeclaredField("b");
            field.setAccessible(true);
            temp = (Map) field.get(null);
            temp.remove(Packet196AirTime.class);
        }
        catch (Exception e) {
            
        }
    }

}
