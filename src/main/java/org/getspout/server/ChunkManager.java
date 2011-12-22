package org.getspout.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import org.getspout.api.util.map.TIntPairObjectHashMap;
import org.getspout.server.io.ChunkIoService;

/**
 * A class which manages the {@link SpoutChunk}s currently loaded in memory.
 *
 * @author Graham Edgecombe
 */
public final class ChunkManager {
	/**
	 * The chunk I/O service used to read chunks from the disk and write them to
	 * the disk.
	 */
	private final ChunkIoService service;

	/**
	 * The chunk generator used to generate new chunks.
	 */
	private final ChunkGenerator generator;

	/**
	 * The world this ChunkManager is managing.
	 */
	private final SpoutWorld world;

	/**
	 * A map of chunks currently loaded in memory.
	 */
	private final TIntPairObjectHashMap<SpoutChunk> chunks = new TIntPairObjectHashMap<SpoutChunk>();

	/**
	 * A Random object to be used to generate chunks.
	 */
	private final Random chunkRandom = new Random();

	/**
	 * A Random object to be used to populate chunks.
	 */
	private final Random popRandom = new Random();

	/**
	 * Creates a new chunk manager with the specified I/O service and world
	 * generator.
	 *
	 * @param service The I/O service.
	 * @param generator The world generator.
	 */
	public ChunkManager(SpoutWorld world, ChunkIoService service, ChunkGenerator generator) {
		this.world = world;
		this.service = service;
		this.generator = generator;
	}

	/**
	 * Gets the chunk at the specified X and Z coordinates, loading it from the
	 * disk or generating it if necessary.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @return The chunk.
	 */
	public SpoutChunk getChunk(int x, int z) {
		SpoutChunk chunk = chunks.get(x, z);
		if (chunk == null) {
			chunk = new SpoutChunk(world, x, z);
			chunks.put(x, z, chunk);
		}
		return chunk;
	}

	/**
	 * Call the ChunkIoService to load a chunk, optionally generating the chunk.
	 *
	 * @param x The X coordinate of the chunk to load.
	 * @param z The Y coordinate of the chunk to load.
	 * @param generate Whether to generate the chunk if needed.
	 * @return True on success, false on failure.
	 */
	public boolean loadChunk(int x, int z, boolean generate) {
		boolean success;
		try {
			success = service.read(getChunk(x, z), x, z);
		} catch (Exception e) {
			SpoutServer.logger.log(Level.SEVERE, "Error while loading chunk ({0},{1})", new Object[] {x, z});
			e.printStackTrace();
			success = false;
		}
		EventFactory.onChunkLoad(getChunk(x, z), !success);
		if (!success && generate) {
			chunkRandom.setSeed(x * 341873128712L + z * 132897987541L);

			SpoutChunk chunk = getChunk(x, z);
			try {
				chunk.initializeTypes(generator.generate(world, chunkRandom, x, z));
			} catch (Exception ex) {
				SpoutServer.logger.log(Level.SEVERE, "Error while generating chunk ({0},{1})", new Object[] {x, z});
				ex.printStackTrace();
				return false;
			}

			for (int x2 = x - 1; x2 <= x + 1; ++x2) {
				for (int z2 = z - 1; z2 <= z + 1; ++z2) {
					if (canPopulate(x2, z2)) {
						SpoutChunk chunk2 = getChunk(x2, z2);
						chunk2.setPopulated(true);

						popRandom.setSeed(world.getSeed());
						long xRand = popRandom.nextLong() / 2 * 2 + 1;
						long zRand = popRandom.nextLong() / 2 * 2 + 1;
						popRandom.setSeed(x * xRand + z * zRand ^ world.getSeed());

						for (BlockPopulator p : world.getPopulators()) {
							p.populate(world, popRandom, chunk2);
						}
					}
				}
			}
			EventFactory.onChunkPopulate(chunk);
			return true;
		}

		return success;
	}

	/**
	 * Checks whether the given chunk can be populated by map features.
	 *
	 * @return Whether population is needed and safe.
	 */
	private boolean canPopulate(int x, int z) {
		if (isLoaded(x, z)) {
			if (getChunk(x, z).getPopulated()) {
				return false;
			}
		} else {
			return false;
		}
		for (int x2 = x - 1; x2 <= x + 1; ++x2) {
			for (int z2 = z - 1; z2 <= z + 1; ++z2) {
				if (!isLoaded(x2, z2)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Forces generation of the given chunk.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @return Whether the chunk was successfully regenerated.
	 */
	public boolean forceRegeneration(int x, int z) {
		SpoutChunk chunk = new SpoutChunk(world, x, z);

		if (chunk == null || !chunk.unload(false, false)) {
			return false;
		}

		chunkRandom.setSeed(x * 341873128712L + z * 132897987541L);
		chunk.initializeTypes(generator.generate(world, chunkRandom, x, z));

		if (canPopulate(x, z)) {
			chunk.setPopulated(true);
			for (BlockPopulator p : world.getPopulators()) {
				p.populate(world, new Random(), chunk);
			}
		}

		chunks.put(x, z, chunk);
		return true;
	}

	/**
	 * Checks whether the given Chunk is loaded.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @return Whether the chunk was loaded.
	 */
	public boolean isLoaded(int x, int z) {
		return chunks.get(x, z) != null;
	}

	/**
	 * Gets a list of loaded chunks.
	 *
	 * @return The currently loaded chunks.
	 */
	public SpoutChunk[] getLoadedChunks() {
		ArrayList<SpoutChunk> result = new ArrayList<SpoutChunk>();
		for (SpoutChunk chunk : chunks.valueCollection()) {
			if (chunk.isLoaded()) {
				result.add(chunk);
			}
		}
		return result.toArray(new SpoutChunk[result.size()]);
	}

	/**
	 * Force-saves the given chunk.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 */
	public boolean forceSave(int x, int z) {
		SpoutChunk chunk = chunks.get(x, z);
		if (chunk != null) {
			try {
				service.write(x, z, chunk);
				return true;
			} catch (IOException ex) {
				SpoutServer.logger.log(Level.SEVERE, "Error while saving chunk: {0}", ex.getMessage());
				ex.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/**
	 * Get the chunk generator.
	 */
	public ChunkGenerator getGenerator() {
		return generator;
	}
}
