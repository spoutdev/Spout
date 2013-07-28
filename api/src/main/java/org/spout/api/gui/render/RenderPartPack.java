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
package org.spout.api.gui.render;

import java.util.ArrayList;
import java.util.List;

import org.spout.api.render.RenderMaterial;
import org.spout.api.render.SpoutRenderMaterials;

/**
 * A group of render parts that share the same RenderMaterial
 */
public class RenderPartPack implements Comparable<RenderPartPack> {
	private final List<RenderPart> parts = new ArrayList<>();
	private RenderMaterial material;
	private int zIndex = 0;

	public RenderPartPack() {
		this(SpoutRenderMaterials.GUI_COLOR);
	}

	public RenderPartPack(RenderMaterial material) {
		this.material = material;
	}

	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}

	public int getZIndex() {
		return zIndex;
	}

	public RenderMaterial getRenderMaterial() {
		return material;
	}

	public void setRenderMaterial(RenderMaterial material) {
		this.material = material;
	}

	public int add(RenderPart part) {
		// Last added on top
		return add(part, parts.size());
	}

	public int add(RenderPart part, int zIndex) {
		part.setZIndex(zIndex);
		parts.add(part);
		return parts.size() - 1;
	}

	public RenderPart get(int index) {
		return parts.get(index);
	}

	public List<RenderPart> getRenderParts() {
		return parts;
	}

	public int getSize() {
		return parts.size();
	}

	@Override
	public int compareTo(RenderPartPack arg0) {
		return arg0.getZIndex() - getZIndex();
	}
}
