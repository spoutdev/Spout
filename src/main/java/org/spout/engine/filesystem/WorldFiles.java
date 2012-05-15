package org.spout.engine.filesystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.logging.Level;

import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.datatable.DataMap;
import org.spout.api.datatable.DatatableMap;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.entity.Entity;
import org.spout.api.entity.type.ControllerRegistry;
import org.spout.api.entity.type.ControllerType;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.util.sanitation.StringSanitizer;
import org.spout.engine.SpoutEngine;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;

import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.FloatTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.LongTag;
import org.spout.nbt.ShortArrayTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

public class WorldFiles {
	private static final byte ENTITY_VERSION = 1;

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

		if (worldData.exists()) {
			NBTInputStream is = null;
			try {
				is = new NBTInputStream(new DataInputStream(new FileInputStream(worldData)), false);
				CompoundTag dataTag = (CompoundTag) is.readTag();
				CompoundMap map = dataTag.getValue();

				@SuppressWarnings("unused")
				byte version = (Byte) map.get("version").getValue();
				long seed = (Long) map.get("seed").getValue();
				String savedGeneratorName = (String) map.get("generator").getValue();
				if (!savedGeneratorName.equals(generatorName)) {
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

	public static void saveChunk(SpoutChunk c, short[] blocks, short[] data, byte[] skyLight, byte[] blockLight, DatatableMap extraData, OutputStream dos) {
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
		chunkTags.put(new CompoundTag("entities", saveEntities(c)));
		chunkTags.put(new ByteArrayTag("extraData", extraData.compress()));

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
			byte[] extraData = (byte[]) map.get("extraData").getValue();
			
			DatatableMap extraDataMap = new GenericDatatableMap();
			extraDataMap.decompress(extraData);

			chunk = new SpoutChunk(r.getWorld(), r, cx, cy, cz, populated, blocks, data, skyLight, blockLight, extraDataMap);

			loadEntities(r, (CompoundMap) map.get("entities").getValue());
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

	private static void loadEntities(SpoutRegion r, CompoundMap map) {
		if (r != null && map != null) {
			for (Tag tag : map) {
				loadEntity(r, (CompoundTag) tag);
			}
		}
	}

	private static CompoundMap saveEntities(SpoutChunk c) {
		Set<Entity> entities = c.getLiveEntities();
		CompoundMap tagMap = new CompoundMap();

		for (Entity e : entities) {
			Tag tag = saveEntity((SpoutEntity) e);
			if (tag != null) {
				tagMap.put(tag);
			}
		}

		return tagMap;
	}

	private static Entity loadEntity(SpoutRegion r, CompoundTag tag) {
		CompoundMap map = tag.getValue();

		@SuppressWarnings("unused")
		byte version = (Byte) map.get("version").getValue();
		String name = (String) map.get("controller").getValue();
		
		ControllerType type = ControllerRegistry.get(name);
		if (type == null) {
			Spout.getEngine().getLogger().log(Level.SEVERE, "No controller type found matching: " + name);
		} else if (type.canCreateController()) {
			
			
			//Read entity
			float pX = (Float) map.get("posX").getValue();
			float pY = (Float) map.get("posY").getValue();
			float pZ = (Float) map.get("posZ").getValue();
			
			float sX = (Float) map.get("scaleX").getValue();
			float sY = (Float) map.get("scaleY").getValue();
			float sZ = (Float) map.get("scaleZ").getValue();
			
			float qX = (Float) map.get("quatX").getValue();
			float qY = (Float) map.get("quatY").getValue();
			float qZ = (Float) map.get("quatZ").getValue();
			float qW = (Float) map.get("quatW").getValue();
			
			int view = (Integer) map.get("view").getValue();
			boolean observer = ((ByteTag) map.get("observer")).getBooleanValue();
			
			//Setup entity
			Transform t = new Transform(new Point(r != null ? r.getWorld() : null, pX, pY, pZ), new Quaternion(qX, qY, qZ, qW, false), new Vector3(sX, sY, sZ));
			SpoutEntity e = new SpoutEntity((SpoutEngine) Spout.getEngine(), t, type.createController(), view, false);
			e.setObserver(observer);
			if (r != null) {
				r.addEntity(e);
			}
			
			System.out.println("Loading a controller of type: " + type.getName() + " (id: " + e.getId() + ")");
			
			//Setup controller
			try {
				if (((ByteTag) map.get("controller_data_exists")).getBooleanValue()) {
					byte[] data = ((ByteArrayTag)map.get("controller_data")).getValue();
					DatatableMap dataMap = ((DataMap)e.getController().data()).getRawMap();
					dataMap.decompress(data);
				}
			} catch (Exception error) {
				Spout.getEngine().getLogger().log(Level.SEVERE, "Unable to load the controller for the type: " + type, error);
			}
			
			return e;
		} else {
			Spout.getEngine().getLogger().log(Level.SEVERE, "Unable to create controller for the type: " + type);
		}
		
		return null;
	}

	private static Tag saveEntity(SpoutEntity e) {
		if (!e.getController().isSavable() || e.isDead()) {
			return null;
		}
		CompoundMap map = new CompoundMap();

		map.put(new ByteTag("version", ENTITY_VERSION));
		map.put(new StringTag("controller", e.getController().getType().getName()));
		
		System.out.println("Saving a controller of type: " + e.getController().getType().getName() + " (id: " + e.getId() + ")");
		
		//Write entity
		map.put(new FloatTag("posX", e.getPosition().getX()));
		map.put(new FloatTag("posY", e.getPosition().getY()));
		map.put(new FloatTag("posZ", e.getPosition().getZ()));
		
		map.put(new FloatTag("scaleX", e.getScale().getX()));
		map.put(new FloatTag("scaleY", e.getScale().getY()));
		map.put(new FloatTag("scaleZ", e.getScale().getZ()));
		
		map.put(new FloatTag("quatX", e.getRotation().getX()));
		map.put(new FloatTag("quatY", e.getRotation().getY()));
		map.put(new FloatTag("quatZ", e.getRotation().getZ()));
		map.put(new FloatTag("quatW", e.getRotation().getW()));
		
		map.put(new IntTag("view", e.getViewDistance()));
		map.put(new ByteTag("observer", e.isObserverLive()));
		
		//Write controller
		try {
			DatatableMap dataMap = ((DataMap)e.getController().data()).getRawMap();
			if (!dataMap.isEmpty()) {
				map.put(new ByteTag("controller_data_exists", true));
				map.put(new ByteArrayTag("controller_data", dataMap.compress()));
			} else {
				map.put(new ByteTag("controller_data_exists", false));
			}
		} catch(Exception error) {
			Spout.getEngine().getLogger().log(Level.SEVERE, "Unable to write the controller information for the type: " + e.getController().getType(), error);
		}
		
		CompoundTag tag = new CompoundTag("entity_" + e.getId(), map);

		return tag;
	}
}
