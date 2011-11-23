package net.glowstone.entity.objects;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;

public class GlowFireball extends GlowProjectile implements Fireball {

    private Vector direction;

    private float radius;

    private boolean incendiary;

    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param server The server.
     * @param world  The world.
     */
    public GlowFireball(GlowServer server, GlowWorld world, Vector direction) {
        super(server, world, 63);
        this.direction = direction;
    }

    public void setDirection(Vector direction) {
        this.direction = direction;
    }

    public Vector getDirection() {
        return direction;
    }

    public void setYield(float yield) {
        this.radius = yield;
    }

    public float getYield() {
        return radius;
    }

    public void setIsIncendiary(boolean isIncendiary) {
        this.incendiary = isIncendiary;
    }

    public boolean isIncendiary() {
        return incendiary;
    }
}
