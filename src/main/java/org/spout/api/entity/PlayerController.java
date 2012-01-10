/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.entity;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.player.Player;

/**
 * Represents a Controller that is controlled by a player
 * An entity is a Player if entity.GetController() instanceof PlayerController == true
 *
 */
public abstract class PlayerController extends Controller {
	protected Player owner;
	
	private final static int TARGET_SIZE = 5 * Chunk.CHUNK_SIZE;
	private final static int CHUNKS_PER_TICK = 200;

	private final int viewDistance = 5;
	private final int blockViewDistance = viewDistance * Chunk.CHUNK_SIZE;
	
	public PlayerController(Player owner){
		this.owner = owner;
	}
	
	public Player getPlayer(){
		return owner;
	}
	
	private Point lastChunkCheck;
	
	// Base points used so as not to load chunks unnecessarily
	private Set<Point> chunkInitQueue = new LinkedHashSet<Point>();
	private Set<Point> priorityChunkSendQueue = new LinkedHashSet<Point>();
	private Set<Point> chunkSendQueue = new LinkedHashSet<Point>();
	private Set<Point> chunkFreeQueue = new LinkedHashSet<Point>();

	private Set<Point> initializedChunks = new LinkedHashSet<Point>();
	private Set<Point> activeChunks = new LinkedHashSet<Point>();
	
	private boolean first = true;
	private volatile boolean teleported = false;
	
	private LinkedHashSet<Chunk> observed = new LinkedHashSet<Chunk>();
	
	public void onDeath() {
		for (Point p : initializedChunks) {
			freeChunk(p);
			activeChunks.remove(p);
			Chunk c = p.getWorld().getChunk(p, false); 
			if (c != null) {
				removeObserver(c);
			}
		}
		initializedChunks.clear();
	}
	
	public void preSnapshot() {
		
		if (parent == null) {
			return;
		}
		
		// TODO - teleport smoothing
		
		Transform lastTransform = parent.getTransform();
		Transform liveTransform = parent.getLiveTransform();
		
		if (liveTransform != null) {
			Point currentPosition = liveTransform.getPosition();
			
			if (currentPosition.getMahattanDistance(lastChunkCheck) > (Chunk.CHUNK_SIZE >> 1)) {
				checkChunkUpdates(currentPosition);
				lastChunkCheck = currentPosition;
			}
			
			if (first || lastTransform == null || lastTransform.getPosition().getWorld() != liveTransform.getPosition().getWorld()) {
				worldChanged(liveTransform.getPosition().getWorld());
				teleported = true;
			}
		}
		
		for (Point p : chunkFreeQueue) {
			if (initializedChunks.remove(p)) {
				freeChunk(p);
				activeChunks.remove(p);
				Chunk c = p.getWorld().getChunk(p, false); 
				if (c != null) {
					removeObserver(c);
				}
			}
		}
		
		chunkFreeQueue.clear();
		
		for (Point p : chunkInitQueue) {
			if (initializedChunks.add(p)) {
				Chunk c = p.getWorld().getChunk(p, true); 
				initChunk(p);
				addObserver(c);
			}
		}
		
		chunkInitQueue.clear();
		
		int chunksSent = 0;
		
		Iterator<Point> i;
		
		i = priorityChunkSendQueue.iterator();
		while (i.hasNext() && chunksSent < CHUNKS_PER_TICK) {
			Point p = i.next();
			Chunk c = p.getWorld().getChunk(p, true);
			sendChunk(c);
			activeChunks.add(p);
			i.remove();
			chunksSent++;
		}
		
		i = chunkSendQueue.iterator();
		while (i.hasNext() && chunksSent < CHUNKS_PER_TICK) {
			Point p = i.next();
			Chunk c = p.getWorld().getChunk(p, true);
			sendChunk(c);
			activeChunks.add(p);
			i.remove();
			chunksSent++;
		}
		
		if (teleported) {
			sendPosition(liveTransform);
			first = false;
			teleported = false;
		}
		
		super.preSnapshot();
	}
	
