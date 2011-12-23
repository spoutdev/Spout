package org.getspout.unchecked.server.io.nbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.getspout.api.util.nbt.ByteArrayTag;
import org.getspout.api.util.nbt.ByteTag;
import org.getspout.api.util.nbt.CompoundTag;
import org.getspout.api.util.nbt.NBTInputStream;
import org.getspout.api.util.nbt.Tag;
import org.getspout.unchecked.server.SpoutChunk;
import org.getspout.unchecked.server.io.ChunkIoService;

/**
 * An implementation of the {@link org.getspout.unchecked.server.io.ChunkIoService} which
 * reads and writes NBT maps.
 *
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

	@Override
	public boolean read(SpoutChunk chunk, int x, int z) {
		int fileX = formatInt(x);
		int fileZ = formatInt(z);

		File chunkFile = new File(dir, Integer.toString(fileX & 63, 36) + File.separatorChar + Integer.toString(fileZ & 63, 36) + File.separatorChar + "c." + Integer.toString(fileX, 36) + "." + Integer.toString(fileZ, 36) + ".dat");

		Map<String, Tag> levelTags;
		try {
			NBTInputStream nbt = new NBTInputStream(new FileInputStream(chunkFile));
			CompoundTag tag = (CompoundTag) nbt.readTag();
			levelTags = ((CompoundTag) tag.getValue().get("Level")).getValue();
		} catch (IOException e) {
			return false;
		}

		final byte[] tileData = ((ByteArrayTag) levelTags.get("Blocks")).getValue();
		final byte[] skyLight = new byte[tileData.length];
		final byte[] blockLight = new byte[tileData.length];
		final byte[] meta = new byte[tileData.length];

		final byte[] skyLightData = ((ByteArrayTag) levelTags.get("SkyLight")).getValue();
		final byte[] blockLightData = ((ByteArrayTag) levelTags.get("BlockLight")).getValue();
		final byte[] metaData = ((ByteArrayTag) levelTags.get("Data")).getValue();

		for (int cx = 0; cx < SpoutChunk.WIDTH; cx++) {
			for (int cz = 0; cz < SpoutChunk.DEPTH; cz++) {
				for (int cy = 0; cy < chunk.getWorld().getMaxHeight(); cy++) {
					final int index = ((cx * SpoutChunk.DEPTH + cz) * chunk.getWorld().getMaxHeight() + cy);
					final boolean mostSignificantNibble = index % 2 == 1;
					final int offset = index / 2;

					if (mostSignificantNibble) {
						skyLight[index] = (byte)((skyLightData[offset] & 0xF0) >> 4);
						blockLight[index] = (byte)((blockLightData[offset] & 0xF0) >> 4);
						meta[index] = (byte)((metaData[offset] & 0xF0) >> 4);
					} else {
						skyLight[index] = (byte)(skyLightData[offset] & 0x0F);
						blockLight[index] = (byte)(blockLightData[offset] & 0x0F);
						meta[index] = (byte)(metaData[offset] & 0x0F);
					}
				}
			}
		}

		chunk.initializeTypes(tileData, skyLight, blockLight, meta);
		chunk.setPopulated(((ByteTag) levelTags.get("TerrainPopulated")).getValue() == 1);

		return true;
	}

	@Override
	public void write(int x, int z, SpoutChunk chunk) throws IOException {

	}

	@Override
	public void unload() throws IOException {
	}

	private int formatInt(int i) {
		if (i >= 0) {
			return i;
		}
		String bin = Integer.toBinaryString(i);
		StringBuilder ret = new StringBuilder();
		byte[] bytes = bin.getBytes();
		for (int ii = 1; i < bytes.length; ii++) {
			if (bytes[ii] == 1) {
				break;
			}
			ret.append(ii == 0 ? 1 : ii);
		}
		return Integer.parseInt(ret.toString());
	}
}
