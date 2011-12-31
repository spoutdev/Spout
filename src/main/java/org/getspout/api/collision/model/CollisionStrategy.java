package org.getspout.api.collision.model;

public enum CollisionStrategy {
	/**
	 * Indicates that the given volume is Soft, and that it counts as colliding with solid volumes, but doesn't hard resolve.  
	 * 
	 * Collision models should handle how softCollisions are handled.
	 */
	SOFT,
	/**
	 * Indicates that the marked Collision Volume is solid, and that other Solid volumes should 
	 * resolve their collisions
	 */
	SOLID,
	/**
	 * Indicates that the marked Collision Volume is not solid, and that it should not collide with Solid Volumes.  
	 */
	NOCOLLIDE;
}
