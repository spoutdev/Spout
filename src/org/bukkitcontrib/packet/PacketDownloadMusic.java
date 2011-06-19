package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Location;

public class PacketDownloadMusic implements BukkitContribPacket{
    int x, y, z;
    int volume;
    boolean loc;
    String URL;
    public PacketDownloadMusic() {
        
    }
    
    public PacketDownloadMusic(String URL, Location loc, int volume) {
        this.URL = URL;
        this.volume = volume;
        if (loc != null) {
            x = loc.getBlockX();
            y = loc.getBlockY();
            z = loc.getBlockZ();
            this.loc = true;
        }
        else {
            this.loc = false;
        }
    }

    @Override
    public int getNumBytes() {
        return 17 + URL.length();
    }

    @Override
    public void readData(DataInputStream input) throws IOException {
        URL = PacketUtil.readString(input, 255);
        loc = input.readBoolean();
        x = input.readInt();
        y = input.readInt();
        z = input.readInt();
        volume = input.readInt();
    }

    @Override
    public void writeData(DataOutputStream output) throws IOException {
        PacketUtil.writeString(output, URL);
        output.writeBoolean(loc);
        output.writeInt(x);
        output.writeInt(y);
        output.writeInt(z);
        output.writeInt(volume);
    }

    @Override
    public void run(int PlayerId) {

    }

    @Override
    public PacketType getPacketType() {
        return PacketType.PacketDownloadMusic;
    }

}
