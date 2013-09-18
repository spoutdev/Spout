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

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import org.spout.api.Spout;
import org.spout.api.render.Texture;
import org.spout.engine.SpoutClient;
import org.spout.math.vector.Vector2;
import org.spout.renderer.GLVersioned.GLVersion;
import org.spout.renderer.data.VertexAttribute.DataType;
import org.spout.renderer.gl.Texture.CompareMode;
import org.spout.renderer.gl.Texture.FilterMode;
import org.spout.renderer.gl.Texture.Format;
import org.spout.renderer.gl.Texture.InternalFormat;
import org.spout.renderer.gl.Texture.WrapMode;
import org.spout.renderer.util.CausticUtil;
import org.spout.renderer.util.Rectangle;

public class SpoutTexture extends Texture {
	org.spout.renderer.gl.Texture texture;
	
	public SpoutTexture(int[] data, Vector2 edge) {
		super(data, edge);
	}
	
	public SpoutTexture(BufferedImage baseImage) {
		super(baseImage.getRGB(0, 0, baseImage.getWidth(), baseImage.getHeight(), null, 0, baseImage.getWidth()), new Vector2(baseImage.getWidth(), baseImage.getHeight()));
	}
	
	public void bind(int i) {
		texture.bind(i);
	}
	
	public void checkCreated() {
		texture.checkCreated();
	}
	
	public void create() {
		ByteBuffer data;
		Rectangle size = new Rectangle();
		texture = ((SpoutClient) Spout.getEngine()).getRenderer().getGL().createTexture();
		data = CausticUtil.getImageData(this.getImage(), Format.RED, getHeight(), getWidth());
		data.flip();
		texture.setImageData(data, getWidth(), getHeight());
		texture.create();
	}
	
	public void destroy() {
		texture.destroy();
	}
	
	public GLVersion getGLVersion() {
		return texture.getGLVersion();
	}
	
	public int getID() {
		return texture.getID();
	}
	
	public boolean isCreated() {
		return texture.isCreated();
	}
	
	public void setAnisotropicFiltering(float f) {
		texture.setAnisotropicFiltering(f);
	}
	
	public void setCompareMode(CompareMode mode) {
		texture.setCompareMode(mode);
	}
	
	public void setComponentType(DataType type) {
		texture.setComponentType(type);
	}
	
	public void setFormat(Format format) {
		texture.setFormat(format);
	}
	
	public void setImageData(ByteBuffer buffer, int x, int y) {
		texture.setImageData(buffer, x, y);
	}
	
	public void setInternalFormat(InternalFormat format) {
		texture.setInternalFormat(format);
	}
	
	public void setMagFilter(FilterMode mode) {
		texture.setMagFilter(mode);
	}
	public void setMinFilter(FilterMode mode) {
		texture.setMinFilter(mode);
	}
	
	public void setWrapS(WrapMode mode) {
		texture.setWrapS(mode);
	}
	
	public void setWrapT(WrapMode mode) {
		texture.setWrapT(mode);
	}
	public void unbind() {
		texture.unbind();
	}
	
	@Override
	public Texture subTexture(int x, int y, int w, int h) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
