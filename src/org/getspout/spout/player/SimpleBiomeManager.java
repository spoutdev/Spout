package org.getspout.spout.player;

import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutWeather;
import org.getspout.spoutapi.packet.PacketBiomeWeather;
import org.getspout.spoutapi.player.BiomeManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimpleBiomeManager implements BiomeManager{

	@Override
	public void setPlayerBiomeWeather(SpoutPlayer player, Biome biome, SpoutWeather weather) {
		if(player.isSpoutCraftEnabled()) {
			byte biomeByte = (byte) biome.ordinal();
			byte weatherByte = (byte) weather.ordinal();
			player.sendPacket(new PacketBiomeWeather(biomeByte,weatherByte));
		}
		
	}

	@Override
	public void setPlayerWeather(SpoutPlayer player, SpoutWeather weather) {
		if(player.isSpoutCraftEnabled()) {
			for(Biome biome : Biome.values()) {
				byte biomeByte = (byte) biome.ordinal();
				byte weatherByte = (byte) weather.ordinal();
				player.sendPacket(new PacketBiomeWeather(biomeByte,weatherByte));
			}
		}
		
	}

	@Override
	public void setGlobalBiomeWeather(Biome biome, SpoutWeather weather) {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
			if(sPlayer.isSpoutCraftEnabled()) {
				byte biomeByte = (byte) biome.ordinal();
				byte weatherByte = (byte) weather.ordinal();
				sPlayer.sendPacket(new PacketBiomeWeather(biomeByte,weatherByte));
			}
		}
		
	}

	@Override
	public void setGlobalWeather(SpoutWeather weather) {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
			if(sPlayer.isSpoutCraftEnabled()) {
				for(Biome biome : Biome.values()) {
					byte biomeByte = (byte) biome.ordinal();
					byte weatherByte = (byte) weather.ordinal();
					sPlayer.sendPacket(new PacketBiomeWeather(biomeByte,weatherByte));
				}
			}
		}
		
	}

}
