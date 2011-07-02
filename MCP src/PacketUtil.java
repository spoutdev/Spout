package net.minecraft.src;
//BukkitContrib
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public abstract class PacketUtil {
    public static final int maxString = 32767;
    
    public static void writeString(DataOutputStream output, String string) {
        try {
            Packet.func_27049_a(string, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String readString(DataInputStream input) {
        return readString(input, maxString);
    }
    
    public static String readString(DataInputStream input, int maxSize) {
        try {
            return Packet.func_27048_a(input, maxSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
