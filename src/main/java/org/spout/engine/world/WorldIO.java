package org.spout.engine.world;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.geo.cuboid.Region;

import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.ShortArrayTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

public class WorldIO {
	protected static void saveChunk(SpoutChunk c, OutputStream dos) {
		CompoundMap chunkTags = new CompoundMap();
		chunkTags.put(new ByteTag("version", (byte) 1));
		chunkTags.put(new ByteTag("format", (byte) 0));
		chunkTags.put(new IntTag("x", c.getX()));
		chunkTags.put(new IntTag("y", c.getY()));
		chunkTags.put(new IntTag("z", c.getZ()));
		chunkTags.put(new ByteTag("populated", c.isPopulated()));
		chunkTags.put(new ShortArrayTag("blocks", c.blockStore.getBlockIdArray()));
		chunkTags.put(new ShortArrayTag("data", c.blockStore.getDataArray()));
		chunkTags.put(new ByteArrayTag("skyLight", c.skyLight));
		chunkTags.put(new ByteArrayTag("blockLight", c.blockLight));

		CompoundTag chunkCompound = new CompoundTag("chunk", chunkTags);

		NBTOutputStream os = null;
		try {
			os = new NBTOutputStream(dos, false);
			os.writeTag(chunkCompound);
		} catch (IOException e) {
			Spout.getLogger().log(Level.SEVERE, "Error saving chunk " + c.toString(), e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	protected static SpoutChunk loadChunk(SpoutRegion r, int x, int y, int z, InputStream dis) {
		SpoutChunk chunk = null;
		NBTInputStream is = null;

		try {
			if (dis == null) {
				//The inputstream is null because no chunk data exists
				return chunk;
			}

			is = new NBTInputStream(dis, false);
			CompoundTag chunkTag = (CompoundTag) is.readTag();
			CompoundMap map = chunkTag.getValue();
			int cx = (r.getX() << Region.REGION_SIZE_BITS) + x;
			int cy = (r.getY() << Region.REGION_SIZE_BITS) + y;
			int cz = (r.getZ() << Region.REGION_SIZE_BITS) + z;

			boolean populated = ((ByteTag) map.get("populated")).getBooleanValue();
			short[] blocks = (short[]) map.get("blocks").getValue();
			short[] data = (short[]) map.get("data").getValue();
			byte[] skyLight = (byte[]) map.get("skyLight").getValue();
			byte[] blockLight = (byte[]) map.get("blockLight").getValue();

			chunk = new SpoutChunk(r.getWorld(), r, cx, cy, cz, populated, blocks, data, skyLight, blockLight);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ignore) {
				}
			}
		}
		return chunk;
	}
}
