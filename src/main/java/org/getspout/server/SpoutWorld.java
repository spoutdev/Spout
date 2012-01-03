package org.getspout.server;

import java.util.Random;
import java.util.UUID;

import org.getspout.api.Server;
import org.getspout.api.entity.Controller;
import org.getspout.api.entity.Entity;
import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Block;
import org.getspout.api.geo.cuboid.Chunk;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.material.BlockMaterial;
import org.getspout.server.entity.EntityManager;
import org.getspout.server.entity.SpoutEntity;
import org.getspout.server.util.thread.AsyncManager;
import org.getspout.server.util.thread.ThreadAsyncExecutor;
import org.getspout.server.util.thread.snapshotable.SnapshotManager;
import org.getspout.server.util.thread.snapshotable.SnapshotableBoolean;
import org.getspout.server.util.thread.snapshotable.SnapshotableInt;
import org.getspout.server.util.thread.snapshotable.SnapshotableLong;
import org.getspout.server.util.thread.snapshotable.SnapshotableReference;

public class SpoutWorld extends AsyncManager implements World {
	
	private SnapshotManager snapshotManager = new SnapshotManager();
	
	/**
	* The server of this world.
	*/
	private final Server server;

	/**
	* The name of this world.
	*/
	private final String name;

	/**
	* The region source
	*/
	private final RegionSource regions;

	/**
	* This world's Random instance.
	*/
	private final Random random = new Random();

	/**
	* The world seed.
	*/
	private final long seed;

	/**
	* The spawn position.
	*/
	private SnapshotableReference<Transform> spawnLocation = new SnapshotableReference<Transform>(snapshotManager, null);

	/**
	* Whether to keep the spawn chunks in memory (prevent them from being
	* unloaded)
	*/
	private SnapshotableBoolean keepSpawnLoaded = new SnapshotableBoolean(snapshotManager, true);

	/**
	* Whether PvP is allowed in this world.
	*/
	private SnapshotableBoolean pvpAllowed = new SnapshotableBoolean(snapshotManager, true);

	/**
	* Whether animals can spawn in this world.
	*/
	private SnapshotableBoolean spawnAnimals = new SnapshotableBoolean(snapshotManager, true);

	/**
	* Whether monsters can spawn in this world.
	*/
	private SnapshotableBoolean spawnMonsters = new SnapshotableBoolean(snapshotManager, true);

	/**
	* Whether it is currently raining/snowing on this world.
	*/
	private SnapshotableBoolean currentlyRaining = new SnapshotableBoolean(snapshotManager, false);

	/**
	* How many ticks until the rain/snow status is expected to change.
	*/
	private SnapshotableInt rainingTicks = new SnapshotableInt(snapshotManager, 0);

	/**
	* Whether it is currently thundering on this world.
	*/
	private SnapshotableBoolean currentlyThundering =  new SnapshotableBoolean(snapshotManager, false);

	/**
	* How many ticks until the thundering status is expected to change.
	*/
	private SnapshotableInt thunderingTicks = new SnapshotableInt(snapshotManager, 0);

	/**
	* The current world age.
	*/
	private SnapshotableLong age = new SnapshotableLong(snapshotManager, 0L);

	/**
	* The current world time.
	*/
	private SnapshotableInt time = new SnapshotableInt(snapshotManager, 0);
	
	/**
	* The current world time.
	*/
	private SnapshotableInt dayLength = new SnapshotableInt(snapshotManager, 0);
	
	/**
	* The time until the next full-save.
	*/
	private SnapshotableInt saveTimer = new SnapshotableInt(snapshotManager, 0);

	/**
	* The check to autosave
	*/
	private SnapshotableBoolean autosave = new SnapshotableBoolean(snapshotManager, true);

	/**
	* The world's UUID
	*/
	private final UUID uid;
	
	/**
	 * Holds all of the entities to be simulated
	 */
	private final EntityManager entityManager;

	
	
