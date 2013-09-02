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

import org.spout.engine.gui.SpoutScreenStack;
import org.spout.engine.renderer.EntityRenderer;
import org.spout.engine.renderer.WorldRenderer;
import org.spout.math.vector.Vector2;

public class SpoutRenderer {
	private final SpoutClient client;
	//private DebugScreen debugScreen;
	//private SpoutScreenStack screenStack;
	private boolean showDebugInfo = true;
	private Vector2 resolution;
	private float aspectRatio;
	private EntityRenderer entityRenderer;
	private WorldRenderer worldRenderer;

	public SpoutRenderer(SpoutClient client, Vector2 resolution) {
		this.client = client;
		this.resolution = resolution;
		this.aspectRatio = resolution.getX() / resolution.getY();
		//FullScreen mainScreen = new FullScreen();
		//mainScreen.setTakesInput(false);
		//this.screenStack = new SpoutScreenStack(mainScreen);
		//this.debugScreen = (DebugScreen) screenStack.getDebugHud();
		this.entityRenderer = new EntityRenderer();
		this.worldRenderer = new WorldRenderer();
	}

	public void initRenderer(Canvas parent) {

	}

	public void render(float dt) {
	}

	public WorldRenderer getWorldRenderer() {
		return worldRenderer;
	}

	public EntityRenderer getEntityRenderer() {
		return entityRenderer;
	}

	public SpoutScreenStack getScreenStack() {
		return null;
		//return screenStack;
	}

	public Vector2 getResolution() {
		return resolution;
	}

	public float getAspectRatio() {
		return aspectRatio;
	}
}
