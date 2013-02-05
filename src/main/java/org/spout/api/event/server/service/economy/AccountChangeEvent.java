/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.event.server.service.economy;

import org.spout.api.event.HandlerList;
import org.spout.api.plugin.services.EconomyService;

public class AccountChangeEvent extends EconomyEvent {
	private static final HandlerList handlers = new HandlerList();

	private final String account;
	private final double amount;
	private final String currency;

	public AccountChangeEvent(EconomyService economy, String account, double amount, String currency) {
		super(economy);
		this.account = account;
		this.amount = amount;
		this.currency = currency;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	/**
	 * The account name being changed
	 * @return account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * The amount of the change
	 * @return amount
	 */
	public double getAmount() {
		return amount;
	}

	/**
	 * The currency being used, or null if no currency.
	 * @return currency
	 */
	public String getCurrency() {
		return currency;
	}
}
