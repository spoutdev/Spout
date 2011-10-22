package org.getspout.spout;

import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutEntityListener extends EntityListener {

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof SpoutPlayer){
			event.setCancelled(event.isCancelled() || !((SpoutPlayer)event.getEntity()).isPreCachingComplete());
		}
	}

	@Override
	public void onEntityTarget(EntityTargetEvent event) {
		if(event.getTarget() instanceof SpoutPlayer){
			event.setCancelled(event.isCancelled() || !((SpoutPlayer)event.getTarget()).isPreCachingComplete());
		}
	}
	
	@Override
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Spout.getInstance().getEntityTrackingManager().onEntityJoin(event.getEntity());
	}
	
	@Override
	public void onItemSpawn(ItemSpawnEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Spout.getInstance().getEntityTrackingManager().onEntityJoin(event.getEntity());
	}
	
	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		Spout.getInstance().getEntityTrackingManager().onEntityDeath(event.getEntity());
	}
}
