package net.glowstone.io.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.monsters.GlowZombie;
import net.glowstone.util.nbt.CompoundTag;

public class ZombieStore extends MonsterStore<GlowZombie> {

    public ZombieStore() {
        super(GlowZombie.class, "Zombie");
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ZombieStore(Class clazz, String id) {
        super(clazz, id);
    }
    
    public GlowZombie load(GlowServer server, GlowWorld world, CompoundTag compound) {
        
        GlowZombie entity = new GlowZombie(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}