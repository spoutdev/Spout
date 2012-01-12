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

import java.nio.IntBuffer;

/**
 * You must specify the width and height of the background bitmap to be used in
 * the constructor!
 */
public interface Bitmap {

	/**
	 * Get the raw RGBA array used to create the texture.
	 * @return an array of width * height * 4 bytes
	 */
	public byte[] getRawBitmap();

	/**
	 * Get the raw width of the bitmap (the widget itself can be scaled to a
	 * different size like normal).
	 * @return bitmap size
	 */
	public int getRawWidth();

	/**
	 * Get the raw height of the bitmap (the widget itself can be scaled to a
	 * different size like normal).
	 * @return bitmap size
	 */
	public int getRawHeight();

	/**
	 * Get an IntBuffer to use for setting individual pixels in single calls.
	 * @return the buffer
	 */
	public IntBuffer getBuffer();
}
