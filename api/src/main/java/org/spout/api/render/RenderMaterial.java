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
package org.spout.api.render;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.spout.api.render.effect.BufferEffect;
import org.spout.api.render.effect.EntityEffect;
import org.spout.api.render.effect.RenderEffect;
import org.spout.api.render.effect.SnapshotEntity;
import org.spout.api.render.effect.SnapshotMesh;
import org.spout.api.render.effect.SnapshotRender;
import org.spout.api.render.shader.Shader;

public abstract class RenderMaterial implements Comparable<RenderMaterial> {
	public static final Comparator<RenderMaterial> COMPARATOR = new RenderMaterialComparator();
	private static final AtomicInteger idCounter = new AtomicInteger();
	private final int id;

	protected RenderMaterial() {
		this.id = idCounter.getAndIncrement();
	}

	/**
	 * Returns a material param or null if that doesn't exist
	 */
	public abstract Object getValue(String name);

	/**
	 * Returns the shader specified in the material
	 */
	public abstract Shader getShader();

	/**
	 * Assigns the current shader and prepares the material for rendering
	 */
	public abstract void assign();

	/**
	 * Called right before generate a mesh
	 */
	public abstract void preMesh(SnapshotMesh snapshotMesh);

	/**
	 * Called right after generate a mesh
	 */
	public abstract void postMesh(SnapshotMesh snapshotMesh);

	/**
	 * Called right before rendering. Uniform values are those available during last render
	 */
	public abstract void preRender(SnapshotRender snapshotRender);

	/**
	 * Called right after rendering
	 */
	public abstract void postRender(SnapshotRender snapshotRender);

	/**
	 * Called right before rendering an entity
	 */
	public abstract void preRenderEntity(SnapshotEntity snapshotEntity);

	/**
	 * Called right after rendering an entity
	 */
	public abstract void postRenderEntity(SnapshotEntity snapshotEntity);

	/**
	 * Return the render pass order
	 */
	public abstract int getLayer();

	/**
	 * Return the renderEffects
	 */
	public abstract Collection<RenderEffect> getRenderEffects();

	/**
	 * Add RenderEffect
	 */
	public abstract void addRenderEffect(RenderEffect renderEffect);

	/**
	 * Return the entityEffects
	 */
	public abstract Collection<EntityEffect> getEntityEffects();

	/**
	 * Add EntityEffect
	 */
	public abstract void addEntityEffect(EntityEffect entityEffect);

	@Override
	public final int compareTo(RenderMaterial o) {
		if (o == this) {
			return 0;
		} else {
			int l1 = getLayer();
			int l2 = o.getLayer();
			if (l1 != l2) {
				return l1 - l2;
			} else {
				return id - o.id;
			}
		}
	}

	@Override
	public final boolean equals(Object o) {
		return o == this;
	}

	@Override
	public int hashCode() {
		return id;
	}

	public abstract void addBufferEffect(BufferEffect effect);

	public abstract List<BufferEffect> getBufferEffects();

	public static final class RenderMaterialComparator implements Comparator<RenderMaterial> {
		@Override
		public int compare(RenderMaterial o1, RenderMaterial o2) {
			if (o1 != null) {
				return o1.compareTo(o2);
			} else if (o2 != null) {
				return -1 * o2.compareTo(o1);
			} else {
				return 0;
			}
		}
	}
}
