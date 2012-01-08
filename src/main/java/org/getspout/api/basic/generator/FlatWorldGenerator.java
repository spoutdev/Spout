package org.getspout.api.basic.generator;

import org.getspout.api.basic.blocks.SpoutBlocks;
import org.getspout.api.generator.Populator;
import org.getspout.api.generator.WorldGenerator;
import org.getspout.api.util.cuboid.CuboidShortBuffer;

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
