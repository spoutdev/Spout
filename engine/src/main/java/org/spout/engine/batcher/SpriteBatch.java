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
package org.spout.engine.batcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gnu.trove.list.array.TFloatArrayList;

import org.lwjgl.opengl.GL11;

import org.spout.api.gui.render.RenderPart;
import org.spout.api.gui.render.RenderPartPack;
import org.spout.api.render.BufferContainer;
import org.spout.api.render.effect.SnapshotRender;

import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.renderer.vertexformat.vertexattributes.VertexAttributes;
import org.spout.math.matrix.Matrix4;

public class SpriteBatch {
	private BatchVertexRenderer renderer;
	private final ArrayList<RenderPartPack> sprites = new ArrayList<>();
	private final Matrix4 view;
	private final Matrix4 projection;

	public SpriteBatch() {
		this.projection = Matrix4.IDENTITY;
		this.view = Matrix4.IDENTITY;
	}

	public void begin() {
		sprites.clear();
	}

	public void flush(RenderPartPack part) {
		List<RenderPartPack> list = new ArrayList<>();
		list.add(part);
		flush(list);
	}

	public void flush(List<RenderPartPack> parts) {
		if (parts.isEmpty()) {
			return;
		}
		sprites.clear();
		sprites.addAll(parts);

		int totalSize = 0;
		for (RenderPartPack pack : sprites) {
			totalSize += pack.getSize();
		}

		BufferContainer container = new BufferContainer();

		container.element = totalSize * 3 * 2;

		TFloatArrayList vertexBuffer = (TFloatArrayList) container.getBuffers().get(VertexAttributes.Position.getLayout());
		TFloatArrayList colorBuffer = (TFloatArrayList) container.getBuffers().get(VertexAttributes.Color.getLayout());
		TFloatArrayList textureBuffer = (TFloatArrayList) container.getBuffers().get(VertexAttributes.Texture0.getLayout());

		if (vertexBuffer == null) {
			vertexBuffer = new TFloatArrayList(totalSize * 4 * 4);
			container.setBuffers(VertexAttributes.Position.getLayout(), vertexBuffer);
		}

		if (colorBuffer == null) {
			colorBuffer = new TFloatArrayList(totalSize * 4 * 4);
			container.setBuffers(VertexAttributes.Color.getLayout(), colorBuffer);
		}

		if (textureBuffer == null) {
			textureBuffer = new TFloatArrayList(totalSize * 4 * 2);
			container.setBuffers(VertexAttributes.Texture0.getLayout(), textureBuffer);
		}

		for (RenderPartPack pack : sprites) {
			List<RenderPart> renderparts = pack.getRenderParts();
			Collections.sort(renderparts);
			for (RenderPart rect : renderparts) {
				for (int j = 0; j < 6; j++) {
					colorBuffer.add(rect.getColor().getRed() / 255f);
					colorBuffer.add(rect.getColor().getGreen() / 255f);
					colorBuffer.add(rect.getColor().getBlue() / 255f);
					colorBuffer.add(rect.getColor().getAlpha() / 255f);
				}

				//Triangle 1

				vertexBuffer.add(rect.getSprite().getX() + rect.getSprite().getWidth());
				vertexBuffer.add(rect.getSprite().getY());
				vertexBuffer.add(0f);
				vertexBuffer.add(1f);
				textureBuffer.add(rect.getSource().getX() + rect.getSource().getWidth());
				textureBuffer.add(rect.getSource().getY() + rect.getSource().getHeight());

				vertexBuffer.add(rect.getSprite().getX());
				vertexBuffer.add(rect.getSprite().getY());
				vertexBuffer.add(0f);
				vertexBuffer.add(1f);
				textureBuffer.add(rect.getSource().getX());
				textureBuffer.add(rect.getSource().getY() + rect.getSource().getHeight());

				vertexBuffer.add(rect.getSprite().getX());
				vertexBuffer.add(rect.getSprite().getY() + rect.getSprite().getHeight());
				vertexBuffer.add(0f);
				vertexBuffer.add(1f);
				textureBuffer.add(rect.getSource().getX());
				textureBuffer.add(rect.getSource().getY());

				//Triangle 2

				vertexBuffer.add(rect.getSprite().getX() + rect.getSprite().getWidth());
				vertexBuffer.add(rect.getSprite().getY() + rect.getSprite().getHeight());
				vertexBuffer.add(0f);
				vertexBuffer.add(1f);
				textureBuffer.add(rect.getSource().getX() + rect.getSource().getWidth());
				textureBuffer.add(rect.getSource().getY());

				vertexBuffer.add(rect.getSprite().getX() + rect.getSprite().getWidth());
				vertexBuffer.add(rect.getSprite().getY());
				vertexBuffer.add(0f);
				vertexBuffer.add(1f);
				textureBuffer.add(rect.getSource().getX() + rect.getSource().getWidth());
				textureBuffer.add(rect.getSource().getY() + rect.getSource().getHeight());

				vertexBuffer.add(rect.getSprite().getX());
				vertexBuffer.add(rect.getSprite().getY() + rect.getSprite().getHeight());
				vertexBuffer.add(0f);
				vertexBuffer.add(1f);
				textureBuffer.add(rect.getSource().getX());
				textureBuffer.add(rect.getSource().getY());
			}
		}

		// NEVER DO THAT
		if (renderer == null) {
			renderer = (BatchVertexRenderer) BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);
		}

		renderer.setBufferContainer(container);
		renderer.flush(true);
	}

	public void render(Matrix4 model) {
		if (renderer == null) {
			return;
		}

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		renderer.preDraw();

		int position = 0;
		for (RenderPartPack pack : sprites) {
			pack.getRenderMaterial().getShader().setUniform("View", this.view);
			pack.getRenderMaterial().getShader().setUniform("Projection", this.projection);
			pack.getRenderMaterial().getShader().setUniform("Model", model);

			SnapshotRender snapshotRender = new SnapshotRender(pack.getRenderMaterial());
			pack.getRenderMaterial().preRender(snapshotRender);
			renderer.draw(pack.getRenderMaterial(), position, pack.getSize() * 6);
			pack.getRenderMaterial().postRender(snapshotRender);

			position += pack.getSize() * 6;
		}

		renderer.postDraw();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
}
