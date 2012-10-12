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
package org.spout.engine.renderer;

import java.util.ArrayList;
import java.util.List;

import org.spout.api.Spout;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Cube;
import org.spout.api.geo.cuboid.Cuboid;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Vector3;
import org.spout.api.render.RenderMaterial;
import org.spout.api.util.map.TInt21TripleObjectHashMap;
import org.spout.engine.SpoutClient;
import org.spout.engine.batcher.ChunkMeshBatch;

public class WorldRenderer {
	private final SpoutClient client;
	private RenderMaterial material;

	private List<ChunkMeshBatch> chunkRenderers = new ArrayList<ChunkMeshBatch>();
	private TInt21TripleObjectHashMap<ChunkMeshBatch> chunkRenderersByPosition = new TInt21TripleObjectHashMap<ChunkMeshBatch>();

	private boolean firstUpdate = true;
	private int lastChunkX;
	private int lastChunkY;
	private int lastChunkZ;

	private World world; // temp

	public WorldRenderer(SpoutClient client) {
		this.client = client;
	}

	public void setup() {
		world = client.getDefaultWorld();
		//world = client.getWorlds().iterator().next();
		material = (RenderMaterial) Spout.getFilesystem().getResource("material://Spout/resources/resources/materials/BasicMaterial.smt");

		updateNearbyChunkMeshes(false);
		// GL11.glEnable(GL11.GL_CULL_FACE);
	}

	public void render() {
		updateNearbyChunkMeshes(false);

		material.getShader().setUniform("View", client.getActiveCamera().getView());
		material.getShader().setUniform("Projection", client.getActiveCamera().getProjection());

		renderChunks();
	}

