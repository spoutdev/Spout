package org.getspout.server.io.entity.animals;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.animals.SpoutChicken;
import org.getspout.server.util.nbt.CompoundTag;

public class ChickenStore extends AnimalsStore<SpoutChicken> {

    public ChickenStore() {
        super(SpoutChicken.class, "Chicken");
    }
    
    public SpoutChicken load(SpoutServer server, SpoutWorld world, CompoundTag compound) {
        
        SpoutChicken entity = new SpoutChicken(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}
