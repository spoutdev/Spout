package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Location;

public class PacketDownloadMusic implements BukkitContribPacket{
    int x, y, z;
    int volume, distance;
    boolean soundEffect, notify;
    String URL, plugin;
    public PacketDownloadMusic() {
        
    }
    
    public PacketDownloadMusic(String plugin, String URL, Location loc, int distance, int volume, boolean soundEffect, boolean notify) {
    	this.plugin = plugin;
        this.URL = URL;
        this.volume = volume;
        this.soundEffect = soundEffect;
        this.notify = notify;
        if (loc != null) {
            x = loc.getBlockX();
            y = loc.getBlockY();
            z = loc.getBlockZ();
            this.distance = distance;
        }
        else {
            this.distance = -1;
        }
    }

    @Override
    public int getNumBytes() {
        return 22 + URL.length() + plugin.length();
    }

    @Override
    public void readData(DataInputStream input) throws IOException {
        URL = PacketUtil.readString(input, 255);
        plugin = PacketUtil.readString(input, 255);
        distance = input.readInt();
        x = input.readInt();
        y = input.readInt();
        z = input.readInt();
        volume = input.readInt();
        soundEffect = input.readBoolean();
        notify = input.readBoolean();
    }

    @Override
    public void writeData(DataOutputStream output) throws IOException {
        PacketUtil.writeString(output, URL);
        PacketUtil.writeString(output, plugin);
        output.writeInt(distance);
        output.writeInt(x);
        output.writeInt(y);
        output.writeInt(z);
        output.writeInt(volume);
        output.writeBoolean(soundEffect);
        output.writeBoolean(notify);
    }

    @Override
    public void run(int PlayerId) {

    }

    @Override
    public PacketType getPacketType() {
        return PacketType.PacketDownloadMusic;
    }

}
