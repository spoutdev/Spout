package org.bukkitcontrib;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.Container;
import net.minecraft.server.ContainerChest;
import net.minecraft.server.ContainerDispenser;
import net.minecraft.server.ContainerFurnace;
import net.minecraft.server.ContainerPlayer;
import net.minecraft.server.ContainerWorkbench;
import net.minecraft.server.CraftingManager;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryCraftResult;
import net.minecraft.server.InventoryCrafting;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet100OpenWindow;
import net.minecraft.server.Packet101CloseWindow;
import net.minecraft.server.Packet102WindowClick;
import net.minecraft.server.Packet103SetSlot;
import net.minecraft.server.Packet104WindowItems;
import net.minecraft.server.Packet105CraftProgressBar;
import net.minecraft.server.Packet106Transaction;
import net.minecraft.server.Packet10Flying;
import net.minecraft.server.Packet130UpdateSign;
import net.minecraft.server.Packet131;
import net.minecraft.server.Packet14BlockDig;
import net.minecraft.server.Packet15Place;
import net.minecraft.server.Packet16BlockItemSwitch;
import net.minecraft.server.Packet17;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.Packet19EntityAction;
import net.minecraft.server.Packet1Login;
import net.minecraft.server.Packet200Statistic;
import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet21PickupSpawn;
import net.minecraft.server.Packet22Collect;
import net.minecraft.server.Packet23VehicleSpawn;
import net.minecraft.server.Packet24MobSpawn;
import net.minecraft.server.Packet255KickDisconnect;
import net.minecraft.server.Packet25EntityPainting;
import net.minecraft.server.Packet27;
import net.minecraft.server.Packet28EntityVelocity;
import net.minecraft.server.Packet29DestroyEntity;
import net.minecraft.server.Packet2Handshake;
import net.minecraft.server.Packet30Entity;
import net.minecraft.server.Packet34EntityTeleport;
import net.minecraft.server.Packet38EntityStatus;
import net.minecraft.server.Packet39AttachEntity;
import net.minecraft.server.Packet3Chat;
import net.minecraft.server.Packet40EntityMetadata;
import net.minecraft.server.Packet4UpdateTime;
import net.minecraft.server.Packet50PreChunk;
import net.minecraft.server.Packet51MapChunk;
import net.minecraft.server.Packet52MultiBlockChange;
import net.minecraft.server.Packet53BlockChange;
import net.minecraft.server.Packet54PlayNoteBlock;
import net.minecraft.server.Packet5EntityEquipment;
import net.minecraft.server.Packet60Explosion;
import net.minecraft.server.Packet61;
import net.minecraft.server.Packet6SpawnPosition;
import net.minecraft.server.Packet70Bed;
import net.minecraft.server.Packet71Weather;
import net.minecraft.server.Packet7UseEntity;
import net.minecraft.server.Packet8UpdateHealth;
import net.minecraft.server.Packet9Respawn;
import net.minecraft.server.Slot;
import net.minecraft.server.TileEntityDispenser;
import net.minecraft.server.TileEntityFurnace;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkitcontrib.event.inventory.InventoryClickEvent;
import org.bukkitcontrib.event.inventory.InventoryCloseEvent;
import org.bukkitcontrib.event.inventory.InventoryCraftEvent;
import org.bukkitcontrib.event.inventory.InventoryOpenEvent;
import org.bukkitcontrib.event.inventory.InventoryPlayerClickEvent;
import org.bukkitcontrib.event.inventory.InventorySlotType;
import org.bukkitcontrib.inventory.ContribCraftInventory;
import org.bukkitcontrib.inventory.ContribCraftInventoryPlayer;
import org.bukkitcontrib.inventory.ContribCraftItemStack;
import org.bukkitcontrib.inventory.ContribCraftingInventory;
import org.bukkitcontrib.inventory.ContribInventory;
import org.bukkitcontrib.inventory.CraftingInventory;
import org.bukkitcontrib.packet.CorePacketType;
import org.bukkitcontrib.packet.listener.PacketListenerHandler;

