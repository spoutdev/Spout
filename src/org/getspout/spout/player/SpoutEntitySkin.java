package org.getspout.spout.player;

import java.util.HashMap;

import org.getspout.spoutapi.player.EntitySkinType;

public class SpoutEntitySkin {
	private HashMap<EntitySkinType, String> textures = new HashMap<EntitySkinType, String>();
	public SpoutEntitySkin(){
		
	}
	
	public void setSkin(EntitySkinType type, String url){
		textures.put(type, url);
	}
	
	public String getSkin(EntitySkinType type){
		return textures.get(type);
	}
	
	public void reset(){
		textures.clear();
	}
}
