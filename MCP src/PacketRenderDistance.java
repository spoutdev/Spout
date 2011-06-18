package net.minecraft.src;

import net.minecraft.client.Minecraft;
//BukkitContrib

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketRenderDistance implements BukkitContribPacket{
    protected byte view;
    protected byte max = -1;
    protected byte min = -1;
    public PacketRenderDistance() {
        
    }
    
    public PacketRenderDistance(byte view) {
        this.view = view;
    }

    @Override
    public int getNumBytes() {
        return 3;
    }

    @Override
    public void readData(DataInputStream input) throws IOException {
        view = input.readByte();
        max = input.readByte();
        min = input.readByte();
    }

    @Override
    public void writeData(DataOutputStream output) throws IOException {
        output.write(view);
        output.write(max);
        output.write(min);
    }

    @Override
    public void run(int PlayerId) {
        Minecraft game = BukkitContrib.getGameInstance();
        if (game != null) {
            GameSettings settings = game.gameSettings;
            if (view > -1 && view < 4) {
                settings.renderDistance = view;
            }
        }
        if (min > -1 && min < 4)
            BukkitContrib.minView = min;
        if (max > -1 && max < 4)
            BukkitContrib.maxView = max;
        if (min == -2)
            BukkitContrib.minView = -1;
        if (max == -2)
            BukkitContrib.maxView = -1;
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.PacketRenderDistance;
    }

}
