package net.glowstone.entity.ai;

import net.glowstone.entity.GlowEntity;
import org.bukkit.Location;

import java.util.Random;

public class WanderAi extends TimedAi<GlowEntity> {
    private static final int WANDER_FREQ = 5;
    private static final Random rand = new Random();
    
    public WanderAi(GlowEntity entity) {
        super(entity, WANDER_FREQ);
    }

    @Override
    public void runAi() {
        if (rand.nextDouble() > 0.7) return;
        Location toLoc = entity.getLocation().clone();
        toLoc.add(rand.nextDouble() * 4, rand.nextDouble() * 4, rand.nextDouble() * 4);
        if (!entity.getWorld().getBlockAt(toLoc).isEmpty()) {
            toLoc.add(0, 1, 0);
        }
        if (entity.getWorld().getBlockAt(toLoc).isEmpty()) {
            toLoc.setX(entity.getLocation().getX());
            toLoc.setZ(entity.getLocation().getZ());
        }
        entity.setRawLocation(toLoc);
    }
}
