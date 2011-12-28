package org.getspout.server.entity;

import org.getspout.unchecked.server.SpoutServer;
import org.getspout.unchecked.server.SpoutWorld;

public interface EntityFactory {
	public SpoutEntity createEntity(SpoutServer server, SpoutWorld world);
}