public class ContribNetServerHandler extends NetServerHandler {
	protected Map<Integer, Short> n = new HashMap<Integer, Short>();
	protected boolean activeInventory = false;
	protected Location activeLocation = null;
	protected ItemStack lastOverrideDisplayStack = null;

	public ContribNetServerHandler(MinecraftServer minecraftserver,
			NetworkManager networkmanager, EntityPlayer entityplayer) {
		super(minecraftserver, networkmanager, entityplayer);
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

	public ContribInventory getActiveInventory() {
		return getInventoryFromContainer(player.activeContainer);
	}

	public ContribInventory getDefaultInventory() {
		if (player.defaultContainer.equals(player.activeContainer))
			return null;
		return getInventoryFromContainer(player.defaultContainer);
	}

	public InventorySlotType getInventorySlotType(int clicked) {
		if (clicked < 9)
			return InventorySlotType.QUICKBAR;
		return InventorySlotType.CONTAINER;
	}

	public InventorySlotType getActiveInventorySlotType(int clicked) {
		if (clicked == -999)
			return InventorySlotType.OUTSIDE;
		ContribInventory active = getActiveInventory();
		int size = active.getSize();
		if (player.activeContainer instanceof ContainerChest)
			return InventorySlotType.CONTAINER;
		else if (player.activeContainer instanceof ContainerPlayer) {
			if (clicked == 0)
				return InventorySlotType.RESULT;
			if (clicked < 5)
				return InventorySlotType.CRAFTING;
			if (clicked == 5)
				return InventorySlotType.HELMET;
			if (clicked == 6)
				return InventorySlotType.ARMOR;
			if (clicked == 7)
				return InventorySlotType.LEGGINGS;
			if (clicked == 8)
				return InventorySlotType.BOOTS;
			if (clicked < size)
				return InventorySlotType.CONTAINER;
			return InventorySlotType.QUICKBAR;
		} else if (player.activeContainer instanceof ContainerFurnace) {
			if (clicked == 0)
				return InventorySlotType.SMELTING;
			if (clicked == 1)
				return InventorySlotType.FUEL;

			return InventorySlotType.RESULT;
		} else if (player.activeContainer instanceof ContainerDispenser)
			return InventorySlotType.CONTAINER;
		else if (player.activeContainer instanceof ContainerWorkbench) {
			if (clicked == 0)
				return InventorySlotType.RESULT;
			else if (clicked < size)
				return InventorySlotType.CRAFTING;
			return InventorySlotType.CONTAINER;
		}
		if (clicked >= size + 27)
			return InventorySlotType.QUICKBAR;
		if (clicked >= size)
			return InventorySlotType.PACK;
		return InventorySlotType.CONTAINER;
	}

	@Override
	public void a(Packet101CloseWindow packet) {
		ContribInventory inventory = getActiveInventory();

		InventoryCloseEvent event = new InventoryCloseEvent(
				(Player) player.getBukkitEntity(), inventory,
				getDefaultInventory(), activeLocation);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			IInventory inv = ((ContribInventory) event.getInventory())
					.getHandle();
			if (inventory instanceof TileEntityFurnace) {
				player.a((TileEntityFurnace) inventory);
			} else if (inventory instanceof TileEntityDispenser) {
				player.a((TileEntityDispenser) inventory);
			} else if (inventory instanceof InventoryCraftResult
					&& player.activeContainer instanceof ContainerWorkbench) {
				sendPacket(new Packet100OpenWindow(packet.a, 1, "Crafting", 9));
				player.syncInventory();
			} else if (inventory instanceof InventoryCraftResult) {
				//There is no way to force a player's own inventory back open.
			} else {
				player.a(inv);
			}
		} else {
			activeInventory = false;
			activeLocation = null;
			super.a(packet);
		}
	}

	@Override
	public void a(Packet106Transaction packet) {
		if (player.dead)
			return;
		if (!PacketListenerHandler.checkPacket(CorePacketType.TRANSACTION,
				packet))
			return;
		Short oshort = n.get(Integer.valueOf(player.activeContainer.windowId));

		if (oshort != null && packet.b == oshort.shortValue()
				&& player.activeContainer.windowId == packet.a
				&& !player.activeContainer.c(player)) {
			player.activeContainer.a(player, true);
		}
	}

