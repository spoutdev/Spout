package net.glowstone.io.entity.animals;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.animals.GlowCow;
import net.glowstone.util.nbt.CompoundTag;

public class CowStore extends AnimalsStore<GlowCow> {

    public CowStore() {
        super(GlowCow.class, "Cow");
    }

    public GlowCow load(GlowServer server, GlowWorld world, CompoundTag compound) {

        GlowCow entity = new GlowCow(server, world);

        super.load(entity, compound);

        return entity;
    }

}
