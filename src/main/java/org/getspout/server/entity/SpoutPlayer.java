package org.getspout.server.entity;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Statistic;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import org.getspout.server.EventFactory;
import org.getspout.server.SpoutChunk;
import org.getspout.server.SpoutOfflinePlayer;
import org.getspout.server.SpoutWorld;
import org.getspout.server.block.SpoutBlockState;
import org.getspout.server.inventory.InventoryViewer;
import org.getspout.server.inventory.SpoutInventory;
import org.getspout.server.inventory.SpoutItemStack;
import org.getspout.server.inventory.SpoutPlayerInventory;
import org.getspout.server.io.StorageOperation;
import org.getspout.server.msg.BlockChangeMessage;
import org.getspout.server.msg.ChatMessage;
import org.getspout.server.msg.DestroyEntityMessage;
import org.getspout.server.msg.EntityEffectMessage;
import org.getspout.server.msg.EntityEquipmentMessage;
import org.getspout.server.msg.EntityMetadataMessage;
import org.getspout.server.msg.EntityRemoveEffectMessage;
import org.getspout.server.msg.ExperienceMessage;
import org.getspout.server.msg.HealthMessage;
import org.getspout.server.msg.IdentificationMessage;
import org.getspout.server.msg.LoadChunkMessage;
import org.getspout.server.msg.Message;
import org.getspout.server.msg.PlayEffectMessage;
import org.getspout.server.msg.PlayNoteMessage;
import org.getspout.server.msg.PositionRotationMessage;
import org.getspout.server.msg.RespawnMessage;
import org.getspout.server.msg.SetWindowSlotMessage;
import org.getspout.server.msg.SpawnPlayerMessage;
import org.getspout.server.msg.SpawnPositionMessage;
import org.getspout.server.msg.StateChangeMessage;
import org.getspout.server.msg.StatisticMessage;
import org.getspout.server.msg.UserListItemMessage;
import org.getspout.server.net.Session;
import org.getspout.server.util.Parameter;
import org.getspout.server.util.Position;
import org.getspout.server.util.TextWrapper;

/**
 * Represents an in-game player.
 *
 * @author Graham Edgecombe
 */
@DelegateDeserialization(SpoutOfflinePlayer.class)
public final class SpoutPlayer extends SpoutHumanEntity implements Player, InventoryViewer {
	/**
	 * The normal height of a player's eyes above their feet.
	 */
	public static final double EYE_HEIGHT = 1.62D;

	/**
	 * This player's session.
	 */
	private final Session session;

	/**
	 * Cumulative amount of experience points the player has collected.
	 */
	private int experience = 0;

	/**
	 * The current level (or skill point amount) of the player.
	 */
	private int level = 0;

	/**
	 * The player's current exhaustion level.
	 */
	private float exhaustion = 0;

	/**
	 * The player's current saturation level.
	 */
	private float saturation = 0;

	/**
	 * This player's current time offset.
	 */
	private long timeOffset = 0;

	/**
	 * Whether the time offset is relative.
	 */
	private boolean timeRelative = true;

	/**
	 * The display name of this player, for chat purposes.
	 */
	private String displayName;

	/**
	 * The player's compass target.
	 */
	private Location compassTarget;

	/**
	 * The entities that the client knows about.
	 */
	private Set<SpoutEntity> knownEntities = new HashSet<SpoutEntity>();

	/**
	 * The chunks that the client knows about.
	 */
	private final Set<SpoutChunk.Key> knownChunks = new HashSet<SpoutChunk.Key>();

	/**
	 * The item the player has on their cursor.
	 */
	private SpoutItemStack itemOnCursor;

	/**
	 * Whether the player is sneaking.
	 */
	private boolean sneaking = false;

	/**
	 * The human entity's current food level
	 */
	private int food = 20;

	/**
	 * Whether to use the display name when sending spawn messages to
	 */
	private boolean dispNameAsEntityName = false;

	/**
	 * The name a player has in the player list
	 */
	private String playerListName;

	/**
	 * Whether the player is sprinting
	 */
	private boolean isSprinting;

	/*
	 * The ID of the window that the player has open
	 * -1 if nothing is open
	 */
	private SpoutInventory openInventory;