	@Override
	public void a(Packet102WindowClick packet) {
		if (player.activeContainer.windowId == packet.a
				&& player.activeContainer.c(player)) {
			ContribInventory inventory = getActiveInventory();
			CraftPlayer player = (CraftPlayer) this.player.getBukkitEntity();
			ItemStack before = ItemStack.b(packet.e);
			ItemStack cursorBefore = this.player.inventory.j();
			ContribCraftItemStack slot = ContribCraftItemStack
					.fromItemStack(before);
			ContribCraftItemStack cursor = ContribCraftItemStack
					.fromItemStack(cursorBefore);
			InventorySlotType type = getActiveInventorySlotType(packet.b);
			boolean clickSuccessful = true;
			final int windowId = packet.a;

			//alert of a newly opened inventory
			if (!activeInventory) {
				activeInventory = true;
				InventoryOpenEvent event = new InventoryOpenEvent(player,
						inventory, getDefaultInventory(), activeLocation);
				Bukkit.getServer().getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					this.player.y();
					activeInventory = false;
					activeLocation = null;
					return;
				}
			}

			// Fire InventoryChange or InventoryCraft event
			if (packet.b != -999) {
				if (inventory instanceof CraftingInventory) {
					CraftingInventory crafting = (CraftingInventory) inventory;
					InventoryCrafting recipe = (InventoryCrafting) crafting
							.getMatrixHandle();

					ContribCraftItemStack craftResult = ContribCraftItemStack
							.fromItemStack(CraftingManager.getInstance().craft(
									recipe));
					ContribCraftItemStack[] recipeContents = new ContribCraftItemStack[recipe
							.getSize()];
					for (int i = 0; i < recipe.getSize(); i++) {
						org.bukkit.inventory.ItemStack temp = crafting
								.getMatrix()[i];
						recipeContents[i] = temp == null ? null
								: new ContribCraftItemStack(temp.getTypeId(),
										temp.getAmount(), temp.getDurability());
					}

					ContribCraftItemStack[][] matrix = null;
					if (recipe.getSize() == 4) {
						matrix = new ContribCraftItemStack[][] {
								Arrays.copyOfRange(recipeContents, 0, 2),
								Arrays.copyOfRange(recipeContents, 2, 4) };
					} else if (recipe.getSize() == 9) {
						matrix = new ContribCraftItemStack[][] {
								Arrays.copyOfRange(recipeContents, 0, 3),
								Arrays.copyOfRange(recipeContents, 3, 6),
								Arrays.copyOfRange(recipeContents, 6, 9) };
					}
					//Clicking to grab the crafting result
					if (type == InventorySlotType.RESULT) {
						InventoryCraftEvent craftEvent = new InventoryCraftEvent(
								getPlayer(), crafting, activeLocation, type,
								packet.b, matrix, craftResult, cursor,
								packet.c == 0, packet.f);
						Bukkit.getServer().getPluginManager()
								.callEvent(craftEvent);
						craftEvent.getInventory().setResult(
								craftEvent.getResult());
						cursor = craftEvent.getCursor() == null ? null
								: new ContribCraftItemStack(craftEvent
										.getCursor().getTypeId(), craftEvent
										.getCursor().getAmount(), craftEvent
										.getCursor().getDurability());
						if (craftEvent.isCancelled()) {
							craftEvent.getInventory().setMatrix(recipeContents);
							setCursorSlot(cursor != null ? cursor.getHandle()
									: null);
							clickSuccessful = false;
						}
					}
				}
			}

			if (clickSuccessful) {
				clickSuccessful = handleInventoryClick(packet, type, slot,
						cursor, inventory);
			}

			if (clickSuccessful) {
				this.player.netServerHandler
						.sendPacket(new Packet106Transaction(windowId,
								packet.d, true));
				this.player.h = true;
				this.player.activeContainer.a();
				this.player.z();
				this.player.h = false;
			} else {
				n.put(Integer.valueOf(this.player.activeContainer.windowId),
						Short.valueOf(packet.d));
				this.player.netServerHandler
						.sendPacket(new Packet106Transaction(windowId,
								packet.d, false));
				this.player.activeContainer.a(this.player, false);
				ArrayList<ItemStack> arraylist = new ArrayList<ItemStack>();

				for (int i = 0; i < this.player.activeContainer.e.size(); ++i) {
					arraylist.add(((Slot) this.player.activeContainer.e.get(i))
							.getItem());
				}

				if (!PacketListenerHandler.checkPacket(
						CorePacketType.WINDOW_CLICK, packet))
					return;
				this.player.a(this.player.activeContainer, arraylist);
			}
		}
	}

	public boolean handleInventoryClick(Packet102WindowClick packet,
			InventorySlotType type, ContribCraftItemStack slot,
			ContribCraftItemStack cursor, ContribInventory inventory) {
		InventoryClickEvent event = null;
		Result result = Result.DEFAULT;
		boolean success = false;
		final int LEFT_CLICK = 0;
		final int RIGHT_CLICK = 1;
		int click = packet.c;

		//clicked on bottom player inventory
		if (!(player.activeContainer instanceof ContainerPlayer)
				&& player.defaultContainer instanceof ContainerPlayer
				&& packet.b >= inventory.getSize()) {
			int activeSlot = packet.b - inventory.getSize() + 9;
			if (activeSlot > getPlayer().getInventory().getSize()) {
				activeSlot -= getPlayer().getInventory().getSize();
			}
			type = getInventorySlotType(activeSlot);
			event = new InventoryPlayerClickEvent(getPlayer(), getPlayer()
					.getInventory(), type, slot, cursor, activeSlot,
					click == LEFT_CLICK, packet.f, activeLocation);
		} else {
			event = new InventoryClickEvent(getPlayer(), inventory, type, slot,
					cursor, packet.b, click == LEFT_CLICK, packet.f,
					activeLocation);
		}

		if (event != null) {
			Bukkit.getServer().getPluginManager().callEvent(event);
			result = event.getResult();
			cursor = ContribCraftItemStack.getContribCraftItemStack(event
					.getCursor());
			slot = ContribCraftItemStack.getContribCraftItemStack(event
					.getItem());
		}

		//initialize setup
		ItemStack itemstack = slot != null ? slot.getHandle() : null;
		ItemStack cursorstack = cursor != null ? cursor.getHandle() : null;

		// NOTE: Successful means that its successful as-is; thus, only becomes true for default behaviour

		switch (result) {
		case DEFAULT:
			itemstack = player.activeContainer.a(packet.b, packet.c, packet.f,
					player);
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
					//cursorstack = new ItemStack(event.getCursor().getTypeId(), event.getCursor().getAmount(), event.getCursor().getDurability());
					setCursorSlot(cursorstack);
				}
			}

			break;
		case ALLOW: // Allow the placement unconditionally
			if (packet.b == -999) { // Clicked outside, just defer to default
				itemstack = player.activeContainer.a(packet.b, packet.c,
						packet.f, player);
			} else {
				if (click == LEFT_CLICK
						&& (itemstack != null && cursorstack != null && itemstack
								.doMaterialsMatch(cursorstack))) {
					// Left-click full slot with full cursor of same item; merge stacks
					itemstack.count += cursorstack.count;
					cursorstack = null;
				} else if (click == LEFT_CLICK
						|| (itemstack != null && cursorstack != null && !itemstack
								.doMaterialsMatch(cursorstack))) {
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
				//update the stacks
				setActiveSlot(packet.b, itemstack);
				setCursorSlot(cursorstack);
			}
			break;
		}
		return success;
	}

	public void setActiveSlot(int slot, ItemStack item) {
		player.activeContainer.b(slot).c(item);
	}

	public void setCursorSlot(ItemStack item) {
		player.inventory.b(item);
	}

	private ContribInventory getInventoryFromContainer(Container container) {
		try {
			if (container instanceof ContainerChest) {
				Field a = ContainerChest.class.getDeclaredField("a");
				a.setAccessible(true);
				return new ContribCraftInventory((IInventory) a.get(container));
			}
			if (container instanceof ContainerPlayer)
				return new ContribCraftInventoryPlayer(player.inventory,
						new ContribCraftingInventory(
								((ContainerPlayer) container).craftInventory,
								((ContainerPlayer) container).resultInventory));
			if (container instanceof ContainerFurnace) {
				Field a = ContainerFurnace.class.getDeclaredField("a");
				a.setAccessible(true);
				return new ContribCraftInventory(
						(TileEntityFurnace) a.get(container));
			}
			if (container instanceof ContainerDispenser) {
				Field a = ContainerDispenser.class.getDeclaredField("a");
				a.setAccessible(true);
				return new ContribCraftInventory(
						(TileEntityDispenser) a.get(container));
			}
			if (container instanceof ContainerWorkbench)
				return new ContribCraftingInventory(
						((ContainerWorkbench) container).craftInventory,
						((ContainerWorkbench) container).resultInventory);
		} catch (Exception e) {
			e.printStackTrace();
			return new ContribCraftInventory(player.inventory);
		}
		return null;
	}

	@Override
	public void a(Packet10Flying packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.FLYING, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet130UpdateSign packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.UPDATE_SIGN,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet14BlockDig packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.BLOCK_DIG, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet15Place packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.PLACE, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet16BlockItemSwitch packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.BLOCK_ITEM_SWITCH,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet18ArmAnimation packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.ARM_ANIMATION,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet19EntityAction packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.ENTITY_ACTION,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet255KickDisconnect packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.KICK_DISCONNECT,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet27 packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.STANCE_UPDATE,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet3Chat packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.CHAT, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet7UseEntity packet) {
		if (PacketListenerHandler
				.checkPacket(CorePacketType.USE_ENTITY, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet9Respawn packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.RESPAWN, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet100OpenWindow packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.OPEN_WINDOW,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet103SetSlot packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.SET_SLOT, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet104WindowItems packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.WINDOW_ITEMS,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet105CraftProgressBar packet) {
		if (PacketListenerHandler.checkPacket(
				CorePacketType.CRAFT_PROGRESS_BAR, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet131 packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.MAP_DATA, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet17 packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.NEW_STATE, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet1Login packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.LOGIN, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet200Statistic packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.STATISTIC, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet20NamedEntitySpawn packet) {
		if (PacketListenerHandler.checkPacket(
				CorePacketType.NAMED_ENTITY_SPAWN, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet21PickupSpawn packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.PICKUP_SPAWN,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet22Collect packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.COLLECT, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet23VehicleSpawn packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.VEHICLE_SPAWN, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet24MobSpawn packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.MOB_SPAWN, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet25EntityPainting packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.ENTITY_PAINTING, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet28EntityVelocity packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.ENTITY_VELOCITY, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet29DestroyEntity packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.DESTROY_ENTITY,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet2Handshake packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.HANDSHAKE, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet30Entity packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.ENTITY, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet34EntityTeleport packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.ENTITY_TELEPORT,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet38EntityStatus packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.ENTITY_STATUS,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet39AttachEntity packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.ATTACH_ENTITY,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet40EntityMetadata packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.ENTITY_METADATA,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet4UpdateTime packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.UPDATE_TIME,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet50PreChunk packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.PRE_CHUNK, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet51MapChunk packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.MAP_CHUNK, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet52MultiBlockChange packet) {
		if (PacketListenerHandler.checkPacket(
				CorePacketType.MULTI_BLOCK_CHANGE, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet53BlockChange packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.BLOCK_CHANGE,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet54PlayNoteBlock packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.PLAY_NOTE_BLOCK,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet5EntityEquipment packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.ENTITY_EQUIPMENT,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet60Explosion packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.EXPLOSION, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet61 packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.SOUND_EFFECT,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet6SpawnPosition packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.SPAWN_POSITION,
				packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet70Bed packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.BED, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet71Weather packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.WEATHER, packet)) {
			super.a(packet);
		}
	}

	@Override
	public void a(Packet8UpdateHealth packet) {
		if (PacketListenerHandler.checkPacket(CorePacketType.UPDATE_HEALTH,
				packet)) {
			super.a(packet);
		}
	}
}
