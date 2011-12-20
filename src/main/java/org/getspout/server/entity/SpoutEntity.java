package org.getspout.server.entity;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import org.getspout.server.SpoutChunk;
import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.msg.EntityRotationMessage;
import org.getspout.server.msg.EntityTeleportMessage;
import org.getspout.server.msg.Message;
import org.getspout.server.msg.RelativeEntityPositionMessage;
import org.getspout.server.msg.RelativeEntityPositionRotationMessage;
import org.getspout.server.util.Position;

/**
 * Represents some entity in the world such as an item on the floor or a player.
 *
 * @author Graham Edgecombe
 */
public abstract class SpoutEntity implements Entity, Damager {
	/**
	 * Indicates how often, in ticks, to send position refresh packets to the
	 * client
	 */
	private static final int POSITION_REFRESH_RATE = 20;

	/**
	 * The server this entity belongs to.
	 */
	protected final SpoutServer server;

	/**
	 * The world this entity belongs to.
	 */
	protected SpoutWorld world;

	/**
	 * A flag indicating if this entity is currently active.
	 */
	protected boolean active = true;

	/**
	 * This entity's unique id.
	 */
	protected int id;

	/**
	 * The current position.
	 */
	protected Location location = Position.ZERO;

	/**
	 * The position in the last cycle.
	 */
	protected Location previousLocation = Position.ZERO;

	/**
	 * An EntityDamageEvent representing the last damage cause on this entity.
	 */
	private EntityDamageEvent lastDamageCause;

	/**
	 * A flag indicting if the entity is on the ground
	 */
	private boolean onGround = true;

	/**
	 * A counter of how long this entity has existed
	 */
	private int ticksLived = 0;
	/**
	 * A Random for use in this entity.
	 */
	protected final Random random = new Random();

	/**
	 * Indicates the last tick that the entity sent a refresh teleport packet
	 */
	private int lastRefresh = 0;

	/**
	 * Creates an entity and adds it to the specified world.
	 *
	 * @param server The server.
	 * @param world The world.
	 */
	public SpoutEntity(SpoutServer server, SpoutWorld world) {
		this.server = server;
		this.world = world;
		world.getEntityManager().allocate(this);
	}

	/**
	 * Checks if this entity is within the {@link SpoutChunk#VISIBLE_RADIUS} of
	 * another.
	 *
	 * @param other The other entity.
	 * @return {@code true} if the entities can see each other, {@code false} if
	 *         not.
	 */
	public boolean isWithinDistance(SpoutEntity other) {
		double dx = Math.abs(location.getX() - other.location.getX());
		double dz = Math.abs(location.getZ() - other.location.getZ());
		return other.getWorld() == getWorld() && dx <= server.getViewDistance() * SpoutChunk.WIDTH && dz <= server.getViewDistance() * SpoutChunk.DEPTH;
	}

	/**
	 * Checks if this entity is within the {@link SpoutChunk#VISIBLE_RADIUS} of
	 * a location.
	 *
	 * @param loc The location.
	 * @return {@code true} if the entities can see each other, {@code false} if
	 *         not.
	 */
	public boolean isWithinDistance(Location loc) {
		double dx = Math.abs(location.getX() - loc.getX());
		double dz = Math.abs(location.getZ() - loc.getZ());
		return loc.getWorld() == getWorld() && dx <= SpoutChunk.VISIBLE_RADIUS * SpoutChunk.WIDTH && dz <= SpoutChunk.VISIBLE_RADIUS * SpoutChunk.DEPTH;
	}

	/**
	 * Gets the world this entity is in.
	 *
	 * @return The world this entity is in.
	 */
	@Override
	public SpoutWorld getWorld() {
		return world;
	}

	/**
	 * Gets the {@link org.bukkit.Server} that contains this Entity
	 *
	 * @return Server instance running this Entity
	 */
	@Override
	public SpoutServer getServer() {
		return server;
	}

	/**
	 * Destroys this entity by removing it from the world and marking it as not
	 * being active.
	 */
	@Override
	public void remove() {
		active = false;
		world.getEntityManager().deallocate(this);
	}

	/**
	 * Checks if this entity is inactive.
	 *
	 * @return {@code true} if so, {@code false} if not.
	 */
	@Override
	public boolean isDead() {
		return !active;
	}

	/**
	 * Gets the id of this entity.
	 *
	 * @return The id.
	 */
	@Override
	public int getEntityId() {
		return id;
	}

	/**
	 * Called every game cycle. Subclasses should implement this to implement
	 * periodic functionality e.g. mob AI.
	 */
	public void pulse() {
		ticksLived++;
	}

	/**
	 * Resets the previous position and rotations of the entity to the current
	 * position and rotation.
	 */
	public void reset() {
		previousLocation = location;
	}

	/**
	 * Gets this entity's position.
	 *
	 * @return The position of this entity.
	 */
	@Override
	public Location getLocation() {
		return location.clone();
	}

	/**
	 * Gets the entity's previous position.
	 *
	 * @return The previous position of this entity.
	 */
	public Location getPreviousLocation() {
		return previousLocation;
	}

