package org.getspout.unchecked.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.getspout.api.entity.Entity;
import org.getspout.api.entity.PlayerController;
import org.getspout.api.generator.Populator;
import org.getspout.api.generator.WorldGenerator;
import org.getspout.api.geo.Biome;
import org.getspout.api.geo.World;
import org.getspout.api.geo.cuboid.Block;
import org.getspout.api.geo.cuboid.Chunk;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.geo.discrete.Pointm;
import org.getspout.api.math.Vector3;
import org.getspout.api.player.Player;
import org.getspout.api.protocol.notch.msg.LoadChunkMessage;
import org.getspout.api.protocol.notch.msg.StateChangeMessage;
import org.getspout.api.protocol.notch.msg.TimeMessage;
import org.getspout.unchecked.server.block.SpoutBlock;
import org.getspout.unchecked.server.entity.EntityManager;
import org.getspout.server.entity.SpoutEntity;
import org.getspout.server.player.SpoutPlayer;
import org.getspout.unchecked.server.io.StorageOperation;
import org.getspout.unchecked.server.io.WorldMetadataService;
import org.getspout.unchecked.server.io.WorldMetadataService.WorldFinalValues;
import org.getspout.unchecked.server.io.WorldStorageProvider;

/**
 * A class which represents the in-game world.
 *
 * @author Graham Edgecombe
 */
public final class SpoutWorld implements World {
	/**
	 * The server of this world.
	 */
	private final SpoutServer server;

	/**
	 * The name of this world.
	 */
	private final String name;

	/**
	 * The chunk manager.
	 */
	private final ChunkManager chunks;

	/**
	 * The entity manager.
	 */
	private final EntityManager entities = new EntityManager();

	/**
	 * This world's Random instance.
	 */
	private final Random random = new Random();

	/**
	 * A map between locations and cached Block objects.
	 */
	private final Map<Point, SpoutBlock> blockCache = new ConcurrentHashMap<Point, SpoutBlock>();

	/**
	 * The world populators for this world.
	 */
	private final List<Populator> populators;
	
	/**
	 * The world seed.
	 */
	private final long seed;

	/**
	 * The spawn position.
	 */
	private Pointm spawnLocation;

	/**
	 * Whether to keep the spawn chunks in memory (prevent them from being
	 * unloaded)
	 */
	private boolean keepSpawnLoaded = true;

	/**
	 * Whether PvP is allowed in this world.
	 */
	private boolean pvpAllowed = true;

	/**
	 * Whether animals can spawn in this world.
	 */
	private boolean spawnAnimals = true;

	/**
	 * Whether monsters can spawn in this world.
	 */
	private boolean spawnMonsters = true;

	/**
	 * Whether it is currently raining/snowing on this world.
	 */
	private boolean currentlyRaining = false;

	/**
	 * How many ticks until the rain/snow status is expected to change.
	 */
	private int rainingTicks = 0;

	/**
	 * Whether it is currently thundering on this world.
	 */
	private boolean currentlyThundering = false;

	/**
	 * How many ticks until the thundering status is expected to change.
	 */
	private int thunderingTicks = 0;

	/**
	 * The current world time.
	 */
	private long time = 0;

	/**
	 * The time until the next full-save.
	 */
	private int saveTimer = 0;

	/**
	 * The check to autosave
	 */
	private boolean autosave = true;

	/*
	 * The world metadata service used
	 */
	private final WorldStorageProvider storageProvider;

	/**
	 * The world's UUID
	 */
	private final UUID uid;
	/**
	 * Last time this world was updated
	 */
	float lastTick = 0.0f;

