package org.getspout.api.collision.model;

public enum CollisionStratagy {
	/**
	 * Indicates that the marked Collision Volume is solid, and that other Solid volumes should 
	 * resolve their collisions
	 */
	SOLID,
	/**
	 * Indictates taht the marked Collision Volume is not solid, and that it should not collide with Solid Volumes.  
	 */
	NOCOLLIDE;
}