	/**
	 * Creates a new player and adds it to the world.
	 *
	 * @param session The player's session.
	 * @param name The player's name.
	 */
	public SpoutPlayer(Session session, String name) {
		super(session.getServer(), (SpoutWorld) session.getServer().getWorlds().get(0), name);
		this.session = session;
		health = 20;
		if (session.getState() != Session.State.GAME) {
			session.send(new IdentificationMessage(getEntityId(), "", world.getSeed(), getGameMode().getValue(), world.getEnvironment().getId(), 1, world.getMaxHeight(), session.getServer().getMaxPlayers()));
		}
		streamBlocks(); // stream the initial set of blocks
		setCompassTarget(world.getSpawnLocation()); // set our compass target
		session.send(new StateChangeMessage((byte) (getWorld().hasStorm() ? 1 : 2), (byte) 0)); // send the world's weather

		getInventory().addViewer(this);
		getInventory().getCraftingInventory().addViewer(this);
	}

	// -- Various internal mechanisms

	/**
	 * Destroys this entity by removing it from the world and marking it as not
	 * being active.
	 */
	@Override
	public void remove() {
		saveData();
		getInventory().removeViewer(this);
		getInventory().getCraftingInventory().removeViewer(this);
		super.remove();
	}

	@Override
	public void pulse() {
		super.pulse();

		streamBlocks();

		for (Iterator<SpoutEntity> it = knownEntities.iterator(); it.hasNext();) {
			SpoutEntity entity = it.next();
			boolean withinDistance = !entity.isDead() && isWithinDistance(entity);

			if (withinDistance) {
				Message msg = entity.createUpdateMessage();
				if (msg != null) {
					session.send(msg);
				}
			} else {
				session.send(new DestroyEntityMessage(entity.getEntityId()));
				it.remove();
			}
		}

		for (SpoutEntity entity : world.getEntityManager()) {
			if (entity == this) {
				continue;
			}
			boolean withinDistance = !entity.isDead() && isWithinDistance(entity);

			if (withinDistance && !knownEntities.contains(entity)) {
				knownEntities.add(entity);
				session.send(entity.createSpawnMessage());
			}
		}
	}

	/**
	 * Streams chunks to the player's client.
	 */
	private void streamBlocks() {
		Set<SpoutChunk.Key> previousChunks = new HashSet<SpoutChunk.Key>(knownChunks);
		ArrayList<SpoutChunk.Key> newChunks = new ArrayList<SpoutChunk.Key>();

		int centralX = (int) location.getX() >> 4;
		int centralZ = (int) location.getZ() >> 4;

		int radius = server.getViewDistance();
		for (int x = centralX - radius; x <= centralX + radius; x++) {
			for (int z = centralZ - radius; z <= centralZ + radius; z++) {
				SpoutChunk.Key key = new SpoutChunk.Key(x, z);
				if (!knownChunks.contains(key)) {
					knownChunks.add(key);
					newChunks.add(key);
				}
				previousChunks.remove(key);
			}
		}

		Collections.sort(newChunks, new Comparator<SpoutChunk.Key>() {
			@Override
			public int compare(SpoutChunk.Key a, SpoutChunk.Key b) {
				double dx = 16 * a.getX() + 8 - location.getX();
				double dz = 16 * a.getZ() + 8 - location.getZ();
				double da = dx * dx + dz * dz;
				dx = 16 * b.getX() + 8 - location.getX();
				dz = 16 * b.getZ() + 8 - location.getZ();
				double db = dx * dx + dz * dz;
				return Double.compare(da, db);
			}
		});

		for (SpoutChunk.Key key : newChunks) {
			session.send(new LoadChunkMessage(key.getX(), key.getZ(), true));
			session.send(world.getChunkAt(key.getX(), key.getZ()).toMessage());
			for (SpoutBlockState state : world.getChunkAt(key.getX(), key.getZ()).getTileEntities()) {
				state.update(this);
			}
		}

		for (SpoutChunk.Key key : previousChunks) {
			session.send(new LoadChunkMessage(key.getX(), key.getZ(), false));
			knownChunks.remove(key);
		}

		previousChunks.clear();
	}

	/**
	 * Checks whether the player can see the given chunk.
	 *
	 * @return If the chunk is known to the player's client.
	 */
	public boolean canSee(SpoutChunk.Key chunk) {
		return knownChunks.contains(chunk);
	}

