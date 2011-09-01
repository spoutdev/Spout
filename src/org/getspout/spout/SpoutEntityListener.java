package org.getspout.spout;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutEntityListener extends EntityListener {

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof SpoutPlayer){
			event.setCancelled(!((SpoutPlayer)event.getEntity()).isPreCachingComplete());
		}
	}

	@Override
	public void onEntityTarget(EntityTargetEvent event) {
		if(event.getTarget() instanceof SpoutPlayer){
			event.setCancelled(!((SpoutPlayer)event.getTarget()).isPreCachingComplete());
		}
	}

}
