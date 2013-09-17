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
package org.spout.engine;

import java.awt.Canvas;
import org.spout.api.gui.FullScreen;
import org.spout.engine.gui.SpoutScreenStack;
import org.spout.math.vector.Vector2;
import org.spout.renderer.data.Color;
import org.spout.renderer.gl.GLFactory;
import org.spout.renderer.gl.Renderer;
import org.spout.renderer.util.Rectangle;

public class SpoutRenderer {
	private final SpoutClient client;
	private final GLFactory gl;
	private Canvas parent;
	private SpoutScreenStack screenStack;
	private final Vector2 resolution;
	private final float aspectRatio;
	private Renderer renderer;

	public SpoutRenderer(SpoutClient client, GLFactory gl, Vector2 resolution) {
		this.client = client;
		this.gl = gl;
		this.resolution = resolution;
		this.aspectRatio = resolution.getX() / resolution.getY();
		final FullScreen mainScreen = new FullScreen();
		mainScreen.setTakesInput(false);
		this.screenStack = new SpoutScreenStack(mainScreen);
	}

	public void init() {
		renderer = gl.createRenderer();
		renderer.setWindowTitle(client.getName());
		renderer.setViewPort(new Rectangle(0, 0, resolution.getFloorX(), resolution.getFloorY()));
		renderer.create();
		renderer.setClearColor(Color.DARK_GRAY);
	}

	public void dispose() {
		renderer.destroy();
	}

	public void render(float dt) {
		renderer.render();
	}

	public SpoutScreenStack getScreenStack() {
		return screenStack;
	}

	public Vector2 getResolution() {
		return resolution;
	}

	public float getAspectRatio() {
		return aspectRatio;
	}

	public Canvas getParent() {
		return parent;
	}

	public void setParent(Canvas parent) {
		this.parent = parent;
	}

	public GLFactory getGL() {
		return gl;
	}
}
