package org.getspout.server.io.entity.monsters;

import org.getspout.server.entity.SpoutCreature;
import org.getspout.server.io.entity.CreatureStore;

public abstract class MonsterStore<T extends SpoutCreature> extends CreatureStore<T> {
	public MonsterStore(Class<T> clazz, String id) {
		super(clazz, id);
	}
}
