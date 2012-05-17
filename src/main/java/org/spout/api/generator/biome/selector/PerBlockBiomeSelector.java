package org.spout.api.generator.biome.selector;

import org.spout.api.generator.biome.Biome;
import org.spout.api.generator.biome.BiomeSelector;

public class PerBlockBiomeSelector extends BiomeSelector {
	private final Biome biome;

	public PerBlockBiomeSelector(Biome biome) {
		this.biome = biome;
	}

	@Override
	public Biome pickBiome(int x, int y, int z, long seed) {
		return biome;
	}
}
