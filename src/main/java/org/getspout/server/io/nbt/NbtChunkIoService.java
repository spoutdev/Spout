package org.getspout.server.io.nbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.getspout.server.SpoutChunk;
import org.getspout.server.io.ChunkIoService;
import org.getspout.server.util.nbt.ByteArrayTag;
import org.getspout.server.util.nbt.CompoundTag;
import org.getspout.server.util.nbt.NBTInputStream;
import org.getspout.server.util.nbt.Tag;

/**
 * An implementation of the {@link org.getspout.server.io.ChunkIoService} which reads and writes NBT
 * maps.
 * @author Graham Edgecombe
 */
public final class NbtChunkIoService implements ChunkIoService {
	private final File dir;

	public NbtChunkIoService() {
		this(new File("world"));
	}

	public NbtChunkIoService(File dir) {
		this.dir = dir;
	}


	public boolean read(SpoutChunk chunk, int x, int z) {
		int fileX = formatInt(x);
		int fileZ = formatInt(z);

		File chunkFile = new File(dir, Integer.toString(fileX & 63, 36) + File.separatorChar + Integer.toString(fileZ & 63, 36)
				+ File.separatorChar + "c." + Integer.toString(fileX, 36) + "." + Integer.toString(fileZ, 36) + ".dat");

		Map<String, Tag> levelTags;
		try {
			NBTInputStream nbt = new NBTInputStream(new FileInputStream(chunkFile));
			CompoundTag tag = (CompoundTag) nbt.readTag();
			levelTags = ((CompoundTag) tag.getValue().get("Level")).getValue();
		} catch (IOException e) {
			return false;
		}

		byte[] tileData = ((ByteArrayTag) levelTags.get("Blocks")).getValue();
		chunk.initializeTypes(tileData);

		byte[] skyLightData = ((ByteArrayTag) levelTags.get("SkyLight")).getValue();
		byte[] blockLightData = ((ByteArrayTag) levelTags.get("BlockLight")).getValue();
		byte[] metaData = ((ByteArrayTag) levelTags.get("Data")).getValue();

		for (int cx = 0; cx < SpoutChunk.WIDTH; cx++) {
			for (int cz = 0; cz < SpoutChunk.DEPTH; cz++) {
				for (int cy = 0; cy < chunk.getWorld().getMaxHeight(); cy++) {
					boolean mostSignificantNibble = ((cx * SpoutChunk.DEPTH + cz) * chunk.getWorld().getMaxHeight() + cy) % 2 == 1;
					int offset = ((cx * SpoutChunk.DEPTH + cz) * chunk.getWorld().getMaxHeight() + cy) / 2;

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

		return true;
	}

	public void write(int x, int z, SpoutChunk chunk) throws IOException {

	}

	public void unload() throws IOException {
	}

	private int formatInt(int i) {
		if (i >= 0)
			return i;
		String bin = Integer.toBinaryString(i);
		StringBuilder ret = new StringBuilder();
		byte[] bytes = bin.getBytes();
		for (int ii = 1; i < bytes.length; ii++) {
			if (bytes[ii] == 1)
				break;
			ret.append((ii == 0) ? 1 : ii);
		}
		return Integer.parseInt(ret.toString());
	}
}
