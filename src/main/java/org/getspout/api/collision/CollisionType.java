package org.getspout.api.collision;

public enum CollisionType {
	/**
	 * Indicates that one object wholley contains another object
	 */
	Contains,
	/**
	 * Indicates that neither object overlap
	 */
	Disjoint,
	/**
	 * Indicates that the objects partially overlap
	 */
	Intersect,
}
