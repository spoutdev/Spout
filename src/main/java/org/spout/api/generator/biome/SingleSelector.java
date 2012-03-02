package org.spout.api.generator.biome;

/**
 * @author zml2008
 */
public class SingleSelector extends BiomeSelector {
	private final int index;

	public SingleSelector(int index) {
		this.index = index;
	}

	@Override
	public int pickBiome(int x, int y, int z, long seed) {
		return index;
	}
}
