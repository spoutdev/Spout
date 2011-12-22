package org.getspout.api.protocol.notch.msg;

import org.getspout.api.protocol.Message;

public final class HealthMessage extends Message {
	private final int health, food;
	private final float foodSaturation;

	public HealthMessage(int health, int food, float foodSaturation) {
		this.health = health;
		this.food = food;
		this.foodSaturation = foodSaturation;
	}

	public int getHealth() {
		return health;
	}

	public int getFood() {
		return food;
	}

	public float getFoodSaturation() {
		return foodSaturation;
	}

	@Override
	public String toString() {
		return "HealthMessage{health=" + health + ",food=" + food + ",foodSaturation=" + foodSaturation + "}";
	}
}
