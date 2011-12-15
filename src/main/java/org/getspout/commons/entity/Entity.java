/*
 * This file is part of SpoutcraftAPI (http://wiki.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.commons.entity;

import java.util.Set;
import java.util.UUID;

import org.getspout.commons.World;
import org.getspout.commons.metadata.Metadatable;
import org.getspout.commons.util.FixedLocation;
import org.getspout.commons.util.FixedVector;
import org.getspout.commons.util.Vector;

/**
 * Represents a base entity in the world
 */
public interface Entity extends Metadatable {

	/**
	 * Gets the current location of the entity
	 * @return location
	 */
	public FixedLocation getLocation();

	/**
	 * Sets the velocity of the entity
	 * @param velocity to set
	 */
	public void setVelocity(Vector velocity);

	/**
	 * Gets the current velocity of the entity
	 * @return velocity
	 */
	public FixedVector getVelocity();

	/**
	 * Gets the world this entity is in
	 * @return world
	 */
	public World getWorld();

	/**
	 * Teleports the entity to the given location
	 * @param location to teleport to
	 * @return true if successful
	 */
	public boolean teleport(FixedLocation location);

	/**
	 * Teleports the entity to the given entity
	 * @param entity to teleport to
	 * @return true if successful
	 */
	public boolean teleport(Entity destination);

	/**
	 * Returns a set of entities within a bounding box defined by x,y,z centered around player
	 *
	 * @param x Size of the box along x axis
	 * @param y Size of the box along y axis
	 * @param z Size of the box along z axis
	 * @return Set<Entity> List of entities nearby
	 */
	public Set<Entity> getNearbyEntities(double x, double y, double z);

	/**
	 * Returns a unique id for this entity
	 *
	 * @return Entity id
	 */
	public int getEntityId();

	/**
	 * Returns the entity's current fire ticks (ticks before the entity stops being on fire).
	 *
	 * @return int fireTicks
	 */
	public int getFireTicks();

	/**
	 * Returns the entity's maximum fire ticks.
	 *
	 * @return int maxFireTicks
	 */
	public int getMaxFireTicks();

	/**
	 * Sets the entity's current fire ticks (ticks before the entity stops being on fire).
	 *
	 * @param ticks Current ticks remaining
	 */
	public void setFireTicks(int ticks);

	/**
	 * Mark the entity's removal.
	 */
	public void remove();

	/**
	 * Returns true if this entity has been marked for removal.
	 * @return True if it is dead.
	 */
	public boolean isDead();

	/**
	 * Gets the primary passenger of a vehicle. For vehicles that could have
	 * multiple passengers, this will only return the primary passenger.
	 *
	 * @return an entity
	 */
	public abstract Entity getPassenger();

	/**
	 * Set the passenger of a vehicle.
	 *
	 * @param passenger The new passenger.
	 * @return false if it could not be done for whatever reason
	 */
	public abstract boolean setPassenger(Entity passenger);

	/**
	 * Check if a vehicle has passengers.
	 *
	 * @return True if the vehicle has no passengers.
	 */
	public abstract boolean isEmpty();

	/**
	 * Eject any passenger.
	 *
	 * @return True if there was a passenger.
	 */
	public abstract boolean eject();

	/**
	 * Returns the distance this entity has fallen
	 * @return The distance.
	 */
	public float getFallDistance();

	/**
	 * Sets the fall distance for this entity
	 * @param distance The new distance.
	 */
	public void setFallDistance(float distance);

	/**
	 * Returns a unique and persistent id for this entity
	 * @return unique id
	 */
	public UUID getUniqueId();

	/**
	 * Gets the amount of ticks this entity has lived for.
	 * <p>
	 * This is the equivalent to "age" in entities.
	 * 
	 * @return Age of entity
	 */
	public int getTicksLived();

	/**
	 * Sets the amount of ticks this entity has lived for.
	 * <p>
	 * This is the equivalent to "age" in entities. May not be
	 * less than one tick.
	 * 
	 * @param value Age of entity
	 */
	public void setTicksLived(int value);

	/**
	 * Sets the skin of this entity to the skin URI
	 * 
	 * @param skinURI
	 * @param type
	 */
	public void setSkin(String skinURI, EntitySkinType type);

}
