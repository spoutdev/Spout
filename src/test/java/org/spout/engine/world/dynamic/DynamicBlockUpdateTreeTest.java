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
package org.spout.engine.world.dynamic;

import org.junit.Test;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.scheduler.TickStage;
import org.spout.engine.faker.ChunkFaker;
import org.spout.engine.faker.RegionFaker;
import org.spout.engine.world.SpoutRegion;


public class DynamicBlockUpdateTreeTest {
	
	@Test
	public void test() throws Exception {
		
		SpoutRegion region = RegionFaker.getSpoutRegion(0, 0, 0);
		
		Chunk chunk = ChunkFaker.getChunk(1, 1, 1);

		DynamicBlockUpdateTree tree = new DynamicBlockUpdateTree(Thread.currentThread(), region);

		TickStage.setStage(TickStage.DYNAMIC_BLOCKS);
		
		tree.setRegionThread(Thread.currentThread());
		
		tree.resetBlockUpdates(20, 20, 20);
		
		tree.resetBlockUpdates(chunk);
		
		tree.commitAsyncPending(1000);
		
	}
	
}
