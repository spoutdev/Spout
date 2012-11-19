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

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.List;

import org.spout.api.render.RenderMaterial;
import org.spout.api.render.Renderer;
import org.spout.api.render.effect.SnapshotRender;
import org.spout.api.render.shader.VertexBuffer;

public class GLES20BatchVertexRenderer extends BatchVertexRenderer {
	public GLES20BatchVertexRenderer(int mode) {
		super(mode);
	}

	@Override
	protected void doFlush() {

	}

	@Override
	protected void doRender(RenderMaterial material, int startVert, int endVert) {

	}

	@Override
	public void doMerge(List<Renderer> renderers) {
		// TODO : To implement
		
	}

	@Override
	public void render(RenderMaterial material, SnapshotRender snapshotRender) {
		render(material);
		//TODO : Apply snapshotRender
	}

	@Override
	public void addVertexBuffers(TIntObjectHashMap<VertexBuffer> vertexBuffers) {
	}

	@Override
	protected void doRender(RenderMaterial material, int startVert,
			int endVert, SnapshotRender snapshotRender) {
		// TODO Auto-generated method stub
		
	}
}
