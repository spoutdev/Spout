package org.spout.api.basic.generator;

import org.spout.api.basic.blocks.SpoutBlocks;
import org.spout.api.generator.Populator;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.util.cuboid.CuboidShortBuffer;

public class FlatWorldGenerator implements WorldGenerator {

	public void generate(CuboidShortBuffer blockData, int chunkX, int chunkY, int chunkZ) {
		if(chunkY < 0){
			blockData.flood(SpoutBlocks.unbreakable.getId());
		}

	}

	public Populator[] getPopulators() {
		return null;
	}

}
