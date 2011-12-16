package org.getspout.server;

import java.net.InetAddress;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.event.vehicle.*;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import org.getspout.server.block.BlockProperties;
import org.getspout.server.block.SpoutBlock;
import org.getspout.server.entity.SpoutPlayer;
import org.getspout.server.net.Session;
import org.getspout.server.util.bans.BanManager;

/**
 * Central class for the calling of events.
 */
public final class EventFactory {

	// Private to prevent creation
	private EventFactory() {}

	/**
	 * Calls an event through the plugin manager.
	 * @param event The event to throw.
	 * @return the called event
	 */
	private static <T extends Event> T callEvent(T event) {
		Bukkit.getServer().getPluginManager().callEvent(event);
		return event;
	}

	// -- Player Events

	public static PlayerChatEvent onPlayerChat(Player player, String message) {
		return callEvent(new PlayerChatEvent(player, message));
	}

	public static PlayerCommandPreprocessEvent onPlayerCommand(Player player, String message) {
		return callEvent(new PlayerCommandPreprocessEvent(player, message));
	}

	public static PlayerJoinEvent onPlayerJoin(Player player) {
		return callEvent(new PlayerJoinEvent(player, ChatColor.YELLOW + player.getName() + " joined the game"));
	}

	public static PlayerKickEvent onPlayerKick(Player player, String reason) {
		return callEvent(new PlayerKickEvent(player, reason, null));
	}

	public static PlayerQuitEvent onPlayerQuit(Player player) {
		return callEvent(new PlayerQuitEvent(player, ChatColor.YELLOW + player.getName() + " left the game"));
	}

	public static PlayerMoveEvent onPlayerMove(Player player, Location from, Location to) {
		return callEvent(new PlayerMoveEvent(player, from, to));
	}

	public static PlayerInteractEvent onPlayerInteract(Player player, Action action) {
		return callEvent(new PlayerInteractEvent(player, action, player.getItemInHand(), null, null));
	}

	public static PlayerInteractEvent onPlayerInteract(Player player, Action action, Block clicked, BlockFace face) {
		return callEvent(new PlayerInteractEvent(player, action, player.getItemInHand(), clicked, face));
	}

	public static PlayerTeleportEvent onPlayerTeleport(Player player, Location from, Location to, TeleportCause cause) {
		return callEvent(new PlayerTeleportEvent(player, from, to, cause));
	}

