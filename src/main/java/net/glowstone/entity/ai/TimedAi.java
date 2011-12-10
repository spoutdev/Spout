package net.glowstone.entity.ai;

import net.glowstone.entity.GlowEntity;

public abstract class TimedAi<T extends GlowEntity> extends EntityAi<T> {
    private int delayCounter = 0;
    private int tickDelay = 1;

    public TimedAi(T entity, int tickDelay) {
        super(entity);
        this.tickDelay = tickDelay;
    }

    public boolean shouldRun() {
        if (++delayCounter % tickDelay == 0) {
            delayCounter = 0;
            return true;
        }
        return false;
    }
}
