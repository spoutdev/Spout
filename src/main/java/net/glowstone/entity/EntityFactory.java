package net.glowstone.entity;


import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;

public interface EntityFactory<T extends GlowEntity> {

    public T createEntity(GlowServer server, GlowWorld world);
}
