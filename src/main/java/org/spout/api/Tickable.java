package org.spout.api;

public interface Tickable {
	/**
	 * Called each simulation tick.
	 * 1       tick  = 1/20 second
	 * 20      ticks = 1 second
	 * 1200    ticks = 1 minute
	 * 72000   ticks = 1 hour
	 * 1728000 ticks = 1 day
	 * 
	 * @param dt time since the last tick in seconds
	 */
	public void onTick(float dt);
}
