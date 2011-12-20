package org.getspout.server;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import org.getspout.server.block.BlockProperties;
import org.getspout.server.block.SpoutBlock;
import org.getspout.server.block.SpoutBlockState;
import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.msg.CompressedChunkMessage;
import org.getspout.server.msg.Message;

/**
 * Represents a chunk of the map.
 *
 * @author Graham Edgecombe
 */
public final class SpoutChunk implements Chunk {
	/**
	 * The radius (not including the current chunk) of the chunks that the
	 * player can see. Used as a default when no custom value is specified.
	 */
	public static final int VISIBLE_RADIUS = 8;

	/**
	 * A chunk key represents the X and Z coordinates of a chunk and implements
	 * the {@link #hashCode()} and {@link #equals(Object)} methods making it
	 * suitable for use as a key in a hash table or set.
	 *
	 * @author Graham Edgecombe
	 */
	public static final class Key {

		/**
		 * The coordinates.
		 */
		private final int x, z;

		/**
		 * Creates a new chunk key with the specified X and Z coordinates.
		 *
		 * @param x The X coordinate.
		 * @param z The Z coordinate.
		 */
		public Key(int x, int z) {
			this.x = x;
			this.z = z;
		}

		/**
		 * Gets the X coordinate.
		 *
		 * @return The X coordinate.
		 */
		public int getX() {
			return x;
		}

		/**
		 * Gets the Z coordinate.
		 *
		 * @return The Z coordinate.
		 */
		public int getZ() {
			return z;
		}

		@Override
		public int hashCode() {
			//final int prime = 31;
			int result = 1;
			//result = prime * result + x;
			//result = prime * result + z;
			result = (result << 5) - result + x;
			result = (result << 5) - result + z;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Key other = (Key) obj;
			if (x != other.x) {
				return false;
			}
			if (z != other.z) {
				return false;
			}
			return true;
		}

	}

	/**
	 * The dimensions of a chunk.
	 */
	public static final int WIDTH = 16, HEIGHT = 128, DEPTH = 16;

	/**
	 * The world of this chunk.
	 */
	private final SpoutWorld world;

	/**
	 * The coordinates of this chunk.
	 */
	private final int x, z;

	/**
	 * The chunk key
	 */
	private final Key key;

	/**
	 * The data in this chunk representing all of the blocks and their state.
	 */
	private byte[] types, metaData, skyLight, blockLight;

	/**
	 * The tile entities that reside in this chunk.
	 */
	private final HashMap<Integer, SpoutBlockState> tileEntities = new HashMap<Integer, SpoutBlockState>();

	/**
	 * Whether the chunk has been populated by special features. Used in map
	 * generation.
	 */
	private boolean populated = false;

	/**
	 * Creates a new chunk with a specified X and Z coordinate.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 */
	public SpoutChunk(SpoutWorld world, int x, int z) {
		this.world = world;
		this.x = x;
		this.z = z;
		key = new Key(x, z);
	}

	// ======== Basic stuff ========

	/**
	 * Gets the world containing this chunk
	 *
	 * @return Parent World
	 */
	@Override
	public SpoutWorld getWorld() {
		return world;
	}

	/**
	 * Gets the X coordinate of this chunk.
	 *
	 * @return The X coordinate of this chunk.
	 */
	@Override
	public int getX() {
		return x;
	}

	/**
	 * Gets the Z coordinate of this chunk.
	 *
	 * @return The Z coordinate of this chunk.
	 */
	@Override
	public int getZ() {
		return z;
	}

	/**
	 * Gets a block from this chunk
	 *
	 * @param x 0-15
	 * @param y 0-127
	 * @param z 0-15
	 * @return the Block
	 */
	@Override
	public SpoutBlock getBlock(int x, int y, int z) {
		return getWorld().getBlockAt(this.x << 4 | x, y, this.z << 4 | z);
	}

	private final static Entity[] blankEntityArray = new Entity[0];

	@Override
	public Entity[] getEntities() {
		List<Entity> entities = world.getEntities();
		List<Entity> chunkEntities = new ArrayList<Entity>();

		for (Entity e : entities) {
			int cx = e.getLocation().getBlockX() >> 4;
			int cz = e.getLocation().getBlockZ() >> 4;

			if (cx == x && cz == z) {
				chunkEntities.add(e);
			}
		}

		return chunkEntities.toArray(blankEntityArray);

	}

	@Override
	public SpoutBlockState[] getTileEntities() {
		return tileEntities.values().toArray(new SpoutBlockState[tileEntities.size()]);
	}

	/**
	 * Capture thread-safe read-only snapshot of chunk data
	 *
	 * @return ChunkSnapshot
	 */
	@Override
	public ChunkSnapshot getChunkSnapshot() {
		return getChunkSnapshot(true, false, false);
	}

