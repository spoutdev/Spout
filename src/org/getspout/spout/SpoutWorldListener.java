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
package org.getspout.spout;

import java.lang.reflect.Field;
import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;
import org.getspout.spout.block.SpoutCraftChunk;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.chunkstore.SimpleChunkDataManager;

public class SpoutWorldListener extends WorldListener{
	
	@Override
	public void onChunkLoad(ChunkLoadEvent event) {
		if (SpoutCraftChunk.replaceBukkitChunk(event.getChunk())) {
			//update the reference to the chunk in the event
			try {
				Field chunk = ChunkEvent.class.getDeclaredField("chunk");
				chunk.setAccessible(true);
				chunk.set(event, event.getChunk().getWorld().getChunkAt(event.getChunk().getX(), event.getChunk().getZ()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//grab all entities associated with the chunk being loaded
			List entities = event.getWorld().getEntities();
                        
			for (int i = 0; i < entities.size(); i++) {
			    Entity temp = (Entity) entities.get(i);
			    Spout.getInstance().getEntityTrackingManager().onEntityJoin(temp);
			}
			
			SimpleChunkDataManager dm = (SimpleChunkDataManager)SpoutManager.getChunkDataManager();
			dm.loadChunk(event.getChunk());
		}
	}

	@Override
	public void onWorldLoad(WorldLoadEvent event) {
		SimpleChunkDataManager dm = (SimpleChunkDataManager)SpoutManager.getChunkDataManager();
		dm.loadWorldChunks(event.getWorld());
	}
}
