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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.getspout.spout.Spout;
import org.getspout.spout.SpoutNetServerHandler;
import org.getspout.spout.SpoutPermissibleBase;
import org.getspout.spout.inventory.SpoutCraftInventory;
import org.getspout.spout.inventory.SpoutCraftInventoryPlayer;
import org.getspout.spout.inventory.SpoutCraftingInventory;
import org.getspout.spout.packet.CustomPacket;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;
import org.getspout.spoutapi.gui.InGameScreen;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.inventory.SpoutPlayerInventory;
import org.getspout.spoutapi.io.CRCStore.URLCheck;
import org.getspout.spoutapi.io.CRCStoreRunnable;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.packet.PacketAirTime;
import org.getspout.spoutapi.packet.PacketAlert;
import org.getspout.spoutapi.packet.PacketClipboardText;
import org.getspout.spoutapi.packet.PacketMovementModifiers;
import org.getspout.spoutapi.packet.PacketNotification;
import org.getspout.spoutapi.packet.PacketOpenScreen;
import org.getspout.spoutapi.packet.PacketOpenSignGUI;
import org.getspout.spoutapi.packet.PacketRenderDistance;
import org.getspout.spoutapi.packet.PacketSetVelocity;
import org.getspout.spoutapi.packet.PacketTexturePack;
import org.getspout.spoutapi.packet.SpoutPacket;
import org.getspout.spoutapi.packet.standard.MCPacket;
import org.getspout.spoutapi.permission.SpoutPermissible;
import org.getspout.spoutapi.player.PlayerInformation;
import org.getspout.spoutapi.player.RenderDistance;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutCraftPlayer extends CraftPlayer implements SpoutPlayer{
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
	private int minorVersion = -1;
	private int majorVersion = -1;
	public RenderDistance currentRender = null;
	protected RenderDistance maximumRender = null;
	protected RenderDistance minimumRender = null;
	protected String clipboard = null;
	protected InGameScreen mainScreen;
	protected SpoutPermissible perm;
	private double gravityMod = 1;
	private double swimmingMod = 1;
	private double walkingMod = 1;
	private double jumpingMod = 1;
	private double airspeedMod = 1;
	private boolean fly;
	private String versionString = "not set";
	private Location lastClicked = null;
	private boolean precachingComplete = false;
	
	public LinkedList<SpoutPacket> queued = new LinkedList<SpoutPacket>();

	public SpoutCraftPlayer(CraftServer server, EntityPlayer entity) {
		super(server, entity);
		createInventory(null);
		CraftPlayer player = entity.netServerHandler.getPlayer();
        perm = new SpoutPermissibleBase((PermissibleBase) player.addAttachment(Bukkit.getServer().getPluginManager().getPlugin("Spout")).getPermissible());
        perm.recalculatePermissions();
		addAttachment(Bukkit.getServer().getPluginManager().getPlugin("Spout")).getPermissible();
		perm.recalculatePermissions();
		mainScreen = new InGameScreen(this.getEntityId());
		fly = ((CraftServer)Bukkit.getServer()).getHandle().server.allowFlight;
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
		return perm.hasPermission(name);
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return this.perm.hasPermission(perm);
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
	
	public boolean hasAttachment(PermissionAttachment attachment) {
		return perm.hasAttachment(attachment);
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
		if (isSpoutCraftEnabled()) {
			sendPacket(new PacketSetVelocity(getEntityId(), velocity.getX(), velocity.getY(), velocity.getZ()));
		}
		else {
			super.setVelocity(velocity);
		}
	}

	/* Inteface New Public Methods */

	public boolean closeActiveWindow() {
		InventoryCloseEvent event = new InventoryCloseEvent(this, getActiveInventory(), getDefaultInventory());
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return false;
		}
		getHandle().x();
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
		//throw new UnsupportedOperationException("Not yet implemented!");
		return mainScreen;
	}

	@Override
	public boolean isSpoutCraftEnabled() {
		return getBuildVersion() > -1 && getMinorVersion() > -1 && getMajorVersion() > -1;
	}

	public int getVersion() {
		if (isSpoutCraftEnabled()) {
			return majorVersion * 100 + minorVersion * 10 + buildVersion;
		}
		return -1;
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
			if (ChatColor.stripColor(title).length() > 26 || title.length() > 78)
				throw new UnsupportedOperationException("Notification titles can not be greater than 26 chars + 26 colors");
			if (ChatColor.stripColor(message).length() > 26 || message.length() > 78)
				throw new UnsupportedOperationException("Notification messages can not be greater than 26 chars + 26 colors");
			sendPacket(new PacketNotification(title, message, toRender.getId(), data, time));
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

	private byte[] urlBuffer = new byte[16384];

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
			URLCheck urlCheck = new URLCheck(url, urlBuffer, new CRCStoreRunnable() {
				
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
	public void openScreen(ScreenType type) {
		sendPacket(new PacketOpenScreen(type));
	}
	
	public double getGravityMultiplier() {
		return gravityMod;
	}
	
	public double getSwimmingMultiplier() {
		return swimmingMod;
	}
	
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
		updateMovement();
	}
	
	@Override
	public boolean isCanFly() {
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

	/*Non Inteface public methods */
	
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
			id = EntityPlayer.class.getDeclaredField("bI");
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
			id = EntityPlayer.class.getDeclaredMethod("af");
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

	public void sendPacket(SpoutPacket packet) {
		if (!isSpoutCraftEnabled()) {
			if (queued != null) {
				queued.add(packet);
			}
		}
		else {
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

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public int getBuildVersion() {
		return buildVersion;
	}

	public void setVersion(int major, int minor, int build) {
		buildVersion = build;
		minorVersion = minor;
		majorVersion = major;
		if (isSpoutCraftEnabled() && queued != null) {
			for (SpoutPacket packet : queued) {
				sendPacket(packet);
			}
		}
	}
	
	public void setVersionString(String versionString) {
		this.versionString = versionString;
	}

	public String getVersionString() {
		return versionString;
	}
	
	public void onTick() {
		mainScreen.onTick();
		getNetServerHandler().syncFlushPacketQueue();
	}
	
	private void updateMovement() {
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
			Field bukkitEntity;
			try {
				bukkitEntity = Entity.class.getDeclaredField("bukkitEntity");
				bukkitEntity.setAccessible(true);
				org.bukkit.entity.Entity e = (org.bukkit.entity.Entity) bukkitEntity.get(ep);
				if (!e.getClass().equals(SpoutCraftPlayer.class)) {
					bukkitEntity.set(ep, new SpoutCraftPlayer((CraftServer)Bukkit.getServer(), ep));
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
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

	@Override
	public void setPreCachingComplete(boolean complete) {
		if(!precachingComplete)
		{
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
		if(sign != null && isSpoutCraftEnabled())
		{
			sendPacket(new PacketOpenSignGUI(sign.getX(), sign.getY(), sign.getZ()));
			TileEntitySign tes = (TileEntitySign) ((CraftWorld)((CraftBlock)sign.getBlock()).getWorld()).getTileEntityAt(sign.getX(), sign.getY(), sign.getZ()); // Found a hidden trace to The Elder Scrolls. Bethestas Lawyers are right!
			tes.a(true);
		}
	}
}
