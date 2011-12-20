package org.getspout.server.io.mcregion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Entity;

import org.getspout.server.SpoutChunk;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.block.SpoutBlockState;
import org.getspout.server.entity.SpoutEntity;
import org.getspout.server.io.ChunkIoService;
import org.getspout.server.io.blockstate.BlockStateStore;
import org.getspout.server.io.blockstate.BlockStateStoreLookupService;
import org.getspout.server.io.entity.EntityStore;
import org.getspout.server.io.entity.EntityStoreLookupService;
import org.getspout.server.io.mcregion.region.RegionFile;
import org.getspout.server.io.mcregion.region.RegionFileCache;
import org.getspout.server.util.nbt.ByteArrayTag;
import org.getspout.server.util.nbt.ByteTag;
import org.getspout.server.util.nbt.CompoundTag;
import org.getspout.server.util.nbt.IntTag;
import org.getspout.server.util.nbt.ListTag;
import org.getspout.server.util.nbt.NBTInputStream;
import org.getspout.server.util.nbt.NBTOutputStream;
import org.getspout.server.util.nbt.StringTag;
import org.getspout.server.util.nbt.Tag;

/**
 * An implementation of the {@link org.getspout.server.io.ChunkIoService} which
 * reads and writes McRegion maps.
 * <p />
 * Information on the McRegion file format can be found on the <a
 * href="http://mojang.com/2011/02/16/minecraft-save-file-format-in-beta-1-3"
 * >Mojang</a> blog.
 *
 * @author Graham Edgecombe
 */
public final class McRegionChunkIoService implements ChunkIoService {
	/**
	 * The size of a region - a 32x32 group of chunks.
	 */
	private static final int REGION_SIZE = 32;

	/**
	 * The root directory of the map.
	 */
	private final File dir;

	/**
	 * The region file cache.
	 */
	private final RegionFileCache cache = new RegionFileCache();

	// TODO: consider the session.lock file

	public McRegionChunkIoService() {
		this(new File("world"));
	}

	public McRegionChunkIoService(File dir) {
		this.dir = dir;
	}

	@Override
	public boolean read(SpoutChunk chunk, int x, int z) throws IOException {
		RegionFile region = cache.getRegionFile(dir, x, z);
		int regionX = x & REGION_SIZE - 1;
		int regionZ = z & REGION_SIZE - 1;
		if (!region.hasChunk(regionX, regionZ)) {
			return false;
		}

		SpoutWorld world = chunk.getWorld();
		SpoutServer server = world.getServer();

		DataInputStream in = region.getChunkDataInputStream(regionX, regionZ);

		NBTInputStream nbt = new NBTInputStream(in, false);
		CompoundTag tag = (CompoundTag) nbt.readTag();
		Map<String, Tag> levelTags = ((CompoundTag) tag.getValue().get("Level")).getValue();
		nbt.close();

		byte[] tileData = ((ByteArrayTag) levelTags.get("Blocks")).getValue();
		chunk.initializeTypes(tileData);
		chunk.setPopulated(((ByteTag) levelTags.get("TerrainPopulated")).getValue() == 1);

		byte[] skyLightData = ((ByteArrayTag) levelTags.get("SkyLight")).getValue();
		byte[] blockLightData = ((ByteArrayTag) levelTags.get("BlockLight")).getValue();
		byte[] metaData = ((ByteArrayTag) levelTags.get("Data")).getValue();

		for (int cx = 0; cx < SpoutChunk.WIDTH; cx++) {
			for (int cz = 0; cz < SpoutChunk.DEPTH; cz++) {
				for (int cy = 0; cy < world.getMaxHeight(); cy++) {
					boolean mostSignificantNibble = ((cx * SpoutChunk.DEPTH + cz) * world.getMaxHeight() + cy) % 2 == 1;
					int offset = ((cx * SpoutChunk.DEPTH + cz) * world.getMaxHeight() + cy) / 2;

					int skyLight, blockLight, meta;
					if (mostSignificantNibble) {
						skyLight = (skyLightData[offset] & 0xF0) >> 4;
						blockLight = (blockLightData[offset] & 0xF0) >> 4;
						meta = (metaData[offset] & 0xF0) >> 4;
					} else {
						skyLight = skyLightData[offset] & 0x0F;
						blockLight = blockLightData[offset] & 0x0F;
						meta = metaData[offset] & 0x0F;
					}

					chunk.setSkyLight(cx, cy, cz, skyLight);
					chunk.setBlockLight(cx, cy, cz, blockLight);
					chunk.setMetaData(cx, cy, cz, meta);
				}
			}
		}
		List<CompoundTag> storedTileEntities = ((ListTag<CompoundTag>) levelTags.get("TileEntities")).getValue();
		for (CompoundTag tileEntityTag : storedTileEntities) {
			SpoutBlockState state = chunk.getBlock(((IntTag) tileEntityTag.getValue().get("x")).getValue(), ((IntTag) tileEntityTag.getValue().get("y")).getValue(), ((IntTag) tileEntityTag.getValue().get("z")).getValue()).getState();
			if (state.getClass() != SpoutBlockState.class) {
				BlockStateStore store = BlockStateStoreLookupService.find(((StringTag) tileEntityTag.getValue().get("id")).getValue());
				if (store != null) {
					store.load(state, tileEntityTag);
				} else {
					SpoutServer.logger.severe("Unable to find store for BlockState " + state.getClass());
				}
			}
		}

		List<CompoundTag> storedEntities = ((ListTag<CompoundTag>) levelTags.get("Entities")).getValue();
		for (CompoundTag entityTag : storedEntities) {
			String id = ((StringTag) entityTag.getValue().get("id")).getValue();
			EntityStore<?> store = EntityStoreLookupService.find(id);

			if (store != null) {
				store.load(server, world, entityTag);
			} else {
				SpoutServer.logger.severe("Unable to find store for Entity " + id);
			}
		}

		return true;
	}

