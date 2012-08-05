/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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

import org.spout.api.Engine;
import org.spout.engine.util.thread.AsyncExecutor;
import org.spout.engine.util.thread.AsyncManager;

/**
 * This class just passes through the period method calls to the SpoutRegion
 */
public class SpoutRegionManager extends AsyncManager {
	private final SpoutRegion parent;

	public SpoutRegionManager(SpoutRegion parent, int maxStage, AsyncExecutor executor, Engine server) {
		super(maxStage, executor, server);
		this.parent = parent;
	}

	public SpoutRegion getParent() {
		return parent;
	}

	@Override
	public void copySnapshotRun() throws InterruptedException {
		parent.copySnapshotRun();
	}

	@Override
	public void startTickRun(int stage, long delta) throws InterruptedException {
		parent.startTickRun(stage, delta);
	}

	@Override
	public void haltRun() throws InterruptedException {
		parent.haltRun();
	}

	@Override
	public void finalizeRun() throws InterruptedException {
		parent.finalizeRun();
	}

	@Override
	public void preSnapshotRun() throws InterruptedException {
		parent.preSnapshotRun();
	}

	@Override
	public void runPhysics(int sequence) throws InterruptedException {
		parent.runPhysics(sequence);
	}

	@Override
	public long getFirstDynamicUpdateTime() {
		return parent.getFirstDynamicUpdateTime();
	}

	@Override
	public void runDynamicUpdates(long time, int sequence) throws InterruptedException {
		parent.runDynamicUpdates(time, sequence);
	}
	
	@Override
	public int getSequence() {
		return parent.getSequence();
	}
}
