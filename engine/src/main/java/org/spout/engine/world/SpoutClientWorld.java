/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
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
package org.spout.engine.world;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.spout.api.component.Component;
import org.spout.api.component.entity.EntityComponent;
import org.spout.api.component.world.WorldComponent;
import org.spout.api.entity.Player;
import org.spout.api.event.entity.EntitySpawnEvent;
import org.spout.api.geo.LoadOption;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.DynamicUpdateEntry;
import org.spout.api.material.range.EffectRange;
import org.spout.api.math.Vector3;
import org.spout.api.scheduler.TaskManager;
import org.spout.engine.SpoutClient;
import org.spout.engine.entity.SpoutClientPlayer;
import org.spout.engine.entity.SpoutEntity;

public class SpoutClientWorld extends SpoutWorld {
	/**
	 * Indicates if the snapshot queue for the renderer should be populated
	 */
	private final AtomicBoolean renderQueueEnabled = new AtomicBoolean(false);

	public SpoutClientWorld(String name, SpoutClient client, UUID uid) {
		super(name, client, 0, 0, null, uid);
	}

	public void addChunk(int chunkX, int chunkY, int chunkZ, short[] blockIds, short[] blockData, Map<Short, byte[]> light) {
		SpoutChunk chunk = getRegionFromBlock(chunkX, chunkY, chunkZ, LoadOption.LOAD_GEN).addChunk(chunkX, chunkY, chunkZ, blockIds, blockData);
		chunk.setLightingBufferData(light);
		chunk.render();
	}

	public void removeChunk(int chunkX, int chunkY, int chunkZ) {
		getRegionFromBlock(chunkX, chunkY, chunkZ, LoadOption.LOAD_GEN).removeChunk(chunkX, chunkY, chunkZ);
	}

	@Override
	public void unload(boolean save) {
		for (Component component : values()) {
			component.onDetached();
		}
		if (save) {
			throw new IllegalArgumentException("Client is not allowed to save");
		}
		Collection<Region> regions = getRegions();
		for (Region r : regions) {
			r.unload(save);
		}
	}

	@Override
	public void resetDynamicBlock(int x, int y, int z) {
		throw new UnsupportedOperationException("Client is not allowed to reset dynamic block");
	}

	@Override
	public void resetDynamicBlocks(Chunk c) {
		throw new UnsupportedOperationException("Client is not allowed to reset dynamic blocks");
	}

	@Override
	public void syncResetDynamicBlock(int x, int y, int z) {
		throw new UnsupportedOperationException("Client is not allowed to sync and reset dynamic blocks");
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, int data, boolean exclusive) {
		throw new UnsupportedOperationException("Client is not allowed to queue dynamic update");
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, long nextUpdate, boolean exclusive) {
		throw new UnsupportedOperationException("Client is not allowed to queue dynamic update");
	}

	@Override
	public DynamicUpdateEntry queueDynamicUpdate(int x, int y, int z, boolean exclusive) {
		throw new UnsupportedOperationException("Client is not allowed to queue dynamic update");
	}

	@Override
	public long getSeed() {
		throw new UnsupportedOperationException("Client is not allowed to retrieve the world seed");
	}

	@Override
	public void updateBlockPhysics(int x, int y, int z) {
		throw new UnsupportedOperationException("Client is not allowed to update block physics");
	}

	@Override
	public void queueBlockPhysics(int x, int y, int z, EffectRange range) {
		throw new UnsupportedOperationException("Client is not allowed to queue block physics");
	}

	@Override
	public void queueBlockPhysics(int x, int y, int z, EffectRange range, BlockMaterial oldMaterial) {
		throw new UnsupportedOperationException("Client is not allowed to queue block physics");
	}

	@Override
	public void removeColumn(int x, int z, SpoutColumn column) {
		throw new UnsupportedOperationException("Client is not allowed to remove a column");
	}

	@Override
	public SpoutColumn setIfNotGenerated(int x, int z, int[][] heightMap) {
		throw new UnsupportedOperationException("Client is not allowed to set modify columns");
	}

	@Override
	public SpoutColumn loadColumn(int x, int z) {
		throw new UnsupportedOperationException("Client is not allowed to load columns");
	}

	@Override
	public void saveChunk(int x, int y, int z) {
		throw new UnsupportedOperationException("Client is not allowed to save a chunk");
	}

	@Override
	public void unloadChunk(int x, int y, int z, boolean save) {
		throw new UnsupportedOperationException("Client is not allowed to unload a chunk");
	}

	@Override
	protected void lockChunks(SpoutChunk[][][] chunks) {
		throw new UnsupportedOperationException("Client is not allowed to lock chunks");
	}

	@Override
	protected void unlockChunks(SpoutChunk[][][] chunks) {
		throw new UnsupportedOperationException("Client is not allowed to unlock chunks");
	}

	@Override
	public void queueChunksForGeneration(List<Vector3> chunks) {
		throw new UnsupportedOperationException("Client is not allowed to queue chunks for generation");
	}

	@Override
	public void queueChunkForGeneration(Vector3 chunk) {
		throw new UnsupportedOperationException("Client is not allowed to queue chunks for generation");
	}

	public void enableRenderQueue() {
		this.renderQueueEnabled.set(true);
	}

	public void disableRenderQueue() {
		this.renderQueueEnabled.set(false);
	}

	public boolean isRenderQueueEnabled() {
		return renderQueueEnabled.get();
	}

	@Override
	public Transform getSpawnPoint() {
		throw new UnsupportedOperationException("Worlds do not have spawnpoints in client mode");
	}

	@Override
	public void setSpawnPoint(Transform transform) {
		throw new UnsupportedOperationException("Worlds do not have spawnpoints in client mode");
	}

	@Override
	public List<Player> getPlayers() {
		throw new UnsupportedOperationException("Client worlds do not hold players");
	}

	@Override
	public void save() {
		throw new UnsupportedOperationException("Client cannot save worlds");
	}

	@Override
	public File getDirectory() {
		throw new UnsupportedOperationException("Client cannot save worlds, therefore it does not have a world directory");
	}

	@Override
	public TaskManager getTaskManager() {
		throw new UnsupportedOperationException("Client does not run world tasks");
	}

	@Override
	public void finalizeRun() {
	}

	@Override
	public void copySnapshotRun() {
		snapshotManager.copyAllSnapshots();
	}

	public void addLocalPlayer(final SpoutClientPlayer player) {
		final SpoutRegion region = (SpoutRegion) player.getRegion();
		EntitySpawnEvent event = getEngine().getEventManager().callEvent(new EntitySpawnEvent(player, player.getPhysics().getPosition()));
		region.getEntityManager().addEntity(player);
		//Alert world components that an entity entered
		for (Component component : values()) {
			if (component instanceof WorldComponent) {
				((WorldComponent) component).onSpawn(event);
			}
		}
		//Alert entity components that their owner spawned
		for (Component component : player.values()) {
			if (component instanceof EntityComponent) {
				((EntityComponent) component).onSpawned(event);
			}
		}
	}
}
