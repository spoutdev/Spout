package org.getspout.event.bukkitcontrib;

import org.bukkit.event.Event;

public class ServerTickEvent extends Event{
	protected long lastTick;
	protected long createdTime = System.currentTimeMillis();
	private static boolean first = true;
	private static long lastTickTime = System.currentTimeMillis();
	public ServerTickEvent() {
		super("ServerTickEvent");
		if (!first) {
			lastTick = lastTickTime;
		}
		else {
			lastTick = createdTime - 1000;
			first = false;
		}
		lastTickTime = createdTime;
	}

	/**
	 * Returns the milliseconds since the last server tick event was created
	 * Ideally, it should be exactly 50 milliseconds, but because of server lag, it may be more
	 * @return milliseconds since last server tick
	 */
	public Long getMillisLastTick() {
		return Math.abs(createdTime - lastTick);
	}
	
	/**
	 * Returns the seconds since the last server tick event was created
	 * Ideally, it should be exactly 0.05 seconds, but because of server lag, it may be more
	 * @return
	 */
	public double getSecondsLastTick() {
		return ((double)Math.abs(createdTime - lastTick)) / 1000;
	}

}
