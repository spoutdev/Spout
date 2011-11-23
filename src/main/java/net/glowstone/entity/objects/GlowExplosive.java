package net.glowstone.entity.objects;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowEntity;
import net.glowstone.msg.Message;
import net.glowstone.msg.SpawnVehicleMessage;
import net.glowstone.util.Position;
import org.bukkit.entity.Explosive;

public abstract class GlowExplosive extends GlowEntity implements Explosive {

    private float radius;

    private boolean incendiary;

    private final int type;

    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param server The server.
     * @param world  The world.
     */
    public GlowExplosive(GlowServer server, GlowWorld world, int type) {
        super(server, world);
        this.type = type;
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

    @Override
    public Message createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);
        return new SpawnVehicleMessage(id, type, x, y, z);
    }
}
