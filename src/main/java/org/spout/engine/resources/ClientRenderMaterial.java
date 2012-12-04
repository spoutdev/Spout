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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.opengl.GL11;
import org.spout.api.geo.cuboid.ChunkSnapshotModel;
import org.spout.api.material.Material;
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
import org.spout.api.render.Shader;
import org.spout.api.render.effect.BatchEffect;
import org.spout.api.render.effect.EntityEffect;
import org.spout.api.render.effect.MeshEffect;
import org.spout.api.render.effect.RenderEffect;
import org.spout.api.render.effect.SnapshotBatch;
import org.spout.api.render.effect.SnapshotEntity;
import org.spout.api.render.effect.SnapshotMesh;
import org.spout.api.render.effect.SnapshotRender;

public class ClientRenderMaterial extends RenderMaterial {

	Shader shader;
	Map<String, Object> materialParameters;

	boolean depthTesting;
	Matrix view;
	Matrix projection;
	int layer;
	private List<BatchEffect> batchEffects = new ArrayList<BatchEffect>();
	private List<RenderEffect> renderEffects = new ArrayList<RenderEffect>();
	private List<EntityEffect> entityEffects = new ArrayList<EntityEffect>();

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
		if(getShader().getMaterialAssigned() != this){
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
			getShader().setMaterialAssigned(this);
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
		for(MeshEffect meshEffect : snapshotMesh.getMaterial().getMeshEffects())
			meshEffect.preMesh(snapshotMesh);
	}

	@Override
	public void postMesh(SnapshotMesh snapshotMesh) {
		for(MeshEffect meshEffect : snapshotMesh.getMaterial().getMeshEffects())
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
	public void preRenderEntity(SnapshotEntity snapshotEntity) {
		for (EntityEffect entityEffect : getEntityEffects()) {
			entityEffect.preRenderEntity(snapshotEntity);
		}
	}

	@Override
	public void postRenderEntity(SnapshotEntity snapshotEntity) {
		for (EntityEffect entityEffect : getEntityEffects()) {
			entityEffect.postRenderEntity(snapshotEntity);
		}
	}
	
	@Override
	public List<MeshFace> render(SnapshotMesh snapshotRender) {
		Mesh mesh = snapshotRender.getMesh();

		if(mesh instanceof OrientedMesh){
			return renderBlock(snapshotRender.getSnapshotModel(), snapshotRender.getMaterial(),
					snapshotRender.getPosition(), snapshotRender.getToRender(), (OrientedMesh)mesh);
		}

		return new ArrayList<MeshFace>();
	}

	/*private void addColor(ChunkSnapshotModel chunkSnapshotModel, Vertex v){
		if(chunkSnapshotModel != null){
			ChunkSnapshot chunk = chunkSnapshotModel.getChunkFromBlock(v.position.getFloorX(), v.position.getFloorY(), v.position.getFloorZ());
			if(chunk != null){
				float light = chunk.getBlockLight(v.position.getFloorX(), v.position.getFloorY(), v.position.getFloorZ()) / 16f;
				float sky = chunk.getBlockSkyLight(v.position.getFloorX(), v.position.getFloorY(), v.position.getFloorZ()) / 16f;
				Color colorLight = new Color(light * 1.00f, light * 0.75f, light * 0.75f);
				Color colorSky = new Color(sky * 0.75f, sky * 0.75f, sky * 1.00f);
				v.color = new Color(
						MathHelper.clamp(colorLight.getRed() + colorSky.getRed(), 0, 255),
						MathHelper.clamp(colorLight.getGreen() + colorSky.getGreen(), 0, 255),
						MathHelper.clamp(colorLight.getBlue() + colorSky.getBlue(), 0, 255)
						);
			}else{
				v.color = Color.WHITE;
			}
		}else{
			v.color = Color.WHITE;
		}
	}*/

	public List<MeshFace> renderBlock(ChunkSnapshotModel chunkSnapshotModel,Material blockMaterial,
			Vector3 position, boolean toRender[], OrientedMesh mesh) {
		List<MeshFace> meshs = new ArrayList<MeshFace>();
		Vector3 model = new Vector3(position.getFloorX(), position.getFloorY(), position.getFloorZ());
		for(OrientedMeshFace meshFace : mesh){

			if(!meshFace.canRender(toRender))
				continue;

			Iterator<Vertex> it = meshFace.iterator();
			Vertex v1 = new Vertex(it.next());
			Vertex v2 = new Vertex(it.next());
			Vertex v3 = new Vertex(it.next());
			v1.position = v1.position.add(model);
			v2.position = v2.position.add(model);
			v3.position = v3.position.add(model);

			v1.color = Color.black;
			v2.color = Color.black;
			v3.color = Color.black;
			/*addColor(chunkSnapshotModel,v1);
			addColor(chunkSnapshotModel,v2);
			addColor(chunkSnapshotModel,v3);*/

			//Be sure we have a color
			//All cube with the same renderMaterial MUST have a color
			//OR All cube with the same renderMaterial MUST not have a color
			/*Color color = Color.WHITE;
			v1.color = color;
			v2.color = color;
			v3.color = color;*/

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
	public Collection<EntityEffect> getEntityEffects() {
		return Collections.unmodifiableCollection(entityEffects);
	}

	@Override
	public void addEntityEffect(EntityEffect entityEffect) {
		entityEffects.add(entityEffect);
	}

}