	/**
	 * Checks whether the player can see the given entity.
	 *
	 * @return If the entity is known to the player's client.
	 */
	public boolean canSee(SpoutEntity entity) {
		return knownEntities.contains(entity);
	}

	// -- Basic getters

	/**
	 * Gets the session.
	 *
	 * @return The session.
	 */
	public Session getSession() {
		return session;
	}

	@Override
	public boolean isOnline() {
		return true;
	}

	@Override
	public boolean isBanned() {
		return server.getBanManager().isBanned(getName());
	}

	@Override
	public void setBanned(boolean banned) {
		server.getBanManager().setBanned(getName(), banned);
	}

	@Override
	public boolean isWhitelisted() {
		return !server.hasWhitelist() || server.getWhitelist().contains(getName());
	}

	@Override
	public boolean hasPlayedBefore() {
		return getWorld().getMetadataService().hasDataFor(this);
	}

	@Override
	public long getLastPlayed() {
		throw new UnsupportedOperationException("not suppored yet");
	}

	@Override
	public long getFirstPlayed() {
		throw new UnsupportedOperationException("not suppored yet");
	}

	@Override
	public void setWhitelisted(boolean value) {
		if (value) {
			server.getWhitelist().add(getName());
		} else {
			server.getWhitelist().remove(getName());
		}
	}

	@Override
	public Player getPlayer() {
		return this;
	}

	@Override
	public InetSocketAddress getAddress() {
		return session.getAddress();
	}

	@Override
	public boolean isOp() {
		return getServer().getOpsList().contains(getName());
	}

	@Override
	public void setOp(boolean value) {
		if (value) {
			getServer().getOpsList().add(getName());
		} else {
			getServer().getOpsList().remove(getName());
		}
		permissions.recalculatePermissions();
	}

	// -- Malleable properties

	@Override
	public String getDisplayName() {
		return displayName == null ? getName() : displayName;
	}

	@Override
	public void setDisplayName(String name) {
		displayName = name;
	}

	@Override
	public String getPlayerListName() {
		return playerListName == null || "".equals(playerListName) ? getName() : playerListName;
	}

	@Override
	public void setPlayerListName(String name) {
		if (name.length() > 15) {
			throw new IllegalArgumentException("The given name was " + name.length() + " chars long, longer than the maximum of 16");
		}
		for (Player player : server.getOnlinePlayers()) {
			if (player.getPlayerListName().equals(getPlayerListName())) {
				throw new IllegalArgumentException("The name given, " + name + ", is already used by " + player.getName() + ".");
			}
		}
		Message removeMessage = new UserListItemMessage(getPlayerListName(), false, (short) 0);
		playerListName = name;
		Message reAddMessage = new UserListItemMessage(getPlayerListName(), true, (short) 0);
		for (Player player : server.getOnlinePlayers()) {
			((SpoutPlayer) player).getSession().send(removeMessage);
			((SpoutPlayer) player).getSession().send(reAddMessage);
		}
	}

	@Override
	public Location getCompassTarget() {
		return compassTarget;
	}

