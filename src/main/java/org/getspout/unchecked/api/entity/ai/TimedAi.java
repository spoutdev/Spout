package org.getspout.unchecked.api.entity.ai;

import org.getspout.unchecked.api.entity.Entity;

public abstract class TimedAi<T extends Entity> extends EntityAi<T> {
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
