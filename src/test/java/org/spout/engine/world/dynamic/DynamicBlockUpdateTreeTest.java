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
