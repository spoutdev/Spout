package org.spout.engine.batcher;

import org.spout.engine.world.SpoutChunkSnapshot;

public class ChunkRendererHelper {
	public static void BuildChunk(PrimitiveBatch batch, SpoutChunkSnapshot chunk) {
		if (!chunk.isRenderDirty()) {
			return; //Rendered the chunk, no need to build it again.
		}
	}
}
