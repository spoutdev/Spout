package org.spout.api.geo.cuboid;

public enum UpdateOption {
	/**
	 * Updates the block itself
	 */
	SELF(true, false),
	/**
	 * Updates the blocks surrounding this block
	 */
	AROUND(false, true),
	/**
	 * Updates the block and the blocks surrounding the blocks
	 */
	SELF_AROUND(true, true);

	private final boolean self;
	private final boolean around;

	private UpdateOption(boolean self, boolean around) {
		this.self = self;
		this.around = around;
	}
	/**
	 * Test if chunk/region should be loaded if not currently loaded
	 * @return true if yes, false if no
	 */
	public final boolean updateSelf() {
		return self;
	}
	/**
	 * Test if chunk/region should be generated if it does not exist
	 * @return true if yes, false if no
	 */
	public final boolean updateAround() {
		return around;
	}

}