	/**
	 * Capture thread-safe read-only snapshot of chunk data
	 *
	 * @param includeMaxblocky - if true, snapshot includes per-coordinate
	 *            maximum Y values
	 * @param includeBiome - if true, snapshot includes per-coordinate biome
	 *            type
	 * @param includeBiomeTempRain - if true, snapshot includes per-coordinate
	 *            raw biome temperature and rainfall
	 * @return ChunkSnapshot
	 */
	@Override
	public ChunkSnapshot getChunkSnapshot(boolean includeMaxblocky, boolean includeBiome, boolean includeBiomeTempRain) {
		return new SpoutChunkSnapshot(x, z, world, types, metaData, skyLight, blockLight, includeMaxblocky, includeBiome, includeBiomeTempRain);
	}

	/**
	 * Gets whether this chunk has been populated by special features.
	 *
	 * @return Population status.
	 */
	public boolean getPopulated() {
		return populated;
	}

	/**
	 * Sets the population status of this chunk.
	 *
	 * @param populated Population status.
	 */
	public void setPopulated(boolean populated) {
		this.populated = populated;
	}

	// ======== Helper Functions ========

	@Override
	public boolean isLoaded() {
		return types != null;
	}

	@Override
	public boolean load() {
		return load(true);
	}

	@Override
	public boolean load(boolean generate) {
		if (isLoaded()) {
			return true;
		}
		return world.getChunkManager().loadChunk(x, z, generate);
	}

	@Override
	public boolean unload() {
		return unload(true, true);
	}

	@Override
	public boolean unload(boolean save) {
		return unload(save, true);
	}

	@Override
	public boolean unload(boolean save, boolean safe) {
		if (safe) {
			for (Player player : getWorld().getPlayers()) {
				if (((SpoutPlayer) player).canSee(key)) {
					return false;
				}
			}
		}

		if (save) {
			world.getChunkManager().forceSave(x, z);
		}

		// any other pre-unload actions

		types = metaData = skyLight = blockLight = null;
		return true;
	}

	/**
	 * Sets the types of all tiles within the chunk.
	 *
	 * @param types The array of types.
	 */
	public void initializeTypes(byte[] types) {
		if (isLoaded()) {
			SpoutServer.logger.log(Level.SEVERE, "Tried to initialize already loaded chunk ({0},{1})", new Object[] {x, z});
			return;
		}

		this.types = new byte[WIDTH * DEPTH * world.getMaxHeight()];
		metaData = new byte[WIDTH * DEPTH * world.getMaxHeight()];
		skyLight = new byte[WIDTH * DEPTH * world.getMaxHeight()];
		blockLight = new byte[WIDTH * DEPTH * world.getMaxHeight()];
		for (int i = 0; i < WIDTH * DEPTH * world.getMaxHeight(); ++i) {
			skyLight[i] = 15;
		}

		//System.out.println("Init'd types, isLoaded = " + isLoaded());

		if (types.length != WIDTH * DEPTH * world.getMaxHeight()) {
			throw new IllegalArgumentException();
		}
		System.arraycopy(types, 0, this.types, 0, types.length);

		for (int cx = 0; cx < WIDTH; ++cx) {
			for (int cy = 0; cy < world.getMaxHeight(); ++cy) {
				for (int cz = 0; cz < DEPTH; ++cz) {
					BlockProperties properties = BlockProperties.get(getType(cx, cy, cz));
					Class<? extends SpoutBlockState> clazz = properties == null ? null : properties.getEntityClass();
					if (clazz != null && clazz != SpoutBlockState.class) {
						try {
							Constructor<? extends SpoutBlockState> constructor = clazz.getConstructor(SpoutBlock.class);
							SpoutBlockState state = constructor.newInstance(getBlock(cx, cy, cz));
							tileEntities.put(coordToIndex(cx, cy, cz), state);
						} catch (Exception ex) {
							SpoutServer.logger.log(Level.SEVERE, "Unable to initialize tile entity {0}: {1}", new Object[] {clazz.getName(), ex.getMessage()});
							ex.printStackTrace();
						}
					}
				}
			}
		}
	}

	// ======== Data access ========

	/**
	 * Attempt to get the tile entity located at the given coordinates.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @param y The Y coordinate.
	 * @return A SpoutBlockState if the entity exists, or null otherwise.
	 */
	public SpoutBlockState getEntity(int x, int y, int z) {
		if (y >= world.getMaxHeight() - 1 || y < 0) {
			return null;
		}
		load();
		return tileEntities.get(coordToIndex(x, y, z));
	}

	/**
	 * Gets the type of a block within this chunk.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @param y The Y coordinate.
	 * @return The type.
	 */
	public int getType(int x, int y, int z) {
		if (y >= world.getMaxHeight() - 1 || y < 0) {
			return 0;
		}
		load();
		return types[coordToIndex(x, y, z)];
	}

