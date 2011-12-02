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
package org.getspout.spout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.Deflater;

import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.Container;
import net.minecraft.server.ContainerBrewingStand;
import net.minecraft.server.ContainerChest;
import net.minecraft.server.ContainerDispenser;
import net.minecraft.server.ContainerEnchantTable;
import net.minecraft.server.ContainerFurnace;
import net.minecraft.server.ContainerPlayer;
import net.minecraft.server.ContainerWorkbench;
import net.minecraft.server.CraftingManager;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import net.minecraft.server.IntHashMap;
import net.minecraft.server.InventoryCraftResult;
import net.minecraft.server.InventoryCrafting;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet100OpenWindow;
import net.minecraft.server.Packet101CloseWindow;
import net.minecraft.server.Packet102WindowClick;
import net.minecraft.server.Packet106Transaction;
import net.minecraft.server.Packet10Flying;
import net.minecraft.server.Packet11PlayerPosition;
import net.minecraft.server.Packet13PlayerLookMove;
import net.minecraft.server.Packet14BlockDig;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet3Chat;
import net.minecraft.server.Packet50PreChunk;
import net.minecraft.server.Packet51MapChunk;
import net.minecraft.server.Packet9Respawn;
import net.minecraft.server.Slot;
import net.minecraft.server.TileEntity;
import net.minecraft.server.TileEntityBrewingStand;
import net.minecraft.server.TileEntityDispenser;
import net.minecraft.server.TileEntityFurnace;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.ChunkCompressionThread;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.inventory.Inventory;
import org.getspout.spout.config.ConfigReader;
import org.getspout.spout.inventory.SpoutCraftInventory;
import org.getspout.spout.inventory.SpoutCraftInventoryPlayer;
import org.getspout.spout.inventory.SpoutCraftItemStack;
import org.getspout.spout.inventory.SpoutCraftingInventory;
import org.getspout.spout.packet.listener.PacketListeners;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spout.packet.standard.MCCraftPacket51MapChunkUncompressed;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;
import org.getspout.spoutapi.event.inventory.InventoryPlayerClickEvent;
import org.getspout.spoutapi.event.inventory.InventorySlotType;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.inventory.CraftingInventory;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SpoutNetServerHandler extends NetServerHandler {
	protected Field entityListField = null;
	public boolean activeInventory = false;
	public Location activeLocation = null;
	protected ItemStack lastOverrideDisplayStack = null;

	private MCCraftPacket[] packetWrappers = new MCCraftPacket[256];

	private final int teleportZoneSize = 3; // grid size is a square of chunks with an edge of (2*teleportZoneSize - 1)
	
	public SpoutNetServerHandler(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
		super(minecraftserver, networkmanager, entityplayer);
		//cache the field for later use
		try {
			entityListField = NetServerHandler.class.getDeclaredField("q");
			entityListField.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Lower the active packet queue size in bytes by 9 megabytes, to allow for 10mb of data in a players queue
		try {
			Field x = NetworkManager.class.getDeclaredField("x");
			x.setAccessible(true);
			int size = (Integer) x.get(this.networkManager);
			x.set(this.networkManager, size - 1024 * 1024 * 9);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public IntHashMap getEntityList() {
		try {
			return (IntHashMap) entityListField.get(this);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean allowReload = false;
	@Override
	public void a(Packet3Chat packet) {
		String chat = packet.message;
		if (chat.trim().isEmpty()) {
			return;
		}
		if (!allowReload && chat.equalsIgnoreCase("/reload")) {
			allowReload = true;
			SpoutCraftPlayer player = (SpoutCraftPlayer)SpoutCraftPlayer.getPlayer(getPlayer());
			if (!player.isSpoutCraftEnabled()) {
				player.sendMessage(ChatColor.RED + "Spout does not support the /reload command.");
				player.sendMessage(ChatColor.RED + "Unexpected behavior may occur.");
				player.sendMessage(ChatColor.RED + "We recommend using /stop and restarting.");
				player.sendMessage(ChatColor.RED + "Or you can use /spout reload to reload the config.");
				player.sendMessage(ChatColor.RED + "If you want to use /reload anyway, use the command again.");
			}
			else {
				Label warning = new DecayingLabel(200, ChatColor.RED + "Spout does not support the /reload command." + "\n" +
						ChatColor.RED + "Unexpected behavior may occur." + "\n" +
						ChatColor.RED + "We recommend using /stop and restarting." + " \n" + 
						ChatColor.RED + "Or you can use /spout reload to reload the config." + "\n" +
						ChatColor.RED + "If you want to use /reload anyway, use the command again.");
				warning.setX(100).setY(100).setPriority(RenderPriority.Lowest);
				player.getMainScreen().attachWidget(Spout.getInstance(), warning);
			}
			return;
		}
		super.a(packet);
	}

	@Override
	public void a(Packet18ArmAnimation packet) {
		if (packet.a == -42) {
			SpoutCraftPlayer player = (SpoutCraftPlayer)SpoutCraftPlayer.getPlayer(getPlayer());
			player.setBuildVersion(1); //Don't know yet, just set above zero
			try {
				Spout.getInstance().playerListener.manager.onSpoutcraftEnable((SpoutPlayer)getPlayer());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			super.a(packet);
		}
	}
	
	@Override
	public void a(Packet14BlockDig packet) {
		SpoutCraftPlayer player = (SpoutCraftPlayer)SpoutCraftPlayer.getPlayer(getPlayer());
		boolean inAir = false;
		if (player.canFly() && !player.getHandle().onGround) {
			inAir = true;
			player.getHandle().onGround = true;
		}
		super.a(packet);
		if (inAir) {
			player.getHandle().onGround = false;
		}
	}

	public void setActiveInventoryLocation(Location location) {
		activeLocation = location;
	}

	public Location getActiveInventoryLocation() {
		return activeLocation;
	}

	public void setActiveInventory(boolean active) {
		activeInventory = active;
	}

	public Inventory getActiveInventory() {
		return getInventoryFromContainer(this.player.activeContainer);
	}

	public Inventory getDefaultInventory() {
		if (this.player.defaultContainer.equals(this.player.activeContainer)) {
			return null;
		}
		return getInventoryFromContainer(this.player.defaultContainer);
	}

	public InventorySlotType getInventorySlotType(int clicked) {
		if (clicked < 9) return InventorySlotType.QUICKBAR;
		return InventorySlotType.CONTAINER;
	}

	public InventorySlotType getActiveInventorySlotType(int clicked) {
		if (clicked == -999) {
			return InventorySlotType.OUTSIDE;
		}
		Inventory active = getActiveInventory();
		int size = active.getSize();
		if (this.player.activeContainer instanceof ContainerChest) {
			return InventorySlotType.CONTAINER;
		} else if (this.player.activeContainer instanceof ContainerPlayer) {
			if (clicked == 0) return InventorySlotType.RESULT;
			if (clicked < 5) return InventorySlotType.CRAFTING;
			if (clicked == 5) return InventorySlotType.HELMET;
			if (clicked == 6) return InventorySlotType.ARMOR;
			if (clicked == 7) return InventorySlotType.LEGGINGS;
			if (clicked == 8) return InventorySlotType.BOOTS;
			if (clicked < size) return InventorySlotType.CONTAINER;
			return InventorySlotType.QUICKBAR;
		} else if (this.player.activeContainer instanceof ContainerFurnace) {
			if (clicked == 0) return InventorySlotType.SMELTING;
			if (clicked == 1) return InventorySlotType.FUEL;
			return InventorySlotType.RESULT;
		} else if (this.player.activeContainer instanceof ContainerDispenser) {
			return InventorySlotType.CONTAINER;
		} else if (this.player.activeContainer instanceof ContainerWorkbench) {
			if (clicked == 0) return InventorySlotType.RESULT;
			else if (clicked < size) return InventorySlotType.CRAFTING;
			return InventorySlotType.CONTAINER;
		}
		if (clicked >= size + 27) return InventorySlotType.QUICKBAR;
		if (clicked >= size) return InventorySlotType.PACK;
		return InventorySlotType.CONTAINER;
	}

	@Override
	public void a(Packet101CloseWindow packet) {
		Inventory inventory = getActiveInventory();

		InventoryCloseEvent event = new InventoryCloseEvent((Player) this.player.getBukkitEntity(), inventory, getDefaultInventory(), activeLocation);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			IInventory inv = ((CraftInventory) event.getInventory()).getInventory();
			if (inventory instanceof TileEntityFurnace) {
				this.player.a((TileEntityFurnace) inventory);
			} else if (inventory instanceof TileEntityDispenser) {
				this.player.a((TileEntityDispenser) inventory);
			} else if (inventory instanceof InventoryCraftResult && this.player.activeContainer instanceof ContainerWorkbench) {
				sendPacket(new Packet100OpenWindow(packet.a, 1, "Crafting", 9));
				this.player.syncInventory();
			} else if (inventory instanceof InventoryCraftResult) {
				// There is no way to force a player's own inventory back open.
			} else {
				this.player.a(inv);
			}
		} else {
			activeInventory = false;
			activeLocation = null;
			super.a(packet);
		}
	}

	@Override
	public void a(Packet106Transaction packet) {
		if (this.player.dead) {
			return;
		}
		Short oshort = (Short) this.getEntityList().a(Integer.valueOf(this.player.activeContainer.windowId));

		if (oshort != null && packet.b == oshort.shortValue() && this.player.activeContainer.windowId == packet.a && !this.player.activeContainer.c(this.player)) {
			this.player.activeContainer.a(this.player, true);
		}
	}

	@Override
	public void a(Packet102WindowClick packet) {
		if (this.player.dead) return;
		
		if (this.player.activeContainer.windowId == packet.a && this.player.activeContainer.c(this.player)) {
			Inventory inventory = getActiveInventory();
			CraftPlayer player = (CraftPlayer) this.player.getBukkitEntity();
			ItemStack before = ItemStack.b(packet.e);
			ItemStack cursorBefore = this.player.inventory.l();
			SpoutCraftItemStack slot = SpoutCraftItemStack.fromItemStack(before);
			SpoutCraftItemStack cursor = SpoutCraftItemStack.fromItemStack(cursorBefore);
			InventorySlotType type = getActiveInventorySlotType(packet.b);
			boolean clickSuccessful = true;
			final int windowId = packet.a;

			// alert of a newly opened inventory
			if (!activeInventory) {
				activeInventory = true;
				InventoryOpenEvent event = new InventoryOpenEvent(player, inventory, getDefaultInventory(), activeLocation);
				Bukkit.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					this.player.C();
					activeInventory = false;
					activeLocation = null;
					return;
				}
			}

			// Fire InventoryChange or InventoryCraft event
			if (packet.b != -999) {
				if (inventory instanceof CraftingInventory) {
					CraftingInventory crafting = (CraftingInventory) inventory;
					InventoryCrafting recipe = null;
					if (inventory instanceof SpoutCraftingInventory) {
						recipe = (InventoryCrafting) ((SpoutCraftingInventory) crafting).getMatrixHandle();
					} else {
						recipe = (InventoryCrafting) ((SpoutCraftInventoryPlayer) crafting).getMatrixHandle();
					}

					SpoutCraftItemStack craftResult = SpoutCraftItemStack.fromItemStack(CraftingManager.getInstance().craft(recipe));
					SpoutCraftItemStack[] recipeContents = new SpoutCraftItemStack[recipe.getSize()];
					for (int i = 0; i < recipe.getSize(); i++) {
						org.bukkit.inventory.ItemStack temp = crafting.getMatrix()[i];
						recipeContents[i] = temp == null ? null : new SpoutCraftItemStack(temp.getTypeId(), temp.getAmount(), temp.getDurability(), temp.getEnchantments());
					}

					SpoutCraftItemStack[][] matrix = null;
					if (recipe.getSize() == 4) {
						matrix = new SpoutCraftItemStack[][] { 
								Arrays.copyOfRange(recipeContents, 0, 2), 
								Arrays.copyOfRange(recipeContents, 2, 4) 
						};
					} else if (recipe.getSize() == 9) {
						matrix = new SpoutCraftItemStack[][] { 
								Arrays.copyOfRange(recipeContents, 0, 3), 
								Arrays.copyOfRange(recipeContents, 3, 6),
								Arrays.copyOfRange(recipeContents, 6, 9) 
						};
					}
					// Clicking to grab the crafting result
					if (type == InventorySlotType.RESULT) {
						InventoryCraftEvent craftEvent = new InventoryCraftEvent(this.getPlayer(), crafting, this.activeLocation, type, packet.b, matrix, craftResult, cursor, packet.c == 0, packet.f);
						Bukkit.getServer().getPluginManager().callEvent(craftEvent);
						craftEvent.getInventory().setResult(craftEvent.getResult());
						cursor = craftEvent.getCursor() == null ? null : new SpoutCraftItemStack(craftEvent.getCursor().getTypeId(), craftEvent.getCursor().getAmount(), craftEvent.getCursor().getDurability(), craftEvent.getCursor().getEnchantments());
						if (craftEvent.isCancelled()) {
							craftEvent.getInventory().setMatrix(recipeContents);
							setCursorSlot(cursor != null ? cursor.getHandle() : null);
							clickSuccessful = false;
						}
					}
				}
			}

			if (clickSuccessful) {
				clickSuccessful = handleInventoryClick(packet, type, slot, cursor, inventory);
			}

			if (clickSuccessful) {
				this.player.netServerHandler.sendPacket(new Packet106Transaction(windowId, packet.d, true));
				this.player.h = true;
				this.player.activeContainer.a();
				this.player.C();
				this.player.h = false;
			} else {
				this.getEntityList().a(Integer.valueOf(this.player.activeContainer.windowId), Short.valueOf(packet.d));
				this.player.netServerHandler.sendPacket(new Packet106Transaction(windowId, packet.d, false));
				this.player.activeContainer.a(this.player, false);
				ArrayList<ItemStack> arraylist = new ArrayList<ItemStack>();

				for (int i = 0; i < this.player.activeContainer.e.size(); ++i) {
					arraylist.add(((Slot) this.player.activeContainer.e.get(i)).getItem());
				}

				this.player.a(this.player.activeContainer, arraylist);
			}
		}
	}

	public boolean handleInventoryClick(Packet102WindowClick packet, InventorySlotType type, SpoutCraftItemStack slot, SpoutCraftItemStack cursor, Inventory inventory) {
		InventoryClickEvent event = null;
		Result result = Result.DEFAULT;
		boolean success = false;
		final int LEFT_CLICK = 0;
		final int RIGHT_CLICK = 1;
		int click = packet.c;

		// clicked on bottom player inventory
		if (!(this.player.activeContainer instanceof ContainerPlayer) && this.player.defaultContainer instanceof ContainerPlayer && packet.b >= inventory.getSize()) {
			int activeSlot = packet.b - inventory.getSize() + 9;
			if (activeSlot >= this.getPlayer().getInventory().getSize()) {
				activeSlot -= this.getPlayer().getInventory().getSize();
			}
			type = getInventorySlotType(activeSlot);
			event = new InventoryPlayerClickEvent(this.getPlayer(), this.getPlayer().getInventory(), type, slot, cursor, activeSlot, click == LEFT_CLICK, packet.f, activeLocation);
		} else {
			event = new InventoryClickEvent(this.getPlayer(), inventory, type, slot, cursor, packet.b, click == LEFT_CLICK, packet.f, activeLocation);
		}

		if (event != null) {
			Bukkit.getServer().getPluginManager().callEvent(event);
			result = event.getResult();
			cursor = SpoutCraftItemStack.getCraftItemStack(event.getCursor());
			slot = SpoutCraftItemStack.getCraftItemStack(event.getItem());
		}

		// initialize setup
		ItemStack itemstack = slot != null ? slot.getHandle() : null;
		ItemStack cursorstack = cursor != null ? cursor.getHandle() : null;

		// NOTE: Successful means that its successful as-is; thus, only becomes true for default behaviour

		switch (result) {
		case DEFAULT:
			itemstack = this.player.activeContainer.a(packet.b, packet.c, packet.f, this.player);
			success = ItemStack.equals(packet.e, itemstack);
			break;
		case DENY:
			if (packet.b != -999) { // Only swap if target is not OUTSIDE
				if (itemstack != null) {
					setActiveSlot(packet.b, itemstack);
					setCursorSlot((ItemStack) null);
				}
				if (event.getCursor() != null) {
					setActiveSlot(packet.b, itemstack);
					setCursorSlot(cursorstack);
				}
			}

			break;
		case ALLOW: // Allow the placement unconditionally
			if (packet.b == -999) { // Clicked outside, just defer to default
				itemstack = this.player.activeContainer.a(packet.b, packet.c, packet.f, this.player);
			} else {
				if (click == LEFT_CLICK && (itemstack != null && cursorstack != null && itemstack.doMaterialsMatch(cursorstack))) {
					// Left-click full slot with full cursor of same item; merge stacks
					itemstack.count += cursorstack.count;
					cursorstack = null;
				} else if (click == LEFT_CLICK || (itemstack != null && cursorstack != null && !itemstack.doMaterialsMatch(cursorstack))) {
					// Either left-click, or right-click full slot with full cursor of different item; just swap contents
					ItemStack temp = itemstack;
					itemstack = cursorstack;
					cursorstack = temp;
				} else if (click == RIGHT_CLICK) { // Right-click with either slot or cursor empty
					if (itemstack == null) { // Slot empty; drop one
						if (cursorstack != null) {
							itemstack = cursorstack.a(1);
							if (cursorstack.count == 0) {
								cursorstack = null;
							}
						}
					} else if (cursorstack == null) { // Cursor empty; take half
						cursorstack = itemstack.a((itemstack.count + 1) / 2);
					} else { // Neither empty, but same item; drop one
						ItemStack drop = cursorstack.a(1);
						itemstack.count += drop.count;
						if (cursorstack.count == 0) {
							cursorstack = null;
						}
					}
				}
				// update the stacks
				setActiveSlot(packet.b, itemstack);
				setCursorSlot(cursorstack);
			}
			break;
		}
		return success;
	}

	public void setActiveSlot(int slot, ItemStack item) {
		this.player.activeContainer.b(slot).c(item);
	}

	public void setCursorSlot(ItemStack item) {
		this.player.inventory.b(item);
	}

	@Override
	public void sendPacket(Packet packet) {
		if (packet != null) {
			if (packet.l) {
				MapChunkThread.sendPacket(this.player, packet);
			} else {
				queueOutputPacket(packet);
			}
		}
	}

	private LinkedBlockingDeque<Packet> resyncQueue = new LinkedBlockingDeque<Packet>();

	// MapChunkThread sends packets to the method. All packets should pass through this method before being sent to the client
	public void queueOutputPacket(Packet packet) {
		packet = updateActiveChunks(packet);
		if (packet == null) {
			return;
		}
		resyncQueue.addLast(packet);
		if (processingKick.get()) {
			this.syncFlushPacketQueue();
		}
	}

	public void sendImmediatePacket(Packet packet) {
		packet = updateActiveChunks(packet);
		if (packet == null) {
			return;
		}

		resyncQueue.addFirst(packet);
	}

	@Override
	public void a() {
		syncFlushPacketQueue();
		super.a();
	}

	AtomicBoolean processingKick = new AtomicBoolean(false);
	
	@Override
	public void disconnect(String kick) {
	
		processingKick.set(true); // If any packets are sent while this flag is true, it will flush the sync queue
		
		super.disconnect(kick);
		if (this.disconnected)
			syncFlushPacketQueue(new MCCraftPacket[256]);
		
		processingKick.set(false);
	}
	
	public void syncFlushPacketQueue() {
		syncFlushPacketQueue(packetWrappers);
	}

	public void syncFlushPacketQueue(MCCraftPacket[] packetWrappers) {
		while (!resyncQueue.isEmpty()) {
			Packet p = resyncQueue.pollFirst();
			if (p != null) {
				syncedSendPacket(p, packetWrappers);
			}
		}
	}
	
	// Called from the main thread only
	private void syncedSendPacket(Packet packet, MCCraftPacket[] packetWrappers) {

		if (!PacketListeners.canSend(getPlayer(), packet, packetWrappers, packet.b())) {
			return;
		} else {
			if (packet instanceof Packet51MapChunk) {
				Packet51MapChunk chunk = (Packet51MapChunk)packet;
				if (chunk.g == null) {
					ChunkCompressionThread.sendPacket(player, packet);
					return;
				}
			}
			super.sendPacket(packet);
		}
	}
	
	AtomicLong lastUnloadCheck = new AtomicLong(0);

	public Packet updateActiveChunks(Packet packet) {
		
		long currentTime = System.currentTimeMillis();
		if (lastUnloadCheck.get() + 1000 < currentTime) {
			lastUnloadCheck.set(currentTime);
			synchronized (unloadQueue) {
				Iterator<ChunkCoordIntPair> i = unloadQueue.iterator();
				while (i.hasNext()) {
					ChunkCoordIntPair coord = i.next();
					if (!nearPlayer(coord.x, coord.z, teleportZoneSize)) {
						if (activeChunks.remove(coord)) {
							resyncQueue.addFirst(new Packet50PreChunk(coord.x, coord.z, false));
						}
						i.remove();
					}
				}
			}
		}
		
		if (packet instanceof Packet50PreChunk) {
			Packet50PreChunk p = (Packet50PreChunk) packet;
			int cx = p.a;
			int cz = p.b;
			boolean init = p.c;
			ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(cx, cz);

			if (init) {
				unloadQueue.remove(chunkPos);
				if (!activeChunks.add(chunkPos)) {
					p = null;
				}
			} else {
				if (!nearPlayer(cx, cz, teleportZoneSize)) {
					if (!activeChunks.remove(chunkPos)) {
						p = null;
					}
				} else {
					unloadQueue.add(new ChunkCoordIntPair(cx, cz));
					p = null;
				}
			}
			return p;
		} else if (packet instanceof Packet9Respawn) {
			Packet9Respawn p = (Packet9Respawn)packet;
			activeChunks.clear();
			return p;
		} else if (packet instanceof Packet51MapChunk) {
			Packet51MapChunk p = (Packet51MapChunk)packet;
			ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(p.a >> 4, p.c >> 4);
			if (!activeChunks.contains(chunkPos)) {
				return null;
			}
			return p;
		} else if (packet instanceof Packet11PlayerPosition) {
			Packet11PlayerPosition p = (Packet11PlayerPosition)packet;
			playerTeleported(((int) p.x) >> 4, ((int) p.z) >> 4);
			return p;
		} else if (packet instanceof Packet13PlayerLookMove) {
			Packet13PlayerLookMove p = (Packet13PlayerLookMove)packet;
			playerTeleported(((int) p.x) >> 4, ((int) p.z) >> 4);
			return p;
		} else {
			return packet;
		}
	}

	@Override
	public void a(Packet10Flying packet) {
		manageChunkQueue(true);
		SpoutPlayer player = SpoutManager.getPlayer(this.getPlayer());
		boolean old = ((CraftServer)Bukkit.getServer()).getHandle().server.allowFlight;
		((CraftServer)Bukkit.getServer()).getHandle().server.allowFlight = player.canFly();
		super.a(packet);
		((CraftServer)Bukkit.getServer()).getHandle().server.allowFlight = old;
	}

	private final LinkedHashSet<ChunkCoordIntPair> chunkUpdateQueue = new LinkedHashSet<ChunkCoordIntPair>();

	private final AtomicInteger updateCounter = new AtomicInteger();

	private final int[] spiralx = new int[] { 0, -1, -1, -1, 0, 1, 1, 1, 0, -2, -2, -2, -2, -2, -1, 0, 1, 2, 2, 2, 2, 2, 1, 0, -1 };
	private final int[] spiralz = new int[] { 0, -1, 0, 1, 1, 1, 0, -1, -1, -2, -1, 0, 1, 2, 2, 2, 2, 2, 1, 0, -1, -2, -2, -2, -2 };

	// This may not catch 100% of packets, but should get most of them, a small number may end up being compressed by main thread
	@SuppressWarnings("unchecked")
	public void manageChunkQueue(boolean flag1) {
		List<ChunkCoordIntPair> playerChunkQueue = player.chunkCoordIntPairQueue;

		try {
			if (!playerChunkQueue.isEmpty()) {
				chunkUpdateQueue.addAll(playerChunkQueue);
				playerChunkQueue.clear();
			}
		}
		catch (ConcurrentModificationException e) {
			//seems to be called from a separate thread during teleports (rogue plugins?)
		}

		int chunkCompressionThreadSize = ChunkCompressionThread.getPlayerQueueSize(this.player);
		if (!chunkUpdateQueue.isEmpty() && (b() + chunkCompressionThreadSize + MapChunkThread.getQueueLength(this.player)) < 4) {
			ChunkCoordIntPair playerChunk = getPlayerChunk();
			Iterator<ChunkCoordIntPair> i = chunkUpdateQueue.iterator();
			ChunkCoordIntPair first = i.next();
			while (first != null && !activeChunks.contains(first)) {
				i.remove();
				if (i.hasNext()) {
					first = i.next();
				} else {
					first = null;
				}
			}
			if (first != null) {
				if (updateCounter.get() > 0) {
					int cx = playerChunk.x;
					int cz = playerChunk.z;
					boolean chunkFound = false;
					for (int c = 0; c < spiralx.length; c++) {
						ChunkCoordIntPair testChunk = new ChunkCoordIntPair(spiralx[c] + cx, spiralz[c] + cz);
						if (chunkUpdateQueue.contains(testChunk)) {
							first = testChunk;
							chunkFound = true;
							break;
						}
					}
					if (!chunkFound) {
						updateCounter.decrementAndGet();
					}
				}
				chunkUpdateQueue.remove(first);
				MapChunkThread.sendPacketMapChunk(first, this.player, this.player.world);
				sendChunkTiles(first.x, first.z, player);
			}
		}
	}

	public Set<ChunkCoordIntPair> getChunkUpdateQueue() {
		return chunkUpdateQueue;
	}

	public void addActiveChunk(ChunkCoordIntPair c) {
		activeChunks.add(c);
	}

	public void flushUnloadQueue() {
		synchronized (unloadQueue) {
			Iterator<ChunkCoordIntPair> i = unloadQueue.iterator();
			while (i.hasNext()) {
				ChunkCoordIntPair coord = i.next();
				if (activeChunks.remove(coord)) {
					super.sendPacket(new Packet50PreChunk(coord.x, coord.z, false));
				}
				i.remove();
			}
		}
	}

	private final AtomicReference<ChunkCoordIntPair> currentChunk = new AtomicReference<ChunkCoordIntPair>(new ChunkCoordIntPair(Integer.MAX_VALUE, Integer.MIN_VALUE));

	private final Set<ChunkCoordIntPair> activeChunks = Collections.synchronizedSet(new HashSet<ChunkCoordIntPair>());

	private final Set<ChunkCoordIntPair> unloadQueue = Collections.synchronizedSet(new LinkedHashSet<ChunkCoordIntPair>());

	@SuppressWarnings("rawtypes")
	public static void sendChunkTiles(int cx, int cz, EntityPlayer player) {
		WorldServer worldserver = (WorldServer) player.world;
		List tileEntities = worldserver.getTileEntities(cx << 4, 0, cz << 4, (cx << 4) + 16, 128, (cz << 4) + 16);
		for (Object tileEntityObject : tileEntities) {
			if (tileEntityObject != null && tileEntityObject instanceof TileEntity) {
				TileEntity tileEntity = (TileEntity) tileEntityObject;
				Packet tilePacket = tileEntity.k();
				if (tilePacket != null) {
					MapChunkThread.sendPacket(player, tilePacket);
				}
			}
		}
	}

	private void playerTeleported(int cx, int cz) {
		if (ConfigReader.isTeleportSmoothing()) {
			for (int x = 1 - teleportZoneSize; x < teleportZoneSize; x++) {
				for (int z = 1 - teleportZoneSize; z < teleportZoneSize; z++) {
					int xx = cx + x;
					int zz = cz + z;
					ChunkCoordIntPair chunkPos = new ChunkCoordIntPair(xx, zz);

					unloadQueue.remove(chunkPos);

					if(!activeChunks.contains(chunkPos)) {
						this.queueOutputPacket(new Packet50PreChunk(xx, zz, true));
						Packet p = getFastPacket51(xx, zz);
						if (p != null) {
							this.queueOutputPacket(p);
							sendChunkTiles(xx, zz, player);
						}
					}
				}
			}
		}
	}

	private ChunkCoordIntPair getPlayerChunk() {
		return currentChunk.get();
	}

	public void setPlayerChunk(int cx, int cz) {
		ChunkCoordIntPair cur = currentChunk.get();
		if (cur.x != cx || cur.z != cz) {
			currentChunk.set(new ChunkCoordIntPair(cx, cz));
			updateCounter.incrementAndGet();
			
			SpoutPlayer player = (SpoutPlayer) getPlayer();
			if (player != null && player.isSpoutCraftEnabled()) {
				Spout.getInstance().getPlayerTrackingManager().onMoveChunk((SpoutPlayer) getPlayer());
			}
		}
	}

	private boolean nearPlayer(int cx, int cz, int d) {
		ChunkCoordIntPair cur = currentChunk.get();
		return cur.x - cx < d && cur.x - cx > -d && cur.z - cz < d && cur.z - cz > -d;
	}

	MCCraftPacket51MapChunkUncompressed MCPacket = new MCCraftPacket51MapChunkUncompressed();

	private Packet getFastPacket51(int cx, int cz) {
		World world = getPlayer().getWorld();
		if (!world.isChunkLoaded(cx, cz)) {
			world.loadChunk(cx, cz);
		}
		Packet packet = new Packet51MapChunk(cx << 4, 0, cz << 4, 16, 128, 16, this.player.world);
		try {
			packet.l = false;
			Field g = Packet51MapChunk.class.getDeclaredField("g");
			g.setAccessible(true);
			byte[] compressedData = (byte[])g.get(packet);
			if(compressedData == null) {
				MCPacket.setPacket(packet, 51);
				if (!PacketListeners.canSendUncompressedPacket(getPlayer(), MCPacket)) {
					return null;
				}
				AtomicInteger size = new AtomicInteger(0);
				Field rawData;
				try {
					rawData = Packet51MapChunk.class.getDeclaredField("rawData");
				} catch (NoSuchFieldException e) {
					rawData = Packet51MapChunk.class.getDeclaredField("g");
				}
				Field h = Packet51MapChunk.class.getDeclaredField("h");
				rawData.setAccessible(true);
				h.setAccessible(true);
				byte[] rawBytes = (byte[])rawData.get(packet);
				if (rawBytes != null) {
					g.set(packet, compressData(rawBytes, size));
					h.set(packet, size.get());
				}
			}
		} catch (NoSuchFieldException e) {
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return packet;
	}

	Deflater deflater = new Deflater(-1);

	private byte[] compressData(byte[] rawBytes, AtomicInteger size) {
		deflater.reset();
		deflater.setInput(rawBytes);
		deflater.finish();
		byte[] compressedData = new byte[rawBytes.length];
		size.set(deflater.deflate(compressedData));
		return compressedData;
	}

	private Inventory getInventoryFromContainer(Container container) {
		try {
			if (container instanceof ContainerChest) {
				Field a = ContainerChest.class.getDeclaredField("a");
				a.setAccessible(true);
				return new SpoutCraftInventory((IInventory) a.get((ContainerChest) container));
			}
			if (container instanceof ContainerPlayer) {
				return new SpoutCraftInventoryPlayer(this.player.inventory, new SpoutCraftingInventory(((ContainerPlayer) container).craftInventory,
						((ContainerPlayer) container).resultInventory));
			}
			if (container instanceof ContainerFurnace) {
				Field a = ContainerFurnace.class.getDeclaredField("a");
				a.setAccessible(true);
				return new SpoutCraftInventory((TileEntityFurnace) a.get((ContainerFurnace) container));
			}
			if (container instanceof ContainerDispenser) {
				Field a = ContainerDispenser.class.getDeclaredField("a");
				a.setAccessible(true);
				return new SpoutCraftInventory((TileEntityDispenser) a.get((ContainerDispenser) container));
			}
			if (container instanceof ContainerWorkbench) {
				return new SpoutCraftingInventory(((ContainerWorkbench) container).craftInventory, ((ContainerWorkbench) container).resultInventory);
			}
			if (container instanceof ContainerBrewingStand) {
				Field a = ContainerBrewingStand.class.getDeclaredField("a");
				a.setAccessible(true);
				return new SpoutCraftInventory((TileEntityBrewingStand) a.get((ContainerBrewingStand) container));
			}
			if (container instanceof ContainerEnchantTable) {
				return new SpoutCraftInventory(((ContainerEnchantTable)container).a);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new SpoutCraftInventory(this.player.inventory);
		}
		return null;
	}

}

class DecayingLabel extends GenericLabel {
	private int ticksAlive = 0;
	public DecayingLabel(int ticks, String s) {
		super(s);
		ticksAlive = ticks;
	}
	
	@Override
	public void onTick() {
		ticksAlive--;
		if (ticksAlive < 0) {
			setVisible(false);
		}
	}
}
