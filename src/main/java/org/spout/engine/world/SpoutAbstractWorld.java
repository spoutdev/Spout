/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.world;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.spout.api.Source;
import org.spout.api.Spout;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.entity.controller.type.ControllerType;
import org.spout.api.entity.spawn.SpawnArrangement;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Vector3;
import org.spout.api.util.sanitation.StringSanitizer;
import org.spout.api.util.thread.LiveRead;
import org.spout.api.util.thread.Threadsafe;

import org.spout.engine.SpoutEngine;
import org.spout.engine.util.thread.AsyncExecutor;
import org.spout.engine.util.thread.AsyncManager;

/**
 * @author zml2008
 */
public abstract class SpoutAbstractWorld extends AsyncManager implements World {
	/**
	 * The server of this world.
	 */
	private final SpoutEngine engine;
	/**
	 * The name of this world.
	 */
	private final String name;
	/**
	 * The world's UUID.
	 */
	private final UUID uid;

	public SpoutAbstractWorld(String name, UUID uid, SpoutEngine engine, int maxStage, AsyncExecutor executor) {
		super(maxStage, executor, engine);
		this.engine = engine;
		if (!StringSanitizer.isAlphaNumericUnderscore(name)) {
			name = Long.toHexString(System.currentTimeMillis());
			Spout.getEngine().getLogger().severe("World name " + name + " is not valid, using " + name + " instead");
		}
		this.name = name;
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public UUID getUID() {
		return uid;
	}

	@Override
	public SpoutBlock getBlock(int x, int y, int z, Source source) {
		return new SpoutBlock(this, x, y, z, source);
	}

	@Override
	public SpoutBlock getBlock(float x, float y, float z, Source source) {
		return this.getBlock(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z), source);
	}

	@Override
	public SpoutBlock getBlock(Vector3 position, Source source) {
		return this.getBlock(position.getX(), position.getY(), position.getZ(), source);
	}

	public abstract void removeColumn(int x, int z, SpoutColumn column);

	public abstract SpoutColumn getColumn(int x, int z, boolean load);

	@Override
	public int getSurfaceHeight(int x, int z, boolean load) {
		SpoutColumn column = getColumn(x, z, load);
		if (column == null) {
			return Integer.MIN_VALUE;
		}

		return column.getSurfaceHeight(x, z);
	}

	@Override
	public int getSurfaceHeight(int x, int z) {
		return getSurfaceHeight(x, z, false);
	}

	@Override
	public BlockMaterial getTopmostBlock(int x, int z, boolean load) {
		SpoutColumn column = getColumn(x, z, load);
		if (column == null) {
			return null;
		}

		return column.getTopmostBlock(x, z);
	}

	@Override
	public BlockMaterial getTopmostBlock(int x, int z) {
		return getTopmostBlock(x, z, false);
	}

	@Override
	public Entity createAndSpawnEntity(Point point, Controller controller) {
		Entity e = createEntity(point, controller);
		//initialize region if needed
		this.getRegionFromBlock(point);
		spawnEntity(e);
		return e;
	}

	@Override
	public Entity[] createAndSpawnEntity(Point[] points, Controller[] controllers) {
		if (points.length != controllers.length) {
			throw new IllegalArgumentException("Point and controller array must be of equal length");
		}
		Entity[] entities = new Entity[points.length];
		for (int i = 0; i < points.length; i++) {
			entities[i] = createAndSpawnEntity(points[i], controllers[i]);
		}
		return entities;
	}

	@Override
	public Entity[] createAndSpawnEntity(Point[] points, ControllerType[] types) {
		Entity[] entities = new Entity[points.length];
		for (int i = 0; i < points.length; i++) {
			entities[i] = createAndSpawnEntity(points[i], types[i].createController());
		}
		return entities;
	}

	@Override
	public Entity[] createAndSpawnEntity(Point[] points, ControllerType type) {
		Entity[] entities = new Entity[points.length];
		for (int i = 0; i < points.length; i++) {
			entities[i] = createAndSpawnEntity(points[i], type.createController());
		}
		return entities;
	}

	@Override
	public Entity[] createAndSpawnEntity(SpawnArrangement arrangement) {
		ControllerType[] types = arrangement.getControllerTypes();
		if (types.length == 1) {
			return createAndSpawnEntity(arrangement.getArrangement(), types[0]);
		}

		return createAndSpawnEntity(arrangement.getArrangement(), types);
	}

	public SpoutEngine getEngine() {
		return engine;
	}

