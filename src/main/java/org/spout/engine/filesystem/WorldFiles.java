package org.spout.engine.filesystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.util.sanitation.StringSanitizer;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;

import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.LongTag;
import org.spout.nbt.ShortArrayTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

public class WorldFiles {

	public static void saveWorldData(SpoutWorld world) {
		File worldData = new File(world.getDirectory(), "world.dat");
		
		String generatorName = world.getGenerator().getName();
		if (!StringSanitizer.isAlphaNumericUnderscore(generatorName)) {
			generatorName = Long.toHexString(System.currentTimeMillis());
			Spout.getEngine().getLogger().severe("Generator name " + generatorName + " is not valid, using " + generatorName + " instead");
		}

		CompoundMap worldTags = new CompoundMap();
		worldTags.put(new ByteTag("version", (byte) 1));
		worldTags.put(new LongTag("seed", world.getSeed()));
		worldTags.put(new StringTag("generator", generatorName));
		
		CompoundTag worldTag = new CompoundTag(world.getName(), worldTags);
		
		NBTOutputStream os = null;
		try {
			os = new NBTOutputStream(new DataOutputStream(new FileOutputStream(worldData)), false);
			os.writeTag(worldTag);
		} catch (IOException e) {
			Spout.getLogger().log(Level.SEVERE, "Error saving world data for " + world.toString(), e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	public static SpoutWorld loadWorldData(Engine engine, String name, WorldGenerator generator) {
		SpoutWorld world = null;
		
		String generatorName = generator.getName();
		if (!StringSanitizer.isAlphaNumericUnderscore(generatorName)) {
			generatorName = Long.toHexString(System.currentTimeMillis());
			Spout.getEngine().getLogger().severe("Generator name " + generatorName + " is not valid, using " + generatorName + " instead");
		}
		
		File worldData = new File(new File(FileSystem.WORLDS_DIRECTORY, name), "world.dat");
		
		if(worldData.exists()) {
			NBTInputStream is = null;
			try {
				is = new NBTInputStream(new DataInputStream(new FileInputStream(worldData)), false);
				CompoundTag dataTag = (CompoundTag) is.readTag();
				CompoundMap map = dataTag.getValue();
				
				@SuppressWarnings("unused")
				byte version = (Byte) map.get("version").getValue();
				Long seed = (Long) map.get("seed").getValue();
				String savedGeneratorName = (String) map.get("generator").getValue();
				if(!savedGeneratorName.equals(generatorName)) {
					Spout.getEngine().getLogger().severe("World was saved last with the generator: " + savedGeneratorName + " but is being loaded with: " + generatorName + " MAY CAUSE WORLD CORRUPTION!");
				}
				
				world = new SpoutWorld(name, engine, seed, generator);
			} catch (IOException e) {
				Spout.getLogger().log(Level.SEVERE, "Error saving load data for " + name, e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException ignore) {
					}
				}
			}
		}
		
		return world;
	}

	public static void saveChunk(SpoutChunk c, short[] blocks, short[] data, byte[] skyLight, byte[] blockLight, OutputStream dos) {
		CompoundMap chunkTags = new CompoundMap();
		chunkTags.put(new ByteTag("version", (byte) 1));
		chunkTags.put(new ByteTag("format", (byte) 0));
		chunkTags.put(new IntTag("x", c.getX()));
		chunkTags.put(new IntTag("y", c.getY()));
		chunkTags.put(new IntTag("z", c.getZ()));
		chunkTags.put(new ByteTag("populated", c.isPopulated()));
		chunkTags.put(new ShortArrayTag("blocks", blocks));
		chunkTags.put(new ShortArrayTag("data", data));
		chunkTags.put(new ByteArrayTag("skyLight", skyLight));
		chunkTags.put(new ByteArrayTag("blockLight", blockLight));

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

	public static SpoutChunk loadChunk(SpoutRegion r, int x, int y, int z, InputStream dis) {
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
