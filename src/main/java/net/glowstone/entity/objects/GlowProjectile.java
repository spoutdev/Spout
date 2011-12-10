package net.glowstone.entity.objects;


import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.Damager;
import net.glowstone.entity.GlowEntity;
import net.glowstone.msg.Message;
import net.glowstone.msg.SpawnVehicleMessage;
import net.glowstone.util.Position;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class GlowProjectile extends GlowEntity implements Projectile {
    private final int type;

    private LivingEntity shooter;

    private boolean bounces;

    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param server The server.
     * @param world  The world.
     */
    public GlowProjectile(GlowServer server, GlowWorld world, int id) {
        super(server, world);
        this.type = id;
    }

    @Override
    public Message createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);
        return new SpawnVehicleMessage(id, type, x, y, z);
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        return null;
    }

    public LivingEntity getShooter() {
        return shooter;
    }

    public void setShooter(LivingEntity shooter) {
        this.shooter = shooter;
    }

    public boolean doesBounce() {
        return bounces;
    }

    public void setBounce(boolean doesBounce) {
        this.bounces = doesBounce;
    }
}
