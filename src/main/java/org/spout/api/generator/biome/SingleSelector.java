package org.spout.api.generator.biome;

/**
 * @author zml2008
 */
public class SingleSelector extends BiomeSelector {
	private final Biome biome;

	public SingleSelector(Biome biome) {
		this.biome = biome;
	}

	@Override
	public Biome pickBiome(int x, int y, int z, long seed) {
		return biome;
	}
}
