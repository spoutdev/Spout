package net.glowstone.io.entity.monsters;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.monsters.GlowSkeleton;
import net.glowstone.util.nbt.CompoundTag;

public class SkeletonStore extends MonsterStore<GlowSkeleton> {

    public SkeletonStore() {
        super(GlowSkeleton.class, "Skeleton");
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public SkeletonStore(Class clazz, String id) {
        super(clazz, id);
    }
    
    public GlowSkeleton load(GlowServer server, GlowWorld world, CompoundTag compound) {
        
        GlowSkeleton entity = new GlowSkeleton(server, world);
        
        super.load(entity, compound);
        
        return entity;
    }
    
}