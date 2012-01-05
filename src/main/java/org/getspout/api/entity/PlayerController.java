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
			Chunk c = p.getWorld().getChunkLive(p, false); 
			if (c != null) {
				removeObserver(c);
			}
		}
		initializedChunks.clear();
	}
	
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
		
		for (Point p : chunkFreeQueue) {
			if (initializedChunks.remove(p)) {
				freeChunk(p);
				activeChunks.remove(p);
				Chunk c = p.getWorld().getChunkLive(p, false); 
				if (c != null) {
					removeObserver(c);
				}
			}
		}
		
		chunkFreeQueue.clear();
		
		for (Point p : chunkInitQueue) {
			if (initializedChunks.add(p)) {
				Chunk c = p.getWorld().getChunkLive(p, true); 
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
			Chunk c = p.getWorld().getChunkLive(p, true);
			sendChunk(c);
			activeChunks.add(p);
			i.remove();
			chunksSent++;
		}
		
		i = chunkSendQueue.iterator();
		while (i.hasNext() && chunksSent < CHUNKS_PER_TICK) {
			Point p = i.next();
			Chunk c = p.getWorld().getChunkLive(p, true);
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
