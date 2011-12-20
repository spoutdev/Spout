package org.getspout.server.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import org.getspout.server.SpoutServer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.inventory.SpoutItemStack;
import org.getspout.server.inventory.SpoutPlayerInventory;
import org.getspout.server.msg.Message;
import org.getspout.server.msg.SpawnPlayerMessage;
import org.getspout.server.util.Position;

/**
 * Represents a human entity, such as an NPC or a player.
 */
public abstract class SpoutHumanEntity extends SpoutLivingEntity implements HumanEntity {
	/**
	 * The name of this human.
	 */
	private final String name;

	/**
	 * The inventory of this human.
	 */
	private final SpoutPlayerInventory inventory = new SpoutPlayerInventory();

	/**
	 * Whether this human is sleeping or not.
	 */
	protected boolean sleeping = false;

	/**
	 * The bed spawn location of a player
	 */
	private Location bedSpawn;

	/**
	 * How long this human has been sleeping.
	 */
	private int sleepingTicks = 0;

	/**
	 * This human's PermissibleBase for permissions.
	 */
	protected PermissibleBase permissions;

	/**
	 * Whether this human is considered an op.
	 */
	private boolean isOp;

	/**
	 * The player's active game mode
	 */
	private GameMode gameMode;

	/**
	 * The human entity's active effects
	 */
	private Set<ActiveEntityEffect> activeEffects = Collections.synchronizedSet(new HashSet<ActiveEntityEffect>());

	/**
	 * Creates a human within the specified world and with the specified name.
	 *
	 * @param world The world.
	 * @param name The human's name.
	 */
	public SpoutHumanEntity(SpoutServer server, SpoutWorld world, String name) {
		super(server, world);
		this.name = name;
		permissions = new PermissibleBase(this);
		gameMode = server.getDefaultGameMode();
	}

	@Override
	public Message createSpawnMessage() {
		int x = Position.getIntX(location);
		int y = Position.getIntY(location);
		int z = Position.getIntZ(location);
		int yaw = Position.getIntYaw(location);
		int pitch = Position.getIntPitch(location);
		return new SpawnPlayerMessage(id, name, x, y, z, yaw, pitch, 0);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public SpoutPlayerInventory getInventory() {
		return inventory;
	}

	@Override
	public SpoutItemStack getItemInHand() {
		return getInventory().getItemInHand();
	}

	@Override
	public void setItemInHand(ItemStack item) {
		getInventory().setItemInHand(item);
	}

	@Override
	public boolean isSleeping() {
		return sleeping;
	}

	@Override
	public int getSleepTicks() {
		return sleepingTicks;
	}

	@Override
	public GameMode getGameMode() {
		return gameMode;
	}

	@Override
	public void setGameMode(GameMode mode) {
		gameMode = mode;
	}

	protected void setSleepTicks(int ticks) {
		sleepingTicks = ticks;
	}

	@Override
	public void pulse() {
		super.pulse();
		if (sleeping) {
			++sleepingTicks;
		} else {
			sleepingTicks = 0;
		}
		for (ActiveEntityEffect effect : activeEffects) {
			if (!effect.pulse()) {
				removeEntityEffect(effect);
			}
		}
	}

	// ---- Permissions stuff

	@Override
	public boolean isPermissionSet(String name) {
		return permissions.isPermissionSet(name);
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		return permissions.isPermissionSet(perm);
	}

	@Override
	public boolean hasPermission(String name) {
		return permissions.hasPermission(name);
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return permissions.hasPermission(perm);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return permissions.addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		return permissions.addAttachment(plugin, ticks);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		return permissions.addAttachment(plugin, name, value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		return permissions.addAttachment(plugin, name, value, ticks);
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		permissions.removeAttachment(attachment);
	}

	@Override
	public void recalculatePermissions() {
		permissions.recalculatePermissions();
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return permissions.getEffectivePermissions();
	}

	@Override
	public boolean isOp() {
		return isOp;
	}

	@Override
	public void setOp(boolean value) {
		isOp = value;
		recalculatePermissions();
	}

	public void addEntityEffect(ActiveEntityEffect effect) {
		activeEffects.add(effect);
	}

	public void addEntityEffect(EntityEffect effect, byte amplitude, short duration) {
		addEntityEffect(new ActiveEntityEffect(effect, amplitude, duration));
	}

	public void removeEntityEffect(ActiveEntityEffect effect) {
		activeEffects.remove(effect);
	}

	public Location getBedSpawnLocation() {
		return bedSpawn;
	}

	public void setBedSpawnLocation(Location bedSpawn) {
		this.bedSpawn = bedSpawn;
	}
}
