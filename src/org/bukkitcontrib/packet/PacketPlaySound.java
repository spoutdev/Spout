package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkitcontrib.sound.Music;
import org.bukkitcontrib.sound.SoundEffect;

public class PacketPlaySound implements BukkitContribPacket{
    short soundId;
    boolean location = false;
    int x, y, z;
    int volume, distance;
    
    public PacketPlaySound() {
        
    }
    
    public PacketPlaySound(SoundEffect sound, int distance, int volume) {
        soundId = (short) sound.getId();
        this.volume = volume;
        this.distance = distance;
    }
    
    public PacketPlaySound(SoundEffect sound, Location loc, int distance, int volume) {
        soundId = (short) sound.getId();
        location = true;
        x = loc.getBlockX();
        y = loc.getBlockY();
        z = loc.getBlockZ();
        this.volume = volume;
        this.distance = distance;
    }
    
    public PacketPlaySound(Music music, int volume) {
        soundId = (short) (music.getId() + (1 + SoundEffect.getMaxId()));
        this.volume = volume;
    }

    @Override
    public int getNumBytes() {
        return 23;
    }

    @Override
    public void readData(DataInputStream input) throws IOException {
        soundId = input.readShort();
        location = input.readBoolean();
        x = input.readInt();
        y = input.readInt();
        z = input.readInt();
        distance = input.readInt();
        volume = input.readInt();
    }

    @Override
    public void writeData(DataOutputStream output) throws IOException {
        output.writeShort(soundId);
        output.writeBoolean(location);
        if (!location) {
            output.writeInt(-1);
            output.writeInt(-1);
            output.writeInt(-1);
            output.writeInt(-1);
        }
        else {
            output.writeInt(x);
            output.writeInt(y);
            output.writeInt(z);
            output.writeInt(distance);
        }
        output.writeInt(volume);
    }

    @Override
    public void run(int PlayerId) {
        
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.PacketPlaySound;
    }

}
