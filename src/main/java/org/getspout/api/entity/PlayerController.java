package org.getspout.api.entity;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Chunk;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.player.Player;

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
	
	private Set<Chunk> chunkInitQueue = new LinkedHashSet<Chunk>();
	private Set<Chunk> priorityChunkSendQueue = new LinkedHashSet<Chunk>();
	private Set<Chunk> chunkSendQueue = new LinkedHashSet<Chunk>();
	private Set<Chunk> chunkFreeQueue = new LinkedHashSet<Chunk>();

	private Set<Chunk> initializedChunks = new LinkedHashSet<Chunk>();
	private Set<Chunk> activeChunks = new LinkedHashSet<Chunk>();
	
	private boolean first = true;
	private volatile boolean teleported = false;
	
	private LinkedHashSet<Chunk> observed = new LinkedHashSet<Chunk>();
	
	public void snapshotStart() {
		
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
		
		for (Chunk c : chunkFreeQueue) {
			if (initializedChunks.remove(c)) {
				freeChunk(c);
				activeChunks.remove(c);
				removeObserver(c);
			}
		}
		
		chunkFreeQueue.clear();
		
		for (Chunk c : chunkInitQueue) {
			if (initializedChunks.add(c)) {
				initChunk(c);
				addObserver(c);
			}
		}
		
		chunkInitQueue.clear();
		
		int chunksSent = 0;
		
		Iterator<Chunk> i;
		
		i = priorityChunkSendQueue.iterator();
		while (i.hasNext() && chunksSent < CHUNKS_PER_TICK) {
			Chunk c = i.next();
			sendChunk(c);
			activeChunks.add(c);
			i.remove();
			chunksSent++;
		}
		
		i = chunkSendQueue.iterator();
		while (i.hasNext() && chunksSent < CHUNKS_PER_TICK) {
			Chunk c = i.next();
			sendChunk(c);
			activeChunks.add(c);
			i.remove();
			chunksSent++;
		}
		
		if (teleported) {
			sendPosition(liveTransform);
			first = false;
			teleported = false;
		}
		
		super.snapshotStart();
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
		
		Chunk currentChunk = world.getChunkLive(currentPosition, true);
		
		Point playerChunkBase = currentChunk.getBase();
		
		for (Chunk c : initializedChunks) {
			Point base = c.getBase();
			
			if (base.getMahattanDistance(playerChunkBase) > blockViewDistance) {
				chunkFreeQueue.add(c);
			}	
		}
		
		int cx = currentChunk.getX();
		int cy = currentChunk.getY();
		int cz = currentChunk.getZ();
		
		// TODO - circle loading
		for (int x = cx - viewDistance; x < cx + viewDistance; x++) {
			for (int y = cy - viewDistance; y < cy + viewDistance; y++) {
				for (int z = cz - viewDistance; z < cz + viewDistance; z++) {
					Chunk c = world.getChunkLive(x, y, z, true);
					double distance = c.getBase().getMahattanDistance(playerChunkBase);
					if (distance <= blockViewDistance) {
						if (!activeChunks.contains(c)) {
							if (distance <= TARGET_SIZE) {
								priorityChunkSendQueue.add(c);
							} else {
								chunkSendQueue.add(c);
							}
						}
						if (!initializedChunks.contains(c)) {
							chunkInitQueue.add(c);
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
	 * @param c the chunk
	 */
	protected void initChunk(Chunk c){
		//TODO: Implement Spout Protocol
	}
	
	/**
	 * Frees a chunk on the client.
	 * 
	 * This method is called during the startSnapshot stage of the tick.
	 * 
	 * This is a MONITOR method, for sending network updates, no changes should be made to the chunk
	 * 
	 * @param c the chunk
	 */
	protected void freeChunk(Chunk c){
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