	/**
	 * Creates a new world with the specified chunk I/O service, environment,
	 * and world generator.
	 *
	 * @param name The name of the world.
	 * @param provider The world storage provider
	 * @param environment The environment.
	 * @param generator The world generator.
	 */
	public SpoutWorld(SpoutServer server, String name, long seed, WorldStorageProvider provider, WorldGenerator generator) {
		this.server = server;
		this.name = name;
		provider.setWorld(this);
		chunks = new ChunkManager(this, provider.getChunkIoService(), generator);
		storageProvider = provider;
		EventFactory.onWorldInit(this);
		WorldFinalValues values = null;
		try {
			values = provider.getMetadataService().readWorldData();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Extra checks for seed
		if (values != null) {
			if (values.getSeed() == 0L) {
				this.seed = seed;
			} else {
				this.seed = values.getSeed();
			}
			uid = values.getUuid();
		} else {
			this.seed = seed;
			uid = UUID.randomUUID();
		}
		populators = generator.getDefaultPopulators(this);
		if (spawnLocation == null) {
			spawnLocation = generator.getFixedSpawnLocation(this, random);
		}

		int centerX = spawnLocation == null ? 0 : (int)spawnLocation.getX() >> 4;
		int centerZ = spawnLocation == null ? 0 : (int)spawnLocation.getZ() >> 4;

		server.getLogger().log(Level.INFO, "Preparing spawn for {0}", name);
		long loadTime = System.currentTimeMillis();

		int radius = 4 * server.getViewDistance() / 3;

		int total = (radius * 2 + 1) * (radius * 2 + 1), current = 0;
		for (int x = centerX - radius; x <= centerX + radius; ++x) {
			for (int z = centerZ - radius; z <= centerZ + radius; ++z) {
				++current;
				loadChunk(x, z);

				if (System.currentTimeMillis() >= loadTime + 1000) {
					int progress = 100 * current / total;
					SpoutServer.logger.log(Level.INFO, "Preparing spawn for {0}: {1}%", new Object[] {name, progress});
					loadTime = System.currentTimeMillis();
				}
			}
		}
		server.getLogger().log(Level.INFO, "Preparing spawn for {0}: done", name);
		if (spawnLocation == null) {
			spawnLocation = generator.getFixedSpawnLocation(this, random);
			if (spawnLocation == null) {
				spawnLocation = new Pointm(this, 0, getHighestBlockYAt(0, 0), 0);

				if (!generator.canSpawn(this, spawnLocation.getX(), spawnLocation.getZ())) {
					// 10 tries only to prevent a return false; bomb
					for (int tries = 0; tries < 10 && !generator.canSpawn(this, spawnLocation.getX(), spawnLocation.getZ()); ++tries) {
						spawnLocation.setX(spawnLocation.getX() + random.nextFloat() * 128 - 64);
						spawnLocation.setZ(spawnLocation.getZ() + random.nextFloat() * 128 - 64);
					}
				}

				spawnLocation.setY(1 + getHighestBlockYAt((int)spawnLocation.getX(), (int)spawnLocation.getZ()));
			}
		}
		EventFactory.onWorldLoad(this);
		save();
		lastTick = (float)System.currentTimeMillis();

	}

	////////////////////////////////////////
	// Various internal mechanisms

	/**
	 * Get the world chunk manager.
	 *
	 * @return The ChunkManager for the world.
	 */
	protected ChunkManager getChunkManager() {
		return chunks;
	}

	/**
	 * Updates all the entities within this world.
	 */
	public void pulse() {
		float dt = ((float)System.currentTimeMillis() - lastTick) * 1000;
		ArrayList<SpoutEntity> temp = new ArrayList<SpoutEntity>(entities.getAll());

		for (SpoutEntity entity : temp) {
			entity.onTick(dt);
		}

		for (SpoutEntity entity : temp) {
			entity.reset();
		}

		/*
		 * 
		//TODO: This should go in Vanilla
		// We currently tick at 1/4 the speed of regular MC
		// Modulus by 12000 to force permanent day.
		time = (time + 1) % 12000;
		if (time % 12 == 0) {
			// Only send the time every so often; clients are smart.
			for (SpoutPlayer player : getRawPlayers()) {
				player.getSession().send(new TimeMessage(player.getPlayerTime()));
			}
		}

		if (--rainingTicks <= 0) {
			setStorm(!currentlyRaining);
		}

		if (--thunderingTicks <= 0) {
			setThundering(!currentlyThundering);
		}

		if (currentlyRaining && currentlyThundering) {
			if (random.nextDouble() < .01) {
				SpoutChunk[] chunkList = chunks.getLoadedChunks();
				SpoutChunk chunk = chunkList[random.nextInt(chunkList.length)];

				int x = (chunk.getX() << 4) + (int) (random.nextDouble() * 16);
				int z = (chunk.getZ() << 4) + (int) (random.nextDouble() * 16);
				int y = getHighestBlockYAt(x, z);

				strikeLightning(new Location(this, x, y, z));
			}
		}
*/
		if (autosave && --saveTimer <= 0) {
			saveTimer = 60 * 20;
			save();
		}
	}

	/**
	 * Gets the entity manager.
	 *
	 * @return The entity manager.
	 */
	public EntityManager getEntityManager() {
		return entities;
	}

	// SpoutEntity lists
	
	public List<Player> getPlayers() {
		Collection<SpoutEntity> players = entities.getAll(PlayerController.class);
		ArrayList<Player> result = new ArrayList<Player>();
		for (SpoutEntity p : players) {
			result.add(((PlayerController)p.getController()).getPlayer());
		}
		return result;
	}


	public List<Entity> getEntities() {
		Collection<SpoutEntity> list = entities.getAll();
		ArrayList<Entity> result = new ArrayList<Entity>();
		for (Entity e : list) {
			result.add(e);
		}
		return result;
	}

	// Various malleable world properties

	public Point getSpawnLocation() {
		return spawnLocation;
	}

	public boolean setSpawnLocation(int x, int y, int z) {
		return setSpawnLocation(new Pointm(this, x, y, z));
	}

	public boolean setSpawnLocation(Pointm loc) {
		Point oldSpawn = spawnLocation;
		loc.setWorld(this);
		spawnLocation = loc;
		EventFactory.onSpawnChange(this, oldSpawn);
		return !loc.equals(oldSpawn);
	}

	public boolean getPVP() {
		return pvpAllowed;
	}


	public void setPVP(boolean pvp) {
		pvpAllowed = pvp;
	}

    //TODO Vanilla?
	public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals) {
		spawnMonsters = allowMonsters;
		spawnAnimals = allowAnimals;
	}


