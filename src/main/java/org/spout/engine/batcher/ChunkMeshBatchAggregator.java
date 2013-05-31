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
package org.spout.engine.batcher;

import org.lwjgl.opengl.GL11;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Cuboid;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Matrix;
import org.spout.api.math.MatrixMath;
import org.spout.api.math.Vector3;
import org.spout.api.render.BufferContainer;
import org.spout.api.render.RenderMaterial;
import org.spout.engine.mesh.ChunkMesh;
import org.spout.engine.renderer.BatchVertexRenderer;

/**
 * Represents a group of chunk meshes to be rendered.
 */
public class ChunkMeshBatchAggregator extends Cuboid {

	public final static int SIZE_X = 1;
	public final static int SIZE_Y = 8;
	public final static int SIZE_Z = 1;
	public final static Vector3 SIZE = new Vector3(SIZE_X, SIZE_Y, SIZE_Z);
	public final static int COUNT = SIZE_X * SIZE_Y * SIZE_Z;

	private int count = 0;

	private BatchVertexRenderer renderer = (BatchVertexRenderer) BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);

	public final static Matrix model = MatrixMath.createIdentity();
	private final RenderMaterial material;

	private boolean dataSended = false;
	private boolean flushing = false;
	private boolean generated = false;
	private boolean closed = false;
	
	//Debug
	private long time;

	private final BufferContainer bufferContainer[] = new BufferContainer[COUNT];

	private int getIndex(int x, int y, int z){
		int index = (x - getBase().getChunkX()) * SIZE_Y * SIZE_Z + (y - getBase().getChunkY()) * SIZE_Z + (z - getBase().getChunkZ());
		
		//Debug
		/*System.out.println("Index : " + x + "/"+ y + "/"+ z + " -> " +
				getBase().getChunkX() + "/" + getBase().getChunkY() + "/" + getBase().getChunkZ() + " -> "+
				index);*/

		
		return index;
	}

	public static Vector3 getBaseFromChunkMesh(ChunkMesh mesh) {
		Vector3 v = new Vector3(Math.floor((float)mesh.getChunkX() / ChunkMeshBatchAggregator.SIZE_X) * ChunkMeshBatchAggregator.SIZE_X,
				Math.floor((float)mesh.getChunkY() / ChunkMeshBatchAggregator.SIZE_Y) * ChunkMeshBatchAggregator.SIZE_Y,
				Math.floor((float)mesh.getChunkZ() / ChunkMeshBatchAggregator.SIZE_Z) * ChunkMeshBatchAggregator.SIZE_Z);

		//Debug
		//System.out.println("Base : " + mesh.getChunkX() + "/"+ mesh.getChunkY() + "/"+ mesh.getChunkZ() + " -> " + v.getFloorX() + "/" + v.getFloorY() + "/" + v.getFloorZ() + "/");

		return v;
	}

	public static Vector3 getCoordFromChunkMesh(ChunkMesh mesh) {
		Vector3 v = new Vector3(Math.floor((float)mesh.getChunkX() / ChunkMeshBatchAggregator.SIZE_X),
				Math.floor((float)mesh.getChunkY() / ChunkMeshBatchAggregator.SIZE_Y),
				Math.floor((float)mesh.getChunkZ() / ChunkMeshBatchAggregator.SIZE_Z));

		//Debug
		//System.out.println("Coord : " + mesh.getChunkX() + "/"+ mesh.getChunkY() + "/"+ mesh.getChunkZ() + " -> " + v.getFloorX() + "/" + v.getFloorY() + "/" + v.getFloorZ() + "/");

		return v;
	}

	public ChunkMeshBatchAggregator(World world, int x, int y, int z, RenderMaterial material) {
		super(new Point(world, x << Chunk.BLOCKS.BITS, y << Chunk.BLOCKS.BITS, z << Chunk.BLOCKS.BITS), SIZE.multiply(Chunk.BLOCKS.SIZE));
		this.material = material;
	}

	public boolean update() {
		//long start = System.nanoTime();
		if (closed) {
			throw new IllegalStateException("Already closed");
		}

		//Send data
		if(!dataSended){
			((BatchVertexRenderer)renderer).setBufferContainers(bufferContainer);
			dataSended = true;
		}

		//Start to flush
		if(renderer.flush(false)){
			generated = true;
			flushing = false;
			return true;
		}else{
			flushing = true;
			return false;
		}
		
		//DEBUG
		/*int vertices = 0;
		for(BufferContainer buffer : bufferContainer)
			if(buffer != null)
				vertices += buffer.element;

		System.out.println("BENCHMARK " + (System.nanoTime() - start) + "\t" + count + "\t" + vertices);*/
		
	}

	public void render(RenderMaterial material) {
		if (closed) {
			throw new IllegalStateException("Already closed");
		}

		renderer.draw(material);
	}

	public boolean isClosed() {
		return closed;
	}

	@Override
	public void finalize() {
		if (closed) {
			throw new IllegalStateException("Already closed");
		}

		for(int i = 0; i < bufferContainer.length; i++)
			bufferContainer[i] = null;

		((BatchVertexRenderer)renderer).release();

		closed = true;
	}

	@Override
	public String toString() {
		return "ChunkMeshBatch [base=" + getBase() + ", size=" + getSize() + "]";
	}

	public void setSubBatch(BufferContainer bufferContainer, int x, int y, int z) {
		int index = getIndex(x, y, z);
		
		if(bufferContainer == null && this.bufferContainer[index] != null)
			count--;
		else if(bufferContainer != null && this.bufferContainer[index] == null)
			count++;
		
		this.bufferContainer[getIndex(x, y, z)] = bufferContainer;
		dataSended = false;
	}

	public RenderMaterial getMaterial() {
		return material;
	}

	public boolean isEmpty() {
		return count == 0;
	}

	/**
	 * We can have only one instance of ChunkMeshBatchAggregator at one position and material
	 * So we need to override the equals of extended class cuboid
	 */
	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getTime() {
		return time;
	}

	private boolean queued = false;
	
	public void setQueued(boolean queued) {
		this.queued = queued;
	}

	public boolean isQueued(){
		return queued;
	}

	public boolean isReady() {
		return generated;
	}

	public void preRender() {
		renderer.preDraw();
	}
	
	public void postRender() {
		renderer.postDraw();
	}
	
}