	/**
	 * Gets a set of nearby players to the point, inside of the range
	 * @param position of the center
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public Set<Player> getNearbyPlayers(Point position, int range) {
		return getNearbyPlayers(position, null, range);
	}

	/**
	 * Gets a set of nearby players to the entity, inside of the range
	 * @param entity marking the center and which is ignored
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public Set<Player> getNearbyPlayers(Entity entity, int range) {
		return getNearbyPlayers(entity.getPosition(), entity, range);
	}

	/**
	 * Gets a set of nearby players to the point, inside of the range.
	 * The search will ignore the specified entity.
	 * @param position of the center
	 * @param ignore Entity to ignore
	 * @param range to look for
	 * @return A set of nearby Players
	 */
	@LiveRead
	@Threadsafe
	public Set<Player> getNearbyPlayers(Point position, Entity ignore, int range) {
		Set<Player> foundPlayers = new HashSet<Player>();
		final int RANGE_SQUARED = range * range;

		for (Player plr : getPlayersNearRegion(position, range)) {
			if (plr != ignore && plr != null) {
				double distance = MathHelper.distanceSquared(position, plr.getPosition());
				if (distance < RANGE_SQUARED) {
					foundPlayers.add(plr);
				}
			}
		}

		return foundPlayers;
	}

	/**
	 * Finds all the players inside of the regions inside the range area
	 * @param position to search from
	 * @param range to search for regions
	 * @return nearby region's players
	 */
	private Set<Player> getPlayersNearRegion(Point position, int range) {
		Region center = this.getRegionFromBlock(position, LoadOption.NO_LOAD);

		HashSet<Player> players = new HashSet<Player>();
		if (center != null) {
			final int regions = (range + Region.BLOCKS.SIZE - 1) / Region.BLOCKS.SIZE; //round up 1 region size
			for (int dx = -regions; dx < regions; dx++) {
				for (int dy = -regions; dy < regions; dy++) {
					for (int dz = -regions; dz < regions; dz++) {
						Region region = this.getRegion(center.getX() + dx, center.getY() + dy, center.getZ() + dz, LoadOption.NO_LOAD);
						if (region != null) {
							players.addAll(region.getPlayers());
						}
					}
				}
			}
		}
		return players;
	}

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * @param position to search from
	 * @param ignore to ignore while searching
	 * @param range to search
	 * @return nearest player
	 */
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Point position, Entity ignore, int range) {
		Player best = null;
		double bestDistance = range * range;

		for (Player plr : getPlayersNearRegion(position, range)) {
			if (plr != ignore && plr != null) {
				double distance = MathHelper.distanceSquared(position, plr.getPosition());
				if (distance < bestDistance) {
					bestDistance = distance;
					best = plr;
				}
			}
		}
		return best;
	}

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * @param range to search
	 * @return nearest player
	 */
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Point position, int range) {
		return getNearestPlayer(position, null, range);
	}

	/**
	 * Gets the absolute closest player from the specified point within a specified range.
	 * @param entity to search from
	 * @param range to search
	 * @return nearest player
	 */
	@LiveRead
	@Threadsafe
	public Player getNearestPlayer(Entity entity, int range) {
		return getNearestPlayer(entity.getPosition(), entity, range);
	}

	public SpoutRegion getRegion(int x, int y, int z) {
		return getRegion(x, y, z, LoadOption.LOAD_GEN);
	}

	public abstract SpoutRegion getRegion(int x, int y, int z, LoadOption loadopt);

	@Override
	public SpoutRegion getRegionFromChunk(int x, int y, int z) {
		return getRegionFromChunk(x, y, z, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutRegion getRegionFromChunk(int x, int y, int z, LoadOption loadopt) {
		return getRegion(x >> Region.CHUNKS.BITS, y >> Region.CHUNKS.BITS, z >> Region.CHUNKS.BITS, loadopt);
	}

	@Override
	public SpoutRegion getRegionFromBlock(Vector3 position) {
		return getRegionFromBlock(position, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutRegion getRegionFromBlock(Vector3 position, LoadOption loadopt) {
		return this.getRegionFromBlock(position.getFloorX(), position.getFloorY(), position.getFloorZ(), loadopt);
	}

	@Override
	public SpoutRegion getRegionFromBlock(int x, int y, int z) {
		return getRegionFromBlock(x, y, z, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutRegion getRegionFromBlock(int x, int y, int z, LoadOption loadopt) {
		return getRegion(x >> Region.BLOCKS.BITS, y >> Region.BLOCKS.BITS, z >> Region.BLOCKS.BITS, loadopt);
	}

	@Override
	public SpoutChunk getChunk(int x, int y, int z) {
		return this.getChunk(x, y, z, LoadOption.LOAD_GEN);
	}

	public abstract SpoutChunk getChunk(int x, int y, int z, LoadOption loadopt);

	@Override
	public SpoutChunk getChunkFromBlock(int x, int y, int z) {
		return this.getChunkFromBlock(x, y, z, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutChunk getChunkFromBlock(int x, int y, int z, LoadOption loadopt) {
		return this.getChunk(x >> Chunk.BLOCKS.BITS, y >> Chunk.BLOCKS.BITS, z >> Chunk.BLOCKS.BITS, loadopt);
	}

	@Override
	public SpoutChunk getChunkFromBlock(Vector3 position) {
		return this.getChunkFromBlock(position, LoadOption.LOAD_GEN);
	}

	@Override
	public SpoutChunk getChunkFromBlock(Vector3 position, LoadOption loadopt) {
		return this.getChunkFromBlock(position.getFloorX(), position.getFloorY(), position.getFloorZ(), loadopt);
	}

	@Override
	public boolean setBlockMaterial(int x, int y, int z, BlockMaterial material, short data, Source source) {
		return this.getChunkFromBlock(x, y, z).setBlockMaterial(x, y, z, material, data, source);
	}

	@Override
	public boolean setBlockData(int x, int y, int z, short data, Source source) {
		return getChunkFromBlock(x, y, z).setBlockData(x, y, z, data, source);
	}

	@Override
	public boolean addBlockData(int x, int y, int z, short data, Source source) {
		return getChunkFromBlock(x, y, z).addBlockData(x, y, z, data, source);
	}

	@Override
	public int getBlockFullState(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockFullState(x, y, z);
	}

	@Override
	public BlockMaterial getBlockMaterial(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockMaterial(x, y, z);
	}

	@Override
	public short getBlockData(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockData(x, y, z);
	}

	@Override
	public byte getBlockSkyLight(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockSkyLight(x, y, z);
	}

	@Override
	public byte getBlockSkyLightRaw(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockSkyLightRaw(x, y, z);
	}

	@Override
	public byte getBlockLight(int x, int y, int z) {
		return getChunkFromBlock(x, y, z).getBlockLight(x, y, z);
	}

	@Override
	public boolean compareAndSetData(int x, int y, int z, int expect, short data, Source source) {
		return getChunkFromBlock(x, y, z).compareAndSetData(x, y, z, expect, data, source);
	}

	@Override
	public short setBlockDataBits(int x, int y, int z, int bits, boolean set, Source source) {
		return getChunkFromBlock(x, y, z).setBlockDataBits(x, y, z, bits, set, source);
	}

	@Override
	public short setBlockDataBits(int x, int y, int z, int bits, Source source) {
		return getChunkFromBlock(x, y, z).setBlockDataBits(x, y, z, bits, source);
	}

	@Override
	public short clearBlockDataBits(int x, int y, int z, int bits, Source source) {
		return getChunkFromBlock(x, y, z).clearBlockDataBits(x, y, z, bits, source);
	}

	@Override
	public int getBlockDataField(int x, int y, int z, int bits) {
		return getChunkFromBlock(x, y, z).getBlockDataField(x, y, z, bits);
	}

	@Override
	public boolean isBlockDataBitSet(int x, int y, int z, int bits) {
		return getChunkFromBlock(x, y, z).isBlockDataBitSet(x, y, z, bits);
	}

	@Override
	public int setBlockDataField(int x, int y, int z, int bits, int value, Source source) {
		return getChunkFromBlock(x, y, z).setBlockDataField(x, y, z, bits, value, source);
	}

	@Override
	public int addBlockDataField(int x, int y, int z, int bits, int value, Source source) {
		return getChunkFromBlock(x, y, z).addBlockDataField(x, y, z, bits, value, source);
	}

	@Override
	public void resetDynamicBlock(int x, int y, int z) {
		this.getRegionFromBlock(x, y, z).resetDynamicBlock(x, y, z);
	}

	@Override
	public void syncResetDynamicBlock(int x, int y, int z) {
		this.getRegionFromBlock(x, y, z).syncResetDynamicBlock(x, y, z);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, int data) {
		return this.getRegionFromBlock(x, y, z).queueDynamicUpdate(x, y, z, nextUpdate, data);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate) {
		return this.getRegionFromBlock(x, y, z).queueDynamicUpdate(x, y, z, nextUpdate);
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z) {
		return this.getRegionFromBlock(x, y, z).queueDynamicUpdate(x, y, z);
	}
}
