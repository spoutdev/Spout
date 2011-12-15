package org.getspout.server.io.entity.animals;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.animals.SpoutCow;
import org.getspout.server.util.nbt.CompoundTag;

public class CowStore extends AnimalsStore<SpoutCow> {

    public CowStore() {
        super(SpoutCow.class, "Cow");
    }

    public SpoutCow load(SpoutServer server, SpoutWorld world, CompoundTag compound) {

        SpoutCow entity = new SpoutCow(server, world);

        super.load(entity, compound);

        return entity;
    }

}
