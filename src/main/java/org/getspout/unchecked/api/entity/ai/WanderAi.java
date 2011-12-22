package org.getspout.unchecked.api.entity.ai;

import java.util.Random;

import org.getspout.api.geo.discrete.Pointm;
import org.getspout.api.math.Vector3;
import org.getspout.unchecked.api.entity.Entity;

public class WanderAi extends TimedAi<Entity> {
	private static final int WANDER_FREQ = 5;
	private static final Random rand = new Random();

	public WanderAi(Entity entity) {
		super(entity, WANDER_FREQ);
	}

	@Override
	public void runAi() {
		if (rand.nextDouble() > 0.7) return;
		Pointm toLoc = new Pointm(entity.getPosition());
		toLoc.add(new Vector3(rand.nextFloat() * 4, rand.nextFloat() * 4, rand.nextFloat() * 4));
		if (entity.getWorld().getBlock(toLoc) != null) {
			toLoc.add(new Vector3(0, 1, 0));
		}
		if (entity.getWorld().getBlock(toLoc) == null) {
			toLoc.setX(entity.getPosition().getX());
			toLoc.setZ(entity.getPosition().getZ());
		}
		//entity.setRawLocation(toLoc);
	}
}
