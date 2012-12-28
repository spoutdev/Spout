/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.engine.filesystem.versioned;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.component.Component;
import org.spout.api.entity.EntitySnapshot;
import org.spout.api.entity.PlayerSnapshot;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.io.nbt.TransformTag;
import org.spout.api.io.nbt.UUIDTag;
import org.spout.api.plugin.CommonClassLoader;
import org.spout.api.util.sanitation.SafeCast;
import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.world.SpoutRegion;
import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;
import org.spout.nbt.util.NBTMapper;

public class EntityFiles {

    private EntityFiles() {
    }
    
	public static final byte ENTITY_VERSION = 2;
	
	@SuppressWarnings("rawtypes")
	protected static void loadEntities(SpoutRegion r, CompoundMap map, List<SpoutEntity> loadedEntities) {
		if (r != null && map != null) {
			for (Tag tag : map) {
				SpoutEntity e = loadEntity(r, (CompoundTag) tag);
				if (e != null) {
					loadedEntities.add(e);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	protected static CompoundMap saveEntities(List<EntitySnapshot> entities) {
		CompoundMap tagMap = new CompoundMap();
		for (EntitySnapshot e : entities) {
			//Players are saved elsewhere
			if (!(e instanceof PlayerSnapshot)) {
				Tag tag = saveEntity(e);
				if (tag != null) {
					tagMap.put(tag);
				}
			}
		}

		return tagMap;
	}
	
	private static SpoutEntity loadEntity(SpoutRegion r, CompoundTag tag) {
		return loadEntity(r.getWorld(), tag, null);
	}

	protected static SpoutEntity loadEntity(World w, CompoundTag tag, String name) {
		try {
			return loadEntityImpl(w, tag, name);
		} catch (Exception e) {
			Spout.getLogger().log(Level.SEVERE, "Unable to load entity", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static SpoutEntity loadEntityImpl(World w, CompoundTag tag, String name) {
		CompoundMap map = tag.getValue();

		byte version = SafeCast.toByte(NBTMapper.toTagValue(map.get("version")), (byte) -1);
		
		if (version == -1) {
			Spout.getLogger().info("Entity version is -1");
			return null;
		}
		
		if (version > ENTITY_VERSION) {
			Spout.getLogger().log(Level.SEVERE, "Entity version " + version + " exceeds maximum allowed value of " + ENTITY_VERSION);
			return null;
		} else if (version < ENTITY_VERSION) {
			if (version < 1) {
				Spout.getLogger().log(Level.SEVERE, "Unknown entity version " + version);
				return null;
			}
			
			if (version <= 1) {
				map = convertV1V2(map);
				if (map == null) {
					return null;
				}
			}

		}
		
		boolean player = SafeCast.toByte(NBTMapper.toTagValue(map.get("player")), (byte) 0) == 1;
		
		Transform t = TransformTag.getValue(w, map.get("position"));
		
		if (t == null) {
			return null;
		}
		
		UUID uid = UUIDTag.getValue(map.get("uuid"));
		
		if (uid == null) {
			return null;
		}

		int view = SafeCast.toInt(NBTMapper.toTagValue(map.get("view")), 0);
		boolean observer = SafeCast.toGeneric(NBTMapper.toTagValue(map.get("observer")), new ByteTag("", (byte) 0), ByteTag.class).getBooleanValue();

		//Setup data
		boolean controllerDataExists = SafeCast.toGeneric(NBTMapper.toTagValue(map.get("controller_data_exists")), new ByteTag("", (byte) 0), ByteTag.class).getBooleanValue();
		byte[] dataMap = null;
		if (controllerDataExists) {
			dataMap = SafeCast.toByteArray(NBTMapper.toTagValue(map.get("controller_data")), new byte[0]);
		}

		//Setup entity
		Region r = w.getRegionFromBlock(t.getPosition(), player ? LoadOption.LOAD_GEN : LoadOption.NO_LOAD);
		if (r == null) {
			// TODO - this should never happen - entities should be located in the chunk that was just loaded
			Spout.getLogger().info("Attempted to load entity to unloaded region");
			Thread.dumpStack();
			return null;
		}

		ListTag<StringTag> components = (ListTag<StringTag>) map.get("components");
		List<Class<? extends Component>> types = new ArrayList<Class<? extends Component>>(components.getValue().size());
		for (StringTag component : components.getValue()) {
			try {
				try {
					Class<? extends Component> clazz = (Class<? extends Component>) CommonClassLoader.findPluginClass(component.getValue());
					types.add(clazz);
				} catch (ClassNotFoundException e) {
					Class<? extends Component> clazz = (Class<? extends Component>) Class.forName(component.getValue());
					types.add(clazz);
				}
			} catch (ClassNotFoundException e) {
				Spout.getLogger().log(Level.SEVERE, "Unable to find component class " + component.getValue(), e);
			}
		}

		SpoutEntity e;
		if (!player) {
			e = new SpoutEntity(t, view, uid, false, dataMap, types.toArray(new Class[types.size()]));
			e.setObserver(observer);
		} else {
			e = new SpoutPlayer(name, t, view, uid, false, dataMap, types.toArray(new Class[types.size()]));
		}

		return e;
	}

	protected static CompoundTag saveEntity(EntitySnapshot e) {
		if (!e.isSavable() && (!(e instanceof PlayerSnapshot))) {
			return null;
		}
		CompoundMap map = new CompoundMap();
		map.put(new ByteTag("version", ENTITY_VERSION));

		map.put(new ByteTag("player", (e instanceof PlayerSnapshot)));

		//Write entity
		map.put(new TransformTag("position", e.getTransform()));
		map.put(new UUIDTag("uuid", e.getUID()));
		
		map.put(new IntTag("view", e.getViewDistance()));
		map.put(new ByteTag("observer", e.isObserver()));

		//Serialize data
		if (!e.getDataMap().isEmpty()) {
			map.put(new ByteTag("controller_data_exists", true));
			map.put(new ByteArrayTag("controller_data", e.getDataMap().serialize()));
		} else {
			map.put(new ByteTag("controller_data_exists", false));
		}

		List<StringTag> components = new ArrayList<StringTag>();
		for (Class<? extends Component> clazz : e.getComponents()) {
			components.add(new StringTag("component", clazz.getName()));
		}
		map.put(new ListTag<StringTag>("components", StringTag.class, components));

		CompoundTag tag;
		if (e instanceof PlayerSnapshot) {
			tag = new CompoundTag(e.getWorldName(), map);
		} else {
			tag = new CompoundTag("entity_" + e.getId(), map);
		}
		return tag;
	}
	

	/**
	 * Version 1 to version 2 conversion
	 *
	 * Transform and UUID use the new tags
	 */
	
	private static CompoundMap convertV1V2(CompoundMap map) {
		
		float pX = SafeCast.toFloat(NBTMapper.toTagValue(map.get("posX")), Float.MAX_VALUE);
		float pY = SafeCast.toFloat(NBTMapper.toTagValue(map.get("posY")), Float.MAX_VALUE);
		float pZ = SafeCast.toFloat(NBTMapper.toTagValue(map.get("posZ")), Float.MAX_VALUE);

		if (pX == Float.MAX_VALUE || pY == Float.MAX_VALUE || pZ == Float.MAX_VALUE) {
			return null;
		}

		float sX = SafeCast.toFloat(NBTMapper.toTagValue(map.get("scaleX")), 1.0F);
		float sY = SafeCast.toFloat(NBTMapper.toTagValue(map.get("scaleY")), 1.0F);
		float sZ = SafeCast.toFloat(NBTMapper.toTagValue(map.get("scaleZ")), 1.0F);

		float qX = SafeCast.toFloat(NBTMapper.toTagValue(map.get("quatX")), 0.0F);
		float qY = SafeCast.toFloat(NBTMapper.toTagValue(map.get("quatY")), 0.0F);
		float qZ = SafeCast.toFloat(NBTMapper.toTagValue(map.get("quatZ")), 0.0F);
		float qW = SafeCast.toFloat(NBTMapper.toTagValue(map.get("quatW")), 1.0F);

		long msb = SafeCast.toLong(NBTMapper.toTagValue(map.get("UUID_msb")), new Random().nextLong());
		long lsb = SafeCast.toLong(NBTMapper.toTagValue(map.get("UUID_lsb")), new Random().nextLong());

		map.put(new TransformTag("position", pX, pY, pZ, qX, qY, qZ, qW, sX, sY, sZ));
		
		map.put(new UUIDTag("uuid", new UUID(msb, lsb)));
		
		return map;
	
	}

}