	/**
	 * Sets this entity's location.
	 *
	 * @param location The new location.
	 */
	public void setRawLocation(Location location) {
		this.location = location;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SpoutEntity other = (SpoutEntity) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	/**
	 * Creates a {@link Message} which can be sent to a client to spawn this
	 * entity.
	 *
	 * @return A message which can spawn this entity.
	 */
	public abstract Message createSpawnMessage();

	/**
	 * Creates a {@link Message} which can be sent to a client to update this
	 * entity.
	 *
	 * @return A message which can update this entity.
	 */
	public Message createUpdateMessage() {
		boolean moved = hasMoved();
		boolean rotated = hasRotated();

		int x = Position.getIntX(location);
		int y = Position.getIntY(location);
		int z = Position.getIntZ(location);

		int dx = x - Position.getIntX(previousLocation);
		int dy = y - Position.getIntY(previousLocation);
		int dz = z - Position.getIntZ(previousLocation);

		boolean teleport = dx > Byte.MAX_VALUE || dy > Byte.MAX_VALUE || dz > Byte.MAX_VALUE || dx < Byte.MIN_VALUE || dy < Byte.MIN_VALUE || dz < Byte.MIN_VALUE;

		int yaw = Position.getIntYaw(previousLocation);
		int pitch = Position.getIntPitch(previousLocation);

		boolean refreshPosition = ticksLived - lastRefresh > POSITION_REFRESH_RATE;

		if (refreshPosition || moved && teleport) {
			lastRefresh = ticksLived;
			return new EntityTeleportMessage(id, x, y, z, yaw, pitch);
		} else if (moved && rotated) {
			return new RelativeEntityPositionRotationMessage(id, dx, dy, dz, yaw, pitch);
		} else if (moved) {
			return new RelativeEntityPositionMessage(id, dx, dy, dz);
		} else if (rotated) {
			return new EntityRotationMessage(id, yaw, pitch);
		}

		return null;
	}

	/**
	 * Checks if this entity has moved this cycle.
	 *
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean hasMoved() {
		return Position.hasMoved(location, previousLocation);
	}

	/**
	 * Checks if this entity has rotated this cycle.
	 *
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean hasRotated() {
		return Position.hasRotated(location, previousLocation);
	}

	@Override
	public void setVelocity(Vector velocity) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Vector getVelocity() {
		return location.toVector().subtract(previousLocation.toVector());
	}

	@Override
	public boolean teleport(Location location) {
		if (location.getWorld() != world) {
			world.getEntityManager().deallocate(this);
			world = (SpoutWorld) location.getWorld();
			world.getEntityManager().allocate(this);
		}
		this.location = location;
		reset();
		return true;
	}

	@Override
	public boolean teleport(Entity destination) {
		return teleport(destination.getLocation());
	}

	@Override
	public boolean teleport(Location location, TeleportCause cause) {
		return teleport(location);
	}

	@Override
	public boolean teleport(Entity destination, TeleportCause cause) {
		return teleport(destination.getLocation());
	}

	@Override
	public List<Entity> getNearbyEntities(double x, double y, double z) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getFireTicks() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getMaxFireTicks() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setFireTicks(int ticks) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getRemainingAir() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setRemainingAir(int ticks) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getMaximumAir() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setMaximumAir(int ticks) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Entity getPassenger() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean setPassenger(Entity passenger) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean eject() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public float getFallDistance() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setFallDistance(float distance) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent event) {
		lastDamageCause = event;
	}

	@Override
	public EntityDamageEvent getLastDamageCause() {
		return lastDamageCause;
	}

	@Override
	public UUID getUniqueId() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getTicksLived() {
		return ticksLived;
	}

	@Override
	public void setTicksLived(int value) {
		ticksLived = value;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	public BlockFace getFacingDirection() {
		double rot = getLocation().getYaw();
		if (0 <= rot && rot < 22.5) {
			return BlockFace.NORTH;
		} else if (22.5 <= rot && rot < 67.5) {
			return BlockFace.NORTH_EAST;
		} else if (67.5 <= rot && rot < 112.5) {
			return BlockFace.EAST;
		} else if (112.5 <= rot && rot < 157.5) {
			return BlockFace.SOUTH_EAST;
		} else if (157.5 <= rot && rot < 202.5) {
			return BlockFace.SOUTH;
		} else if (202.5 <= rot && rot < 247.5) {
			return BlockFace.SOUTH_WEST;
		} else if (247.5 <= rot && rot < 292.5) {
			return BlockFace.WEST;
		} else if (292.5 <= rot && rot < 337.5) {
			return BlockFace.NORTH_WEST;
		} else if (337.5 <= rot && rot < 360.0) {
			return BlockFace.NORTH;
		} else {
			return null;
		}
	}

	public BlockFace getCardinalDirection() {
		double rot = getLocation().getYaw();
		if (0 <= rot && rot < 45.5) {
			return BlockFace.NORTH;
		} else if (45.5 <= rot && rot < 135.5) {
			return BlockFace.EAST;
		} else if (135.5 <= rot && rot < 225.5) {
			return BlockFace.SOUTH;
		} else if (225.5 <= rot && rot < 315.5) {
			return BlockFace.WEST;
		} else if (315.5 <= rot && rot < 360.0) {
			return BlockFace.NORTH;
		} else {
			return null;
		}
	}

	public abstract List<ItemStack> getLoot(Damager damager);
}
