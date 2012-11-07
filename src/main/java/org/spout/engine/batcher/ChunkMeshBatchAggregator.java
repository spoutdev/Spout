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
package org.spout.engine.batcher;

import java.util.ArrayList;
import java.util.List;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Cuboid;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Matrix;
import org.spout.api.math.Vector3;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.Renderer;
import org.spout.engine.mesh.ChunkMesh;
import org.spout.engine.mesh.ComposedMesh;
import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.renderer.WorldRenderer;

/**
 * Represents a group of chunk meshes to be rendered.
 */
public class ChunkMeshBatchAggregator extends Cuboid {

	public final static int SIZE_X = 1;
	public final static int SIZE_Y = 1;
	public final static int SIZE_Z = 1;
	public final static Vector3 SIZE = new Vector3(SIZE_X, SIZE_Y, SIZE_Z);
	public final static int COUNT = SIZE_X * SIZE_Y * SIZE_Z;
	
	private ChunkMeshBatch []batchs = new ChunkMeshBatch[COUNT];
	private List<ChunkMeshBatch> dirties = new ArrayList<ChunkMeshBatch>();
	private int count = 0;
	
	private PrimitiveBatch renderer = new PrimitiveBatch();
	
	private Matrix modelMat = MathHelper.createIdentity();
	private final BlockFace face;
	private final RenderMaterial material;
	private boolean dirty = true;
	private boolean hasVertice = false;
	
	public ChunkMeshBatchAggregator(World world, int baseX, int baseY, int baseZ, BlockFace face, RenderMaterial material) {
		super(new Point(world, baseX, baseY, baseZ), SIZE);
		this.face = face;
		this.material = material;
		//Not need of modelMat if render use world block position
		//modelMat = MathHelper.translate(new Vector3(baseX * ChunkMesh.SUBSIZE_X, baseY * ChunkMesh.SUBSIZE_Y, baseZ * ChunkMesh.SUBSIZE_Z));
	}

	public boolean update(long start) {
		for(ChunkMeshBatch batch : dirties){
			batch.update();
			
			if( System.currentTimeMillis() - start > WorldRenderer.TIME_LIMIT)
				return false;
		}
		
		if(isFull()){
			List<Renderer> renderers = new ArrayList<Renderer>();
			
			for(ChunkMeshBatch batch : batchs){
				renderers.add(batch.getRenderer().getRenderer());
			}
			
			renderer.getRenderer().merge(renderers);

			dirty = false;
		}
		return true;
	}

	private boolean isFull() {
		return count == COUNT;
	}

	public int render(RenderMaterial material) {
		int rended = 0;
		if (dirty){
			for(ChunkMeshBatch batch : batchs){
				if(batch != null){
					batch.render(material);
					rended++;
				}
			}
		}else{
			renderer.draw(material);
			rended++;
		}
		return rended;
	}

	public void finalize() {
		for(ChunkMeshBatch batch : batchs)
			if(batch != null)
				batch.finalize();
		
		((BatchVertexRenderer)renderer.getRenderer()).finalize();
	}

	public Matrix getTransform() {
		return modelMat;
	}

	@Override
	public String toString() {
		return "ChunkMeshBatch [base=" + base + ", size=" + size + "]";
	}

	public void setSubBatch(int x, int y, int z, ComposedMesh mesh) {
		x -= getBase().getFloorX();
		y -= getBase().getFloorY();
		z -= getBase().getFloorZ();

		int index = x * SIZE_Y * SIZE_Z + y * SIZE_Z + z;

		if(index < 0 || index >= COUNT){
			System.out.println("Aggregator : " + getX() + "/" + getY() + "/" + getZ());
			System.out.println("Mesh : " + x + "/" + y + "/" + z);
			System.out.println("Error index" + index);
			index = 0;
		}

		if( mesh == null ){
			if(batchs[index] != null)
				count --;
			batchs[index] = null;
		}else{
			if(batchs[index] == null){
				batchs[index] = new ChunkMeshBatch(x, y, z, face, material);
				count ++;
			}

			batchs[index].setMesh(mesh);
			dirties.add(batchs[index]);
		}
		dirty = true;
	}

	public BlockFace getFace() {
		return face;
	}

	public RenderMaterial getMaterial() {
		return material;
	}

	public static Vector3 getCoordFromChunkMesh(ChunkMesh mesh) {
		return new Vector3(Math.floor((float)mesh.getSubX() / ChunkMeshBatchAggregator.SIZE_X) * ChunkMeshBatchAggregator.SIZE_X,
				Math.floor((float)mesh.getSubY() / ChunkMeshBatchAggregator.SIZE_Y) * ChunkMeshBatchAggregator.SIZE_Y,
				Math.floor((float)mesh.getSubZ() / ChunkMeshBatchAggregator.SIZE_Z) * ChunkMeshBatchAggregator.SIZE_Z);
	}

}