	@Override
	public void setCompassTarget(Location loc) {
		compassTarget = loc;
		session.send(new SpawnPositionMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
	}

	@Override
	public boolean isSneaking() {
		return sneaking;
	}

	@Override
	public void setSneaking(boolean sneak) {
		if (EventFactory.onPlayerToggleSneak(this, sneak).isCancelled()) {
			return;
		}
		sneaking = sneak;
		setMetadata(new Parameter<Byte>(Parameter.TYPE_BYTE, 0, (byte) (sneaking ? 0x02 : 0)));
		// FIXME: other bits in the bitmask would be wiped out
		EntityMetadataMessage message = new EntityMetadataMessage(id, metadata);
		for (Player player : world.getPlayers()) {
			if (player != this && canSee((SpoutPlayer) player)) {
				((SpoutPlayer) player).session.send(message);
			}
		}
	}

	@Override
	public boolean isSprinting() {
		return isSprinting;
	}

	@Override
	public void setSprinting(boolean sprinting) {
		isSprinting = sprinting;
		setMetadataFlag(0, 0x03, sprinting);
	}

	@Override
	public boolean isSleepingIgnored() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setSleepingIgnored(boolean isSleeping) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setGameMode(GameMode mode) {
		boolean changed = getGameMode() != mode;
		super.setGameMode(mode);
		if (changed) {
			session.send(new StateChangeMessage((byte) 3, (byte) mode.getValue())); // The gamemode changed message is clientside (NOTCH!!!!!!!), so don't annoy users unnecessarily.
		}
	}

	@Override
	public int getExperience() {
		return experience % ((getLevel() + 1) * 7);
	}

	@Override
	public void setExperience(int exp) {
		setTotalExperience(experience - getExperience() + exp);
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public void setLevel(int level) {
		int calcExperience = getExperience();
		this.level = level;
		for (int i = 0; i <= level; i++) {
			calcExperience += (level + 1) * 7;
		}
		setExperience(calcExperience);
		session.send(createExperienceMessage());
	}

	@Override
	public int getTotalExperience() {
		return experience;
	}

	@Override
	public void setTotalExperience(int exp) {
		int calcExperience = exp;
		experience = exp;
		level = 0;
		while ((calcExperience -= (getLevel() + 1) * 7) > 0) {
			++level;
		}
		session.send(createExperienceMessage());
	}

	/**
	 * Add xp to Player.
	 *
	 * @param xp to add
	 */
	@Override
	public void giveExp(int xp) {
		experience += xp;
		while (experience > (getLevel() + 1) * 7) {
			experience -= (getLevel() + 1) * 7;
			++level;
		}
		session.send(createExperienceMessage());
	}

	/**
	 * Get the percent to level for player.
	 *
	 * @return value between 0 and 1: percent to next level.
	 */
	@Override
	public float getExp() {
		float xpToLevel = (getLevel() + 1) * 7;
		return experience / xpToLevel;
	}

	/**
	 * Set the experience progress to next level. 0 sets to current level, 1 is
	 * next level Values greater than 1 or less that 0 are undefined. TODO Test
	 * boundary conditions.
	 *
	 * @param percentToLevel
	 */
	@Override
	public void setExp(float percentToLevel) {
		float xpToLevel = (getLevel() + 1) * 7;
		experience = (int) (percentToLevel * xpToLevel);
	}

	@Override
	public float getExhaustion() {
		return exhaustion;
	}

	@Override
	public void setExhaustion(float value) {
		exhaustion = value;
	}

	@Override
	public float getSaturation() {
		return saturation;
	}

	@Override
	public void setSaturation(float value) {
		saturation = value;
		session.send(createHealthMessage());
	}

	// -- Actions

	/**
	 * Teleport the player.
	 *
	 * @param location The destination to teleport to.
	 * @return Whether the teleport was a success.
	 */
	@Override
	public boolean teleport(Location location) {
		return teleport(location, TeleportCause.UNKNOWN);
	}

	@Override
	public boolean teleport(Location location, TeleportCause cause) {
		if (this.location != null && this.location.getWorld() != null) {
			PlayerTeleportEvent event = EventFactory.onPlayerTeleport(this, getLocation(), location, cause);
			if (event.isCancelled()) {
				return false;
			}
			location = event.getTo();
		}
		if (location.getWorld() != world) {
			SpoutWorld oldWorld = world;
			world.getEntityManager().deallocate(this);

			world = (SpoutWorld) location.getWorld();
			world.getEntityManager().allocate(this);

			for (SpoutChunk.Key key : knownChunks) {
				session.send(new LoadChunkMessage(key.getX(), key.getZ(), false));
			}
			knownChunks.clear();

			session.send(new RespawnMessage((byte) world.getEnvironment().getId(), (byte) 1, (byte) getGameMode().getValue(), world.getMaxHeight(), world.getSeed()));
			streamBlocks(); // stream blocks

			setCompassTarget(world.getSpawnLocation()); // set our compass target
			session.send(new PositionRotationMessage(location.getX(), location.getY() + EYE_HEIGHT + 0.01, location.getZ(), location.getY(), location.getYaw(), location.getPitch(), true));
			this.location = location; // take us to spawn position
			session.send(new StateChangeMessage((byte) (getWorld().hasStorm() ? 1 : 2), (byte) 0)); // send the world's weather
			reset();
			EventFactory.onPlayerChangedWorld(this, oldWorld);
		} else {
			session.send(new PositionRotationMessage(location.getX(), location.getY() + EYE_HEIGHT + 0.01, location.getZ(), location.getY(), location.getYaw(), location.getPitch(), true));
			this.location = location;
			reset();
		}

		return true;
	}

	@Override
	public List<ItemStack> getLoot(Damager damager) {
		return new ArrayList<ItemStack>(Arrays.asList(getInventory().getContents()));
	}

	@Override
	public void sendMessage(String message) {
		for (String line : TextWrapper.wrapText(message)) {
			sendRawMessage(line);
		}
	}

	@Override
	public void sendRawMessage(String message) {
		session.send(new ChatMessage(message.length() <= 119 ? message : message.substring(0, 119)));
	}

	@Override
	public void kickPlayer(String message) {
		session.disconnect(message == null ? "" : message);
	}

	@Override
	public boolean performCommand(String command) {
		return getServer().dispatchCommand(this, command);
	}

	/**
	 * Says a message (or runs a command).
	 *
	 * @param text message to print
	 */
	@Override
	public void chat(String text) {
		if (text.startsWith("/")) {
			try {
				if (EventFactory.onPlayerCommand(this, text).isCancelled()) {
					return;
				}

				if (!performCommand(text.substring(1))) {
					String firstword = text.substring(1);
					if (firstword.indexOf(' ') >= 0) {
						firstword = firstword.substring(0, firstword.indexOf(' '));
					}

					sendMessage(ChatColor.GRAY + "Command not found: " + firstword);
				}
			} catch (Exception ex) {
				sendMessage(ChatColor.RED + "An internal error occured while executing your command.");
				getServer().getLogger().log(Level.SEVERE, "Exception while executing command: {0}", ex.getMessage());
				ex.printStackTrace();
			}
		} else {
			PlayerChatEvent event = EventFactory.onPlayerChat(this, text);
			if (event.isCancelled()) {
				return;
			}

			String message = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
			getServer().getLogger().info(message);
			for (Player recipient : event.getRecipients()) {
				recipient.sendMessage(message);
			}
		}
	}

	@Override
	public void saveData() {
		saveData(true);
	}

	public void saveData(boolean async) {
		final SpoutWorld dataWorld = (SpoutWorld) server.getWorlds().get(0);
		if (async) {
			final SpoutPlayer player = this;
			server.getStorageQueue().queue(new StorageOperation() {
				@Override
				public boolean isParallel() {
					return true;
				}

				@Override
				public String getGroup() {
					return getName() + "_" + getWorld().getName();
				}

				@Override
				public boolean queueMultiple() {
					return true;
				}

				@Override
				public String getOperation() {
					return "player-data-save";
				}

				@Override
				public void run() {
					dataWorld.getMetadataService().writePlayerData(player);
				}
			});
		} else {
			dataWorld.getMetadataService().writePlayerData(this);
		}
	}

	@Override
	public void loadData() {

		SpoutWorld dataWorld = (SpoutWorld) server.getWorlds().get(0);
		dataWorld.getMetadataService().readPlayerData(this);
	}

	// -- Data transmission

	@Override
	public void playNote(Location loc, Instrument instrument, Note note) {
		playNote(loc, instrument.getType(), note.getId());
	}

	@Override
	public void playNote(Location loc, byte instrument, byte note) {
		session.send(new PlayNoteMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), instrument, note));
	}

