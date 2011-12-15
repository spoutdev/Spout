package org.getspout.server.io.entity.monsters;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.entity.monsters.GlowSilverfish;
import org.getspout.server.util.nbt.CompoundTag;

public class SilverfishStore extends MonsterStore<GlowSilverfish> {

    public SilverfishStore() {
        super(GlowSilverfish.class, "Silverfish");
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public SilverfishStore(Class clazz, String id) {
        super(clazz, id);
    }
    
    public GlowSilverfish load(SpoutServer server, SpoutWorld world, CompoundTag compound) {
        
        GlowSilverfish entity = new GlowSilverfish(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}