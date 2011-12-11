package net.glowstone.io.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.monsters.GlowSilverfish;
import net.glowstone.util.nbt.CompoundTag;

public class SilverfishStore extends MonsterStore<GlowSilverfish> {

    public SilverfishStore() {
        super(GlowSilverfish.class, "Silverfish");
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public SilverfishStore(Class clazz, String id) {
        super(clazz, id);
    }
    
    public GlowSilverfish load(GlowServer server, GlowWorld world, CompoundTag compound) {
        
        GlowSilverfish entity = new GlowSilverfish(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}