/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.getspout.unchecked.api.event.entity;

import org.getspout.unchecked.api.event.Cancellable;
import org.getspout.unchecked.api.event.HandlerList;

/**
 * Called when an entity gains health.
 */
public class EntityGainHealthEvent extends EntityEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	private int amount;

	private GainHealthReason reason;

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public GainHealthReason getReason() {
		return reason;
	}

	public void setReason(GainHealthReason reason) {
		this.reason = reason;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public enum GainHealthReason {
		PEACEFUL,
		HUNGER,
		EATING,
		CUSTOM;

	}

}
