package org.getspout.api.basic.generator;

import org.getspout.api.generator.Populator;
import org.getspout.api.generator.WorldGenerator;
import org.getspout.api.util.cuboid.CuboidShortBuffer;

public class EmptyWorldGenerator implements WorldGenerator {

	public void generate(CuboidShortBuffer blockData) {
		blockData.flood((short)0);
	}

	public Populator[] getPopulators() {
		// TODO Auto-generated method stub
		return null;
	}

}
