package org.spout.api.scheduler;

import org.spout.api.geo.cuboid.Region;

public interface ParallelRunnable extends Runnable {
	
	/**
	 * Gets a new instance of the ParallelRunnable.  This method is only required if the Runnable is going to store Region specific state info.
	 * 
	 * @param r the Region that the new instance is responsible for
	 */
	public ParallelRunnable newInstance(Region r);
}
