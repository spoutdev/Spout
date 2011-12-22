package org.getspout.unchecked.server.entity;

import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;

public interface EntityFactory<T extends SpoutEntity> {
	public T createEntity(SpoutServer server, SpoutWorld world);
}
