package org.getspout.server.entity;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;

public interface EntityFactory {
	public SpoutEntity createEntity(SpoutServer server, SpoutWorld world);
}