	public static PlayerLoginEvent onPlayerLogin(SpoutPlayer player) {
		BanManager manager = player.getServer().getBanManager();
		String address = player.getAddress().getAddress().getHostAddress();
		PlayerLoginEvent event = new PlayerLoginEvent(player);
		if (player.isBanned()) {
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, manager.getBanMessage(player.getName()));
		} else if (manager.isIpBanned(address)) {
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, manager.getIpBanMessage(address));
		} else if (player.getServer().hasWhitelist() && player.getServer().getWhitelist().contains(player.getName())) {
			event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "You are not whitelisted on this server");
		} else if (player.getServer().getOnlinePlayers().length >= player.getServer().getMaxPlayers()) {
			event.disallow(PlayerLoginEvent.Result.KICK_FULL,
					"The server is full (" + player.getServer().getMaxPlayers() + " players).");
		}
		return callEvent(event);
	}

	public static PlayerPreLoginEvent onPlayerPreLogin(String name, Session session) {
		return callEvent(new PlayerPreLoginEvent(name, session.getAddress().getAddress()));
	}

	public static PlayerChangedWorldEvent onPlayerChangedWorld(SpoutPlayer player, SpoutWorld fromWorld) {
		return callEvent(new PlayerChangedWorldEvent(player, fromWorld));
	}

	public static PlayerAnimationEvent onPlayerAnimate(SpoutPlayer player) {
		return callEvent(new PlayerAnimationEvent(player));
	}

	// -- Block Events

	public static PlayerToggleSneakEvent onPlayerToggleSneak(Player player, boolean isSneaking) {
		return callEvent(new PlayerToggleSneakEvent(player, isSneaking));
	}

	public static BlockBreakEvent onBlockBreak(Block block, Player player) {
		return callEvent(new BlockBreakEvent(block, player));
	}

	public static BlockDamageEvent onBlockDamage(Player player, Block block) {
		return onBlockDamage(player, block, player.getItemInHand(), false);
	}

	public static BlockDamageEvent onBlockDamage(Player player, Block block, ItemStack tool, boolean instaBreak) {
		return callEvent(new BlockDamageEvent(player, block, tool, instaBreak));
	}

	public static BlockPlaceEvent onBlockPlace(Block block, BlockState newState, Block against, Player player) {
		return callEvent(new BlockPlaceEvent(block, newState, against, player.getItemInHand(), player, true));
	}

	public static BlockPhysicsEvent onBlockPhysics(SpoutBlock block) {
		return callEvent(new BlockPhysicsEvent(block, block.getTypeId()));
	}

	public static BlockPhysicsEvent onBlockPhysics(SpoutBlock block, int changedType) {
		return callEvent(new BlockPhysicsEvent(block, changedType));
	}

	public static BlockCanBuildEvent onBlockCanBuild(SpoutBlock block, int newId, BlockFace against) {
		return callEvent(new BlockCanBuildEvent(block, newId, BlockProperties.get(newId).getPhysics().canPlaceAt(block, against)));
	}

	// -- Server Events

	public static ServerListPingEvent onServerListPing(InetAddress address, String message, int online, int max) {
		return callEvent(new ServerListPingEvent(address, message, online, max));
	}

	public static ServerCommandEvent onServerCommand(ConsoleCommandSender sender, String command) {
		return callEvent(new ServerCommandEvent(sender, command));
	}

	// -- World Events

	public static ChunkLoadEvent onChunkLoad(SpoutChunk chunk, boolean isNew) {
		return callEvent(new ChunkLoadEvent(chunk, isNew));
	}

	public static ChunkPopulateEvent onChunkPopulate(SpoutChunk populatedChunk) {
		return callEvent(new ChunkPopulateEvent(populatedChunk));
	}

	public static ChunkUnloadEvent onChunkUnload(SpoutChunk chunk) {
		return callEvent(new ChunkUnloadEvent(chunk));
	}

	public static SpawnChangeEvent onSpawnChange(SpoutWorld world, Location previousLocation) {
		return callEvent(new SpawnChangeEvent(world, previousLocation));
	}

	public static WorldInitEvent onWorldInit(SpoutWorld world) {
		return callEvent(new WorldInitEvent(world));
	}

	public static WorldLoadEvent onWorldLoad(SpoutWorld world) {
		return callEvent(new WorldLoadEvent(world));
	}

	public static WorldSaveEvent onWorldSave(SpoutWorld world) {
		return callEvent(new WorldSaveEvent(world));
	}

	public static WorldUnloadEvent onWorldUnload(SpoutWorld world) {
		return callEvent(new WorldUnloadEvent(world));
	}

	// -- Weather Events

	public static LightningStrikeEvent onLightningStrike(LightningStrike strike, SpoutWorld world) {
		return callEvent(new LightningStrikeEvent(world, strike));
	}

	public static ThunderChangeEvent onThunderChange(SpoutWorld world, boolean to) {
		return callEvent(new ThunderChangeEvent(world, to));
	}

	public static WeatherChangeEvent onWeatherChange(SpoutWorld world, boolean to) {
		return callEvent(new WeatherChangeEvent(world, to));
	}

	// -- Entity Events

	public static CreatureSpawnEvent onCreatureSpawn(Entity entity, CreatureSpawnEvent.SpawnReason reason) {
		return callEvent(new CreatureSpawnEvent(entity, null, entity.getLocation(), reason));
	}

	// -- Vehicle Events

	public static VehicleCreateEvent onVehicleCreate(Vehicle vehicle) {
		return callEvent(new VehicleCreateEvent(vehicle));
	}

	public static VehicleMoveEvent onVehicleMove(Vehicle vehicle, Location from) {
		return callEvent(new VehicleMoveEvent(vehicle, from, vehicle.getLocation()));
	}

	public static VehicleDamageEvent onVehicleDamage(Vehicle vehicle, Entity attacker, int damage) {
		return callEvent(new VehicleDamageEvent(vehicle, attacker, damage));
	}

	public static VehicleDestroyEvent onVehicleDestroy(Vehicle vehicle, Entity attacker) {
		return callEvent(new VehicleDestroyEvent(vehicle, attacker));
	}

	public static VehicleEnterEvent onVehicleEnter(Vehicle vehicle, LivingEntity entered) {
		return callEvent(new VehicleEnterEvent(vehicle, entered));
	}

	public static VehicleExitEvent onVehicleExit(Vehicle vehicle, LivingEntity exiting) {
		return callEvent(new VehicleExitEvent(vehicle, exiting));
	}

	public static VehicleBlockCollisionEvent onVehicleBlockCollide(Vehicle vehicle, Block block) {
		return callEvent(new VehicleBlockCollisionEvent(vehicle, block));
	}

	public static VehicleEntityCollisionEvent onVehicleEntityCollide(Vehicle vehicle, Entity entity) {
		return callEvent(new VehicleEntityCollisionEvent(vehicle, entity));
	}

	public static VehicleUpdateEvent onVehicleUpdate(Vehicle vehicle) {
		return callEvent(new VehicleUpdateEvent(vehicle));
	}
}
