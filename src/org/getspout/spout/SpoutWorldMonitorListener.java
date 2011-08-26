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

import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.getspout.spout.chunkstore.SimpleChunkDataManager;
import org.getspout.spoutapi.SpoutManager;

public class SpoutWorldMonitorListener extends WorldListener {

	@Override
	public void onChunkUnload(ChunkUnloadEvent event) {
		if (event.isCancelled()) {
			return;
		}
		SimpleChunkDataManager dm = (SimpleChunkDataManager)SpoutManager.getChunkDataManager();
		dm.unloadChunk(event.getChunk());

	}

	@Override
	public void onWorldSave(WorldSaveEvent event) {
		SimpleChunkDataManager dm = (SimpleChunkDataManager)SpoutManager.getChunkDataManager();
		dm.saveWorldChunks(event.getWorld());
	}

	@Override
	public void onWorldUnload(WorldUnloadEvent event) {
		if (event.isCancelled()) {
			return;
		}
		SimpleChunkDataManager dm = (SimpleChunkDataManager)SpoutManager.getChunkDataManager();
		dm.unloadWorldChunks(event.getWorld());
	}
	
}
