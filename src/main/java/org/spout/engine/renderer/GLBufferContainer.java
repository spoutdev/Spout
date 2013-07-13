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
package org.spout.engine.renderer;

import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;

public class GLBufferContainer {

	private int[] verticeIndex;
	private final Map<Integer, Buffer> buffers = new HashMap<>();
	public int element = 0;

	public Map<Integer, Buffer> getBuffers() {
		return buffers;
	}

	public void setBuffers(int layout, Buffer buffer) {
		buffers.put(layout, buffer);
	}

	public void setVerticeIndex(int[] verticeIndex) {
		this.verticeIndex = verticeIndex;
	}

	public int[] getVerticeIndex() {
		return verticeIndex;
	}
}
