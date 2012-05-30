/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev License Version 1.
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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.engine.world;

import org.spout.api.Source;
import org.spout.engine.SpoutConfiguration;

public class SpoutWorldLighting extends Thread implements Source {

	public final SpoutWorldLightingModel skyLight;
	public final SpoutWorldLightingModel blockLight;
	private final SpoutWorld world;
	private boolean running = false;

	public boolean isRunning() {
		return this.running;
	}

	public SpoutWorldLighting(SpoutWorld world) {
		super("Lighting thread for world " + world.getName());
		this.world = world;
		this.skyLight = new SpoutWorldLightingModel(this, true);
		this.blockLight = new SpoutWorldLightingModel(this, false);
	}

	public void abort() {
		this.running = false;
	}

	public SpoutWorld getWorld() {
		return this.world;
	}

	@Override
	public void run() {
		this.running = SpoutConfiguration.LIGHTING_ENABLED.getBoolean();

		while (this.running) {
			this.skyLight.resolve();
			this.blockLight.resolve();

			try {
				Thread.sleep(50);
			} catch (InterruptedException ex) {}
		}
	}
}