	private void addObserver(Chunk c) {
		observed.add(c);
		c.addObserver(owner);
	}
	
	private void removeObserver(Chunk c) {
		observed.remove(c);
		c.removeObserver(owner);
	}
	
	private void checkChunkUpdates(Point currentPosition) {
			
		// Recalculating these
		priorityChunkSendQueue.clear();
		chunkSendQueue.clear();
		chunkFreeQueue.clear();
		chunkInitQueue.clear();

		World world = currentPosition.getWorld();
		int bx = (int)currentPosition.getX();
		int by = (int)currentPosition.getY();
		int bz = (int)currentPosition.getZ();
		
		Point playerChunkBase = Chunk.pointToBase(currentPosition);
		
		for (Point p : initializedChunks) {
			if (p.getMahattanDistance(playerChunkBase) > blockViewDistance) {
				chunkFreeQueue.add(p);
			}	
		}
		
		int cx = bx >> Chunk.CHUNK_SIZE_BITS;
		int cy = by >> Chunk.CHUNK_SIZE_BITS;
		int cz = bz >> Chunk.CHUNK_SIZE_BITS;
		
		// TODO - circle loading
		for (int x = cx - viewDistance; x < cx + viewDistance; x++) {
			for (int y = cy - viewDistance; y < cy + viewDistance; y++) {
				for (int z = cz - viewDistance; z < cz + viewDistance; z++) {
					Point base = new Point(world, x << Chunk.CHUNK_SIZE_BITS, y << Chunk.CHUNK_SIZE_BITS, z << Chunk.CHUNK_SIZE_BITS);
					double distance = base.getMahattanDistance(playerChunkBase);
					if (distance <= blockViewDistance) {
						if (!activeChunks.contains(base)) {
							if (distance <= TARGET_SIZE) {
								priorityChunkSendQueue.add(base);
							} else {
								chunkSendQueue.add(base);
							}
						}
						if (!initializedChunks.contains(base)) {
							chunkInitQueue.add(base);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Sends a chunk to the client.
	 * 
	 * This method is called during the startSnapshot stage of the tick.
	 * 
	 * This is a MONITOR method, for sending network updates, no changes should be made to the chunk
	 * 
	 * @param c the chunk
	 */
	protected void sendChunk(Chunk c){
		//TODO: Implement Spout Protocol
	}
	
	/**
	 * Frees a chunk on the client.
	 * 
	 * This method is called during the startSnapshot stage of the tick.
	 * 
	 * This is a MONITOR method, for sending network updates, no changes should be made to the chunk
	 * 
	 * @param p the base Point for the chunk
	 */
	protected void initChunk(Point p){
		//TODO: Implement Spout Protocol
	}
	
	/**
	 * Frees a chunk on the client.
	 * 
	 * This method is called during the startSnapshot stage of the tick.
	 * 
	 * This is a MONITOR method, for sending network updates, no changes should be made to the chunk
	 * 
	 * @param p the base Point for the chunk
	 */
	protected void freeChunk(Point p){
		//TODO: Inplement Spout Protocol
	}
	
	/**
	 * Sends the player's position to the client
	 * 
	 * This method is called during the startSnapshot stage of the tick.
	 * 
	 * This is a MONITOR method, for sending network updates, no changes should be made to the chunk
	 * 
	 * @param t the transform
	 */
	protected void sendPosition(Transform t){
		//TODO: Implement Spout Protocol
	}
	
	/**
	 * Called when the player's world changes.
	 * 
	 * This method is called during the startSnapshot stage of the tick.
	 * 
	 * This is a MONITOR method, for sending network updates, no changes should be made to the chunk
	 * 
	 * @param t the transform
	 */
	protected void worldChanged(World world){
		//TODO: Implement Spout Protocol
	}
}
