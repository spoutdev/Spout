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

import gnu.trove.list.array.TFloatArrayList;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import org.spout.api.gui.render.RenderPart;
import org.spout.api.math.Matrix;
import org.spout.api.math.MatrixMath;
import org.spout.api.render.BufferContainer;
import org.spout.api.render.RenderMode;
import org.spout.api.render.effect.SnapshotRender;

import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.renderer.vertexformat.vertexattributes.VertexAttributes;

public class SpriteBatch {
	private BatchVertexRenderer renderer;
	private final ArrayList<RenderPart> sprites = new ArrayList<RenderPart>();
	private final Matrix view;
	private final Matrix projection;

	public static SpriteBatch createSpriteBatch(RenderMode renderMode) {
		if (renderMode == RenderMode.GL11) {
			return new GL11SpriteBatch();
		}
		return new SpriteBatch();
	}

	public SpriteBatch() {
		this.projection = MatrixMath.createIdentity();
		this.view = MatrixMath.createIdentity();
	}

	public void begin() {
		sprites.clear();
	}
	
	public void flush(RenderPart part) {
		List<RenderPart> list = new ArrayList<RenderPart>();
		list.add(part);
		flush(list);
	}

	public void flush(List<RenderPart> parts) {
		if (parts.isEmpty()) {
			return;
		}
		sprites.clear();
		sprites.addAll(parts);
		
		BufferContainer container = new BufferContainer();

		container.element = sprites.size() * 3 * 2;

		TFloatArrayList vertexBuffer = (TFloatArrayList) container.getBuffers().get(VertexAttributes.Position.getLayout());
		TFloatArrayList colorBuffer = (TFloatArrayList) container.getBuffers().get(VertexAttributes.Color.getLayout());
		TFloatArrayList textureBuffer = (TFloatArrayList) container.getBuffers().get(VertexAttributes.Texture0.getLayout());

		if (vertexBuffer == null) {
			vertexBuffer = new TFloatArrayList(sprites.size() * 4 * 4);
			container.setBuffers(VertexAttributes.Position.getLayout(), vertexBuffer);
		}

		if (colorBuffer == null) {
			colorBuffer = new TFloatArrayList(sprites.size() * 4 * 4);
			container.setBuffers(VertexAttributes.Color.getLayout(), colorBuffer);
		}

		if (textureBuffer == null) {
			textureBuffer = new TFloatArrayList(sprites.size() * 4 * 2);
			container.setBuffers(VertexAttributes.Texture0.getLayout(), textureBuffer);
		}

		for (int i = 0; i < sprites.size(); i++) {
			RenderPart rect = sprites.get(i);

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

		if (renderer!=null) {
			((BatchVertexRenderer) renderer).release();
		}
		renderer = (BatchVertexRenderer) BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);

		renderer.setBufferContainer(container);
		renderer.flush(true);
	}
	
	public void render() {
		if (renderer==null) {
			return;
		}

		GL11.glDisable(GL11.GL_DEPTH_TEST);
		renderer.preDraw();
		
		for (int i = 0; i < sprites.size(); i++) {
			RenderPart rect = sprites.get(i);

			rect.getRenderMaterial().getShader().setUniform("View", this.view);
			rect.getRenderMaterial().getShader().setUniform("Projection", this.projection);
			rect.getRenderMaterial().getShader().setUniform("Model", this.view); //View is always an identity matrix.

			SnapshotRender snapshotRender = new SnapshotRender(rect.getRenderMaterial());
			rect.getRenderMaterial().preRender(snapshotRender);
			renderer.draw(rect.getRenderMaterial(), (i * 6), 6);
			rect.getRenderMaterial().postRender(snapshotRender);
		}
		
		renderer.postDraw();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
}
