/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout.player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.block.Biome;
import org.bukkit.entity.LivingEntity;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutWeather;
import org.getspout.spoutapi.player.PlayerInformation;

public class SimplePlayerInformation implements PlayerInformation{
	
	HashMap<Biome,SpoutWeather> weatherMap = new HashMap<Biome, SpoutWeather>();
	HashMap<LivingEntity, String> entitySkin = new HashMap<LivingEntity, String>();
	HashMap<LivingEntity, String> entitySecondarySkin = new HashMap<LivingEntity, String>();

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

	@Override
	public void setEntitySkin(LivingEntity entity, String url) {
		entitySkin.put(entity, url);
	}

	@Override
	public String getEntitySkin(LivingEntity entity) {
		return entitySkin.get(entity);
	}

	@Override
	public void setEntitySecondarySkin(LivingEntity entity, String url) {
		entitySecondarySkin.put(entity, url);
	}

	@Override
	public String getEntitySecondarySkin(LivingEntity entity) {
		return entitySecondarySkin.get(entity);
	}
	
	/**
	 * This method gets a property from the object using the getter for it. If the player of the object hasn't got this property (aka property==null), it'll try to get the global property instead.
	 * @param key of the property. For example: "EntitySkin"
	 * @param args for the used getter
	 * @return the property, if found, else null
	 * @throws Exception when the property wasn't found
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getProperty(String key, Object ...args) throws Exception{
		//Use in this form (String)getProperty("EntitySkin", entity);
		Object ret = null;
		Class clazz = getClass();
		try{
			//Generate an argument list from the given arguments
			List<Class> classes = new ArrayList<Class>();
			for(Object o:args){
				classes.add(o.getClass());
			}
			Class arguments[] = {};
			arguments = classes.toArray(arguments);
			//get the property-get-method
			Method getter = clazz.getDeclaredMethod("get" + key, arguments);
			//get information from this
			ret = getter.invoke(this, args);
			//get information from global information, if ret==null
			if(ret==null)
			{
				ret = getter.invoke(SpoutManager.getPlayerManager().getGlobalInfo(), args);
			}
		} catch(Exception e){
			throw new Exception("No get-method for the property '"+key+"' could be found.");
		}
		
		return ret;
	}
}