	@Override
	public void playEffect(Location loc, Effect effect, int data) {
		session.send(new PlayEffectMessage(effect.getId(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), data));
	}

	@Override
	public void sendBlockChange(Location loc, Material material, byte data) {
		sendBlockChange(loc, material.getId(), data);
	}

	@Override
	public void sendBlockChange(Location loc, int material, byte data) {
		session.send(new BlockChangeMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), material, data));
	}

	@Override
	public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	// -- Achievements & Statistics [mostly borrowed from CraftBukkit]

	@Override
	public void awardAchievement(Achievement achievement) {
		sendStatistic(achievement.getId(), 1);
	}

	@Override
	public void incrementStatistic(Statistic statistic) {
		incrementStatistic(statistic, 1);
	}

	@Override
	public void incrementStatistic(Statistic statistic, int amount) {
		sendStatistic(statistic.getId(), amount);
	}

	@Override
	public void incrementStatistic(Statistic statistic, Material material) {
		incrementStatistic(statistic, material, 1);
	}

	@Override
	public void incrementStatistic(Statistic statistic, Material material, int amount) {
		if (!statistic.isSubstatistic()) {
			throw new IllegalArgumentException("Given statistic is not a substatistic");
		}
		if (statistic.isBlock() != material.isBlock()) {
			throw new IllegalArgumentException("Given material is not valid for this substatistic");
		}

		int mat = material.getId();

		if (!material.isBlock()) {
			mat -= 255;
		}

		sendStatistic(statistic.getId() + mat, amount);
	}

	private void sendStatistic(int id, int amount) {
		while (amount > Byte.MAX_VALUE) {
			sendStatistic(id, Byte.MAX_VALUE);
			amount -= Byte.MAX_VALUE;
		}

		if (amount > 0) {
			session.send(new StatisticMessage(id, (byte) amount));
		}
	}

	// -- Inventory

	@Override
	public void updateInventory() {
		getInventory().setContents(getInventory().getContents());
	}

	/**
	 * Get the current item on the player's cursor, for inventory screen
	 * purposes.
	 *
	 * @return The ItemStack the player is holding.
	 */
	public ItemStack getItemOnCursor() {
		return itemOnCursor;
	}

	/**
	 * Set the item on the player's cursor, for inventory screen purposes.
	 *
	 * @param item The ItemStack to set the cursor to.
	 */
	public void setItemOnCursor(SpoutItemStack item) {
		itemOnCursor = item;
		if (item == null) {
			session.send(new SetWindowSlotMessage(-1, -1));
		} else {
			session.send(new SetWindowSlotMessage(-1, -1, item.getTypeId(), item.getAmount(), item.getDurability(), item.getNbtData()));
		}
	}

	/**
	 * Inform the client that an item has changed.
	 *
	 * @param inventory The SpoutInventory in which a slot has changed.
	 * @param slot The slot number which has changed.
	 * @param item The ItemStack which the slot has changed to.
	 */
	@Override
	public void onSlotSet(SpoutInventory inventory, int slot, SpoutItemStack item) {
		if (inventory == getInventory()) {
			int type = item == null ? -1 : item.getTypeId();
			int data = item == null ? 0 : item.getDurability();

			int equipSlot = -1;
			if (slot == getInventory().getHeldItemSlot()) {
				equipSlot = EntityEquipmentMessage.HELD_ITEM;
			} else if (slot == SpoutPlayerInventory.HELMET_SLOT) {
				equipSlot = EntityEquipmentMessage.HELMET_SLOT;
			} else if (slot == SpoutPlayerInventory.CHESTPLATE_SLOT) {
				equipSlot = EntityEquipmentMessage.CHESTPLATE_SLOT;
			} else if (slot == SpoutPlayerInventory.LEGGINGS_SLOT) {
				equipSlot = EntityEquipmentMessage.LEGGINGS_SLOT;
			} else if (slot == SpoutPlayerInventory.BOOTS_SLOT) {
				equipSlot = EntityEquipmentMessage.BOOTS_SLOT;
			}

			if (equipSlot >= 0) {
				EntityEquipmentMessage message = new EntityEquipmentMessage(getEntityId(), equipSlot, type, data);
				for (SpoutPlayer player : new ArrayList<SpoutPlayer>(getWorld().getRawPlayers())) {
					if (player != this && player.canSee(this)) {
						player.getSession().send(message);
					}
				}
			}
		}

		if (item == null) {
			session.send(new SetWindowSlotMessage(inventory.getId(), inventory.getNetworkSlot(slot)));
		} else {
			session.send(new SetWindowSlotMessage(inventory.getId(), inventory.getNetworkSlot(slot), item.getTypeId(), item.getAmount(), item.getDurability(), item.getNbtData()));
		}
	}

	// -- Goofy relative time stuff --

	/**
	 * Sets the current time on the player's client. When relative is true the
	 * player's time will be kept synchronized to its world time with the
	 * specified offset.
	 *
	 * When using non relative time the player's time will stay fixed at the
	 * specified time parameter. It's up to the caller to continue updating the
	 * player's time. To restore player time to normal use resetPlayerTime().
	 *
	 * @param time The current player's perceived time or the player's time
	 *            offset from the server time.
	 * @param relative When true the player time is kept relative to its world
	 *            time.
	 */
	@Override
	public void setPlayerTime(long time, boolean relative) {
		timeOffset = time % 24000;
		timeRelative = relative;

		if (timeOffset < 0) {
			timeOffset += 24000;
		}
	}

	/**
	 * Returns the player's current timestamp.
	 *
	 * @return
	 */
	@Override
	public long getPlayerTime() {
		if (timeRelative) {
			// add timeOffset ticks to current time
			return (world.getTime() + timeOffset) % 24000;
		} else {
			// return time offset
			return timeOffset % 24000;
		}
	}

	/**
	 * Returns the player's current time offset relative to server time, or the
	 * current player's fixed time if the player's time is absolute.
	 *
	 * @return
	 */
	@Override
	public long getPlayerTimeOffset() {
		return timeOffset;
	}

	/**
	 * Returns true if the player's time is relative to the server time,
	 * otherwise the player's time is absolute and will not change its current
	 * time unless done so with setPlayerTime().
	 *
	 * @return true if the player's time is relative to the server time.
	 */
	@Override
	public boolean isPlayerTimeRelative() {
		return timeRelative;
	}

	/**
	 * Restores the normal condition where the player's time is synchronized
	 * with the server time. Equivalent to calling setPlayerTime(0, true).
	 */
	@Override
	public void resetPlayerTime() {
		setPlayerTime(0, true);
	}

	/**
	 * Render a map and send it to the player in its entirety. This may be used
	 * when streaming the map in the normal manner is not desirbale.
	 *
	 * @param map The map to be sent
	 */
	@Override
	public void sendMap(MapView map) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setHealth(int health) {
		super.setHealth(health);
		session.send(createHealthMessage());
	}

	@Override
	public int getMaxHealth() {
		return 20;
	}

	@Override
	public void addEntityEffect(ActiveEntityEffect effect) {
		super.addEntityEffect(effect);
		EntityEffectMessage msg = new EntityEffectMessage(getEntityId(), effect.getEffect().getId(), effect.getAmplitude(), effect.getDuration());
		for (Player player : server.getOnlinePlayers()) {
			((SpoutPlayer) player).getSession().send(msg);
		}
	}

	@Override
	public void removeEntityEffect(ActiveEntityEffect effect) {
		super.removeEntityEffect(effect);
		EntityRemoveEffectMessage msg = new EntityRemoveEffectMessage(getEntityId(), effect.getEffect().getId());
		for (Player player : server.getOnlinePlayers()) {
			((SpoutPlayer) player).getSession().send(msg);
		}
	}

	@Override
	public Message createSpawnMessage() {
		int x = Position.getIntX(location);
		int y = Position.getIntY(location);
		int z = Position.getIntZ(location);
		int yaw = Position.getIntYaw(location);
		int pitch = Position.getIntPitch(location);
		return new SpawnPlayerMessage(id, dispNameAsEntityName ? displayName : getName(), x, y, z, yaw, pitch, 0);
	}

	@Override
	public int getFoodLevel() {
		return food;
	}

	@Override
	public void setFoodLevel(int food) {
		this.food = Math.min(food, 20);
		session.send(createHealthMessage());
	}

	public Message createHealthMessage() {
		return new HealthMessage(getHealth(), getFoodLevel(), getSaturation());
	}

	public Message createExperienceMessage() {
		return new ExperienceMessage((byte) getExperience(), (byte) getLevel(), (short) getTotalExperience());
	}

	/**
	 * Should check whether a window is open with hasWindowOpen first
	 *
	 * @return null if no inventory is open
	 */
	public SpoutInventory getOpenInventory() {
		return openInventory;
	}

	/**
	 * Keep in mind - own inventory does not count as a window on Notchian
	 * client!
	 */
	public boolean hasWindowOpen() {
		return openInventory != null;
	}

	public void onClosedWindow() {
		if (openInventory != null) {
			openInventory.removeViewer(this);
			openInventory = null;
		}
	}

	/**
	 * Tries to open a window of the given type (WindowID.<type>)
	 *
	 * @param inv The inventory to open
	 * @return false if the window could not be opened (one was already open)
	 *         True otherwise
	 */
	public boolean openWindow(SpoutInventory inv) {
		if (!hasWindowOpen()) {
			inv.addViewer(this);
			openInventory = inv;
			return true;
		}
		return false;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("name", getName());
		return ret;
	}
}
