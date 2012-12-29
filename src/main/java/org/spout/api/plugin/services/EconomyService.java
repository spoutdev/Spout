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
package org.spout.api.plugin.services;

import java.util.List;

import org.spout.api.Spout;
import org.spout.api.entity.Player;

/**
 * The economy service is a basic service that can be extended and registered as a service provider.<br/>
 * To implement your own economy, create a new class which extends EconomyService and overrides the abstract methods.<br/>
 * <p/>
 * To register your EconomyService you will need to do something similar to:<br/>
 * <code>getServiceManager().register(EconomyService.class, myEconomyInstance, myPlugin, ServicePriority)</code>
 * <p/>
 * For plugins that wish to get the current economy provider, they will need to:
 * {@link EconomyService#getEconomy()} this method can possibly return null, if an economy service has not been registered yet with the ServiceManager.
 * <p/>
 * Another option is to hook the {@link ServiceRegisterEvent} and get the service provider that is being registered in the event.
 */
public abstract class EconomyService {
	/**
	 * Checks if an EconomyService has been registered in the ServiceManager.
	 * @return true if an EconomyService has been registered
	 */
	public static boolean isEconomyEnabled() {
		return Spout.getEngine().getServiceManager().getRegistration(EconomyService.class) != null;
	}

	/**
	 * Gets the highest priority EconomyService registered in the Spout Services API.<br/>
	 * If there is currently no EconomyService registered null will be returned instead.
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
	 * @param name of the account to check
	 * @param amount to check if the account has
	 * @return true if the account has the given amount
	 */
	public abstract boolean has(String name, double amount);

	/**
	 * Returns the balance of the given account name.
	 * @param name of the account to check
	 * @return double balance of the account
	 */
	public abstract double get(String name);

	/**
	 * Withdraws the given amount from the account name specified<br/>
	 * This operation should fail if the account would drop below 0.
	 * @param name of the account to withdraw from
	 * @param amount to withdraw from the account
	 * @return true if the withdrawal was successful
	 */
	public abstract boolean withdraw(String name, double amount);

	/**
	 * Deposits the given amount into the account specified.<br/>
	 * This operation should only fail if the economy implementation has maximum values for accounts.
	 * @param name of the account to deposit into
	 * @param amount to deposit into the account
	 * @return true if the deposit was successful
	 */
	public abstract boolean deposit(String name, double amount);

