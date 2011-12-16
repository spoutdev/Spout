package org.getspout.server.block;

import org.getspout.server.inventory.WorkbenchInventory;

public class SpoutWorkbench extends SpoutContainerBlock {

    public SpoutWorkbench(SpoutBlock block) {
        super(block, new WorkbenchInventory());
    }

	public SpoutWorkbench shallowClone() {
		SpoutWorkbench result = new SpoutWorkbench(getBlock());
		result.inventory = inventory;
		return result;
	}
}
