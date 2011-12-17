package org.getspout.server.io.blockstate;

import org.getspout.server.block.SpoutWorkbench;

public class WorkbenchStore extends BlockStateStore<SpoutWorkbench> {
	public WorkbenchStore() {
		super(SpoutWorkbench.class, "Crafting");
	}
	
	// Workbench doesn't persist
}
