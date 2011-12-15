package org.getspout.server.entity;


import org.bukkit.entity.LightningStrike;

import org.bukkit.inventory.ItemStack;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.msg.Message;
import org.getspout.server.msg.SpawnLightningStrikeMessage;
import org.getspout.server.util.Position;

import java.util.List;

/**
 * A GlowLightning strike is an entity produced during thunderstorms.
 */
public class SpoutLightningStrike extends SpoutWeather implements LightningStrike {

    /**
     * Whether the lightning strike is just for effect.
     */
    private boolean effect;
    
    /**
     * How long this lightning strike has to remain in the world.
     */
    private final int ticksToLive;

    public SpoutLightningStrike(SpoutServer server, SpoutWorld world, boolean effect) {
        super(server, world);
        this.effect = effect;
        this.ticksToLive = 30;
    }

    public boolean isEffect() {
        return effect;
    }

    @Override
    public void pulse() {
        super.pulse();
        if (getTicksLived() >= ticksToLive) {
            remove();
        }
    }

    @Override
    public Message createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);
        return new SpawnLightningStrikeMessage(id, x, y, z);
    }

    @Override
    public Message createUpdateMessage() {
        return null;
    }

    @Override
    public List<ItemStack> getLoot(Damager damager) {
        return null;
    }

}
