/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.gui;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.spout.api.ClientOnly;

public class GenericBitmap extends GenericTexture implements Bitmap {

	/** Current version for serialisation and packet handling.*/
	private static final long serialVersionUID = 0L;
	private final byte[] bitmap;
	private final ByteBuffer buffer;
	private final int bitmapWidth;
	private final int bitmapHeight;

	public GenericBitmap(int width, int height) {
		bitmapWidth = width;
		bitmapHeight = height;
		bitmap = new byte[width * height * 4];
		buffer = ByteBuffer.wrap(bitmap);
		super.setWidth(width);
		super.setHeight(height);
	}

	public byte[] getRawBitmap() {
		return bitmap;
	}

	public int getRawWidth() {
		return bitmapWidth;
	}

	public int getRawHeight() {
		return bitmapHeight;
	}

	public ByteBuffer getByteBuffer() {
		return buffer;
	}

	public IntBuffer getBuffer() {
		return buffer.asIntBuffer();
	}

	@Override
	@ClientOnly
	public void render() {
//		Spoutcraft.getClient().getRenderDelegate().render(this);
	}
}