	/**
	 * Writes a chunk. Currently not compatible with the vanilla server.
	 *
	 * @param x The X coordinate.
	 * @param z The Z coordinate.
	 * @param chunk The {@link SpoutChunk}.
	 * @throws IOException
	 */
	@Override
	public void write(int x, int z, SpoutChunk chunk) throws IOException {
		RegionFile region = cache.getRegionFile(dir, x, z);
		int regionX = x & REGION_SIZE - 1;
		int regionZ = z & REGION_SIZE - 1;

		DataOutputStream out = region.getChunkDataOutputStream(regionX, regionZ);
		NBTOutputStream nbt = new NBTOutputStream(out, false);
		Map<String, Tag> levelTags = new HashMap<String, Tag>();

		byte[] skyLightData = new byte[chunk.getWorld().getMaxHeight() * SpoutChunk.WIDTH * SpoutChunk.DEPTH / 2];
		byte[] blockLightData = new byte[chunk.getWorld().getMaxHeight() * SpoutChunk.WIDTH * SpoutChunk.DEPTH / 2];
		byte[] metaData = new byte[chunk.getWorld().getMaxHeight() * SpoutChunk.WIDTH * SpoutChunk.DEPTH / 2];
		byte[] heightMap = new byte[SpoutChunk.WIDTH * SpoutChunk.DEPTH];

		for (int cx = 0; cx < SpoutChunk.WIDTH; cx++) {
			for (int cz = 0; cz < SpoutChunk.DEPTH; cz++) {
				heightMap[(cx * SpoutChunk.DEPTH + cz) / 2] = (byte) chunk.getWorld().getHighestBlockYAt(x > 0 ? x * cx : cx, z > 0 ? z * cz : cz);
				for (int cy = 0; cy < chunk.getWorld().getMaxHeight(); cy += 2) {
					int offset = ((cx * SpoutChunk.DEPTH + cz) * chunk.getWorld().getMaxHeight() + cy) / 2;
					skyLightData[offset] = (byte) (chunk.getSkyLight(cx, cy + 1, cz) << 4 | chunk.getSkyLight(cx, cy, cz));
					blockLightData[offset] = (byte) (chunk.getBlockLight(cx, cy + 1, cz) << 4 | chunk.getBlockLight(cx, cy, cz));
					metaData[offset] = (byte) (chunk.getMetaData(cx, cy + 1, cz) << 4 | chunk.getMetaData(cx, cy, cz));
				}
			}
		}

		levelTags.put("Blocks", new ByteArrayTag("Blocks", chunk.getTypes()));
		levelTags.put("SkyLight", new ByteArrayTag("SkyLight", skyLightData));
		levelTags.put("BlockLight", new ByteArrayTag("BlockLight", blockLightData));
		levelTags.put("Data", new ByteArrayTag("Data", metaData));
		levelTags.put("HeightMap", new ByteArrayTag("HeightMap", heightMap));

		levelTags.put("xPos", new IntTag("xPos", chunk.getX()));
		levelTags.put("zPos", new IntTag("zPos", chunk.getZ()));
		levelTags.put("TerrainPopulated", new ByteTag("TerrainPopulated", (byte) (chunk.getPopulated() ? 1 : 0)));

		List<CompoundTag> entities = new ArrayList<CompoundTag>();

		for (Entity entity : chunk.getEntities()) {
			SpoutEntity spoutEntity = (SpoutEntity) entity;
			EntityStore store = EntityStoreLookupService.find(spoutEntity.getClass());
			if (store == null) {
				continue;
			}
			entities.add(new CompoundTag("", store.save(spoutEntity)));
		}
		levelTags.put("Entities", new ListTag<CompoundTag>("Entities", CompoundTag.class, entities));

		List<CompoundTag> tileEntities = new ArrayList<CompoundTag>();
		for (SpoutBlockState state : chunk.getTileEntities()) {
			if (state.getClass() != SpoutBlockState.class) {
				BlockStateStore store = BlockStateStoreLookupService.find(state.getClass());
				if (store != null) {
					tileEntities.add(new CompoundTag("", store.save(state)));
				} else {
					SpoutServer.logger.severe("Unable to find store for BlockState " + state.getClass());
				}
			}
		}
		levelTags.put("TileEntities", new ListTag<CompoundTag>("TileEntities", CompoundTag.class, tileEntities));
		Map<String, Tag> levelOut = new HashMap<String, Tag>();
		levelOut.put("Level", new CompoundTag("Level", levelTags));
		nbt.writeTag(new CompoundTag("", levelOut));
		nbt.close();

	}

	@Override
	public void unload() throws IOException {
		cache.clear();
	}
}
