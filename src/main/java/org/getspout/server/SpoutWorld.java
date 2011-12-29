package org.getspout.server;

import java.util.Random;
import java.util.UUID;

import org.getspout.api.Server;
import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Block;
import org.getspout.api.geo.cuboid.Region;
import org.getspout.api.geo.discrete.Point;
import org.getspout.server.util.thread.snapshotutils.SnapshotManager;
import org.getspout.server.util.thread.snapshotutils.Snapshotable;

public class SpoutWorld implements World {
	
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
	private Snapshotable<Point> spawnLocation;

	/**
	* Whether to keep the spawn chunks in memory (prevent them from being
	* unloaded)
	*/
	private Snapshotable<Boolean> keepSpawnLoaded = new Snapshotable<Boolean>(snapshotManager, true);

	/**
	* Whether PvP is allowed in this world.
	*/
	private Snapshotable<Boolean> pvpAllowed = new Snapshotable<Boolean>(snapshotManager, true);

	/**
	* Whether animals can spawn in this world.
	*/
	private Snapshotable<Boolean> spawnAnimals = new Snapshotable<Boolean>(snapshotManager, true);

	/**
	* Whether monsters can spawn in this world.
	*/
	private Snapshotable<Boolean> spawnMonsters = new Snapshotable<Boolean>(snapshotManager, true);

	/**
	* Whether it is currently raining/snowing on this world.
	*/
	private Snapshotable<Boolean> currentlyRaining = new Snapshotable<Boolean>(snapshotManager, false);

	/**
	* How many ticks until the rain/snow status is expected to change.
	*/
	private Snapshotable<Integer> rainingTicks = new Snapshotable<Integer>(snapshotManager, 0);

	/**
	* Whether it is currently thundering on this world.
	*/
	private Snapshotable<Boolean> currentlyThundering =  new Snapshotable<Boolean>(snapshotManager, false);

	/**
	* How many ticks until the thundering status is expected to change.
	*/
	private Snapshotable<Integer> thunderingTicks = new Snapshotable<Integer>(snapshotManager, 0);

	/**
	* The current world age.
	*/
	private Snapshotable<Long> age = new Snapshotable<Long>(snapshotManager, 0L);

	/**
	* The current world time.
	*/
	private Snapshotable<Integer> time = new Snapshotable<Integer>(snapshotManager, 0);
	
	/**
	* The current world time.
	*/
	private Snapshotable<Integer> dayLength = new Snapshotable<Integer>(snapshotManager, 0);
	
	/**
	* The time until the next full-save.
	*/
	private Snapshotable<Integer> saveTimer = new Snapshotable<Integer>(snapshotManager, 0);

	/**
	* The check to autosave
	*/
	private Snapshotable<Boolean> autosave = new Snapshotable<Boolean>(snapshotManager, true);

	/**
	* The world's UUID
	*/
	private final UUID uid;
	
	// TODO need world that loads from disk
	public SpoutWorld(String name, Server server, long seed) {
		this.uid = UUID.randomUUID();
		this.server = server;
		this.seed = seed;
		this.name = name;
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

}