	// various fixed world properties

	public long getSeed() {
		return seed;
	}


	public UUID getUID() {
		return uid;
	}

	@Override
	public String getName() {
		return name;
	}

	public long getId() {
		return (getSeed() + "_" + getName()).hashCode();
	}

	public int getMaxHeight() {
		return SpoutChunk.HEIGHT;
	}

	public int getSeaLevel() {
		return getMaxHeight() / 2;
	}

	// force-save

	public void save() {
		save(true);
	}

	public void save(boolean async) {
		EventFactory.onWorldSave(this);
		if (async) {
			server.getStorageQueue().queue(new StorageOperation() {
				@Override
				public boolean isParallel() {
					return true;
				}

				@Override
				public String getGroup() {
					return getName();
				}

				@Override
				public String getOperation() {
					return "world-save";
				}

				@Override
				public boolean queueMultiple() {
					return false;
				}

				@Override
				public void run() {
					for (SpoutChunk chunk : chunks.getLoadedChunks()) {
						chunks.forceSave(chunk.getX(), chunk.getZ());
					}
				}
			});
		} else {
			for (SpoutChunk chunk : chunks.getLoadedChunks()) {
				chunks.forceSave(chunk.getX(), chunk.getZ());
			}
		}

		for (Player player : getPlayers()) {
			player.saveData(async);
		}

		writeWorldData(async);
	}

	// map generation

	@Override
	public WorldGenerator getGenerator() {
		return chunks.getGenerator();
	}

	@Override
	public List<Populator> getPopulators() {
		return populators;
	}

	// get block, chunk, id, highest methods with coords

	@Override
	public synchronized SpoutBlock getBlockAt(int x, int y, int z) {
		Location blockLoc = new Location(this, x, y, z);
		if (blockCache.containsKey(blockLoc)) {
			return blockCache.get(blockLoc);
		} else {
			SpoutBlock block = new SpoutBlock(getChunkAt(x >> 4, z >> 4), x, y, z);
			blockCache.put(blockLoc, block);
			return block;
		}
	}

	@Override
	public int getBlockTypeIdAt(int x, int y, int z) {
		return getChunkAt(x >> 4, z >> 4).getType(x & 0xF, y, z & 0xF);
	}

	@Override
	public int getHighestBlockYAt(int x, int z) {
		for (int y = getMaxHeight() - 1; y >= 0; --y) {
			if (getBlockTypeIdAt(x, y, z) != 0) {
				return y + 1;
			}
		}
		return 0;
	}

	@Override
	public synchronized SpoutChunk getChunkAt(int x, int z) {
		return chunks.getChunk(x, z);
	}

	// get block, chunk, id, highest with locations

	@Override
	public SpoutBlock getBlockAt(Point location) {
		return getBlockAt(location.getX(), location.getY(), location.getZ());
	}

	@Override
	public int getBlockTypeIdAt(Point location) {
		return getBlockTypeIdAt(location.getX(), location.getY(), location.getZ());
	}

	@Override
	public int getHighestBlockYAt(Point location) {
		return getHighestBlockYAt((int)location.getX(), (int)location.getZ());
	}

	@Override
	public Block getHighestBlockAt(int x, int z) {
		return getBlockAt(x, getHighestBlockYAt(x, z), z);
	}

	@Override
	public Block getHighestBlockAt(Point location) {
		return getBlockAt((int)location.getX(), getHighestBlockYAt(location), (int)location.getZ());
	}

