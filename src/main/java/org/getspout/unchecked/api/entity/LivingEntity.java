/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.unchecked.api.entity;

import java.util.HashSet;
import java.util.List;

import org.bukkit.block.Block;
import org.getspout.api.geo.discrete.Transform;
import org.getspout.unchecked.api.util.Location;

public interface LivingEntity extends Entity {
	/**
	 * Gets the title that appears over top of this entity in game
	 *
	 * @return
	 */
	public String getTitle();

	/**
	 * Sets the title that appears over top of this entity in game
	 *
	 * @param title
	 */
	public void setTitle(String title);

	/**
	 * Resets the title that appears over top of this entity in game
	 */
	public void resetTitle();

	/**
	 * Gets the entity's health from 0-20, where 0 is dead and 20 is full
	 *
	 * @return Health represented from 0-20
	 */
	public int getHealth();

	/**
	 * Sets the entity's health from 0-20, where 0 is dead and 20 is full
	 *
	 * @param health New health represented from 0-20
	 */
	public void setHealth(int health);

	/**
	 * Gets the height of the entity's head above its Location
	 *
	 * @return Height of the entity's eyes above its Location
	 */
	public double getEyeHeight();

	/**
	 * Gets the height of the entity's head above its Location
	 *
	 * @param ignoreSneaking If set to true, the effects of sneaking will be
	 *            ignored
	 * @return Height of the entity's eyes above its Location
	 */
	public double getEyeHeight(boolean ignoreSneaking);

	/**
	 * Get a Location detailing the current eye position of the LivingEntity.
	 *
	 * @return a Location at the eyes of the LivingEntity.
	 */
	public Location getEyeLocation();

	/**
	 * Gets all blocks along the player's line of sight List iterates from
	 * player's position to target inclusive
	 *
	 * @param transparent HashSet containing all transparent block IDs. If set
	 *            to null only air is considered transparent.
	 * @param maxDistance This is the maximum distance to scan. This may be
	 *            further limited by the server, but never to less than 100
	 *            blocks.
	 * @return List containing all blocks along the player's line of sight
	 */
	public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance);

	/**
	 * Gets the block that the player has targeted
	 *
	 * @param transparent HashSet containing all transparent block IDs. If set
	 *            to null only air is considered transparent.
	 * @param maxDistance This is the maximum distance to scan. This may be
	 *            further limited by the server, but never to less than 100
	 *            blocks.
	 * @return Block that the player has targeted
	 */
	public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance);

	/**
	 * Gets the last two blocks along the player's line of sight. The target
	 * block will be the last block in the list.
	 *
	 * @param transparent HashSet containing all transparent block IDs. If set
	 *            to null only air is considered transparent.
	 * @param maxDistance This is the maximum distance to scan. This may be
	 *            further limited by the server, but never to less than 100
	 *            blocks
	 * @return List containing the last 2 blocks along the player's line of
	 *         sight
	 */
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance);

	/**
	 * Returns whether this entity is inside a vehicle.
	 *
	 * @return True if the entity is in a vehicle.
	 */
	public boolean isInsideVehicle();

	/**
	 * Leave the current vehicle. If the entity is currently in a vehicle (and
	 * is removed from it), true will be returned, otherwise false will be
	 * returned.
	 *
	 * @return True if the entity was in a vehicle.
	 */
	public boolean leaveVehicle();

	/**
	 * Returns the amount of air that this entity has remaining, in ticks
	 *
	 * @return Amount of air remaining
	 */
	public int getRemainingAir();

	/**
	 * Sets the amount of air that this entity has remaining, in ticks
	 *
	 * @param ticks Amount of air remaining
	 */
	public void setRemainingAir(int ticks);

	/**
	 * Returns the maximum amount of air this entity can have, in ticks
	 *
	 * @return Maximum amount of air
	 */
	public int getMaximumAir();

	/**
	 * Sets the maximum amount of air this entity can have, in ticks
	 *
	 * @param ticks Maximum amount of air
	 */
	public void setMaximumAir(int ticks);

	/**
	 * Deals the given amount of damage to this entity
	 *
	 * @param amount Amount of damage to deal
	 */
	public void damage(int amount);

	/**
	 * Deals the given amount of damage to this entity, from a specified entity
	 *
	 * @param amount Amount of damage to deal
	 * @param source Entity which to attribute this damage from
	 */
	public void damage(int amount, Entity source);

	/**
	 * Returns the entities current maximum noDamageTicks This is the time in
	 * ticks the entity will become unable to take equal or less damage than the
	 * lastDamage
	 *
	 * @return noDamageTicks
	 */
	public int getMaximumNoDamageTicks();

	/**
	 * Sets the entities current maximum noDamageTicks
	 *
	 * @param ticks maximumNoDamageTicks
	 */
	public void setMaximumNoDamageTicks(int ticks);

	/**
	 * Returns the entities lastDamage taken in the current noDamageTicks time.
	 * Only damage higher than this amount will further damage the entity.
	 *
	 * @return lastDamage
	 */
	public int getLastDamage();

	/**
	 * Sets the entities current maximum noDamageTicks
	 *
	 * @param damage last damage
	 */
	public void setLastDamage(int damage);

	/**
	 * Returns the entities current noDamageTicks
	 *
	 * @return noDamageTicks
	 */
	public int getNoDamageTicks();

	/**
	 * Sets the entities current noDamageTicks
	 *
	 * @param ticks NoDamageTicks
	 */
	public void setNoDamageTicks(int ticks);

}