	/**
	 * Sets the type of a block within this chunk.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @param y The Y coordinate.
	 * @param type The type.
	 */
	public void setType(int x, int y, int z, int type) {
		load();
		if (type < 0 || type >= 256) {
			throw new IllegalArgumentException();
		}

		if (tileEntities.containsKey(coordToIndex(x, y, z))) {
			getEntity(x, y, z).destroy();
			tileEntities.remove(coordToIndex(x, y, z));
		}

		types[coordToIndex(x, y, z)] = (byte) type;
		BlockProperties property = BlockProperties.get(type);
		if (property != null) {
			Class<? extends SpoutBlockState> clazz = property.getEntityClass();
			if (clazz != null && clazz != SpoutBlockState.class) {
				try {
					Constructor<? extends SpoutBlockState> constructor = clazz.getConstructor(SpoutBlock.class);
					SpoutBlockState state = constructor.newInstance(getBlock(x, y, z));
					tileEntities.put(coordToIndex(x, y, z), state);
				} catch (Exception ex) {
					SpoutServer.logger.log(Level.SEVERE, "Unable to initialize tile entity {0}: {1}", new Object[] {clazz.getName(), ex.getMessage()});
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Gets the metadata of a block within this chunk.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @param y The Y coordinate.
	 * @return The metadata.
	 */
	public int getMetaData(int x, int y, int z) {
		if (y >= world.getMaxHeight() - 1 || y < 0) {
			return 0;
		}
		load();
		return metaData[coordToIndex(x, y, z)];
	}

	/**
	 * Sets the metadata of a block within this chunk.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @param y The Y coordinate.
	 * @param metaData The metadata.
	 */
	public void setMetaData(int x, int y, int z, int metaData) {
		load();
		if (metaData < 0 || metaData >= 16) {
			throw new IllegalArgumentException();
		}

		this.metaData[coordToIndex(x, y, z)] = (byte) metaData;
	}

	/**
	 * Gets the sky light level of a block within this chunk.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @param y The Y coordinate.
	 * @return The sky light level.
	 */
	public int getSkyLight(int x, int y, int z) {
		if (y >= world.getMaxHeight() - 1 || y < 0) {
			return 0;
		}
		load();
		return skyLight[coordToIndex(x, y, z)];
	}

	/**
	 * Sets the sky light level of a block within this chunk.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @param y The Y coordinate.
	 * @param skyLight The sky light level.
	 */
	public void setSkyLight(int x, int y, int z, int skyLight) {
		load();
		if (skyLight < 0 || skyLight >= 16) {
			throw new IllegalArgumentException();
		}

		this.skyLight[coordToIndex(x, y, z)] = (byte) skyLight;
	}

	/**
	 * Gets the block light level of a block within this chunk.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @param y The Y coordinate.
	 * @return The block light level.
	 */
	public int getBlockLight(int x, int y, int z) {
		if (y >= world.getMaxHeight() - 1 || y < 0) {
			return 0;
		}
		load();
		return blockLight[coordToIndex(x, y, z)];
	}

	/**
	 * Sets the block light level of a block within this chunk.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @param y The Y coordinate.
	 * @param blockLight The block light level.
	 */
	public void setBlockLight(int x, int y, int z, int blockLight) {
		load();
		if (blockLight < 0 || blockLight >= 16) {
			throw new IllegalArgumentException();
		}

		this.blockLight[coordToIndex(x, y, z)] = (byte) blockLight;
	}

	public byte[] getTypes() {
		load();
		return types.clone();
	}

	// ======== Helper functions ========

	/**
	 * Creates a new {@link Message} which can be sent to a client to stream
	 * this chunk to them.
	 *
	 * @return The {@link CompressedChunkMessage}.
	 */
	public Message toMessage() {
		return new CompressedChunkMessage(x * SpoutChunk.WIDTH, 0, z * SpoutChunk.DEPTH, WIDTH, DEPTH, world.getMaxHeight(), serializeTileData());
	}

	/**
	 * Converts a three-dimensional coordinate to an index within the
	 * one-dimensional arrays.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @param y The Y coordinate.
	 * @return The index within the arrays.
	 */
	private int coordToIndex(int x, int y, int z) {
		if (x < 0 || z < 0 || y < 0 || x >= WIDTH || z >= DEPTH || y >= world.getMaxHeight()) {
			throw new IndexOutOfBoundsException();
		}

		return (x * DEPTH + z) * world.getMaxHeight() + y;
	}

	/**
	 * Serializes tile data into a byte array.
	 *
	 * @return The byte array populated with the tile data.
	 */
	private byte[] serializeTileData() {
		byte[] dest = new byte[(WIDTH * DEPTH * world.getMaxHeight() * 5 / 2)];

		load();
		System.arraycopy(types, 0, dest, 0, types.length);

		int pos = types.length;

		for (int i = 0; i < metaData.length; i += 2) {
			byte meta1 = metaData[i];
			byte meta2 = metaData[i + 1];
			dest[pos++] = (byte) (meta2 << 4 | meta1);
		}

		for (int i = 0; i < skyLight.length; i += 2) {
			byte light1 = skyLight[i];
			byte light2 = skyLight[i + 1];
			dest[pos++] = (byte) (light2 << 4 | light1);
		}

		for (int i = 0; i < blockLight.length; i += 2) {
			byte light1 = blockLight[i];
			byte light2 = blockLight[i + 1];
			dest[pos++] = (byte) (light2 << 4 | light1);
		}

		return dest;
	}
}
