package org.getspout.server.entity;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;

public interface EntityFactory<T extends SpoutEntity> {
	public T createEntity(SpoutServer server, SpoutWorld world);
}