	@Override
	public Chunk getChunkAt(Point location) {
		return getChunkAt((int)location.getX(), (int)location.getZ());
	}

	@Override
	public Chunk getChunkAt(Block block) {
		return getChunkAt(block.getX(), block.getZ());
	}

	// Chunk loading and unloading

	@Override
	public boolean isChunkLoaded(Chunk chunk) {
		return chunk.isLoaded();
	}

	@Override
	public boolean isChunkLoaded(int x, int z) {
		return getChunkAt(x, z).isLoaded();
	}

	@Override
	public Chunk[] getLoadedChunks() {
		return chunks.getLoadedChunks();
	}

	@Override
	public void loadChunk(Chunk chunk) {
		chunk.load();
	}

	@Override
	public void loadChunk(int x, int z) {
		getChunkAt(x, z).load();
	}

	@Override
	public boolean loadChunk(int x, int z, boolean generate) {
		if (generate) {
			throw new UnsupportedOperationException("Not supported yet.");
		} else {
			loadChunk(x, z);
			return true;
		}
	}

	@Override
	public boolean unloadChunk(Chunk chunk) {
		return unloadChunk(chunk.getX(), chunk.getZ(), true);
	}

	@Override
	public boolean unloadChunk(int x, int z) {
		return unloadChunk(x, z, true);
	}

	@Override
	public boolean unloadChunk(int x, int z, boolean save) {
		return unloadChunk(x, z, save, true);
	}

	@Override
	public boolean unloadChunk(int x, int z, boolean save, boolean safe) {
		if (!safe) {
			throw new UnsupportedOperationException("unloadChunk does not yet support unsafe unloading.");
		}
		if (save) {
			getChunkManager().forceSave(x, z);
		}
		return unloadChunkRequest(x, z, safe);
	}

	@Override
	public boolean unloadChunkRequest(int x, int z) {
		return unloadChunkRequest(x, z, true);
	}

	@Override
	public boolean unloadChunkRequest(int x, int z, boolean safe) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean regenerateChunk(int x, int z) {
		if (!chunks.forceRegeneration(x, z)) {
			return false;
		}
		refreshChunk(x, z);
		return true;
	}

	@Override
	public boolean refreshChunk(int x, int z) {
		if (!isChunkLoaded(x, z)) {
			return false;
		}

		boolean result = false;

		for (Player p : getPlayers()) {
			SpoutPlayer player = (SpoutPlayer) p;
			if (player.canSee(x, z)) {
				player.getSession().send(new LoadChunkMessage(x, z, false));
				player.getSession().send(new LoadChunkMessage(x, z, true));
				player.getSession().send(getChunkAt(x, z).toMessage());
				result = true;
			}
		}

		return result;
	}

	// biomes

	@Override
	public Biome getBiome(int x, int z) {
		if (environment == Environment.THE_END) {
			return Biome.END;
		} else if (environment == Environment.NETHER) {
			return Biome.NETHER;
		}

		return Biome.FOREST;
	}

	@Override
	public double getTemperature(int x, int z) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public double getHumidity(int x, int z) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	// entity spawning

	@Override
	@SuppressWarnings("unchecked")
	public Entity spawn(Point location) throws IllegalArgumentException {
		//TODO Make this spawn spout entities
		
		return null;
		

	}
/*
	@Override
	public Item dropItem(Location location, ItemStack item) {
		//TODO: Make this work with the new item code;
		throw new RuntimeException("Old Entity Code did this");
		//Item itemEntity = new SpoutItem(server, this, item);
		//itemEntity.teleport(location);
		//return itemEntity;
	}

	@Override
	public Item dropItemNaturally(Location location, ItemStack item) {
		double xs = random.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
		double ys = random.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
		double zs = random.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
		location = location.clone().add(new Location(this, xs, ys, zs));
		return dropItem(location, item);
	}

	public void dropItemNaturally(Location location, ItemStack item, int iterations) {
		for (int i = 0; i < iterations; i++) {
			dropItemNaturally(location, item.clone());
		}
	}
	*/
	/*
	@Override
	//TODO This should go in Vanilla
	
	public Arrow spawnArrow(Location location, Vector velocity, float speed, float spread) {
		Arrow arrow = spawn(location, Arrow.class);

		// Transformative magic
		Vector3 randVec = new Vector3((float)random.nextGaussian(), (float)random.nextGaussian(), (float)random.nextGaussian());
		randVec = randVec.scale(0.007499999832361937f * spread);

		velocity.normalize();
		velocity.add(randVec);
		velocity.multiply(speed);

		// yaw = Math.atan2(x, z) * 180.0D / 3.1415927410125732D;
		// pitch = Math.atan2(y, Math.sqrt(x * x + z * z)) * 180.0D / 3.1415927410125732D

		arrow.setVelocity(velocity);
		return arrow;
	}
*/
	@Override
	public LivingEntity spawnCreature(Location loc, CreatureType type) {
		EntityProperties properties = EntityProperties.getByCreatureType(type);
		if (properties == null) {
			throw new IllegalArgumentException("This CreatureType is unknown to Spout!");
		}
		LivingEntity entity = (LivingEntity) properties.getFactory().createEntity(server, this);
		entity.teleport(loc);
		return entity;
	}

