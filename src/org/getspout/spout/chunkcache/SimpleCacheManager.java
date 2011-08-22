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
package org.getspout.spout.chunkcache;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet51MapChunk;
import net.minecraft.server.World;

import org.getspout.spout.SpoutNetServerHandler;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.chunkcache.CacheManager;

public class SimpleCacheManager implements CacheManager {

	@Override
	public void handle(int id, boolean add, long[] hashes) {
		ChunkCache.addToHashUpdateQueue(id, add, hashes);
	}
	
	@Override
	public void refreshChunkRequest(int id, int cx, int cz) {
		SpoutCraftPlayer player = (SpoutCraftPlayer)SpoutManager.getPlayerFromId(id);
		World world = player.getHandle().world;
		Packet packet51 = new Packet51MapChunk(cx << 4, 0, cz << 4, 16, 128, 16, world);
		player.getNetServerHandler().sendPacket(packet51);
		SpoutNetServerHandler.sendChunkTiles(cx, cz, player.getHandle());
	}

}
