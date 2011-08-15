package org.getspout.spout.player;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutWeather;
import org.getspout.spoutapi.packet.PacketBiomeWeather;
import org.getspout.spoutapi.player.BiomeManager;
import org.getspout.spoutapi.player.PlayerInformation;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimpleBiomeManager implements BiomeManager {
	
	HashMap<Biome, SpoutWeather> globalWeather = new HashMap<Biome, SpoutWeather>();

	@Override
	public void setPlayerBiomeWeather(SpoutPlayer player, Biome biome, SpoutWeather weather) {
		PlayerInformation info = SpoutManager.getPlayerManager().getPlayerInfo(player);
		info.setBiomeWeather(biome, weather);
		if (player.isSpoutCraftEnabled()) {
			player.sendPacket(new PacketBiomeWeather(biome, weather));
		}
	}

	@Override
	public void setPlayerWeather(SpoutPlayer player, SpoutWeather weather) {
		PlayerInformation info = SpoutManager.getPlayerManager().getPlayerInfo(player);
		for (Biome biome : Biome.values()) {
			info.setBiomeWeather(biome, weather);
			if (player.isSpoutCraftEnabled()) {
				player.sendPacket(new PacketBiomeWeather(biome, weather));
			}
		}
	}

	@Override
	public void setGlobalBiomeWeather(Biome biome, SpoutWeather weather) {
		globalWeather.put(biome, weather);
		
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
			PlayerInformation info = SpoutManager.getPlayerManager().getPlayerInfo(player);
			info.setBiomeWeather(biome, weather);
			if (sPlayer.isSpoutCraftEnabled()) {
				sPlayer.sendPacket(new PacketBiomeWeather(biome, weather));
			}
		}
	}

	@Override
	public void setGlobalWeather(SpoutWeather weather) {
		
		for(Biome biome : Biome.values()) {
			globalWeather.put(biome, weather);
		}
		
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
			PlayerInformation info = SpoutManager.getPlayerManager().getPlayerInfo(player);
			for (Biome biome : Biome.values()) {
				info.setBiomeWeather(biome, weather);
				if (sPlayer.isSpoutCraftEnabled()) {
					sPlayer.sendPacket(new PacketBiomeWeather(biome, weather));
				}
			}
		}
	}

	public void onPlayerJoin(SpoutPlayer player) {
		PlayerInformation info = SpoutManager.getPlayerManager().getPlayerInfo(player);
		
		if(!globalWeather.isEmpty()) {
			for(Biome biome : globalWeather.keySet()) {
				info.setBiomeWeather(biome, globalWeather.get(biome));
				player.sendPacket(new PacketBiomeWeather(biome, globalWeather.get(biome)));
			}
		}

	}

	@Override
	public SpoutWeather getGlobalBiomeWeather(Biome biome) {
		return globalWeather.get(biome);
	}

	@Override
	public SpoutWeather getPlayerBiomeWeather(Player player, Biome biome) {
		PlayerInformation info = SpoutManager.getPlayerManager().getPlayerInfo(player);
		return info.getBiomeWeather(biome);
	}

}
