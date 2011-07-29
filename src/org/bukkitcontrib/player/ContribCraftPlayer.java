package org.bukkitcontrib.player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.logging.Logger;

import net.minecraft.server.Container;
import net.minecraft.server.ContainerChest;
import net.minecraft.server.ContainerPlayer;
import net.minecraft.server.ContainerWorkbench;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ICrafting;
import net.minecraft.server.IInventory;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet100OpenWindow;
import net.minecraft.server.TileEntityDispenser;
import net.minecraft.server.TileEntityFurnace;
import net.minecraft.server.ChunkCoordIntPair;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkitcontrib.ContribNetServerHandler;
import org.bukkitcontrib.event.input.RenderDistance;
import org.bukkitcontrib.event.inventory.InventoryCloseEvent;
import org.bukkitcontrib.event.inventory.InventoryOpenEvent;
import org.bukkitcontrib.gui.InGameScreen;
import org.bukkitcontrib.inventory.ContribCraftInventory;
import org.bukkitcontrib.inventory.ContribCraftInventoryPlayer;
import org.bukkitcontrib.inventory.ContribCraftingInventory;
import org.bukkitcontrib.inventory.ContribInventory;
import org.bukkitcontrib.inventory.ContribPlayerInventory;
import org.bukkitcontrib.keyboard.Keyboard;
import org.bukkitcontrib.packet.BukkitContribPacket;
import org.bukkitcontrib.packet.CustomPacket;
import org.bukkitcontrib.packet.PacketAirTime;
import org.bukkitcontrib.packet.PacketBukkitContribAlert;
import org.bukkitcontrib.packet.PacketClipboardText;
import org.bukkitcontrib.packet.PacketNotification;
import org.bukkitcontrib.packet.PacketRenderDistance;
import org.bukkitcontrib.packet.PacketSkinURL;
import org.bukkitcontrib.packet.PacketTexturePack;
import org.bukkitcontrib.util.ReflectUtil;

