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
package org.getspout.spout.inventory;

import java.util.Collection;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.inventory.InventoryBuilder;

public class SpoutInventoryBuilder implements InventoryBuilder {

	@Override
	public Inventory construct(ItemStack[] items, String name) {
		return new CustomInventory(items, name);
	}

	@Override
	public Inventory construct(Collection<ItemStack> items, String name) {
		return new CustomInventory(items, name);
	}

	@Override
	public Inventory construct(int size, String name) {
		return new CustomInventory(size, name);
	}

}
