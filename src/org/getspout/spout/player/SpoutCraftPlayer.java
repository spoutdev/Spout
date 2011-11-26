/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout.player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.ContainerPlayer;
import net.minecraft.server.ContainerWorkbench;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.TileEntityDispenser;
import net.minecraft.server.TileEntityFurnace;
import net.minecraft.server.TileEntitySign;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.getspout.spout.PacketCompressionThread;
import org.getspout.spout.Spout;
import org.getspout.spout.SpoutNetServerHandler;
import org.getspout.spout.SpoutPermissibleBase;
import org.getspout.spout.block.SpoutCraftChunk;
import org.getspout.spout.inventory.SpoutCraftInventory;
import org.getspout.spout.inventory.SpoutCraftInventoryPlayer;
import org.getspout.spout.inventory.SpoutCraftingInventory;
import org.getspout.spout.packet.CustomPacket;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;
import org.getspout.spoutapi.event.permission.PlayerPermissionEvent;
import org.getspout.spoutapi.gui.InGameScreen;
import org.getspout.spoutapi.gui.GenericOverlayScreen;
import org.getspout.spoutapi.gui.OverlayScreen;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.inventory.SpoutPlayerInventory;
import org.getspout.spoutapi.io.CRCStore.URLCheck;
import org.getspout.spoutapi.io.CRCStoreRunnable;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.packet.CompressablePacket;
import org.getspout.spoutapi.packet.PacketAirTime;
import org.getspout.spoutapi.packet.PacketAlert;
import org.getspout.spoutapi.packet.PacketClipboardText;
import org.getspout.spoutapi.packet.PacketEntitySkin;
import org.getspout.spoutapi.packet.PacketEntityTitle;
import org.getspout.spoutapi.packet.PacketMovementModifiers;
import org.getspout.spoutapi.packet.PacketNotification;
import org.getspout.spoutapi.packet.PacketOpenScreen;
import org.getspout.spoutapi.packet.PacketOpenSignGUI;
import org.getspout.spoutapi.packet.PacketRenderDistance;
import org.getspout.spoutapi.packet.PacketScreenshot;
import org.getspout.spoutapi.packet.PacketSetVelocity;
import org.getspout.spoutapi.packet.PacketSkinURL;
import org.getspout.spoutapi.packet.PacketTexturePack;
import org.getspout.spoutapi.packet.PacketWidget;
import org.getspout.spoutapi.packet.SpoutPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;
import org.getspout.spoutapi.player.EntitySkinType;
import org.getspout.spoutapi.player.PlayerInformation;
import org.getspout.spoutapi.player.RenderDistance;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutCraftPlayer extends CraftPlayer implements SpoutPlayer {
	protected SpoutCraftInventoryPlayer inventory = null;
	protected Keyboard forward = Keyboard.KEY_UNKNOWN;
	protected Keyboard back = Keyboard.KEY_UNKNOWN;
	protected Keyboard left = Keyboard.KEY_UNKNOWN;
	protected Keyboard right = Keyboard.KEY_UNKNOWN;
	protected Keyboard jump = Keyboard.KEY_UNKNOWN;
	protected Keyboard inventoryKey = Keyboard.KEY_UNKNOWN;
	protected Keyboard drop = Keyboard.KEY_UNKNOWN;
	protected Keyboard chat = Keyboard.KEY_UNKNOWN;
	protected Keyboard togglefog = Keyboard.KEY_UNKNOWN;
	protected Keyboard sneak = Keyboard.KEY_UNKNOWN;
	private int buildVersion = -1;
	public RenderDistance currentRender = null;
	protected RenderDistance maximumRender = null;
	protected RenderDistance minimumRender = null;
	protected String clipboard = null;
	protected InGameScreen mainScreen;
	protected Permissible perm;
	private double gravityMod = 1;
	private double swimmingMod = 1;
	private double walkingMod = 1;
	private double jumpingMod = 1;
	private double airspeedMod = 1;
	private boolean fly;
	private String versionString = "not set";
	private Location lastClicked = null;
	private boolean precachingComplete = false;
	private ScreenType activeScreen = ScreenType.GAME_SCREEN;
	private GenericOverlayScreen currentScreen = null;
	private Location lastTickLocation = null;
	public SpoutCraftChunk lastTickChunk = null;
	public Set<SpoutCraftChunk> lastTickAdjacentChunks = new HashSet<SpoutCraftChunk>(); 
	private boolean screenOpenThisTick = false;
	public LinkedList<SpoutPacket> queued = new LinkedList<SpoutPacket>();
	private LinkedList<SpoutPacket> delayedPackets = new LinkedList<SpoutPacket>();
	public long velocityAdjustmentTime = System.currentTimeMillis();

	public SpoutCraftPlayer(CraftServer server, EntityPlayer entity) {
		super(server, entity);
		createInventory(null);
		if (entity.netServerHandler != null) {
			CraftPlayer player = entity.netServerHandler.getPlayer();
			perm = new SpoutPermissibleBase((Permissible) player.addAttachment(Bukkit.getServer().getPluginManager().getPlugin("Spout")).getPermissible());
			perm.recalculatePermissions();
		}
		else {
			perm = new SpoutPermissibleBase((Permissible)new PermissibleBase(this));
			perm.recalculatePermissions();
		}
		mainScreen = new InGameScreen(this.getEntityId());

		mainScreen.toggleSurvivalHUD(!getGameMode().equals(GameMode.CREATIVE));
		fly = ((CraftServer)Bukkit.getServer()).getHandle().server.allowFlight;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CraftPlayer)) {
			return false;
		}
		final CraftPlayer other = (CraftPlayer) obj;
		if ((this.getName() == null) ? (other.getName() != null) : !this.getName().equals(other.getName())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 97 * hash + (this.getName() != null ? this.getName().hashCode() : 0);
		return hash;
	}
	/* Interace Overriden Public Methods */

	@Override
	public boolean isPermissionSet(String name) {
		return perm.isPermissionSet(name);
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		return this.perm.isPermissionSet(perm);
	}

	@Override
	public boolean hasPermission(String name) {
		boolean defaultResult = this.perm.hasPermission(name);
		PlayerPermissionEvent event = new PlayerPermissionEvent(this, name, Bukkit.getServer().getPluginManager().getPermission(name), defaultResult);
		Bukkit.getServer().getPluginManager().callEvent(event);
		return event.getResult();
	}

	@Override
	public boolean hasPermission(Permission perm) {
		boolean defaultResult = this.perm.hasPermission(perm);
		PlayerPermissionEvent event = new PlayerPermissionEvent(this, perm.getName(), perm, defaultResult);
		Bukkit.getServer().getPluginManager().callEvent(event);
		return event.getResult();
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		return perm.addAttachment(plugin, name, value);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		return perm.addAttachment(plugin);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		return perm.addAttachment(plugin, name, value, ticks);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		return perm.addAttachment(plugin, ticks);
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		perm.removeAttachment(attachment);
	}

	@Override
	public void recalculatePermissions() {
		perm.recalculatePermissions();
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return perm.getEffectivePermissions();
	}
	
	@Override
	public SpoutPlayerInventory getInventory() {
		if (this.inventory == null) {
			createInventory(null);
		}
		else if (!((SpoutCraftInventoryPlayer)this.inventory).getHandle().equals(this.getHandle().inventory)) {
			createInventory(this.inventory.getName());
		}
		return (SpoutPlayerInventory)this.inventory;
	}

	@Override
	public void setMaximumAir(int time) {
		if (isSpoutCraftEnabled()) {
			sendPacket(new PacketAirTime(time, this.getRemainingAir()));
		}
		super.setMaximumAir(time);
	}

	@Override
	public void setRemainingAir(int time) {
		if (isSpoutCraftEnabled()) {
			sendPacket(new PacketAirTime(this.getMaximumAir(), time));
		}
		super.setRemainingAir(time);
	}
	
	@Override
	public void setVelocity(Vector velocity) {
		super.setVelocity(velocity);
		if (isSpoutCraftEnabled()) {
			PlayerVelocityEvent event = new PlayerVelocityEvent(this, velocity);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if(!event.isCancelled()) {
				sendPacket(new PacketSetVelocity(getEntityId(), event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ()));
			}
			double speedX = Math.abs(event.getVelocity().getX() * event.getVelocity().getX());
			double speedY = Math.abs(event.getVelocity().getY() * event.getVelocity().getY());
			double speedZ = Math.abs(event.getVelocity().getZ() * event.getVelocity().getZ());
			double speed = speedX + speedY + speedZ;
			
			velocityAdjustmentTime = System.currentTimeMillis() + (long)(speed * 5);
			getHandle().velocityChanged = false; //prevents nms from sending an override packet later, but still tells the server about the new velocity
		}
	}

	/* Inteface New Public Methods */
	public boolean closeActiveWindow() {
		InventoryCloseEvent event = new InventoryCloseEvent(this, getActiveInventory(), getDefaultInventory());
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return false;
		}
		
		getHandle().closeInventory();

		getNetServerHandler().setActiveInventory(false);
		getNetServerHandler().setActiveInventoryLocation(null);
		return true;
	}

	public boolean openInventoryWindow(Inventory inventory) {
		return openInventoryWindow(inventory, null, false);
	}

	public boolean openInventoryWindow(Inventory inventory, Location location) {
		return openInventoryWindow(inventory, location, false);
	}

	public boolean openInventoryWindow(Inventory inventory, Location location, boolean ignoreDistance) {
		InventoryOpenEvent event = new InventoryOpenEvent(this, inventory, this.inventory, location);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return false;
		}
		getNetServerHandler().setActiveInventory(true);
		getNetServerHandler().setActiveInventoryLocation(location);
		IInventory dialog = ((CraftInventory)event.getInventory()).getInventory();
		if (dialog instanceof TileEntityDispenser) {
			getHandle().a((TileEntityDispenser)dialog);
		}
		else if (dialog instanceof TileEntityFurnace) {
			getHandle().a((TileEntityFurnace)dialog);
		}
		else {
			getHandle().a(dialog);
		}
		return true;
		/*int id;
		if (dialog instanceof TileEntityDispenser) {
			id = 3;
		}
		else if (dialog instanceof TileEntityFurnace) {
			id = 2;
		}
		else {
			id = 0;
		}
		String title = dialog.getName();
		if (inventory instanceof SpoutInventory) {
			title = ((SpoutInventory)inventory).getTitle();
		}

		updateWindowId();
		getNetServerHandler().sendPacket(new Packet100OpenWindow(getActiveWindowId(), id, title, dialog.getSize()));
		getHandle().activeContainer = new ContainerChest(getHandle().inventory, dialog);
		getHandle().activeContainer.f = getActiveWindowId();
		getHandle().activeContainer.a((ICrafting) this);
		return true;*/
	}

	@Override
	public boolean openWorkbenchWindow(Location location) {
		if (location.getBlock().getType() != Material.WORKBENCH) {
			throw new UnsupportedOperationException("Must be a valid workbench!");
		}
		else {
			ContainerWorkbench temp = new ContainerWorkbench(getHandle().inventory, ((CraftWorld)location.getWorld()).getHandle(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
			IInventory inventory = temp.resultInventory;
			InventoryOpenEvent event = new InventoryOpenEvent(this, new SpoutCraftInventory(inventory), this.inventory, location);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return false;
			}
			getNetServerHandler().setActiveInventory(true);
			getNetServerHandler().setActiveInventoryLocation(location);
			getHandle().b(location.getBlockX(), location.getBlockY(), location.getBlockZ());
			return true;
		}
	}

	@Override
	public InGameScreen getMainScreen() {
		return mainScreen;
	}

	@Override
	public Screen getCurrentScreen() {
		if(getActiveScreen() == ScreenType.GAME_SCREEN){
			return getMainScreen();
		} else {
			return currentScreen;
		}
	}

	@Override
	public boolean isSpoutCraftEnabled() {
		return getBuildVersion() > -1;
	}

	@Override
	public Keyboard getForwardKey() {
		return forward;
	}

	@Override
	public Keyboard getBackwardKey() {
		return back;
	}

	@Override
	public Keyboard getLeftKey() {
		return left;
	}

	@Override
	public Keyboard getRightKey() {
		return right;
	}

	@Override
	public Keyboard getJumpKey() {
		return jump;
	}

	@Override
	public Keyboard getInventoryKey() {
		return inventoryKey;
	}

	@Override
	public Keyboard getDropItemKey() {
		return drop;
	}

	@Override
	public Keyboard getChatKey() {
		return chat;
	}

	@Override
	public Keyboard getToggleFogKey() {
		return togglefog;
	}

	@Override
	public Keyboard getSneakKey() {
		return sneak;
	}

	@Override
	public RenderDistance getRenderDistance() {
		return currentRender;
	}

	@Override
	public void setRenderDistance(RenderDistance distance) {
		if (isSpoutCraftEnabled()) {
			currentRender = distance;
			sendPacket(new PacketRenderDistance(distance, null, null));
		}
	}

	@Override
	public void setRenderDistance(RenderDistance distance, boolean update) {
		if (update) {
			setRenderDistance(distance);
		}
		else {
			currentRender = distance;
		}
	}

	@Override
	public RenderDistance getMaximumRenderDistance() {
		return maximumRender;
	}

	@Override
	public void setMaximumRenderDistance(RenderDistance maximum) {
		if (isSpoutCraftEnabled()) {
			maximumRender = maximum;
			sendPacket(new PacketRenderDistance(null, maximum, null));
		}
	}

	@Override
	public void resetMaximumRenderDistance() {
		if (isSpoutCraftEnabled()) {
			maximumRender = null;
			sendPacket(new PacketRenderDistance(true, false));
		}
	}

	@Override
	public RenderDistance getMinimumRenderDistance() {
		return minimumRender;
	}

	@Override
	public void setMinimumRenderDistance(RenderDistance minimum) {
		if (isSpoutCraftEnabled()) {
			minimumRender = minimum;
			sendPacket(new PacketRenderDistance(null, null, minimum));
		}
	}

	@Override
	public void resetMinimumRenderDistance() {
		if (isSpoutCraftEnabled()) {
			minimumRender = null;
			sendPacket(new PacketRenderDistance(false, true));
		}
	}

	@Override
	public void sendNotification(String title, String message, Material toRender) {
		if (isSpoutCraftEnabled()) {
			if (toRender == null || toRender == Material.AIR)
				throw new IllegalArgumentException("The item to render may not be null or air");
			if (ChatColor.stripColor(title).length() > 26 || title.length() > 78)
				throw new UnsupportedOperationException("Notification titles can not be greater than 26 chars + 26 colors");
			if (ChatColor.stripColor(message).length() > 26 || message.length() > 78)
				throw new UnsupportedOperationException("Notification messages can not be greater than 26 chars + 26 colors");
			sendPacket(new PacketAlert(title, message, toRender.getId()));
		}
	}

	@Override
	public void sendNotification(String title, String message, Material toRender, short data, int time) {
		if (isSpoutCraftEnabled()) {
			if (toRender == null || toRender == Material.AIR)
				throw new IllegalArgumentException("The item to render may not be null or air");
			if (ChatColor.stripColor(title).length() > 26 || title.length() > 78)
				throw new UnsupportedOperationException("Notification titles can not be greater than 26 chars + 26 colors");
			if (ChatColor.stripColor(message).length() > 26 || message.length() > 78)
				throw new UnsupportedOperationException("Notification messages can not be greater than 26 chars + 26 colors");
			sendPacket(new PacketNotification(title, message, toRender.getId(), data, time));
		}
	}
	
	@Override
	public void sendNotification(String title, String message, ItemStack item, int time) {
		if (isSpoutCraftEnabled()) {
			if (item == null || item.getTypeId() == Material.AIR.getId())
				throw new IllegalArgumentException("The item to render may not be null or air");
			if (ChatColor.stripColor(title).length() > 26 || title.length() > 78)
				throw new UnsupportedOperationException("Notification titles can not be greater than 26 chars + 26 colors");
			if (ChatColor.stripColor(message).length() > 26 || message.length() > 78)
				throw new UnsupportedOperationException("Notification messages can not be greater than 26 chars + 26 colors");
			sendPacket(new PacketNotification(title, message, item.getTypeId(), item.getDurability(), time));
		}
	}

	@Override
	public String getClipboardText() {
		return clipboard;
	}

	@Override
	public void setClipboardText(String text) {
		setClipboardText(text, true);
	}

	@Override
	public void setTexturePack(String url) {
		if (isSpoutCraftEnabled()) {
			if (url == null || url.length() < 5) {
				throw new IllegalArgumentException("Invalid URL!");
			}
			if (!url.toLowerCase().endsWith(".zip")) {
				throw new IllegalArgumentException("A Texture Pack must be in a .zip format");
			}
			final String finalURL = url;
			URLCheck urlCheck = new URLCheck(url, new byte[16384], new CRCStoreRunnable() {
				
				Long CRC;
				
				public void setCRC(Long CRC) {
					this.CRC = CRC;
				}
				
				public void run() {
					sendPacket(new PacketTexturePack(finalURL, CRC));
				}
				
			});
			urlCheck.start();
		}
	}

	@Override
	public void resetTexturePack() {
		if (isSpoutCraftEnabled()) {
			sendPacket(new PacketTexturePack("[none]", 0));
		}
	}

	@Override
	public void setClipboardText(String text, boolean updateClient) {
		if (isSpoutCraftEnabled()) {
			clipboard = text;
			if (updateClient){
				sendPacket(new PacketClipboardText(text));
			}
		}
	}
	

	@Override
	public Location getActiveInventoryLocation() {
		return getNetServerHandler().getActiveInventoryLocation();
	}

	@Override
	public void setActiveInventoryLocation(Location loc) {
		getNetServerHandler().setActiveInventoryLocation(loc);
	}

	public void reconnect(String hostname, int port) {
		if (hostname.indexOf(":") != -1) {
			throw new IllegalArgumentException("Hostnames may not the : symbol");
		}
		this.kickPlayer("[Redirect] Please reconnect to : " + hostname + ":" + port);
	}

	@Override
	public void reconnect(String hostname) {
		if (hostname.indexOf(":") != -1) {
			String[] split = hostname.split(":");
			if (split.length != 2) {
				throw new IllegalArgumentException("Improperly formatted hostname: " + hostname);
			}
			int port;
			try {
				port = Integer.parseInt(split[1]);
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException("Unable to parse port number: " + split[1] + " in " + hostname);
			}
			reconnect(split[0], port);
		}
		this.kickPlayer("[Redirect] Please reconnect to : " + hostname);
	}

	@Override
	public PlayerInformation getInformation() {
		return SpoutManager.getPlayerManager().getPlayerInfo(this);
	}
	
	@Override
	public ScreenType getActiveScreen() {
		return activeScreen;
	}
	
	@Override
	public void openScreen(ScreenType type) {
		openScreen(type, true);
	}

	@Override
	public void sendScreenshotRequest() {
		PacketScreenshot packets = new PacketScreenshot();
		sendPacket(packets);
	}

	@Override
	public void openScreen(ScreenType type, boolean packet) {
		if(type == activeScreen || screenOpenThisTick) {
			return;
		}
		screenOpenThisTick = packet;
		activeScreen = type;
		if (packet) {
			sendPacket(new PacketOpenScreen(type));
		}
		if(activeScreen != ScreenType.GAME_SCREEN && activeScreen != ScreenType.CUSTOM_SCREEN) {
			currentScreen = new GenericOverlayScreen(getEntityId(), getActiveScreen());
			PacketWidget packetw = new PacketWidget(currentScreen, currentScreen.getId());
			sendPacket(packetw);
			currentScreen.onTick();
		} else {
			currentScreen = null;
		}
	}

	@Override
	public double getGravityMultiplier() {
		return gravityMod;
	}

	@Override
	public double getSwimmingMultiplier() {
		return swimmingMod;
	}

	@Override
	public double getWalkingMultiplier() {
		return walkingMod;
	}

	@Override
	public void setGravityMultiplier(double multiplier) {
		gravityMod = multiplier;
		updateMovement();
	}

	@Override
	public void setSwimmingMultiplier(double multiplier) {
		swimmingMod = multiplier;
		updateMovement();
	}

	@Override
	public void setWalkingMultiplier(double multiplier) {
		walkingMod = multiplier;
		updateMovement();
	}

	@Override
	public double getJumpingMultiplier() {
		return jumpingMod;
	}

	@Override
	public void setJumpingMultiplier(double multiplier) {
		this.jumpingMod = multiplier;
		updateMovement();
	}

	@Override
	public double getAirSpeedMultiplier() {
		return airspeedMod;
	}

	@Override
	public void setAirSpeedMultiplier(double multiplier) {
		airspeedMod = multiplier;
		updateMovement();
		
	}

	@Override
	public void resetMovement() {
		gravityMod = 1;
		walkingMod = 1;
		swimmingMod = 1;
		jumpingMod = 1;
		airspeedMod = 1;
		updateMovement();
	}

	@Override
	public boolean canFly() {
		return fly;
	}

	@Override
	public void setCanFly(boolean fly) {
		this.fly = fly;
	}

	@Override
	public boolean sendInventoryEvent() {
		SpoutNetServerHandler snsh = (SpoutNetServerHandler) this.getHandle().netServerHandler;
		snsh.activeInventory = true;
		InventoryOpenEvent event = new InventoryOpenEvent(this, snsh.getActiveInventory(), snsh.getDefaultInventory(), snsh.getActiveInventoryLocation());
		Bukkit.getServer().getPluginManager().callEvent(event);
		return event.isCancelled();
	}

	@Override
	public Location getLastClickedLocation() {
		if (lastClicked != null) {
			return lastClicked.clone();
		}
		return null;
	}

	@Override
	public void setPreCachingComplete(boolean complete) {
		if(!precachingComplete) {
			precachingComplete = complete;
		}
	}

	@Override
	public boolean isPreCachingComplete() {
		if(isSpoutCraftEnabled()){
			return precachingComplete;
		} else {
			return true;
		}
	}

	@Override
	public void openSignEditGUI(Sign sign) {
		if(sign != null && isSpoutCraftEnabled()) {
			sendPacket(new PacketOpenSignGUI(sign.getX(), sign.getY(), sign.getZ()));
			TileEntitySign tes = (TileEntitySign) ((CraftWorld)((CraftBlock)sign.getBlock()).getWorld()).getTileEntityAt(sign.getX(), sign.getY(), sign.getZ()); // Found a hidden trace to The Elder Scrolls. Bethesdas Lawyers are right!
			tes.isEditable = true;
		}
	}

	public void updateKeys(byte[] keys) {
		this.forward = Keyboard.getKey(keys[0]);
		this.back = Keyboard.getKey(keys[2]);
		this.left = Keyboard.getKey(keys[1]);
		this.right = Keyboard.getKey(keys[3]);
		this.jump = Keyboard.getKey(keys[4]);
		this.inventoryKey = Keyboard.getKey(keys[5]);
		this.drop = Keyboard.getKey(keys[6]);
		this.chat = Keyboard.getKey(keys[7]);
		this.togglefog = Keyboard.getKey(keys[8]);
		this.sneak = Keyboard.getKey(keys[9]);
	}
	
	//Sends a packet delayed by 1 tick
	public void sendDelayedPacket(SpoutPacket packet) {
		delayedPackets.add(packet);
	}

	public void sendPacket(SpoutPacket packet) {
		if (!isSpoutCraftEnabled()) {
			if (queued != null) {
				queued.add(packet);
			}
		}
		else {
			if (packet instanceof CompressablePacket) {
				CompressablePacket compressable = ((CompressablePacket)packet);
				//uncompressed, send it to the compression thread
				if (!compressable.isCompressed()) {
					PacketCompressionThread.add(compressable, this);
					return;
				}
			}
			getNetServerHandler().sendPacket(new CustomPacket(packet));
		}
	}

	public void sendPacket(MCPacket packet) {
		if(!(packet instanceof MCCraftPacket)) {
			throw new IllegalArgumentException("Packet not of type MCCraftPacket");
		}
		MCCraftPacket p = (MCCraftPacket)packet;
		getHandle().netServerHandler.sendPacket(p.getPacket());
	}

	public void sendImmediatePacket(MCPacket packet) {
		if(!(packet instanceof MCCraftPacket)) {
			throw new IllegalArgumentException("Packet not of type MCCraftPacket");
		}
		MCCraftPacket p = (MCCraftPacket)packet;
		if (getHandle().netServerHandler.getClass().equals(SpoutNetServerHandler.class)) {
			getNetServerHandler().sendImmediatePacket(p.getPacket());
		}
		else {
			sendPacket(packet);
		}
	}
	
	private void checkUrl(String url) {
		if (url == null || url.length() < 5) {
			throw new UnsupportedOperationException("Invalid URL");
		}
		if (!url.substring(url.length() - 4, url.length()).equalsIgnoreCase(".png")) {
			throw new UnsupportedOperationException("All skins must be a PNG image");
		}
		if (url.length() > 255) {
			throw new UnsupportedOperationException("All Url's must be shorter than 256 characters");
		}
	}
	
	private String skin = "http://s3.amazonaws.com/MinecraftSkins/" + getName() + ".png";
	private HashMap<String, String> skinsFor = new HashMap<String, String>();
	
	private String cape = "http://s3.amazonaws.com/MinecraftCloaks/" + getName() + ".png";
	private HashMap<String, String> capesFor = new HashMap<String, String>();
	
	private String title = getName();
	private HashMap<String, String> titlesFor = new HashMap<String, String>();
	
	public void updateAppearance() {
		for (LivingEntity le : getWorld().getLivingEntities()) {
			//Update entity skins
			for (EntitySkinType type : EntitySkinType.values()) {
				if (getInformation().getEntitySkin(le, type) != null) {
					sendDelayedPacket(new PacketEntitySkin(le, getInformation().getEntitySkin(le, type), type.getId()));
				}
			}
		}
		
		for (Player p : getWorld().getPlayers()) {
			if (p instanceof SpoutPlayer)
				sendDelayedPacket(new PacketSkinURL(p.getEntityId(), getSkin((SpoutPlayer) p), getCape((SpoutPlayer) p)));
		}
	}
	
	@Override
	public void setSkin(String url) {
		checkUrl(url);
		skin = url;
		
		for (Player p : getWorld().getPlayers()) {
			((SpoutPlayer)p).sendPacket(new PacketSkinURL(getEntityId(), getSkin((SpoutPlayer)p)));
		}
	}

	@Override
	public void setSkinFor(SpoutPlayer viewingPlayer, String url) {
		checkUrl(url);
		skinsFor.put(viewingPlayer.getName(), url);
		viewingPlayer.sendPacket(new PacketSkinURL(getEntityId(), url));
	}

	@Override
	public String getSkin() {
		return skin;
	}

	@Override
	public String getSkin(SpoutPlayer viewingPlayer) {
		if (skinsFor.containsKey(viewingPlayer.getName())) {
			return skinsFor.get(viewingPlayer.getName());
		}
		return getSkin();
	}

	@Override
	public void resetSkin() {
		setSkin("http://s3.amazonaws.com/MinecraftSkins/" + getName() + ".png");
	}

	@Override
	public void resetSkinFor(SpoutPlayer viewingPlayer) {
		setSkinFor(viewingPlayer, "http://s3.amazonaws.com/MinecraftSkins/" + getName() + ".png");
	}

	@Override
	public void setCape(String url) {
		checkUrl(url);
		cape = url;
		
		for (Player p : getWorld().getPlayers()) {
			((SpoutPlayer)p).sendPacket(new PacketSkinURL(getCape((SpoutPlayer)p), getEntityId()));
		}
	}

	@Override
	public void setCapeFor(SpoutPlayer viewingPlayer, String url) {
		checkUrl(url);
		capesFor.put(viewingPlayer.getName(), url);
		viewingPlayer.sendPacket(new PacketSkinURL(url, getEntityId()));
	}

	@Override
	public String getCape() {
		return cape;
	}

	@Override
	public String getCape(SpoutPlayer viewingPlayer) {
		if (capesFor.containsKey(viewingPlayer.getName())) {
			return capesFor.get(viewingPlayer.getName());
		}
		return getCape();
	}

	@Override
	public void resetCape() {
		setCape("http://s3.amazonaws.com/MinecraftCloaks/" + getName() + ".png");
	}

	@Override
	public void resetCapeFor(SpoutPlayer viewingPlayer) {
		setCapeFor(viewingPlayer, "http://s3.amazonaws.com/MinecraftCloaks/" + getName() + ".png");
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
		
		for (Player p : getWorld().getPlayers()) {
			((SpoutPlayer)p).sendPacket(new PacketEntityTitle(getEntityId(), getTitleFor((SpoutPlayer)p)));
		}
	}

	@Override
	public void setTitleFor(SpoutPlayer viewingPlayer, String title) {
		titlesFor.put(viewingPlayer.getName(), title);
		viewingPlayer.sendPacket(new PacketEntityTitle(getEntityId(), title));
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getTitleFor(SpoutPlayer viewingPlayer) {
		if (titlesFor.containsKey(viewingPlayer.getName())) {
			return titlesFor.get(viewingPlayer.getName());
		}
		return getTitle();
	}

	@Override
	public void hideTitle() {
		setTitle("[hide]");
	}

	@Override
	public void hideTitleFrom(SpoutPlayer viewingPlayer) {
		setTitleFor(viewingPlayer, "[hide]");
	}

	@Override
	public void resetTitle() {
		setTitle(getName());
	}

	@Override
	public void resetTitleFor(SpoutPlayer viewingPlayer) {
		setTitleFor(viewingPlayer, getName());
	}

	@Override
	public void setEntitySkin(LivingEntity target, String url, EntitySkinType type) {
		getInformation().setEntitySkin(target, url, type);
		sendDelayedPacket(new PacketEntitySkin(target, url, type.getId()));
	}

	@Override
	public void resetEntitySkin(LivingEntity target) {
		getInformation().setEntitySkin(target, null);
		sendPacket(new PacketEntitySkin(target, "[reset]", (byte) 0));
	}
	
	/*Non Inteface public methods */
	
	public Location getLastTickLocation() {
		return lastTickLocation;
	}
	
	public void setLastTickLocation(Location loc) {
		lastTickLocation = loc;
	}

	public Location getRawLastClickedLocation() {
		return lastClicked;
	}
	
	public void setLastClickedLocation(Location location) {
		lastClicked = location;
	}

	public void createInventory(String name) {
		if (this.getHandle().activeContainer instanceof ContainerPlayer) {
			this.inventory = new SpoutCraftInventoryPlayer(this.getHandle().inventory, 
					new SpoutCraftingInventory(((ContainerPlayer)this.getHandle().activeContainer).craftInventory, ((ContainerPlayer)this.getHandle().activeContainer).resultInventory));
			if (name != null) {
				this.inventory.setName(name);
			}
		}
		else {
			this.inventory = new SpoutCraftInventoryPlayer(this.getHandle().inventory, 
					new SpoutCraftingInventory(((ContainerPlayer)this.getHandle().defaultContainer).craftInventory, ((ContainerPlayer)this.getHandle().defaultContainer).resultInventory));
			if (name != null) {
				this.inventory.setName(name);
			}
		}
	}

	public int getActiveWindowId() {
		Field id;
		try {
			id = EntityPlayer.class.getDeclaredField("bX");
			id.setAccessible(true);
			return (Integer)id.get(getHandle());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void updateWindowId() {
		Method id;
		try {
			id = EntityPlayer.class.getDeclaredMethod("aq");
			id.setAccessible(true);
			id.invoke(getHandle());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Inventory getActiveInventory() {
		return getNetServerHandler().getActiveInventory();
	}

	public Inventory getDefaultInventory() {
		return getNetServerHandler().getDefaultInventory();
	}

	public SpoutNetServerHandler getNetServerHandler() {
		if (!getHandle().netServerHandler.getClass().equals(SpoutNetServerHandler.class)) {
			updateNetServerHandler(this);
		}
		return (SpoutNetServerHandler) getHandle().netServerHandler;
	}

	public int getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(int build) {
		buildVersion = build;
		if (isSpoutCraftEnabled() && queued != null) {
			for (SpoutPacket packet : queued) {
				sendPacket(packet);
			}
		}
		queued = null;
	}

	public void setVersionString(String versionString) {
		this.versionString = versionString;
	}

	public String getVersionString() {
		return versionString;
	}

	public void onTick() {
		mainScreen.onTick();
		Screen currentScreen = getCurrentScreen();
		if(currentScreen != null && currentScreen instanceof OverlayScreen){
			currentScreen.onTick();
		}
		screenOpenThisTick = false;
		
		//Because the player teleport event doesn't always fire :(
		Location current = getLocation();
		if (lastTickLocation != null) {
			if (!lastTickLocation.getWorld().equals(current.getWorld())) {
				doPostPlayerChangeWorld(lastTickLocation.getWorld(), current.getWorld());
			}
		}
		lastTickLocation = current;
		
		for (SpoutPacket packet : delayedPackets) {
			sendPacket(packet);
		}
		delayedPackets.clear();
		
		//Do this last!
		getNetServerHandler().syncFlushPacketQueue();
	} 
	
	public void doPostPlayerChangeWorld(World oldWorld, World newWorld) {
		SpoutCraftPlayer.updateBukkitEntity(this);
		if (isSpoutCraftEnabled()) {
			updateMovement();
			Spout.getInstance().getEntityTrackingManager().onPostWorldChange(this);
			Spout.getInstance().getPlayerTrackingManager().onWorldChange(this);
		}
		updateAppearance();
	}

	public void updateMovement() {
		if (isSpoutCraftEnabled()) {
			sendPacket(new PacketMovementModifiers(gravityMod, walkingMod, swimmingMod, jumpingMod, airspeedMod));
		}
	}

	/* Non Interface public static methods */

	public static boolean setNetServerHandler(NetworkManager nm, NetServerHandler nsh) {
		try {
			Field p = nm.getClass().getDeclaredField("p");
			p.setAccessible(true);
			p.set(nm, nsh);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static boolean resetNetServerHandler(Player player) {
		CraftPlayer cp = (CraftPlayer)player;
		CraftServer server = (CraftServer)Bukkit.getServer();

		if (cp.getHandle().netServerHandler instanceof SpoutNetServerHandler) {
			NetServerHandler oldHandler = cp.getHandle().netServerHandler;
			Set<ChunkCoordIntPair> chunkUpdateQueue = ((SpoutNetServerHandler)cp.getHandle().netServerHandler).getChunkUpdateQueue();
			for(ChunkCoordIntPair c : chunkUpdateQueue) {
				cp.getHandle().chunkCoordIntPairQueue.add(c);
			}
			((SpoutNetServerHandler)cp.getHandle().netServerHandler).flushUnloadQueue();
			cp.getHandle().netServerHandler.a();
			Location loc = player.getLocation();
			NetServerHandler handler = new NetServerHandler(server.getHandle().server, cp.getHandle().netServerHandler.networkManager, cp.getHandle());
			handler.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			cp.getHandle().netServerHandler = handler;
			NetworkManager nm = cp.getHandle().netServerHandler.networkManager;
			setNetServerHandler(nm, cp.getHandle().netServerHandler);
			oldHandler.disconnected = true;
			((CraftServer)Spout.getInstance().getServer()).getServer().networkListenThread.a(handler);
			return true;
		}
		return false;
	}

	public static boolean updateNetServerHandler(Player player) {
		CraftPlayer cp = (CraftPlayer)player;
		CraftServer server = (CraftServer)Bukkit.getServer();
		if (!(cp.getHandle().netServerHandler.getClass().equals(SpoutNetServerHandler.class))) {
			NetServerHandler oldHandler = cp.getHandle().netServerHandler;
			Location loc = player.getLocation();
			SpoutNetServerHandler handler = new SpoutNetServerHandler(server.getHandle().server, cp.getHandle().netServerHandler.networkManager, cp.getHandle());
			for(Object o : cp.getHandle().playerChunkCoordIntPairs) {
				ChunkCoordIntPair c = (ChunkCoordIntPair) o;
				handler.addActiveChunk(c);
			}
			handler.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			cp.getHandle().netServerHandler = handler;
			NetworkManager nm = cp.getHandle().netServerHandler.networkManager;
			setNetServerHandler(nm, cp.getHandle().netServerHandler);
			oldHandler.disconnected = true;
			((CraftServer)Spout.getInstance().getServer()).getServer().networkListenThread.a(handler);
			return true;
		}
		return false;
	}

	public static boolean updateBukkitEntity(Player player) {
		if (!(player instanceof SpoutCraftPlayer)) {
			CraftPlayer cp = (CraftPlayer)player;
			EntityPlayer ep = cp.getHandle();
			return updateBukkitEntity(ep);
		}
		return false;
	}
	
	public static boolean updateBukkitEntity(EntityPlayer ep) {
		Field bukkitEntity;
		try {
			bukkitEntity = Entity.class.getDeclaredField("bukkitEntity");
			bukkitEntity.setAccessible(true);
			org.bukkit.entity.Entity e = (org.bukkit.entity.Entity) bukkitEntity.get(ep);
			if (e == null || !e.getClass().equals(SpoutCraftPlayer.class)) {
				bukkitEntity.set(ep, new SpoutCraftPlayer((CraftServer)Bukkit.getServer(), ep));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static SpoutPlayer getPlayer(Player player) {
		if (player instanceof SpoutCraftPlayer) {
			return (SpoutCraftPlayer)player;
		}
		if ((((CraftPlayer)player).getHandle()).getBukkitEntity() instanceof SpoutCraftPlayer) {
			return (SpoutCraftPlayer)((((CraftPlayer)player).getHandle()).getBukkitEntity());
		}
		//We should never get here
		//Logger.getLogger("Minecraft").warning("Player: " + player.getName() + " was not properly updated during login!");
		updateBukkitEntity(player);
		return (SpoutCraftPlayer)((((CraftPlayer)player).getHandle()).getBukkitEntity());
	}
}
