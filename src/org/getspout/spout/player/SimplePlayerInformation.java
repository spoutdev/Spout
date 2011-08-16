package org.getspout.spout.player;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.block.Biome;
import org.getspout.spoutapi.block.SpoutWeather;
import org.getspout.spoutapi.player.PlayerInformation;

public class SimplePlayerInformation implements PlayerInformation{
	
	HashMap<Biome,SpoutWeather> weatherMap = new HashMap<Biome, SpoutWeather>();

	@Override
	public SpoutWeather getBiomeWeather(Biome biome) {
		if(weatherMap.containsKey(biome)) {
			return weatherMap.get(biome);
		}
		else {
			return SpoutWeather.RESET;
		}
	}

	@Override
	public void setBiomeWeather(Biome biome, SpoutWeather weather) {
		weatherMap.put(biome, weather);
	}

	@Override
	public Set<Biome> getBiomes() {
		return weatherMap.keySet();
	}


}
