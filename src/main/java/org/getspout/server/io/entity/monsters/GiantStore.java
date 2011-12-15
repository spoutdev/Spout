package org.getspout.server.io.entity.monsters;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.monsters.SpoutGiant;
import org.getspout.server.util.nbt.CompoundTag;

public class GiantStore extends ZombieStore {

    public GiantStore() {
        super(SpoutGiant.class, "Giant");
    }
    
    public GiantStore(Class<?> clazz, String id) {
        super(clazz, id);
    }
    
    public SpoutGiant load(SpoutServer server, SpoutWorld world, CompoundTag compound) {
        
        SpoutGiant entity = new SpoutGiant(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}