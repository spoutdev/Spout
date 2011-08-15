package org.getspout.spout.player;

import org.bukkit.block.Biome;
import org.getspout.spoutapi.packet.PacketBiomeWeather;
import org.getspout.spoutapi.player.BiomeManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimpleBiomeManager implements BiomeManager{

	@Override
	public void setPlayerBiomeWeather(SpoutPlayer player, Biome biome, byte weather) {
		if(player.isSpoutCraftEnabled()) {
			byte biomeByte = (byte) biome.ordinal();
			player.sendPacket(new PacketBiomeWeather(biomeByte,weather));
		}
		
	}

}
