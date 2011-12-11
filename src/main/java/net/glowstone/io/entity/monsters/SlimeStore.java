package net.glowstone.io.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.monsters.GlowSlime;
import net.glowstone.util.nbt.CompoundTag;

public class SlimeStore extends MonsterStore<GlowSlime> {

    public SlimeStore() {
        super(GlowSlime.class, "Slime");
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public SlimeStore(Class clazz, String id) {
        super(clazz, id);
    }
    
    public GlowSlime load(GlowServer server, GlowWorld world, CompoundTag compound) {
        
        GlowSlime entity = new GlowSlime(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}