	@Override
	public SpoutLightningStrike strikeLightning(Location loc) {
		return strikeLightning(loc, false);
	}

	@Override
	public SpoutLightningStrike strikeLightningEffect(Location loc) {
		return strikeLightning(loc, true);
	}

	public SpoutLightningStrike strikeLightning(Location loc, boolean isEffect) {
		SpoutLightningStrike strike = new SpoutLightningStrike(server, this, isEffect);
		if (!EventFactory.onLightningStrike(strike, this).isCancelled()) {
			strike.teleport(loc);
		} else {
			strike.remove();
		}
		return strike;
	}

	// Time related methods

	@Override
	public long getTime() {
		return time;
	}

	@Override
	public void setTime(long time) {
		if (time < 0) {
			time = time % 24000 + 24000;
		}
		if (time > 24000) {
			time %= 24000;
		}
		this.time = time;
	}

	@Override
	public long getFullTime() {
		return getTime();
	}

	@Override
	public void setFullTime(long time) {
		setTime(time);
	}



	// effects

	@Override
	public void playEffect(Point location, Effect effect, int data) {
		playEffect(location, effect, data, 64);
	}

	@Override
	public void playEffect(Point location, Effect effect, int data, int radius) {
		for (Player player : getPlayers()) {
			if (player.getLocation().distance(location) <= radius) {
				player.playEffect(location, effect, data);
			}
		}
	}

	public void playEffectExceptTo(Point location, Effect effect, int data, int radius, Player exclude) {
		for (Player player : getPlayers()) {
			if (!player.equals(exclude) && player.getLocation().distance(location) <= radius) {
				player.playEffect(location, effect, data);
			}
		}
	}

	// misc

	@Override
	public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain) {
		return new SpoutChunkSnapshot.EmptySnapshot(x, z, this, includeBiome, includeBiomeTempRain);
	}

	@Override
	public boolean getKeepSpawnInMemory() {
		return keepSpawnLoaded;
	}

	@Override
	public void setKeepSpawnInMemory(boolean keepLoaded) {
		keepSpawnLoaded = keepLoaded;
	}

	@Override
	public boolean isAutoSave() {
		return autosave;
	}

	@Override
	public void setAutoSave(boolean value) {
		autosave = value;
	}

	// level data write

	void writeWorldData(boolean async) {
		if (async) {
			server.getStorageQueue().queue(new StorageOperation() {
				@Override
				public boolean isParallel() {
					return true;
				}

				@Override
				public String getGroup() {
					return getName();
				}

				@Override
				public boolean queueMultiple() {
					return false;
				}

				@Override
				public String getOperation() {
					return "world-metadata-save";
				}

				@Override
				public void run() {
					try {
						storageProvider.getMetadataService().writeWorldData();
					} catch (IOException e) {
						server.getLogger().severe("Could not save world metadata file for world" + getName());
						e.printStackTrace();
					}
				}
			});
		} else {
			try {
				storageProvider.getMetadataService().writeWorldData();
			} catch (IOException e) {
				server.getLogger().severe("Could not save world metadata file for world" + getName());
				e.printStackTrace();
			}
		}
	}

	public WorldMetadataService getMetadataService() {
		return storageProvider.getMetadataService();
	}

	/**
	 * Unloads the world
	 *
	 * @return true if successful
	 */
	public boolean unload() {
		try {
			storageProvider.getChunkIoService().unload();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	/**
	 * Get the world folder.
	 *
	 * @return world folder
	 */
	public File getWorldFolder() {
		return storageProvider.getFolder();
	}

	public SpoutServer getServer() {
		return server;
	}

	@Override
	public long getAge() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setTime(int time) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDayLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setDayLength(int time) {
		// TODO Auto-generated method stub
		return 0;
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
	public Chunk getRegion(int x, int y, int z) {
		// TODO Auto-generated method stub
		return null;
	}
}