@SuppressWarnings("unused")
public class ContribCraftPlayer extends CraftPlayer implements ContribPlayer{
	protected ContribCraftInventoryPlayer inventory = null;
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
	protected PermissibleBase perm;
	public ContribCraftPlayer(CraftServer server, EntityPlayer entity) {
		super(server, entity);
		createInventory(null);
		try {
			CraftPlayer cp = entity.netServerHandler.getPlayer();
			Field permissionBase = CraftHumanEntity.class.getDeclaredField("perm");
			permissionBase.setAccessible(true);
			perm = (PermissibleBase) permissionBase.get(cp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		mainScreen = new InGameScreen(this.getEntityId());
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
	public ContribPlayerInventory getInventory() {
		if (this.inventory == null) {
			createInventory(null);
		}
		else if (!((ContribCraftInventoryPlayer)this.inventory).getHandle().equals(this.getHandle().inventory)) {
			createInventory(this.inventory.getName());
		}
		return (ContribPlayerInventory)this.inventory;
	}
	
	@Override
	public void setMaximumAir(int time) {
		if (isBukkitContribEnabled()) {
			sendPacket(new PacketAirTime(time, this.getRemainingAir()));
		}
		super.setMaximumAir(time);
	}
	
	@Override
	public void setRemainingAir(int time) {
		if (isBukkitContribEnabled()) {
			sendPacket(new PacketAirTime(this.getMaximumAir(), time));
		}
		super.setRemainingAir(time);
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
		if (inventory instanceof ContribInventory) {
			title = ((ContribInventory)inventory).getTitle();
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
			InventoryOpenEvent event = new InventoryOpenEvent(this, new ContribCraftInventory(inventory), this.inventory, location);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return false;
			}
			getNetServerHandler().setActiveInventory(true);
			getNetServerHandler().setActiveInventoryLocation(location);
			getHandle().a(location.getBlockX(), location.getBlockY(), location.getBlockZ());
			return true;
		}
	}
	
	@Override
	public InGameScreen getMainScreen() {
		//throw new UnsupportedOperationException("Not yet implemented!");
		return mainScreen;
	}

	@Override
	@Deprecated
	public boolean isEnabledBukkitContribSinglePlayerMod() {
		return getBuildVersion() > -1 && getMinorVersion() > -1 && getMajorVersion() > -1;
	}
	
	@Override
	public boolean isBukkitContribEnabled() {
		return getBuildVersion() > -1 && getMinorVersion() > -1 && getMajorVersion() > -1;
	}
	
	public int getVersion() {
		if (isBukkitContribEnabled()) {
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
		if (getVersion() > 5) {
			currentRender = distance;
			sendPacket(new PacketRenderDistance(distance, null, null));
		}
	}

	@Override
	public RenderDistance getMaximumRenderDistance() {
		return maximumRender;
	}

	@Override
	public void setMaximumRenderDistance(RenderDistance maximum) {
		if (getVersion() > 5) {
			maximumRender = maximum;
			sendPacket(new PacketRenderDistance(null, maximum, null));
		}
	}
	
	@Override
	public void resetMaximumRenderDistance() {
		if (getVersion() > 5) {
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
		if (getVersion() > 5) {
			minimumRender = minimum;
			sendPacket(new PacketRenderDistance(null, null, minimum));
		}
	}
	
	@Override
	public void resetMinimumRenderDistance() {
		if (getVersion() > 5) {
			minimumRender = null;
			sendPacket(new PacketRenderDistance(false, true));
		}
	}
	
	@Override
	public void sendNotification(String title, String message, Material toRender) {
		if (getVersion() > 5) {
			if (title.length() > 26)
				throw new UnsupportedOperationException("Notification titles can not be greater than 26 chars");
			if (message.length() > 26)
				throw new UnsupportedOperationException("Notification messages can not be greater than 26 chars");
			sendPacket(new PacketBukkitContribAlert(title, message, toRender.getId()));
		}
	}
	
	@Override
	public void sendNotification(String title, String message, Material toRender, short data, int time) {
		if (getVersion() > 17) {
			if (title.length() > 26)
				throw new UnsupportedOperationException("Notification titles can not be greater than 26 chars");
			if (message.length() > 26)
				throw new UnsupportedOperationException("Notification messages can not be greater than 26 chars");
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
	
	@Override
	public void setTexturePack(String url) {
		 if (getVersion() > 8) {
			 if (url == null || url.length() < 5) {
				 throw new IllegalArgumentException("Invalid URL!");
			 }
			 if (!url.toLowerCase().endsWith(".zip")) {
				 throw new IllegalArgumentException("A Texture Pack must be in a .zip format");
			 }
			 sendPacket(new PacketTexturePack(url));
		 }
	}
	
	public void setClipboardText(String text, boolean updateClient) {
		if (getVersion() > 7) {
			clipboard = text;
			if (updateClient){
				sendPacket(new PacketClipboardText(text));
			}
		}
	}
	
	/*Non Inteface public methods */
	
	public void createInventory(String name) {
		if (this.getHandle().activeContainer instanceof ContainerPlayer) {
			this.inventory = new ContribCraftInventoryPlayer(this.getHandle().inventory, 
					new ContribCraftingInventory(((ContainerPlayer)this.getHandle().activeContainer).craftInventory, ((ContainerPlayer)this.getHandle().activeContainer).resultInventory));
			if (name != null) {
				this.inventory.setName(name);
			}
		}
		else {
			this.inventory = new ContribCraftInventoryPlayer(this.getHandle().inventory, 
					new ContribCraftingInventory(((ContainerPlayer)this.getHandle().defaultContainer).craftInventory, ((ContainerPlayer)this.getHandle().defaultContainer).resultInventory));
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

   public ContribNetServerHandler getNetServerHandler() {
	   return (ContribNetServerHandler) getHandle().netServerHandler;
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
   
   public void sendPacket(BukkitContribPacket packet) {
	   getNetServerHandler().sendPacket(new CustomPacket(packet));
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
	
	public void setVersion(String version) {
		try {
			String split[] = version.split("\\.");
			buildVersion = Integer.valueOf(split[2]);
			minorVersion = Integer.valueOf(split[1]);
			majorVersion = Integer.valueOf(split[0]);
		}
		catch (Exception e) {reset();}
	}
	
	public void onTick() {
		mainScreen.onTick();
	}
	
	private void reset() {
		buildVersion = -1;
		minorVersion = -1;
		majorVersion = -1;
	}
   
   /* Non Interface public static methods */

   public static boolean resetNetServerHandler(Player player) {
	   CraftPlayer cp = (CraftPlayer)player;
	   CraftServer server = (CraftServer)Bukkit.getServer();
	   
	   if ((cp.getHandle().netServerHandler instanceof ContribNetServerHandler)) {
                   Set<ChunkCoordIntPair> chunkUpdateQueue = ((ContribNetServerHandler)cp.getHandle().netServerHandler).getChunkUpdateQueue();
	           for(ChunkCoordIntPair c : chunkUpdateQueue) {
	                   cp.getHandle().chunkCoordIntPairQueue.add(c);
	           }
	           ((ContribNetServerHandler)cp.getHandle().netServerHandler).flushUnloadQueue();
		   Location loc = player.getLocation();
		   NetServerHandler handler = new NetServerHandler(server.getHandle().server, cp.getHandle().netServerHandler.networkManager, cp.getHandle());
		   handler.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		   cp.getHandle().netServerHandler = handler;
		   return true;
	   }
	   return false;
   }

   public static boolean updateNetServerHandler(Player player) {
	   CraftPlayer cp = (CraftPlayer)player;
	   CraftServer server = (CraftServer)Bukkit.getServer();
	   
	   if (!(cp.getHandle().netServerHandler.getClass().equals(ContribNetServerHandler.class))) {
		   Location loc = player.getLocation();
		   ContribNetServerHandler handler = new ContribNetServerHandler(server.getHandle().server, cp.getHandle().netServerHandler.networkManager, cp.getHandle());
		   handler.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	           for(Object o : cp.getHandle().playerChunkCoordIntPairs) {
	                   ChunkCoordIntPair c = (ChunkCoordIntPair) o;
	                   handler.addActiveChunk(c);
	           }
		   cp.getHandle().netServerHandler = handler;
		   return true;
	   }
	   return false;
   }

   public static boolean updateBukkitEntity(Player player) {
	   if (!(player instanceof ContribCraftPlayer)) {
		   CraftPlayer cp = (CraftPlayer)player;
		   EntityPlayer ep = cp.getHandle();
		   Field bukkitEntity;
		   try {
			   bukkitEntity = Entity.class.getDeclaredField("bukkitEntity");
			   bukkitEntity.setAccessible(true);
			   org.bukkit.entity.Entity e = (org.bukkit.entity.Entity) bukkitEntity.get(ep);
			   if (!(e instanceof ContribCraftPlayer)) {
				   bukkitEntity.set(ep, new ContribCraftPlayer((CraftServer)Bukkit.getServer(), ep));
			   }
			   return true;
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
	   }
		  return false;
   }

   public static void removeBukkitEntity(Player player) {
	   CraftPlayer cp = (CraftPlayer)player;
	   EntityPlayer ep = cp.getHandle();
	   Field bukkitEntity;
	   try {
		   bukkitEntity = Entity.class.getDeclaredField("bukkitEntity");
		   bukkitEntity.setAccessible(true);
		   bukkitEntity.set(ep, null);
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
   }

   public static ContribPlayer getContribPlayer(Player player) {
	   if (player instanceof ContribCraftPlayer) {
		   return (ContribCraftPlayer)player;
	   }
	   if ((((CraftPlayer)player).getHandle()).getBukkitEntity() instanceof ContribCraftPlayer) {
		   return (ContribCraftPlayer)((((CraftPlayer)player).getHandle()).getBukkitEntity());
	   }
	   //We should never get here
	   Logger.getLogger("Minecraft").severe("Player: " + player.getName() + " was not properly updated during login!");
	   updateBukkitEntity(player);
	   return (ContribCraftPlayer)((((CraftPlayer)player).getHandle()).getBukkitEntity());
   }
}