	/**
	 * Updates the list of chunks around the player.
	 *
	 * @param force
	 *            Forces the update
	 * @return True if the list was changed
	 */
	public boolean updateNearbyChunkMeshes(boolean force) {
		if (world == null) {
			
			world = client.getDefaultWorld();
			if (world != null) System.out.println("World updated to " + world.getName() + "-" + world.getUID());
			//else System.out.println("World is null!");
		}

		if (world == null) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
			}
			return false;
		}

		// TODO: We will work with 1 chunk before trying to expand
		int chunkViewDistance = 0;//client.getActivePlayer().getViewDistance() / 16;
		//System.out.println("ChunkViewDistance ="+ ((int)client.getActivePlayer().getViewDistance() / 16));
		
		
		Point currentPos = client.getActivePlayer().getTransform().getPosition();

		int currentChunkX = currentPos.getChunkX();
		int currentChunkY = currentPos.getChunkY();
		int currentChunkZ = currentPos.getChunkZ();

		if (currentChunkX == lastChunkX && currentChunkY == lastChunkY && currentChunkZ == lastChunkZ && !force && !firstUpdate) {
			return false;
		}
		// just add all visible chunks

		if (chunkRenderers.size() == 0 || force) {
			chunkRenderers.clear();

			int cubeMinX = currentChunkX - chunkViewDistance;
			int cubeMinY = currentChunkY - chunkViewDistance;
			int cubeMinZ = currentChunkZ - chunkViewDistance;

			int cubeMaxX = currentChunkX + chunkViewDistance;
			int cubeMaxY = currentChunkY + chunkViewDistance;
			int cubeMaxZ = currentChunkZ + chunkViewDistance;

			Vector3 batchMin = ChunkMeshBatch.getBatchCoordinates(new Vector3(cubeMinX, cubeMinY, cubeMinZ));
			Vector3 batchMax = ChunkMeshBatch.getBatchCoordinates(new Vector3(cubeMaxX, cubeMaxY, cubeMaxZ));

			for (int x = batchMin.getFloorX(); x <= batchMax.getFloorX(); x++) {
				for (int y = batchMin.getFloorY(); y <= batchMax.getFloorY(); y++) {
					for (int z = batchMin.getFloorZ(); z <= batchMax.getFloorZ(); z++) {
						Vector3 chunkCoords = ChunkMeshBatch.getChunkCoordinates(new Vector3(x, y, z));
						ChunkMeshBatch batch = new ChunkMeshBatch(world, chunkCoords.getFloorX(), chunkCoords.getFloorY(), chunkCoords.getFloorZ());
						addChunkMeshBatch(batch);
						batch.update();

						System.out.println(batch);
					}
				}
			}
		} else {
			Cube oldView = new Cube(new Point(world, lastChunkX - chunkViewDistance, lastChunkY - chunkViewDistance, lastChunkZ - chunkViewDistance), chunkViewDistance * 2);
			Cube newView = new Cube(new Point(world, currentChunkX - chunkViewDistance, currentChunkY - chunkViewDistance, currentChunkZ - chunkViewDistance), chunkViewDistance * 2);

			Vector3 min = oldView.getBase().min(newView.getBase());
			Vector3 max = oldView.getBase().add(oldView.getSize()).max(newView.getBase().add(newView.getSize()));

			// Shared area
			Vector3 ignoreMin = oldView.getBase().max(newView.getBase());
			Vector3 ignoreMax = oldView.getBase().add(oldView.getSize()).min(newView.getBase().add(newView.getSize()));
			Cuboid ignore = new Cuboid(new Point(ignoreMin, world), ignoreMax.subtract(ignoreMin));

			for (int x = min.getFloorX(); x < max.getFloorX(); x++) {
				for (int y = min.getFloorY(); y < max.getFloorY(); y++) {
					for (int z = min.getFloorZ(); z < max.getFloorZ(); z++) {
						Vector3 vec = new Vector3(x, y, z);
						if (ignore.contains(vec)) {
							continue;
						}

						Vector3 pos = ChunkMeshBatch.getChunkCoordinates(vec);

						if (oldView.contains(vec)) {
							ChunkMeshBatch c = chunkRenderersByPosition.get(pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
							removeChunkMeshBatch(c);
							continue;
						}

						if (newView.contains(vec)) {
							ChunkMeshBatch c = new ChunkMeshBatch(world, pos.getFloorX(), pos.getFloorY(), pos.getFloorZ());
							addChunkMeshBatch(c);
							c.update();
						}
					}
				}
			}
		}

		firstUpdate = false;
		lastChunkX = currentChunkX;
		lastChunkY = currentChunkY;
		lastChunkZ = currentChunkZ;

		return true;
	}

	private void addChunkMeshBatch(ChunkMeshBatch batch) {
		chunkRenderers.add(batch);
		chunkRenderersByPosition.put(batch.getX(), batch.getY(), batch.getZ(), batch);
	}

	private void removeChunkMeshBatch(ChunkMeshBatch batch) {
		chunkRenderers.remove(batch);
		chunkRenderersByPosition.remove(batch.getX(), batch.getY(), batch.getZ());
	}
	
	/**
	 * Gets the chunk mesh batch corresponding with the given chunk.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	private ChunkMeshBatch getChunkMeshBatchByChunkPosition(int x, int y, int z) {
		return chunkRenderersByPosition.get(x, y, z);
	}
	
	/**
	 * Updates the ChunkMeshBatch at the given chunk position.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void updateChunk(int x, int y, int z) {
		ChunkMeshBatch batch = getChunkMeshBatchByChunkPosition(x, y, z);
		if (batch == null) {
			return; // Don't call if this chunk isn't in sight...
		}
		batch.update();
	}

	private void renderChunks() {
		for (ChunkMeshBatch renderer : chunkRenderers) {
			material.getShader().setUniform("Model", renderer.getTransform());

			// It's hard to look right
			// at the world baby
			// But here's my frustrum
			// so cull me maybe?
			//if (client.getActiveCamera().getFrustum().intersects(renderer)) {
				renderer.render(material);
			//}
		}
	}
}
