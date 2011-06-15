package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkitcontrib.BukkitContrib;
import org.bukkitcontrib.player.ContribCraftPlayer;
import org.bukkitcontrib.player.ContribPlayer;

public class PacketPluginReload implements BukkitContribPacket{
    public int activeInventoryX;
    public int activeInventoryY;
    public int activeInventoryZ;
    public String worldName;
    
    public PacketPluginReload() {
        
    }
    
    public PacketPluginReload(ContribCraftPlayer player) {
        if (player.getNetServerHandler().getActiveInventoryLocation() != null) {
            activeInventoryX = player.getNetServerHandler().getActiveInventoryLocation().getBlockX();
            activeInventoryY = player.getNetServerHandler().getActiveInventoryLocation().getBlockY();
            activeInventoryZ = player.getNetServerHandler().getActiveInventoryLocation().getBlockZ();
            worldName = player.getNetServerHandler().getActiveInventoryLocation().getWorld().getName();
        }
        else {
            activeInventoryX = -1;
            activeInventoryY = Integer.MAX_VALUE; //invalid coord
            activeInventoryZ = -1;
            worldName = "null";
        }
    }

    @Override
    public int getNumBytes() {
        return 9 + worldName.length();
    }

    @Override
    public void readData(DataInputStream input) throws IOException {
        activeInventoryX = input.readInt();
        activeInventoryY = input.readInt();
        activeInventoryZ = input.readInt();
        PacketUtil.readString(input, 64);
    }

    @Override
    public void writeData(DataOutputStream output) throws IOException {
        output.writeInt(activeInventoryX);
        output.writeInt(activeInventoryY);
        output.writeInt(activeInventoryZ);
        PacketUtil.writeString(output, worldName);
    }

    @Override
    public void run(int playerId) {
        ContribPlayer player = BukkitContrib.getPlayerFromId(playerId);
        if (player != null) {
            if (activeInventoryX != -1 && activeInventoryY != Integer.MAX_VALUE && activeInventoryZ != -1) {
                Location active = new Location(Bukkit.getServer().getWorld(worldName), activeInventoryX, activeInventoryY, activeInventoryZ);
                ((ContribCraftPlayer)player).getNetServerHandler().setActiveInventoryLocation(active);
                ((ContribCraftPlayer)player).getNetServerHandler().setActiveInventory(true);
            }
        }
    }

    @Override
    public PacketType getPacketType() {
        return PacketType.PacketPluginReload;
    }

}