	// TODO need world that loads from disk
	// TODO set up number of stages ?
	public SpoutWorld(String name, Server server, long seed) {
		super(1, new ThreadAsyncExecutor(), server);
		this.uid = UUID.randomUUID();
		this.server = server;
		this.seed = seed;
		this.name = name;
		this.entityManager = new EntityManager();
		this.regions = new RegionSource(this);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getAge() {
		return age.get();
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Block getBlock(Point point) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UUID getUID() {
		// TODO non-null
		return uid;
	}
	
	@Override
	public Region getRegion(int x, int y, int z) {
		return regions.getRegion(x, y, z);
	}
	
	@Override
	public Region getRegion(Point point) {
		int x = (int)Math.floor(point.getX());
		int y = (int)Math.floor(point.getY());
		int z = (int)Math.floor(point.getZ());
		return regions.getRegionFromBlock(x, y, z);
	}
	
	@Override
	public Region getRegionLive(int x, int y, int z, boolean load) {
		return regions.getRegionLive(x, y, z, load);
	}
	
	@Override
	public Region getRegionLive(Point point, boolean load) {
		int x = (int)Math.floor(point.getX());
		int y = (int)Math.floor(point.getY());
		int z = (int)Math.floor(point.getZ());
		return regions.getRegionFromBlockLive(x, y, z,  load);
	}

	@Override
	public Chunk getChunk(int x, int y, int z) {
		Region region = getRegion(x >> Region.REGION_SIZE_BITS, y >> Region.REGION_SIZE_BITS, z >> Region.REGION_SIZE_BITS);
		if (region != null) {
			return region.getChunk(x & (Region.REGION_SIZE - 1), y & (Region.REGION_SIZE - 1), z & (Region.REGION_SIZE - 1));
		}
		return null;
	}
	
	@Override
	public Chunk getChunkLive(int x, int y, int z, boolean load) {
		Region region = getRegionLive(x >> Region.REGION_SIZE_BITS, y >> Region.REGION_SIZE_BITS, z >> Region.REGION_SIZE_BITS, load);
		if (region != null) {
			return region.getChunkLive(x & (Region.REGION_SIZE - 1), y & (Region.REGION_SIZE - 1), z & (Region.REGION_SIZE - 1), load);
		}
		return null;
	}

	@Override
	public Chunk getChunk(Point point) {
		int x = (int)Math.floor(point.getX());
		int y = (int)Math.floor(point.getY());
		int z = (int)Math.floor(point.getZ());
		return getChunk(x >> Chunk.CHUNK_SIZE_BITS, y >> Chunk.CHUNK_SIZE_BITS, z >> Chunk.CHUNK_SIZE_BITS);
	}
	
	@Override
	public Chunk getChunkLive(Point point, boolean load) {
		int x = (int)Math.floor(point.getX());
		int y = (int)Math.floor(point.getY());
		int z = (int)Math.floor(point.getZ());
		return getChunkLive(x >> Chunk.CHUNK_SIZE_BITS, y >> Chunk.CHUNK_SIZE_BITS, z >> Chunk.CHUNK_SIZE_BITS, load);
	}
	
	@Override
	public int hashCode() {
		UUID uid = getUID();
		long hash = uid.getMostSignificantBits();
		hash += (hash << 5) + uid.getLeastSignificantBits();
		
		return (int)(hash ^ (hash >> 32));
	}
	
	@Override
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (!(obj instanceof SpoutWorld)) {
			return false;
		} else {
			SpoutWorld world = (SpoutWorld)obj;
			
			return world.getUID().equals(getUID());
		}
		
	}

	@Override
	public Entity createEntity() {		
		return new SpoutEntity((SpoutServer)server);
	}

	@Override
	public void spawnEntity(Entity e) {
		if(e.isSpawned()) throw new IllegalArgumentException("Cannot spawn an entity that is already spawned!");
		SpoutRegion region = (SpoutRegion)e.getRegion();
		region.allocate((SpoutEntity)e);
	}

	@Override
	public Entity createAndSpawnEntity(Point point, Controller controller) {
		Entity e = createEntity();
		e.getTransform().setPosition(point);
		e.setController(controller);
		spawnEntity(e);
		return e;
	}

	@Override
	public void copySnapshotRun() throws InterruptedException {
		entityManager.copyAllSnapshots();
		snapshotManager.copyAllSnapshots();
	}

	@Override
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
				throw new IllegalStateException("Number of states exceeded limit for SpoutWorld");
			}
		}
		
	}
	
	@Override
	public void haltRun() throws InterruptedException {
		// TODO - save on halt ?
	}

	@Override
	public long getSeed() {
		return seed;
	}

	@Override
	public BlockMaterial setBlockMaterial(int x, int y, int z, BlockMaterial material) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short setBlockId(int x, int y, int z, short id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z, boolean live) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getBlockId(int x, int y, int z) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short getBlockId(int x, int y, int z, boolean live) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getBlockData(int x, int y, int z) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getBlockData(int x, int y, int z, boolean live) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte setBlockData(int x, int y, int z, byte data) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Transform getSpawnPoint() {
		return this.spawnLocation.get();
	}
	
	@Override
	public void setSpawnPoint(Transform transform) {
		this.spawnLocation.set(transform.copy());
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	@Override
	public void preSnapshotRun() throws InterruptedException {
		entityManager.preSnapshot();
	}
	
}
