package org.spout.engine.filesystem;

import java.util.ArrayList;
import java.util.List;

import org.spout.engine.entity.SpoutEntity;
import org.spout.engine.world.dynamic.DynamicBlockUpdate;

public class ChunkDataForRegion {
	public final List<SpoutEntity> loadedEntities = new ArrayList<SpoutEntity>(10);
	public final List<DynamicBlockUpdate> loadedUpdates = new ArrayList<DynamicBlockUpdate>(10);
}
