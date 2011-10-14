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

public class CustomInventory extends SpoutCraftInventory implements Inventory{
	public CustomInventory(ItemStack[] items, String name) {
		super(new CustomMCInventory(items, name));
	}
	
	public CustomInventory(Collection<ItemStack> items, String name) {
		super(new CustomMCInventory(items, name));
	}
	
	public CustomInventory(int size, String name) {
		super(new CustomMCInventory(size, name));
	}
	
	public CustomMCInventory getHandle() {
		return (CustomMCInventory)getInventory();
	}
	
	public String getName() {
		return this.getHandle().getName();
	}
	
	public void setName(String name) {
		this.getHandle().setName(name);
	}
}
