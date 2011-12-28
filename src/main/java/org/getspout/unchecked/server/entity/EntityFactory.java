package org.getspout.unchecked.server.entity;

import org.getspout.server.entity.SpoutEntity;
import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;

public interface EntityFactory {
	public SpoutEntity createEntity(SpoutServer server, SpoutWorld world);
}
