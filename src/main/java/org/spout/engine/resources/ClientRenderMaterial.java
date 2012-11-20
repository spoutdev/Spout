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
package org.spout.engine.resources;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.ChunkSnapshotModel;
import org.spout.api.material.Material;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.MathHelper;
import org.spout.api.math.Matrix;
import org.spout.api.math.Vector2;
import org.spout.api.math.Vector3;
import org.spout.api.math.Vector4;
import org.spout.api.model.mesh.Mesh;
import org.spout.api.model.mesh.MeshFace;
import org.spout.api.model.mesh.OrientedMesh;
import org.spout.api.model.mesh.OrientedMeshFace;
import org.spout.api.model.mesh.Vertex;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.RenderMode;
import org.spout.api.render.Shader;
import org.spout.api.render.effect.BatchEffect;
import org.spout.api.render.effect.MeshEffect;
import org.spout.api.render.effect.RenderEffect;
import org.spout.api.render.effect.SnapshotBatch;
import org.spout.api.render.effect.SnapshotMesh;
import org.spout.api.render.effect.SnapshotRender;
import org.spout.api.render.shader.VertexBuffer;
import org.spout.engine.renderer.vertexbuffer.FakeVertexBuffer;
import org.spout.engine.renderer.vertexbuffer.VertexBufferImpl;

public class ClientRenderMaterial extends RenderMaterial {

	Shader shader;
	Map<String, Object> materialParameters;

	boolean depthTesting;
	Matrix view;
	Matrix projection;
	int layer;
	private List<MeshEffect> meshEffects = new ArrayList<MeshEffect>();
	private List<BatchEffect> batchEffects = new ArrayList<BatchEffect>();
	private List<RenderEffect> renderEffects = new ArrayList<RenderEffect>();

	public ClientRenderMaterial(Shader s, Map<String, Object> params){
		this(s, params, null, null, true, 0);
	}

	public ClientRenderMaterial(Shader s, Map<String, Object> params, int layer){
		this(s, params, null, null, true, layer);
	}

	public ClientRenderMaterial(Shader s, Map<String, Object> params, Matrix projection, Matrix view, boolean depth, int layer){
		this.shader = s;
		this.materialParameters = params;
		this.projection = projection;
		this.view = view;
		this.depthTesting = depth;
		this.layer = layer;
	}

	@Override
	public void assign(){
		Set<Map.Entry<String, Object>> s = materialParameters.entrySet();

		for(Map.Entry<String, Object> entry : s){
			if(entry.getValue() instanceof Integer){
				shader.setUniform(entry.getKey(), ((Integer)entry.getValue()).intValue());
			} else if( entry.getValue() instanceof Float){
				shader.setUniform(entry.getKey(), ((Float)entry.getValue()).floatValue());
			} else if( entry.getValue() instanceof Double){
				shader.setUniform(entry.getKey(), ((Double)entry.getValue()).floatValue());
			} else if( entry.getValue() instanceof ClientTexture){
				shader.setUniform(entry.getKey(), (ClientTexture)entry.getValue());
			} else if( entry.getValue() instanceof Vector2){
				shader.setUniform(entry.getKey(), (Vector2)entry.getValue());
			} else if( entry.getValue() instanceof Vector3){
				shader.setUniform(entry.getKey(), (Vector3)entry.getValue());
			} else if( entry.getValue() instanceof Vector4){
				shader.setUniform(entry.getKey(), (Vector4)entry.getValue());
			} else if( entry.getValue() instanceof Color){
				shader.setUniform(entry.getKey(), (Color)entry.getValue());
			} else if( entry.getValue() instanceof Matrix) {
				shader.setUniform(entry.getKey(), (Matrix)entry.getValue());
			}
		}

		shader.assign();

	}

	@Override
	public Object getValue(String name) {
		return materialParameters.get(name);
	}

	@Override
	public Shader getShader(){
		return shader;
	}

	@Override
	public void preMesh(SnapshotMesh snapshotMesh) {
		for(MeshEffect meshEffect : getMeshEffects())
			meshEffect.preMesh(snapshotMesh);
	}

	@Override
	public void postMesh(SnapshotMesh snapshotMesh) {
		for(MeshEffect meshEffect : getMeshEffects())
			meshEffect.postMesh(snapshotMesh);
	}

	@Override
	public void preBatch(SnapshotBatch snapshotBatch) {
		for(BatchEffect batchEffect : getBatchEffects())
			batchEffect.preBatch(snapshotBatch);
	}

	@Override
	public void postBatch(SnapshotBatch snapshotBatch) {
		for(BatchEffect batchEffect : getBatchEffects())
			batchEffect.postBatch(snapshotBatch);
	}

	@Override
	public void preRender(SnapshotRender snapshotRender) {
		for(RenderEffect renderEffect : getRenderEffects())
			renderEffect.preRender(snapshotRender);

		if(!depthTesting){
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}

	}

