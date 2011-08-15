package org.getspout.spout.player;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutWeather;
import org.getspout.spoutapi.packet.PacketBiomeWeather;
import org.getspout.spoutapi.player.BiomeManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimpleBiomeManager implements BiomeManager{
	
	private final HashMap<String, HashMap<Biome, SpoutWeather>> weatherMap = new HashMap<String, HashMap<Biome, SpoutWeather>>();

	@Override
	public void setPlayerBiomeWeather(SpoutPlayer player, Biome biome, SpoutWeather weather) {
		if(!weatherMap.containsKey(player.getName())) {
			weatherMap.put(player.getName(), new HashMap<Biome, SpoutWeather>());
			System.out.println("New Player");
		}
		weatherMap.get(player.getName()).put(biome, weather);
		if(player.isSpoutCraftEnabled()) {
			byte biomeByte = (byte) biome.ordinal();
			byte weatherByte = (byte) weather.ordinal();
			player.sendPacket(new PacketBiomeWeather(biomeByte,weatherByte));
		}
		
	}

	@Override
	public void setPlayerWeather(SpoutPlayer player, SpoutWeather weather) {
		if(!weatherMap.containsKey(player.getName())) {
			weatherMap.put(player.getName(), new HashMap<Biome, SpoutWeather>());
		}
		if(player.isSpoutCraftEnabled()) {
			for(Biome biome : Biome.values()) {
				weatherMap.get(player.getName()).put(biome, weather);
				byte biomeByte = (byte) biome.ordinal();
				byte weatherByte = (byte) weather.ordinal();
				player.sendPacket(new PacketBiomeWeather(biomeByte,weatherByte));
			}
		}
		
	}

	@Override
	public void setGlobalBiomeWeather(Biome biome, SpoutWeather weather) {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			if(!weatherMap.containsKey(player.getName())) {
				weatherMap.put(player.getName(), new HashMap<Biome, SpoutWeather>());
			}
			SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
			if(sPlayer.isSpoutCraftEnabled()) {
				weatherMap.get(player.getName()).put(biome, weather);
				byte biomeByte = (byte) biome.ordinal();
				byte weatherByte = (byte) weather.ordinal();
				sPlayer.sendPacket(new PacketBiomeWeather(biomeByte,weatherByte));
			}
		}
		
	}

	@Override
	public void setGlobalWeather(SpoutWeather weather) {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			if(!weatherMap.containsKey(player.getName())) {
				weatherMap.put(player.getName(), new HashMap<Biome, SpoutWeather>());
			}
			SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
			if(sPlayer.isSpoutCraftEnabled()) {
				for(Biome biome : Biome.values()) {
					weatherMap.get(player.getName()).put(biome, weather);
					byte biomeByte = (byte) biome.ordinal();
					byte weatherByte = (byte) weather.ordinal();
					sPlayer.sendPacket(new PacketBiomeWeather(biomeByte,weatherByte));
				}
			}
		}
		
	}

	public void onPlayerJoin(SpoutPlayer player) {
		if(player.isSpoutCraftEnabled()) {
			if(weatherMap.containsKey(player.getName())) {
				SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
				for(Biome biome : weatherMap.get(player.getName()).keySet()) {
					byte biomeByte = (byte) biome.ordinal();
					byte weatherByte = (byte) weatherMap.get(player.getName()).get(biome).ordinal();
					sPlayer.sendPacket(new PacketBiomeWeather(biomeByte, weatherByte));
				}
			}
		}
	}
	
	public void onPluginEnable() {
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			weatherMap.put(player.getName(), new HashMap<Biome, SpoutWeather>());
		}
	}
	
	public void reset() {
		weatherMap.clear();
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			SpoutPlayer sPlayer = SpoutManager.getPlayer(player);
			if(sPlayer.isSpoutCraftEnabled()) {
				for(Biome biome : Biome.values()) {
					byte biomeByte = (byte) biome.ordinal();
					byte weatherByte = (byte) SpoutWeather.RESET.ordinal();
					sPlayer.sendPacket(new PacketBiomeWeather(biomeByte, weatherByte));
				}
			}
		}
	}
}
