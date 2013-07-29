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
package org.spout.engine.filesystem.resource;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import org.spout.math.vector.Vector3;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.effect.BufferEffect;
import org.spout.api.render.effect.EntityEffect;
import org.spout.api.render.effect.MeshEffect;
import org.spout.api.render.effect.RenderEffect;
import org.spout.api.render.effect.SnapshotEntity;
import org.spout.api.render.effect.SnapshotMesh;
import org.spout.api.render.effect.SnapshotRender;
import org.spout.api.render.shader.Shader;
import org.spout.engine.SpoutRenderer;
import org.spout.engine.renderer.shader.SpoutShader;
import org.spout.math.matrix.Matrix2;
import org.spout.math.matrix.Matrix3;
import org.spout.math.matrix.Matrix4;
import org.spout.math.vector.Vector2;
import org.spout.math.vector.Vector4;

public class ClientRenderMaterial extends RenderMaterial {
	SpoutShader shader;
	Map<String, Object> materialParameters;
	boolean depthTesting;
	int layer;
	private List<RenderEffect> renderEffects = new ArrayList<>();
	private List<EntityEffect> entityEffects = new ArrayList<>();
	private List<BufferEffect> bufferEffects = new ArrayList<>();

	public ClientRenderMaterial(Shader s, Map<String, Object> params) {
		this(s, params, true, 0);
	}

	public ClientRenderMaterial(Shader s, Map<String, Object> params, int layer) {
		this(s, params, true, layer);
	}

	public ClientRenderMaterial(Shader s, Map<String, Object> params, boolean depth, int layer) {
		this.shader = (SpoutShader) s;
		this.materialParameters = params;
		this.depthTesting = depth;
		this.layer = layer;
	}

	@Override
	public void assign() {
		if (materialParameters != null && shader.getMaterialAssigned() != this) {
			Set<Map.Entry<String, Object>> s = materialParameters.entrySet();

			for (Map.Entry<String, Object> entry : s) {
				if (entry.getValue() instanceof Integer) {
					shader.setUniform(entry.getKey(), ((Integer) entry.getValue()).intValue());
				} else if (entry.getValue() instanceof Float) {
					shader.setUniform(entry.getKey(), ((Float) entry.getValue()).floatValue());
				} else if (entry.getValue() instanceof Double) {
					shader.setUniform(entry.getKey(), ((Double) entry.getValue()).floatValue());
				} else if (entry.getValue() instanceof ClientTexture) {
					shader.setUniform(entry.getKey(), (ClientTexture) entry.getValue());
				} else if (entry.getValue() instanceof Vector2) {
					shader.setUniform(entry.getKey(), (Vector2) entry.getValue());
				} else if (entry.getValue() instanceof Vector3) {
					shader.setUniform(entry.getKey(), (Vector3) entry.getValue());
				} else if (entry.getValue() instanceof Vector4) {
					shader.setUniform(entry.getKey(), (Vector4) entry.getValue());
				} else if (entry.getValue() instanceof Color) {
					shader.setUniform(entry.getKey(), (Color) entry.getValue());
				} else if (entry.getValue() instanceof Matrix2) {
					shader.setUniform(entry.getKey(), (Matrix2) entry.getValue());
				} else if (entry.getValue() instanceof Matrix3) {
					shader.setUniform(entry.getKey(), (Matrix3) entry.getValue());
				} else if (entry.getValue() instanceof Matrix4) {
					shader.setUniform(entry.getKey(), (Matrix4) entry.getValue());
				}
			}
			shader.setMaterialAssigned(this);
		}

		shader.assign();
		shader.checkUniform();
	}

	@Override
	public Object getValue(String name) {
		return materialParameters.get(name);
	}

	@Override
	public Shader getShader() {
		return shader;
	}

	@Override
	public void preMesh(SnapshotMesh snapshotMesh) {
		for (MeshEffect meshEffect : snapshotMesh.getMaterial().getMeshEffects()) {
			meshEffect.preMesh(snapshotMesh);
		}
	}

	@Override
	public void postMesh(SnapshotMesh snapshotMesh) {
		for (MeshEffect meshEffect : snapshotMesh.getMaterial().getMeshEffects()) {
			meshEffect.postMesh(snapshotMesh);
		}
	}

	@Override
	public void preRender(SnapshotRender snapshotRender) {
		for (RenderEffect renderEffect : getRenderEffects()) {
			renderEffect.preRender(snapshotRender);
		}

		if (!depthTesting) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			SpoutRenderer.checkGLError();
		}
	}

	@Override
	public void postRender(SnapshotRender snapshotRender) {
		for (RenderEffect renderEffect : getRenderEffects()) {
			renderEffect.postRender(snapshotRender);
		}

		if (!depthTesting) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			SpoutRenderer.checkGLError();
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
	public Collection<EntityEffect> getEntityEffects() {
		return Collections.unmodifiableCollection(entityEffects);
	}

	@Override
	public void addEntityEffect(EntityEffect entityEffect) {
		entityEffects.add(entityEffect);
	}

	@Override
	public void addBufferEffect(BufferEffect effect) {
		bufferEffects.add(effect);
	}

	@Override
	public List<BufferEffect> getBufferEffects() {
		return bufferEffects;
	}
}