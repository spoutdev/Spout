package org.getspout.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.getspout.api.Server;
import org.getspout.api.Spout;
import org.getspout.api.entity.Controller;
import org.getspout.api.entity.Entity;
import org.getspout.api.generator.WorldGenerator;
import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Chunk;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.util.cuboid.CuboidShortBuffer;
import org.getspout.api.util.thread.DelayedWrite;
import org.getspout.api.util.thread.LiveRead;
import org.getspout.api.util.thread.SnapshotRead;
import org.getspout.server.entity.EntityManager;
import org.getspout.server.entity.SpoutEntity;
import org.getspout.server.util.TripleInt;
import org.getspout.server.util.thread.ThreadAsyncExecutor;
import org.getspout.server.util.thread.snapshotable.SnapshotManager;
import org.getspout.server.util.thread.snapshotable.SnapshotableReference;

public class SpoutRegion extends Region {
	
	private AtomicInteger numberActiveChunks = new AtomicInteger();

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
		this.x = (int) Math.floor(x);
		this.y = (int) Math.floor(y);
		this.z = (int) Math.floor(z);
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
		if (x < Region.REGION_SIZE && x >= 0 && y < Region.REGION_SIZE && y >= 0 && z < Region.REGION_SIZE && z >= 0) {
			return chunks[x][y][z].get();
		}
		throw new IndexOutOfBoundsException("Invalid coordinates");
	}

	@Override
	@LiveRead
	public Chunk getChunkLive(int x, int y, int z, boolean load) {
		if (x < Region.REGION_SIZE && x >= 0 && y < Region.REGION_SIZE && y >= 0 && z < Region.REGION_SIZE && z >= 0) {
			Chunk chunk = chunks[x][y][z].get();
			if (chunk != null || !load) {
				return chunk;
			}
			//TODO: generate new chunk
			//this.getWorld().

			SnapshotableReference<Chunk> ref = chunks[x][y][z];

			boolean success = false;

			while (!success) {
				int cx = (this.getX() * Region.REGION_SIZE + x) * Chunk.CHUNK_SIZE;
				int cy = (this.getY() * Region.REGION_SIZE + y) * Chunk.CHUNK_SIZE;
				int cz = (this.getZ() * Region.REGION_SIZE + z) * Chunk.CHUNK_SIZE;
				
				short[] buffer = new short[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE];
				CuboidShortBuffer cBuffer = new CuboidShortBuffer(getWorld(), cx << Chunk.CHUNK_SIZE_BITS, cy << Chunk.CHUNK_SIZE_BITS, cz << Chunk.CHUNK_SIZE_BITS, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE, Chunk.CHUNK_SIZE, buffer);

				WorldGenerator generator = getWorld().getGenerator();
				generator.generate(cBuffer, this.source.random);
				
				SpoutChunk newChunk = new SpoutChunk(getWorld(), this, cx, cy , cz, buffer, null);
				success = ref.compareAndSet(null, newChunk);

				if (success) {
					numberActiveChunks.incrementAndGet();
					return newChunk;
				} else {
					Chunk oldChunk = ref.getLive();
					if (oldChunk != null) {
						return oldChunk;
					}
				}
			}
		}
		throw new IndexOutOfBoundsException("Invalid coordinates");
	}
	
	/**
	 * Removes a chunk from the region and indicates if the region is empty
	 * 
	 * @param c the chunk to remove
	 * @return true if the region is now empty
	 */
	public boolean forceRemoveChunk(Chunk c) {
		System.out.println("Attempting to remove: " + c);
		if (c.getRegion() != this) {
			return false;
		}
		int cx = c.getX() & (Region.REGION_SIZE - 1);
		int cy = c.getY() & (Region.REGION_SIZE - 1);
		int cz = c.getZ() & (Region.REGION_SIZE - 1);
		
		SnapshotableReference<Chunk> current = chunks[cx][cy][cz];
		Chunk currentChunk = current.getLive();
		if (currentChunk != c) {
			return false;
		}
		boolean success = current.compareAndSet(currentChunk, null);
		if (success) {
			System.out.println("Chunk removed");
			int num = numberActiveChunks.decrementAndGet();
			if (num == 0) {
				return true;
			} else if (num < 0) {
				throw new IllegalStateException("Region has less than 0 active chunks");
			}
		}
		return false;
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
		entityManager.copyAllSnapshots();

		for (int dx = 0; dx < Region.REGION_SIZE; dx++) {
			for (int dy = 0; dy < Region.REGION_SIZE; dy++) {
				for (int dz = 0; dz < Region.REGION_SIZE; dz++) {
					Chunk chunk = chunks[dx][dy][dz].get();
					if (chunk != null) {
						((SpoutChunk) chunk).copySnapshotRun();
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
				((SpoutChunk) chunk).syncSave();
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
				for (SpoutEntity ent : entityManager) {
					try {
						ent.onTick(dt);
					}
					catch (Exception e){
						Spout.getGame().getLogger().severe("Unhandled exception during tick for " + ent.toString());
						e.printStackTrace();
					}
				}
				break;
			}
			case 1: {
				//Resolve and collisions and prepare for a snapshot.
				for (SpoutEntity ent : entityManager) {
					try {
						ent.resolve();
					}
					catch (Exception e){
						Spout.getGame().getLogger().severe("Unhandled exception during tick resolution for " + ent.toString());
						e.printStackTrace();
					}
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

	public void preSnapshotRun() throws InterruptedException {
		entityManager.preSnapshot();
	}

	@SuppressWarnings( {"rawtypes", "unchecked"})
	public Collection<Entity> getAll(Class<? extends Controller> type) {
		return (Collection<Entity>) (Collection) entityManager.getAll(type);
	}

	@SuppressWarnings( {"rawtypes", "unchecked"})
	public Collection<Entity> getAll() {
		return (Collection<Entity>) (Collection) entityManager.getAll();
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

	public EntityManager getEntityManager() {
		return entityManager;
	}
}
