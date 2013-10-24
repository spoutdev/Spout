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
package org.spout.api.component.entity;

import org.spout.api.map.DefaultedKey;
import org.spout.api.map.DefaultedKeyImpl;
import org.spout.api.render.Font;
import org.spout.api.render.SpoutRenderMaterials;

import org.spout.math.vector.Vector3f;

public class TextModelComponent extends EntityComponent {
	private static final DefaultedKey<String> KEY_TEXT = new DefaultedKeyImpl<>("entity-text", "(your text here)");
	private static final DefaultedKey<Font> KEY_FONT = new DefaultedKeyImpl<>("font", SpoutRenderMaterials.DEFAULT_FONT);
	private Vector3f translation = Vector3f.ZERO;
	private boolean lookCamera = true;
	private boolean dirty = true;
	private float size = 1;

	public String getText() {
		return getData().get(KEY_TEXT);
	}

	public void setText(String text) {
		getData().put(KEY_TEXT, text);
		dirty = true;
	}

	public Font getFont() {
		return getData().get(KEY_FONT);
	}

	public void setFont(Font font) {
		getData().put(KEY_FONT, font);
		dirty = true;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
		dirty = true;
	}

	public Vector3f getTranslation() {
		return translation;
	}

	public void setTranslation(Vector3f translation) {
		this.translation = translation;
		dirty = true;
	}

	public boolean shouldLookCamera() {
		return lookCamera;
	}

	public void setShouldLookCamera(boolean yes) {
		this.lookCamera = yes;
	}
}
