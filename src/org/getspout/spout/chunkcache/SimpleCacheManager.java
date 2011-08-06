package org.getspout.spout.chunkcache;

import org.getspout.spoutapi.chunkcache.CacheManager;

public class SimpleCacheManager implements CacheManager {

	@Override
	public void handle(int id, boolean add, long[] hashes) {
		ChunkCache.addToHashUpdateQueue(id, add, hashes);
	}

}
