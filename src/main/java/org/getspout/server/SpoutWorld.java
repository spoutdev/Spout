package org.getspout.server;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import org.getspout.api.metadata.BlockMetadataStore;
import org.getspout.api.metadata.MetadataValue;
import org.getspout.api.plugin.Plugin;
import org.getspout.server.block.SpoutBlock;
import org.getspout.server.entity.*;
import org.getspout.server.entity.objects.SpoutItem;
import org.getspout.server.io.StorageOperation;
import org.getspout.server.io.WorldMetadataService;
import org.getspout.server.io.WorldStorageProvider;
import org.getspout.server.io.WorldMetadataService.WorldFinalValues;
import org.getspout.server.msg.LoadChunkMessage;
import org.getspout.server.msg.StateChangeMessage;
import org.getspout.server.msg.TimeMessage;

/**
 * A class which represents the in-game world.
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
	private final Map<Location, SpoutBlock> blockCache = new ConcurrentHashMap<Location, SpoutBlock>();
	/**
	 * The world populators for this world.
	 */
	private final List<BlockPopulator> populators;
	/**
	 * The environment.
	 */
	private final Environment environment;
	/**
	 * The world seed.
	 */
	private final long seed;
	/**
	 * The spawn position.
	 */
	private Location spawnLocation;
	/**
	 * Whether to keep the spawn chunks in memory (prevent them from being unloaded)
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
	 * Block metadata store.
	 * TODO: this instead of null
	 */
	private final BlockMetadataStore blockMetadata = new BlockMetadataStore(null);

	/**
	 * Creates a new world with the specified chunk I/O service, environment,
	 * and world generator.
	 * @param name The name of the world.
	 * @param provider The world storage provider
	 * @param environment The environment.
	 * @param generator The world generator.
	 */
	public SpoutWorld(SpoutServer server, String name, Environment environment, long seed, WorldStorageProvider provider, ChunkGenerator generator) {
		this.server = server;
		this.name = name;
		this.environment = environment;
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
			this.uid = values.getUuid();
		} else {
			this.seed = seed;
			this.uid = UUID.randomUUID();
		}
		populators = generator.getDefaultPopulators(this);
		if (spawnLocation == null) {
			spawnLocation = generator.getFixedSpawnLocation(this, random);
		}

		int centerX = (spawnLocation == null) ? 0 : spawnLocation.getBlockX() >> 4;
		int centerZ = (spawnLocation == null) ? 0 : spawnLocation.getBlockZ() >> 4;

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
					SpoutServer.logger.log(Level.INFO, "Preparing spawn for {0}: {1}%", new Object[]{name, progress});
					loadTime = System.currentTimeMillis();
				}
			}
		}
		server.getLogger().log(Level.INFO, "Preparing spawn for {0}: done", name);
		if (spawnLocation == null) {
			spawnLocation = generator.getFixedSpawnLocation(this, random);
			if (spawnLocation == null) {
				spawnLocation = new Location(this, 0, getHighestBlockYAt(0, 0), 0);

				if (!generator.canSpawn(this, spawnLocation.getBlockX(), spawnLocation.getBlockZ())) {
					// 10 tries only to prevent a return false; bomb
					for (int tries = 0; tries < 10 && !generator.canSpawn(this, spawnLocation.getBlockX(), spawnLocation.getBlockZ()); ++tries) {
						spawnLocation.setX(spawnLocation.getX() + random.nextDouble() * 128 - 64);
						spawnLocation.setZ(spawnLocation.getZ() + random.nextDouble() * 128 - 64);
					}
				}

				spawnLocation.setY(1 + getHighestBlockYAt(spawnLocation.getBlockX(), spawnLocation.getBlockZ()));
			}
		}
		EventFactory.onWorldLoad(this);
		save();

	}

	////////////////////////////////////////
	// Various internal mechanisms
	/**
	 * Get the world chunk manager.
	 * @return The ChunkManager for the world.
	 */
	protected ChunkManager getChunkManager() {
		return chunks;
	}

	/**
	 * Updates all the entities within this world.
	 */
	public void pulse() {
		ArrayList<SpoutEntity> temp = new ArrayList<SpoutEntity>(entities.getAll());

		for (SpoutEntity entity : temp) {
			entity.pulse();
		}

		for (SpoutEntity entity : temp) {
			entity.reset();
		}

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

		if (autosave && --saveTimer <= 0) {
			saveTimer = 60 * 20;
			save();
		}
	}

	/**
	 * Gets the entity manager.
	 * @return The entity manager.
	 */
	public EntityManager getEntityManager() {
		return entities;
	}

	public Collection<SpoutPlayer> getRawPlayers() {
		return entities.getAll(SpoutPlayer.class);
	}

	// SpoutEntity lists
	public List<Player> getPlayers() {
		Collection<SpoutPlayer> players = entities.getAll(SpoutPlayer.class);
		ArrayList<Player> result = new ArrayList<Player>();
		for (Player p : players) {
			result.add(p);
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

	public List<LivingEntity> getLivingEntities() {
		Collection<SpoutEntity> list = entities.getAll();
		ArrayList<LivingEntity> result = new ArrayList<LivingEntity>();
		for (Entity e : list) {
			if (e instanceof SpoutLivingEntity) {
				result.add((SpoutLivingEntity) e);
			}
		}
		return result;
	}

	// Various malleable world properties
	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public boolean setSpawnLocation(int x, int y, int z) {
		return setSpawnLocation(new Location(this, x, y, z));
	}

	public boolean setSpawnLocation(Location loc) {
		Location oldSpawn = spawnLocation;
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

	public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals) {
		spawnMonsters = allowMonsters;
		spawnAnimals = allowAnimals;
	}

	public boolean getAllowAnimals() {
		return spawnAnimals;
	}

	public boolean getAllowMonsters() {
		return spawnMonsters;
	}

	// various fixed world properties
	public Environment getEnvironment() {
		return environment;
	}

	public long getSeed() {
		return seed;
	}

	public UUID getUID() {
		return uid;
	}

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
		save(false);
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

		for (SpoutPlayer player : getRawPlayers()) {
			player.saveData(async);
		}

		writeWorldData(async);
	}

	// map generation
	public ChunkGenerator getGenerator() {
		return chunks.getGenerator();
	}

	public List<BlockPopulator> getPopulators() {
		return populators;
	}

	public boolean generateTree(Location location, TreeType type) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	// get block, chunk, id, highest methods with coords
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

	public int getBlockTypeIdAt(int x, int y, int z) {
		return getChunkAt(x >> 4, z >> 4).getType(x & 0xF, y, z & 0xF);
	}

	public int getHighestBlockYAt(int x, int z) {
		for (int y = getMaxHeight() - 1; y >= 0; --y) {
			if (getBlockTypeIdAt(x, y, z) != 0) {
				return y + 1;
			}
		}
		return 0;
	}

	public synchronized SpoutChunk getChunkAt(int x, int z) {
		return chunks.getChunk(x, z);
	}

	// get block, chunk, id, highest with locations
	public SpoutBlock getBlockAt(Location location) {
		return getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public int getBlockTypeIdAt(Location location) {
		return getBlockTypeIdAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public int getHighestBlockYAt(Location location) {
		return getHighestBlockYAt(location.getBlockX(), location.getBlockZ());
	}

	public Block getHighestBlockAt(int x, int z) {
		return getBlockAt(x, getHighestBlockYAt(x, z), z);
	}

	public Block getHighestBlockAt(Location location) {
		return getBlockAt(location.getBlockX(), getHighestBlockYAt(location), location.getBlockZ());
	}

	public Chunk getChunkAt(Location location) {
		return getChunkAt(location.getBlockX(), location.getBlockZ());
	}

	public Chunk getChunkAt(Block block) {
		return getChunkAt(block.getX(), block.getZ());
	}

	// Chunk loading and unloading
	public boolean isChunkLoaded(Chunk chunk) {
		return chunk.isLoaded();
	}

	public boolean isChunkLoaded(int x, int z) {
		return getChunkAt(x, z).isLoaded();
	}

	public Chunk[] getLoadedChunks() {
		return chunks.getLoadedChunks();
	}

	public void loadChunk(Chunk chunk) {
		chunk.load();
	}

	public void loadChunk(int x, int z) {
		getChunkAt(x, z).load();
	}

	public boolean loadChunk(int x, int z, boolean generate) {
		if (generate) {
			throw new UnsupportedOperationException("Not supported yet.");
		} else {
			loadChunk(x, z);
			return true;
		}
	}

	public boolean unloadChunk(Chunk chunk) {
		return unloadChunk(chunk.getX(), chunk.getZ(), true);
	}

	public boolean unloadChunk(int x, int z) {
		return unloadChunk(x, z, true);
	}

	public boolean unloadChunk(int x, int z, boolean save) {
		return unloadChunk(x, z, save, true);
	}

	public boolean unloadChunk(int x, int z, boolean save, boolean safe) {
		if (!safe) {
			throw new UnsupportedOperationException("unloadChunk does not yet support unsafe unloading.");
		}
		if (save) {
			getChunkManager().forceSave(x, z);
		}
		return unloadChunkRequest(x, z, safe);
	}

	public boolean unloadChunkRequest(int x, int z) {
		return unloadChunkRequest(x, z, true);
	}

	public boolean unloadChunkRequest(int x, int z, boolean safe) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean regenerateChunk(int x, int z) {
		if (!chunks.forceRegeneration(x, z)) {
			return false;
		}
		refreshChunk(x, z);
		return true;
	}

	public boolean refreshChunk(int x, int z) {
		if (!isChunkLoaded(x, z)) {
			return false;
		}

		SpoutChunk.Key key = new SpoutChunk.Key(x, z);
		boolean result = false;

		for (Player p : getPlayers()) {
			SpoutPlayer player = (SpoutPlayer) p;
			if (player.canSee(key)) {
				player.getSession().send(new LoadChunkMessage(x, z, false));
				player.getSession().send(new LoadChunkMessage(x, z, true));
				player.getSession().send(getChunkAt(x, z).toMessage());
				result = true;
			}
		}

		return result;
	}

	// biomes
	public Biome getBiome(int x, int z) {
		if (environment == Environment.THE_END) {
			return Biome.SKY;
		} else if (environment == Environment.NETHER) {
			return Biome.HELL;
		}

		return Biome.FOREST;
	}

	public double getTemperature(int x, int z) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public double getHumidity(int x, int z) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	// entity spawning
	@SuppressWarnings("unchecked")
	public <T extends Entity> T spawn(Location location, Class<T> clazz) throws IllegalArgumentException {
		if (clazz.isInstance(SpoutEntity.class)) {
			return (T) spawnSpoutEntity(location, (Class<? extends SpoutEntity>) clazz);
		} else {
			return spawnBukkitEntity(location, clazz);
		}
	}

	public <T extends Entity> T spawnBukkitEntity(Location location, Class<T> clazz) throws IllegalArgumentException {
		EntityProperties properties = EntityProperties.getByBukkitClass(clazz);
		if (properties == null) {
			throw new IllegalArgumentException("This entity type is unknown to Spout!");
		}

		T entity = (T) properties.getFactory().createEntity(server, this);
		entity.teleport(location);
		return entity;
	}

	public <T extends SpoutEntity> T spawnSpoutEntity(Location location, Class<T> clazz) throws IllegalArgumentException {
		EntityProperties properties = EntityProperties.getBySpoutClass(clazz);
		if (properties == null) {
			throw new IllegalArgumentException("This entity type is unknown to Spout!");
		}

		T entity = (T) properties.getFactory().createEntity(server, this);
		entity.teleport(location);
		return entity;
	}

	public Item dropItem(Location location, ItemStack item) {
		Item itemEntity = new SpoutItem(server, this, item);
		itemEntity.teleport(location);
		return itemEntity;
	}

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

	public Arrow spawnArrow(Location location, Vector velocity, float speed, float spread) {
		Arrow arrow = spawn(location, Arrow.class);

		// Transformative magic
		Vector randVec = new Vector(random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
		randVec.multiply(0.007499999832361937D * (double) spread);

		velocity.normalize();
		velocity.add(randVec);
		velocity.multiply(speed);

		// yaw = Math.atan2(x, z) * 180.0D / 3.1415927410125732D;
		// pitch = Math.atan2(y, Math.sqrt(x * x + z * z)) * 180.0D / 3.1415927410125732D

		arrow.setVelocity(velocity);
		return arrow;
	}

	public LivingEntity spawnCreature(Location loc, CreatureType type) {
		EntityProperties properties = EntityProperties.getByCreatureType(type);
		if (properties == null) {
			throw new IllegalArgumentException("This CreatureType is unknown to Spout!");
		}
		LivingEntity entity = (LivingEntity) properties.getFactory().createEntity(server, this);
		entity.teleport(loc);
		return entity;
	}

	public SpoutLightningStrike strikeLightning(Location loc) {
		return strikeLightning(loc, false);
	}

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
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		if (time < 0) {
			time = (time % 24000) + 24000;
		}
		if (time > 24000) {
			time %= 24000;
		}
		this.time = time;
	}

	public long getFullTime() {
		return getTime();
	}

	public void setFullTime(long time) {
		setTime(time);
	}

	// Weather related methods
	public boolean hasStorm() {
		return currentlyRaining;
	}

	public void setStorm(boolean hasStorm) {
		if (!EventFactory.onWeatherChange(this, hasStorm).isCancelled()) {
			currentlyRaining = hasStorm;
		}

		// Numbers borrowed from CraftBukkit.
		if (currentlyRaining) {
			setWeatherDuration(random.nextInt(12000) + 12000);
		} else {
			setWeatherDuration(random.nextInt(168000) + 12000);
		}

		for (SpoutPlayer player : getRawPlayers()) {
			player.getSession().send(new StateChangeMessage((byte) (currentlyRaining ? 1 : 2), (byte) 0));
		}
	}

	public int getWeatherDuration() {
		return rainingTicks;
	}

	public void setWeatherDuration(int duration) {
		rainingTicks = duration;
	}

	public boolean isThundering() {
		return currentlyThundering;
	}

	public void setThundering(boolean thundering) {
		if (!EventFactory.onThunderChange(this, thundering).isCancelled()) {
			currentlyThundering = thundering;
		}

		// Numbers borrowed from CraftBukkit.
		if (currentlyThundering) {
			setThunderDuration(random.nextInt(12000) + 3600);
		} else {
			setThunderDuration(random.nextInt(168000) + 12000);
		}
	}

	public int getThunderDuration() {
		return thunderingTicks;
	}

	public void setThunderDuration(int duration) {
		thunderingTicks = duration;
	}

	// explosions
	public boolean createExplosion(Location loc, float power, boolean setFire) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public boolean createExplosion(Location loc, float power) {
		return createExplosion(loc, power, false);
	}

	public boolean createExplosion(double x, double y, double z, float power, boolean setFire) {
		return createExplosion(new Location(this, x, y, z), power, setFire);
	}

	public boolean createExplosion(double x, double y, double z, float power) {
		return createExplosion(new Location(this, x, y, z), power, false);
	}

	// effects
	public void playEffect(Location location, Effect effect, int data) {
		playEffect(location, effect, data, 64);
	}

	public void playEffect(Location location, Effect effect, int data, int radius) {
		for (Player player : getPlayers()) {
			if (player.getLocation().distance(location) <= radius) {
				player.playEffect(location, effect, data);
			}
		}
	}

	public void playEffectExceptTo(Location location, Effect effect, int data, int radius, Player exclude) {
		for (Player player : getPlayers()) {
			if (!player.equals(exclude) && player.getLocation().distance(location) <= radius) {
				player.playEffect(location, effect, data);
			}
		}
	}

	// misc
	public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain) {
		return new SpoutChunkSnapshot.EmptySnapshot(x, z, this, includeBiome, includeBiomeTempRain);
	}

	public boolean getKeepSpawnInMemory() {
		return keepSpawnLoaded;
	}

	public void setKeepSpawnInMemory(boolean keepLoaded) {
		keepSpawnLoaded = keepLoaded;
	}

	public boolean isAutoSave() {
		return autosave;
	}

	public void setAutoSave(boolean value) {
		autosave = value;
	}

	public void setDifficulty(Difficulty difficulty) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public Difficulty getDifficulty() {
		return Difficulty.PEACEFUL;
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

	/** Get the world folder.
	 * @return world folder
	 */
	public File getWorldFolder() {
		return storageProvider.getFolder();
	}

	public SpoutServer getServer() {
		return server;
	}
	//Metadata
	//TODO: this instead of null when this implements Spout World

	public BlockMetadataStore getBlockMetadata() {
		return blockMetadata;
	}

	public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
		server.getWorldMetadata().setMetadata(null, metadataKey, newMetadataValue);
	}

	public List<MetadataValue> getMetadata(String metadataKey) {
		return server.getWorldMetadata().getMetadata(null, metadataKey);
	}

	public boolean hasMetadata(String metadataKey) {
		return server.getWorldMetadata().hasMetadata(null, metadataKey);
	}

	public void removeMetadata(String metadataKey, Plugin owningPlugin) {
		server.getWorldMetadata().removeMetadata(null, metadataKey, owningPlugin);
	}
}
