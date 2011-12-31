package org.getspout.server;

import java.util.Random;
import java.util.UUID;

import org.getspout.api.Server;
import org.getspout.api.entity.Controller;
import org.getspout.api.entity.Entity;
import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Block;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.geo.discrete.Point;
import org.getspout.server.entity.EntityManager;
import org.getspout.server.entity.SpoutEntity;
import org.getspout.server.util.thread.AsyncManager;
import org.getspout.server.util.thread.ThreadAsyncExecutor;
import org.getspout.server.util.thread.snapshotable.SnapshotManager;
import org.getspout.server.util.thread.snapshotable.SnapshotableBoolean;
import org.getspout.server.util.thread.snapshotable.SnapshotableImmutable;
import org.getspout.server.util.thread.snapshotable.SnapshotableInt;
import org.getspout.server.util.thread.snapshotable.SnapshotableLong;

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
	// TODO
	//private final RegionSource regions;

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
	private SnapshotableImmutable<Point> spawnLocation;

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
	EntityManager entityManager;

	
	
	// TODO need world that loads from disk
	public SpoutWorld(String name, Server server, long seed) {
		super(new ThreadAsyncExecutor(), server);
		this.uid = UUID.randomUUID();
		this.server = server;
		this.seed = seed;
		this.name = name;
		this.entityManager = new EntityManager();
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
		// TODO Auto-generated method stub
		return null;
	}
	
	public int hashCode() {
		UUID uid = getUID();
		long hash = uid.getMostSignificantBits();
		hash += (hash << 5) + uid.getLeastSignificantBits();
		
		return (int)(hash ^ (hash >> 32));
	}
	
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
		return new SpoutEntity();
	}

	@Override
	public void spawnEntity(Entity e) {
		if(e.isSpawned()) throw new IllegalArgumentException("Cannot spawn an entity that is already spawned!");
		entityManager.allocate((SpoutEntity)e);		
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
		snapshotManager.copyAllSnapshots();
	}

	@Override
	public void startTickRun(long delta) throws InterruptedException {
		System.out.println("Tick: " + delta);
		
		float dt = delta / 1000.f;
		//Update all entities
		for(SpoutEntity ent : entityManager){
			ent.onTick(dt);
		}
		
		//Resolve and collisions and prepare for a snapshot.
		for(SpoutEntity ent : entityManager){
			ent.resolve();
		}
		
	}

	@Override
	public long getSeed() {
		return seed;
	}

}
