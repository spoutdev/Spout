/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.plugin.services;

import org.spout.api.Spout;
import org.spout.api.player.Player;
import org.spout.api.plugin.ServiceProvider;

/**
 * The economy service is a basic service that can be extended and registered as a service provider.
 * To implement your own economy, create a new class which extends EconomyService and overrides
 * the abstract methods.
 * To register your EconomyService you will need to do something similar to:
 * <code>getServiceManager().register(EconomyService.class, myEconomyInstance, myPlugin, ServicePriority)</code>
 * 
 * For plugins that wish to get the current economy provider, they will need to:
 * <code>getServiceManager().getRegistration(EconomyService.class).getProvider();</code>
 * <code>getRegistration(EconomyService.class)</code> CAN return null, if there is no current registration.
 *
 */
public abstract class EconomyService {

	/**
	 * Checks if an EconomyService has been registered in the ServiceManager.
	 * 
	 * @return true if an EconomyService has been registered
	 */
	public static boolean isEconomyEnabled() {
		return Spout.getEngine().getServiceManager().getRegistration(EconomyService.class) != null;
	}
	
	/**
	 * Gets the highest priority EconomyService registered in the Spout Services API.
	 * If there is currently no EconomyService registered null will be returned instead.
	 * 
	 * @return EconomyService 
	 */
	public static EconomyService getEconomy() {
		if (!isEconomyEnabled()) {
			return null;
		}
		return Spout.getEngine().getServiceManager().getRegistration(EconomyService.class).getProvider();
	}
	
	/**
	 * Checks if the given account has at least as much as the amount specified.
	 * 
	 * @param name of the account to check
	 * @param amount to check if the account has
	 * @return true if the account has the given amount
	 */
	public abstract boolean has(String name, double amount);

	/**
	 * Returns the balance of the given account name.
	 * 
	 * @param name of the account to check
	 * @return double balance of the account
	 */
	public abstract double get(String name);

	/**
	 * Withdraws the given amount from the account name specified, this operation should fail
	 * if the account would drop below 0.
	 * 
	 * @param name of the account to withdraw from
	 * @param amount to withdraw from the account
	 * @return true if the withdrawal was successful
	 */
	public abstract boolean withdraw(String name, double amount);

	/**
	 * Deposits the given amount into the account specific, this operation should only fail
	 * if the economy implementation has maximum values for accounts.
	 * 
	 * @param name of the account to deposit into
	 * @param amount to deposit into the account
	 * @return true if the deposit was successful
	 */
	public abstract boolean deposit(String name, double amount);


	/**
	 * Checks if the account exists in the economy service.
	 * 
	 * @param name of the account
	 * @return if the account exists
	 */
	public abstract boolean exists(String name);

	/**
	 * This is a copied-method that assumes the player's name is their account name and
	 * Checks if the given account has at least as much as the amount specified.
	 * 
	 * @param player of the account to check
	 * @param amount to check if the account has
	 * @return true if the account has the given amount
	 */
	public boolean has(Player player, double amount) {
		return has(player.getName(), amount);
	}

	/**
	 * This is a copied-method that assumes the player's name is their account name and
	 * Returns the balance of the given account name.
	 * 
	 * @param player of the account to check
	 * @return double balance of the account
	 */
	public double get(Player player) {
		return get(player.getName());
	}

	/**
	 * This is a copied-method that assumes the player's name is their account name and
	 * Withdraws the given amount from the account name specified, this operation should fail
	 * if the account would drop below 0.
	 * 
	 * @param player of the account to withdraw from
	 * @param amount to withdraw from the account
	 * @return true if the withdrawal was successful
	 */
	public boolean withdraw(Player player, double amount) {
		return withdraw(player.getName(), amount);
	}

	/**
	 * This is a copied-method that assumes the player's name is their account name and
	 * Deposits the given amount into the account specific, this operation should only fail
	 * if the economy implementation has maximum values for accounts.
	 * 
	 * @param player of the account to deposit into
	 * @param amount to deposit into the account
	 * @return true if the deposit was successful
	 */
	public boolean deposit(Player player, double amount) {
		return deposit(player.getName(), amount);
	}

	/**
	 * This is a copied-method that assumes the player's name is their account name and
	 * Checks if the account exists in the economy service.
	 * 
	 * @param player of the account to check existence of.
	 * @return true if the account exists, otherwise false
	 */
	public boolean exists(Player player) {
		return exists(player.getName());
	}

	/**
	 * Returns the name of the currency in singular form.
	 * 
	 * @return name of the currency (singular)
	 */
	public abstract String getCurrencyNameSingular();


	/**
	 * Returns the name of the currency in plural form.
	 * 
	 * @return name of the currency (plural)
	 */
	public abstract String getCurrencyNamePlural();


	/**
	 * Some economy services round off after a specific number of digits.
	 * This function returns the number of digits the service keeps
	 * or -1 if no rounding occurs.
	 * An economy may return 0 if it is using integers for storing data.
	 * 
	 * @return number of digits after the decimal point kept
	 */
	public abstract int numSignificantDigits();
}
