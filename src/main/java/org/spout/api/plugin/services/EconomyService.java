/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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

import org.spout.api.player.Player;

public abstract class EconomyService {

	public abstract boolean has(String name, double amount);

	public abstract double get(String name);

	public abstract boolean withdraw(String name, double amount);

	public abstract boolean deposit(String name, double amount);

	public boolean has(Player player, double amount) {
		return has(player.getName(), amount);
	}

	public double get(Player player) {
		return get(player.getName());
	}

	public boolean withdraw(Player player, double amount) {
		return withdraw(player.getName(), amount);
	}

	public boolean deposit(Player player, double amount) {
		return deposit(player.getName(), amount);
	}
}