	@Override
	public void postRender(SnapshotRender snapshotRender) {
		for(RenderEffect renderEffect : getRenderEffects())
			renderEffect.postRender(snapshotRender);

		if(!depthTesting){
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}

	@Override
	public List<MeshFace> render(SnapshotMesh snapshotRender) {
		Mesh mesh = snapshotRender.getMesh();

		if(mesh instanceof OrientedMesh){
			return renderBlock(snapshotRender.getSnapshotModel(), snapshotRender.getMaterial(),
					snapshotRender.getPosition(), snapshotRender.getFace(), snapshotRender.getToRender(), (OrientedMesh)mesh);
		}

		return new ArrayList<MeshFace>();
	}

	private void addColor(ChunkSnapshotModel chunkSnapshotModel, Vector3 position, Vertex ...vertexs){
	
		for(Vertex v : vertexs){
			int x1 = position.getFloorX();
			int y1 = position.getFloorY();
			int z1 = position.getFloorZ();

			int x2 = v.position.getFloorX() < position.getFloorX() + 0.5 ? position.getFloorX() - 1 : position.getFloorX() + 1;
			int y2 = v.position.getFloorY() < position.getFloorY() + 0.5 ? position.getFloorY() - 1 : position.getFloorY() + 1;
			int z2 = v.position.getFloorZ() < position.getFloorZ() + 0.5 ? position.getFloorZ() - 1 : position.getFloorZ() + 1;

			double weight1 = new Vector3(v.position.getX(), v.position.getY(), v.position.getZ()).distance(new Vector3(x1 + 0.5, y1 + 0.5, z1 + 0.5));
			double weight2 = new Vector3(v.position.getX(), v.position.getY(), v.position.getZ()).distance(new Vector3(x2 + 0.5, y2 + 0.5, z2 + 0.5));
			
			double weightSum = (weight1 + weight2) / 2;
			
			weight1 /= weightSum;
			weight2 /= weightSum;
			
			float i = 254 / 16f; 
			
			//if(chunkSnapshotModel == null || position == null || chunkSnapshotModel.getChunkFromBlock(x1, y1, z1) == null || chunkSnapshotModel.getChunkFromBlock(x2, y2, z2) == null)
			//	v.color = Color.MAGENTA;

			ChunkSnapshot chunk1 = chunkSnapshotModel.getChunkFromBlock(x1, y1, z1);
			ChunkSnapshot chunk2 = chunkSnapshotModel.getChunkFromBlock(x2, y2, z2);

			byte l1 = chunk1.getBlockLight(x1 & Chunk.BLOCKS.MASK, y1 & Chunk.BLOCKS.MASK, z1 & Chunk.BLOCKS.MASK);
			byte s1 = chunk1.getBlockSkyLight(x1 & Chunk.BLOCKS.MASK, y1 & Chunk.BLOCKS.MASK, z1 & Chunk.BLOCKS.MASK);
			byte l2 = chunk2 != null ? chunk2.getBlockLight(x2 & Chunk.BLOCKS.MASK, y2 & Chunk.BLOCKS.MASK, z2 & Chunk.BLOCKS.MASK): 0;
			byte s2 = chunk2 != null ? chunk2.getBlockSkyLight(x2 & Chunk.BLOCKS.MASK, y2 & Chunk.BLOCKS.MASK, z2 & Chunk.BLOCKS.MASK): 0;

			float light = (float) ((l1 * weight1 + l2 * weight2) * i);

			float sky = (float) ((s1 * weight1 + s2 * weight2) * i);

			v.color = new Color((float)MathHelper.clamp(light, 0f, 1f),(float)MathHelper.clamp(sky, 0f, 1f),0.0f,1.0f);
		}
	}
	
	public List<MeshFace> renderBlock(ChunkSnapshotModel chunkSnapshotModel,Material blockMaterial,
			Vector3 position, BlockFace face, boolean toRender[], OrientedMesh mesh) {
		List<MeshFace> meshs = new ArrayList<MeshFace>();
		Vector3 model = new Vector3(position.getFloorX(), position.getFloorY(), position.getFloorZ());
		for(OrientedMeshFace meshFace : mesh){

			if(!meshFace.canRender(toRender,face))
				continue;

			Iterator<Vertex> it = meshFace.iterator();
			Vertex v1 = new Vertex(it.next());
			Vertex v2 = new Vertex(it.next());
			Vertex v3 = new Vertex(it.next());
			v1.position = v1.position.add(model);
			v2.position = v2.position.add(model);
			v3.position = v3.position.add(model);

			addColor(chunkSnapshotModel, position, v1, v2, v3);

			meshs.add(new MeshFace(v1, v2, v3));
		}
		return meshs;
	}

	@Override
	public int getLayer() {
		return layer;
	}

	@Override
	public void addRenderEffect(RenderEffect renderEffect) {
		renderEffects.add(renderEffect);
	}
	
	@Override
	public Collection<RenderEffect> getRenderEffects() {
		return Collections.unmodifiableCollection(renderEffects);
	}

	@Override
	public Collection<BatchEffect> getBatchEffects() {
		return Collections.unmodifiableCollection(batchEffects);
	}

	@Override
	public void addRenderEffect(BatchEffect batchEffect) {
		batchEffects.add(batchEffect);
	}

	@Override
	public Collection<MeshEffect> getMeshEffects() {
		return Collections.unmodifiableCollection(meshEffects);
	}

	@Override
	public void addMeshEffect(MeshEffect meshEffect) {
		meshEffects.add(meshEffect);
	}

	@Override
	public VertexBuffer getVertexBuffer(String name, int elements, int layout) {
		if(((Client) Spout.getEngine()).getRenderMode() == RenderMode.GL11)
			return new FakeVertexBuffer();
		return new VertexBufferImpl(name, elements, layout);
	}

	@Override
	public FloatBuffer getFloatBuffer(int size) {
		return BufferUtils.createFloatBuffer(size);
	}

}