	/**
	 * Attempts to transfer the given amount from one account to another.<br/>
	 * <p/>
	 * <p>This call will check if the from account has enough funds, then attempt to deposit funds into the to account.<br/>
	 * If there is an issue depositing funds into the to account, the transfer fails immediately.
	 * Next, it will then attempt to remove the amount from the from account, if this fails for any reason, it will attempt to remove the funds from the TO account.<br/>
	 * This may result in an edge case where the funds are left in the TO account but were never removed out of the FROM account.</p>
	 * @param account from
	 * @param account to
	 * @param amount to transfer
	 * @return if the transfer was successful
	 */
	public boolean transfer(String from, String to, double amount) {
		if (has(from, amount)) {
			if (deposit(to, amount)) {
				if (!withdraw(from, amount)) {
					withdraw(to, amount);
				} else {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the account exists in the economy service.
	 * @param name of the account
	 * @return if the account exists
	 */
	public abstract boolean exists(String name);

	/**
	 * This is a copied-method that assumes the player's name is their account name and<br/>
	 * Checks if the given account has at least as much as the amount specified.
	 * @param player of the account to check
	 * @param amount to check if the account has
	 * @return true if the account has the given amount
	 */
	public boolean has(Player player, double amount) {
		return has(player.getName(), amount);
	}

	/**
	 * Creates the account.
	 * @param name of the account
	 * @return true if the operation was successfull
	 */
	public abstract boolean create(String name);

	/**
	 * This is a copied-method that assumes the player's name is their account name and<br/>
	 * Returns the balance of the given account name.
	 * @param player of the account to check
	 * @return double balance of the account
	 */
	public double get(Player player) {
		return get(player.getName());
	}

	/**
	 * This is a copied-method that assumes the player's name is their account name and<br/>
	 * Withdraws the given amount from the account name specified, this operation should fail if the account would drop below 0.
	 * @param player of the account to withdraw from
	 * @param amount to withdraw from the account
	 * @return true if the withdrawal was successful
	 */
	public boolean withdraw(Player player, double amount) {
		return withdraw(player.getName(), amount);
	}

	/**
	 * This is a copied-method that assumes the player's name is their account name and<br/>
	 * Deposits the given amount into the account specific, this operation should only fail if the economy implementation has maximum values for accounts.
	 * @param player of the account to deposit into
	 * @param amount to deposit into the account
	 * @return true if the deposit was successful
	 */
	public boolean deposit(Player player, double amount) {
		return deposit(player.getName(), amount);
	}

	/**
	 * This is a copied-method that assumes the player's name is their account name and<br/>
	 * Checks if the account exists in the economy service.
	 * @param player of the account to check existence of.
	 * @return true if the account exists, otherwise false
	 */
	public boolean exists(Player player) {
		return exists(player.getName());
	}

	/**
	 * Returns the name of the default currency in singular form.
	 * @return name of the default currency (singular)
	 */
	public abstract String getCurrencyNameSingular();

	/**
	 * Returns the name of the default currency in plural form.
	 * @return name of the default currency (plural)
	 */
	public abstract String getCurrencyNamePlural();

	/**
	 * Returns the currency symbol used by this economy plugin, or null if none is used.<br/>
	 * For instance, if a plugin uses dollars this would be '$'.<br/>
	 * Some economies may not have a currency symbol, or the server may not use it, resulting in null or a blank string.
	 * @return the currency symbol
	 */
	public abstract String getCurrencySymbol();

	/**
	 * Returns a string formatted with the default currency name.<br/>
	 * Please see {@link EconomyService#formatShort()} for use with signs, or for the shorter symbol based output.
	 * @param amount to format
	 * @return formatted string
	 */
	public abstract String format(double amount);

	/**
	 * Returns a short-format of the amount, often for use in displaying on signs, or in character-limited areas.<br/>
	 * Most economy services will opt to use the currency symbol in this display.
	 * @param amount to format
	 * @return formatted string
	 */
	public abstract String formatShort(double amount);

	/**
	 * This will return a list of the top account names from the Economy.<br/>
	 * If playersOnly is true, only player accounts will be returned from the Economy.<br/>
	 * <p/>
	 * An implementation should allow -1 for the end value to assume all accounts.<br/>
	 * Depending on how the economy loads and stores accounts, this method may be particularly slow for getting large numbers of accounts.<br/>
	 * @param the start number, 1 for the account with the most money.
	 * @param the end number, must be greater than the start.
	 * @param only players
	 * @return ordered list of accounts
	 */
	public abstract List<String> getTopAccounts(int start, int end, boolean playersOnly);

	/**
	 * This is a copied-method that assumes you only want the top player accounts<br/>
	 * See {@link #getTopAccounts()}
	 * @param the start number, 1 for the account with the most money.
	 * @param the end number, must be greater than the start.
	 * @return ordered list of top player accounts within the range
	 */
	public List<String> getTopPlayerAccounts(int start, int end) {
		return getTopAccounts(start, end, true);
	}

	/**
	 * Some economy services round off after a specific number of digits.<br/>
	 * This function returns the number of digits the service keeps or -1 if no rounding occurs.<br/>
	 * An economy may return 0 if it is using integers for storing data.
	 * @return number of digits after the decimal point kept
	 */
	public abstract int numSignificantDigits();

	/**
	 * Whether this economy supports Multiple currencies or not.
	 * @return true if the economy supports multiple currencies.
	 */
	public abstract boolean hasMulticurrencySupport();

	/**
	 * MULTICURRENCY ONLY:  Get the list of currency names, these should be the singular names.<br/>
	 * It is necessary that this list return an ordered list that will not change over restarts.
	 * @return list of currency names
	 */
	public abstract List<String> getCurrencyNames();

	/**
	 * MULTICURRENCY ONLY: Returns the plural form of the given currency name.
	 * @param singular name
	 * @return plural name
	 */
	public abstract String getCurrencyNamePlural(String name);

	/**
	 * MULTICURRENCY ONLY: Returns the symbol of the given currency.
	 * @param name of the currency
	 * @return the currency's symbol
	 */
	public abstract String getCurrencySymbol(String name);

	/**
	 * MULTICURRENCY ONLY: Returns a formatted amount of the given currency name.<br/>
	 * Please see {@link EconomyService#formatShort()} for use with signs, or for the shorter symbol based output.
	 * @param name of the currency
	 * @param amount
	 * @return formatted output of the amount
	 */
	public abstract String format(String name, double amount);

	/**
	 * MULTICURRENCY ONLY: Returns a short formatted version of the amount with the given currency.<br/>
	 * This should be used for signs, or other places where you'd like to format the amount using the symbol.
	 * @param name
	 * @param amount
	 * @return formatted amount using the currency symbol.
	 */
	public abstract String formatShort(String name, double amount);

	/**
	 * MULTICURRENCY ONLY: Withdraws the given amount of the specified currency from the account given.<br/>
	 * This operation should fail if the account would drop below 0.
	 * @param name of the account to withdraw from
	 * @param amount to withdraw from the account
	 * @param currency name
	 * @return true if the withdrawal was successful
	 */
	public abstract boolean withdraw(String name, double amount, String currency);

	/**
	 * MULTICURRENCY ONLY: This is a copied-method that assumes the player's name is their account name and<br/>
	 * Withdraws the given amount from the account name specified, this operation should fail if the account would drop below 0.
	 * @param player of the account to withdraw from
	 * @param amount to withdraw from the account
	 * @param currency name
	 * @return true if the withdrawal was successful
	 */
	public boolean withdraw(Player player, double amount, String currency) {
		return withdraw(player.getName(), amount, currency);
	}

	/**
	 * Deposits the given amount of the currency into the account specified<br/>
	 * This operation should only fail if the economy implementation has maximum values for accounts.
	 * @param name of the account to deposit into
	 * @param amount to deposit into the account
	 * @param currency name
	 * @return true if the deposit was successful
	 */
	public abstract boolean deposit(String name, double amount, String currency);

	/**
	 * MULTICURRENCY ONLY: This is a copied-method that assumes the player's name is their account name and deposits the given amount into the account specific.<br/>
	 * This operation should only fail if the economy implementation has maximum values for accounts.
	 * @param player of the account to deposit into
	 * @param amount to deposit into the account
	 * @param currency name
	 * @return true if the deposit was successful
	 */
	public boolean deposit(Player player, double amount, String currency) {
		return deposit(player.getName(), amount, currency);
	}

	/**
	 * MULTICURRENCY ONLY: Attempts to transfer the given amount from one account to another of the specified currency.<br/>
	 * <p/>
	 * <p>This call will check if the from account has enough funds, then attempt to deposit funds into the to account.<br/>
	 * If there is an issue depositing funds into the to account, the transfer fails immediately.
	 * Next, it will then attempt to remove the amount from the from account, if this fails for any reason, it will attempt to remove the funds from the TO account.<br/>
	 * This may result in an edge case where the funds are left in the TO account but were never removed out of the FROM account.</p>
	 * @param account from
	 * @param account to
	 * @param amount to transfer
	 * @param name of the currency
	 * @return if the transfer was successful
	 */
	public boolean transfer(String from, String to, double amount, String currency) {
		if (has(from, amount, currency)) {
			if (deposit(to, amount, currency)) {
				if (!withdraw(from, amount, currency)) {
					withdraw(to, amount, currency);
				} else {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * MULTICURRENCY ONLY: Checks if the given account has at least as much as the amount specified of the given currency.
	 * @param name of the account to check
	 * @param amount to check if the account has
	 * @param currency name
	 * @return true if the account has the given amount
	 */
	public abstract boolean has(String name, double amount, String currency);

	/**
	 * MULTICURRENCY ONLY: Returns the balance of the given account name for the specified currency.
	 * @param name of the account to check
	 * @param currency name
	 * @return double balance of the account
	 */
	public abstract double get(String name, String currency);

	/**
	 * MULTICURRENCY ONLY: This is a copied-method that assumes the player's name is their account name and<br/>
	 * Checks if the given account has at least as much as the amount specified.
	 * @param player of the account to check
	 * @param amount to check if the account has
	 * @param currency name
	 * @return true if the account has the given amount
	 */
	public boolean has(Player player, double amount, String currency) {
		return has(player.getName(), amount, currency);
	}

	/**
	 * MULTICURRENCY ONLY: This is a copied-method that assumes the player's name is their account name and<br/>
	 * Returns the balance of the given account name.
	 * @param player of the account to check
	 * @param currency name
	 * @return double balance of the account
	 */
	public double get(Player player, String currency) {
		return get(player.getName(), currency);
	}

	/**
	 * MULTICURRENCY ONLY: This will return a list of the top account names from the Economy.<br/>
	 * If playersOnly is true, only player accounts will be returned from the Economy.<br/>
	 * <p/>
	 * It is assumed that a start value of 1 will return the highest value account.<br/>
	 * An implementation should allow -1 for the end value to assume all accounts.<br/>
	 * Depending on how the economy loads and stores accounts, this method may be particularly slow for getting large numbers of accounts.
	 * @param the start number, 1 for the account with the most money.
	 * @param the end number, must be greater than the start.
	 * @param currency name to check
	 * @param only players
	 * @return
	 */
	public abstract List<String> getTopAccounts(int start, int end, String currency, boolean playersOnly);

	/**
	 * MULTICURRENCY ONLY: This is a copied-method that assumes you only want the top player accounts of a given currency<br/>
	 * See {@link #getTopAccounts()}
	 * @param the start number, 1 for the account with the most money.
	 * @param the end number, must be greater than the start.
	 * @return ordered list of player accounts
	 */
	public List<String> getTopPlayerAccounts(int start, int end, String currency) {
		return getTopAccounts(start, end, currency, true);
	}

	/**
	 * MULTICURRENCY ONLY: returns the given exchange rate as a double value between the two given currencies.<br/>
	 * This result is: 1 currencyFrom equals X of currency to.
	 * @param currency going from
	 * @param currency going to
	 * @return how much currencyTo you get for 1 currencyFrom
	 */
	public abstract double getExchangeRate(String currencyFrom, String currencyTo);
}
