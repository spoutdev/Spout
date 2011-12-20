package org.getspout.server.block;

public class SpoutWorkbench extends SpoutContainerBlock {

	public SpoutWorkbench(SpoutBlock block) {
		super(block, null);
	}

	@Override
	public SpoutWorkbench shallowClone() {
		SpoutWorkbench result = new SpoutWorkbench(getBlock());
		result.inventory = inventory;
		return result;
	}
}
