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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import org.spout.api.chat.ChatArguments;
import org.spout.api.gui.Widget;
import org.spout.api.gui.component.LabelComponent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.math.Matrix;
import org.spout.api.math.MatrixMath;
import org.spout.api.math.Rectangle;
import org.spout.api.render.BufferContainer;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.RenderMode;
import org.spout.api.render.effect.SnapshotRender;

import org.spout.engine.gui.SpoutWidget;
import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.renderer.vertexformat.vertexattributes.VertexAttributes;
import org.spout.engine.resources.ClientFont;

public class SpriteBatch {
	BatchVertexRenderer renderer;
	ArrayList<RenderPart> sprites = new ArrayList<RenderPart>();
	Matrix view;
	Matrix projection;
	float screenWidth;
	float screenHeight;
	float aspectRatio;

	public static SpriteBatch createSpriteBatch(RenderMode renderMode, float screenW, float screenH) {
		if (renderMode == RenderMode.GL11) {
			return new GL11SpriteBatch(screenW, screenH);
		}
		return new SpriteBatch(screenW, screenH);
	}

	public SpriteBatch(float screenW, float screenH) {
		this.projection = MatrixMath.createIdentity();
		this.view = MatrixMath.createIdentity();
		this.screenWidth = screenW;
		this.screenHeight = screenH;
		this.aspectRatio = screenW / screenH;
	}

	public void begin() {
		sprites.clear();
	}

	public void render() {
		if (sprites.isEmpty()) {
			return;
		}

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

		renderer = (BatchVertexRenderer) BatchVertexRenderer.constructNewBatch(GL11.GL_TRIANGLES);

		renderer.setBufferContainer(container);
		renderer.flush(true);

		GL11.glDisable(GL11.GL_DEPTH_TEST);

		for (int i = 0; i < sprites.size(); i++) {
			RenderPart rect = sprites.get(i);

			rect.getRenderMaterial().getShader().setUniform("View", this.view);
			rect.getRenderMaterial().getShader().setUniform("Projection", this.projection);
			rect.getRenderMaterial().getShader().setUniform("Model", this.view); //View is always an identity matrix.

			SnapshotRender snapshotRender = new SnapshotRender(rect.getRenderMaterial());
			rect.getRenderMaterial().preRender(snapshotRender);
			renderer.render(rect.getRenderMaterial(), (i * 6), 6);
			rect.getRenderMaterial().postRender(snapshotRender);

		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		((BatchVertexRenderer) renderer).release();
	}

	public void drawText(ChatArguments text, ClientFont font, float x, float y, float size) {
		Widget tmp = new SpoutWidget();
		tmp.setGeometry(new Rectangle(x, y, 0, 0));
		LabelComponent txt = tmp.add(LabelComponent.class);

		txt.setFont(font);
		txt.setText(text);

		draw(tmp.getRenderParts());
	}

	public void draw(RenderPart part) {
		sprites.add(part);
	}

	public void draw(List<RenderPart> parts) {
		for (RenderPart part : parts) {
			draw(part);
		}
	}

	public void draw(RenderMaterial material, float x, float y, float w, float h) {
		draw(material, new Rectangle(0, 0, 1, 1), new Rectangle(x, y, w, h * aspectRatio), Color.white);
	}
	
	public void draw(RenderMaterial material, float uvx, float uvy, float uvw, float uvh, float x, float y, float w, float h) {
		draw(material, new Rectangle(uvx, uvy, uvw, uvh), new Rectangle(x, y, w, h * aspectRatio), Color.white);
	}

	public void draw(RenderMaterial material, Rectangle source, Rectangle destination, Color color) {
		RenderPart part = new RenderPart();
		part.setRenderMaterial(material);
		part.setSource(source);
		part.setSprite(destination);
		part.setColor(color);
		draw(part);
	}
}
