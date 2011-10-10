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

import org.bukkit.entity.Player;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;
import org.getspout.spout.block.SpoutCraftChunk;
import org.getspout.spout.chunkstore.SimpleChunkDataManager;
import org.getspout.spout.inventory.SimpleItemManager;
import org.getspout.spoutapi.SpoutManager;

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
			
			SimpleChunkDataManager dm = (SimpleChunkDataManager)SpoutManager.getChunkDataManager();
			dm.loadChunk(event.getChunk());
			SimpleItemManager im = (SimpleItemManager)SpoutManager.getItemManager();
			List<Player> players = event.getChunk().getWorld().getPlayers();
			im.sendBlockOverrideToPlayers(players.toArray(new Player[0]), event.getChunk());
		}
	}

	@Override
	public void onWorldLoad(WorldLoadEvent event) {
		SimpleChunkDataManager dm = (SimpleChunkDataManager)SpoutManager.getChunkDataManager();
		dm.loadWorldChunks(event.getWorld());
		SimpleItemManager im = (SimpleItemManager)SpoutManager.getItemManager();
		im.sendBlockOverrideToPlayers(event.getWorld().getPlayers().toArray(new Player[0]), event.getWorld());
	}
}
