package org.getspout.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.getspout.api.Server;
import org.getspout.api.entity.Controller;
import org.getspout.api.entity.Entity;
import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Chunk;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.SnapshotRead;
import org.getspout.server.entity.EntityManager;
import org.getspout.server.entity.SpoutEntity;
import org.getspout.server.util.TripleInt;
import org.getspout.server.util.thread.ThreadAsyncExecutor;
import org.getspout.server.util.thread.snapshotable.SnapshotManager;
import org.getspout.server.util.thread.snapshotable.SnapshotableReference;

public class SpoutRegion extends Region{
	
	// Can't extend AsyncManager and Region
	private final SpoutRegionManager manager;
	private final Server server;
	
	private ConcurrentLinkedQueue<TripleInt> save = new ConcurrentLinkedQueue<TripleInt>();
	private ConcurrentLinkedQueue<TripleInt> unload = new ConcurrentLinkedQueue<TripleInt>();
	
	@SuppressWarnings("unchecked")
	public SnapshotableReference<Chunk>[][][] chunks = new SnapshotableReference[Region.REGION_SIZE][Region.REGION_SIZE][Region.REGION_SIZE];
	
	/**
	 * Region coordinates of the lower, left start of the region. Add {@link Region#REGION_SIZE} to the coords to get the upper right end of the region.
	 */
	private final int x, y, z;
	
	/**
	 * The source of this region
	 */
	private final RegionSource source;
	
	/**
	 * Snapshot manager for this region
	 */
	protected SnapshotManager snapshotManager = new SnapshotManager();

	/**
	 * Holds all of the entities to be simulated
	 */
	protected final EntityManager entityManager = new EntityManager();

	public SpoutRegion(World world, float x, float y, float z, RegionSource source) {
		super(world, x, y, z);
		this.x = (int)Math.floor(x);
		this.y = (int)Math.floor(y);
		this.z = (int)Math.floor(z);
		this.source = source;
		this.server = world.getServer();
		this.manager = new SpoutRegionManager(this, 1, new ThreadAsyncExecutor(), server);
		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					chunks[dx][dy][dz] = new SnapshotableReference<Chunk>(snapshotManager, null);
				}
			}
		}
	}
	
	@Override
	@SnapshotRead
	public Chunk getChunk(int x, int y, int z) {
		if (x < Region.REGION_SIZE && x > 0 && y < Region.REGION_SIZE && y > 0 && z < Region.REGION_SIZE && z > 0) {
			return chunks[x][y][z].get();
		}
		throw new IndexOutOfBoundsException("Invalid coordinates");
	}
	

	@Override
	public Chunk getChunk(int x, int y, int z, boolean load) {
		if (x < Region.REGION_SIZE && x > 0 && y < Region.REGION_SIZE && y > 0 && z < Region.REGION_SIZE && z > 0) {
			Chunk chunk = chunks[x][y][z].get();
			if (chunk != null || !load) {
				return chunk;
			}
			//TODO: generate new chunk
			//this.getWorld().
		}
		throw new IndexOutOfBoundsException("Invalid coordinates");
	}

	@Override
	public boolean hasChunk(int x, int y, int z) {
		// TODO Auto-generated method stub
		return false;
	}

	SpoutRegionManager getManager() {
		return manager;
	}
	
	/**
	 * Queues a Chunk for saving
	 */
	@DelayedWrite
	public void saveChunk(int x, int y, int z) {
		this.save.add(new TripleInt(x, y, z));
	}
	
	/**
	 * Queues all chunks for saving
	 */
	@DelayedWrite
	public void save() {
		this.save.add(TripleInt.NULL);
	}
	
	@Override
	public void unload(boolean save) {
		if (save) {
			save();
		}
		unload.add(TripleInt.NULL);
		//Ensure this region is removed from the source. This may be calling the parent method twice, but is harmless.
		source.unloadRegion(x, y, z, save);
	}
	
	public void unloadChunk(int x, int y, int z, boolean save) {
		if (save) {
			saveChunk(x, y, z);
		}
		unload.add(new TripleInt(x, y, z));
	}
	
	
	public void copySnapshotRun() throws InterruptedException {
		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					Chunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null) {
						((SpoutChunk)chunk).copySnapshotRun();
					}
				}
			}
		}
		snapshotManager.copyAllSnapshots();
		
		TripleInt chunkCoords;
		while ((chunkCoords = save.poll()) != null) {
			 if (chunkCoords == TripleInt.NULL) {
				 saveAllSync();
			 } else {
				 saveChunkSync(chunkCoords.x, chunkCoords.y, chunkCoords.z);
			 }
		}
		
		while ((chunkCoords = unload.poll()) != null) {
			 if (chunkCoords == TripleInt.NULL) {
				 unloadAllSync();
			 } else {
				 unloadChunkSync(chunkCoords.x, chunkCoords.y, chunkCoords.z);
			 }
		}
		
		// Updates an nulled chunks
		snapshotManager.copyAllSnapshots();
 		
	}
	
	private void saveAllSync() {
		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					saveChunkSync(x, y, z);
				}
			}
		}
	}
	
	private void unloadAllSync() {
		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					unloadChunkSync(x, y, z);
				}
			}
		}
	}
	
	private void saveChunkSync(int x, int y, int z) {
		if (x < Region.REGION_SIZE && x > 0 && y < Region.REGION_SIZE && y > 0 && z < Region.REGION_SIZE && z > 0) {
			Chunk chunk = chunks[x][y][z].get();
			if (chunk != null) {
				((SpoutChunk)chunk).syncSave();
			}
		}
	}
	
	private void unloadChunkSync(int x, int y, int z) {
		if (x < Region.REGION_SIZE && x > 0 && y < Region.REGION_SIZE && y > 0 && z < Region.REGION_SIZE && z > 0) {
			Chunk chunk = chunks[x][y][z].get();
			if (chunk != null) {
				chunks[x][y][z].set(null);
			}
		}
	}

	public void startTickRun(int stage, long delta) throws InterruptedException {
		switch (stage) {
		case 0: {
			float dt = delta / 1000.f;
			//Update all entities
			for(SpoutEntity ent : entityManager){
				ent.onTick(dt);
			}
			break;
		}
		case 1: {
			//Resolve and collisions and prepare for a snapshot.
			for(SpoutEntity ent : entityManager){
				ent.resolve();
			}
			break;
		}
		default: {
			throw new IllegalStateException("Number of states exceeded limit for SpoutRegion");
		}
	}
	}

	public void haltRun() throws InterruptedException {
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection<Entity> getAll(Class<? extends Controller> type) {
		return (Collection<Entity>)(Collection)entityManager.getAll(type);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection<Entity> getAll() {
		return (Collection<Entity>)(Collection)entityManager.getAll();
	}

	public SpoutEntity getEntity(int id) {
		return entityManager.getEntity(id);
	}

	/**
	 * Allocates the id for an entity.
	 *
	 * @param entity The entity.
	 * @return The id.
	 */
	public int allocate(SpoutEntity entity) {
		return entityManager.allocate(entity);
	}

	/**
	 * Deallocates the id for an entity.
	 *
	 * @param entity The entity.
	 */
	public void deallocate(SpoutEntity entity) {
		entityManager.deallocate(entity);
	}

	public Iterator<SpoutEntity> iterator() {
		return entityManager.iterator();
	}
}
