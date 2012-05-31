package org.spout.engine.world.dynamic;

import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;

public class PointAlone extends Point implements BlockUpdateQueueElement {

	private static final long serialVersionUID = 1L;

	public PointAlone(World w, int x, int y, int z) {
		super(w, x, y, z);
	}

	@Override
	public long getUpdateTime() {
		return 0;
	}

	@Override
	public Object getHint() {
		return null;
	}
	
}
