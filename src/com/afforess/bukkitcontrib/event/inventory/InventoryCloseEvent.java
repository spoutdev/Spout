package com.afforess.bukkitcontrib.event.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

public class InventoryCloseEvent extends Event implements Cancellable {
	private static final long serialVersionUID = -316124458220245924L;
	protected Inventory inventory;
	protected Player player;
	protected boolean cancelled;
	
	public InventoryCloseEvent(Player player) {
		super("InventoryCloseEvent");
		this.player = player;
	}
	
	public InventoryCloseEvent(Player player, Inventory inventory) {
		super("InventoryCloseEvent");
		this.player = player;
		this.inventory = inventory;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
