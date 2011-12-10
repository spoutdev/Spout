package net.glowstone.entity.vehicles;

import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowEntity;
import net.glowstone.msg.Message;
import net.glowstone.msg.SpawnVehicleMessage;
import net.glowstone.util.Position;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleMoveEvent;


public abstract class GlowVehicle extends GlowEntity implements Vehicle {
    private final int type;
    
    protected double maxSpeed;
    
    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param world The world.
     */
    public GlowVehicle(GlowServer server, GlowWorld world, int type) {
        super(server, world);
        this.type = type;
    }

    @Override
    public Message createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);
        return new SpawnVehicleMessage(id, type, x, y, z);
    }
    
    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double speed) {
        maxSpeed = speed;
    }
}
