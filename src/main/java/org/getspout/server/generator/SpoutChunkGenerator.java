package org.getspout.server.generator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.OctaveGenerator;

import org.getspout.server.SpoutChunk;
import org.getspout.server.block.BlockID;
import org.getspout.server.block.BlockProperties;

/**
 * Base chunk generator class.
 */
public abstract class SpoutChunkGenerator extends ChunkGenerator {
	private static final Set<Integer> noSpawnFloors = new HashSet<Integer>(Arrays.asList(BlockID.FIRE, BlockID.CACTUS, BlockID.LEAVES));
	private final Map<String, Map<String, OctaveGenerator>> octaveCache = new HashMap<String, Map<String, OctaveGenerator>>();
	private final List<BlockPopulator> populators;

	protected SpoutChunkGenerator(BlockPopulator... args) {
		populators = Arrays.asList(args);
	}

	/**
	 * @param world The world to create OctaveGenerators for
	 * @param octaves The map to put the OctaveGenerators into
	 */
	protected void createWorldOctaves(World world, Map<String, OctaveGenerator> octaves) {
	}

	/**
	 * @param world The world to look for in the cache
	 * @return A map of {@link OctaveGenerator}s created by
	 *         {@link #createWorldOctaves(World, Map)}
	 */
	protected final Map<String, OctaveGenerator> getWorldOctaves(World world) {
		if (octaveCache.get(world.getName()) == null) {
			Map<String, OctaveGenerator> octaves = new HashMap<String, OctaveGenerator>();
			createWorldOctaves(world, octaves);
			octaveCache.put(world.getName(), octaves);
			return octaves;
		}
		return octaveCache.get(world.getName());
	}

	/**
	 * Create a new byte[] buffer of the proper size.
	 *
	 * @param fill The Material to fill with.
	 * @return A new filled byte[16 * 16 * 128];
	 */
	protected byte[] start(World world, int fill) {
		if (BlockProperties.get(fill) == null) {
			throw new IllegalArgumentException("Invalid block type!");
		}
		byte[] data = new byte[SpoutChunk.DEPTH * SpoutChunk.WIDTH * world.getMaxHeight()];
		Arrays.fill(data, (byte) fill);
		return data;
	}

	/**
	 * Set the given block to the given type.
	 *
	 * @param data The buffer to write to.
	 * @param x The chunk X coordinate.
	 * @param y The Y coordinate.
	 * @param z The chunk Z coordinate.
	 * @param id The block type.
	 */
	protected void set(byte[] data, World world, int x, int y, int z, int id) {
		if (data == null) {
			throw new IllegalStateException();
		}
		if (BlockProperties.get(id) == null) {
			throw new IllegalArgumentException("Unknown block type!");
		}
		if (x < 0 || y < 0 || z < 0 || x >= SpoutChunk.DEPTH || y >= world.getMaxHeight() || z >= SpoutChunk.WIDTH) {
			return;
		}
		data[(x * SpoutChunk.DEPTH + z) * world.getMaxHeight() + y] = (byte) id;
	}

	/**
	 * Get the given block type.
	 *
	 * @param data The buffer to read from.
	 * @param x The chunk X coordinate.
	 * @param y The Y coordinate.
	 * @param z The chunk Z coordinate.
	 * @return The type of block at the location.
	 */
	protected int get(byte[] data, World world, int x, int y, int z) {
		if (data == null) {
			throw new IllegalStateException();
		}
		if (x < 0 || y < 0 || z < 0 || x >= SpoutChunk.DEPTH || y >= world.getMaxHeight() || z >= SpoutChunk.WIDTH) {
			return BlockID.AIR;
		}
		return data[(x * SpoutChunk.DEPTH + z) * world.getMaxHeight() + y];
	}

	@Override
	public final List<BlockPopulator> getDefaultPopulators(World world) {
		return populators;
	}

	@Override
	public boolean canSpawn(World world, int x, int z) {
		Block block = world.getHighestBlockAt(x, z).getRelative(BlockFace.DOWN);
		return !block.isLiquid() && !block.isEmpty() && !noSpawnFloors.contains(block.getTypeId());
	}
}
