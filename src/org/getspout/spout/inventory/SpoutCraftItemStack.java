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

import org.bukkit.craftbukkit.inventory.CraftItemStack;

public class SpoutCraftItemStack extends CraftItemStack{

	public SpoutCraftItemStack(int type, int amount, short damage) {
		super(type, amount, damage);
	}
	
	public net.minecraft.server.ItemStack getHandle() {
		return this.item;
	}
	
	public static SpoutCraftItemStack fromItemStack(net.minecraft.server.ItemStack item) {
		if (item == null) return null;
		return new SpoutCraftItemStack(item.id, item.count, (short) item.getData());
	}
	
	public static SpoutCraftItemStack getContribCraftItemStack(org.bukkit.inventory.ItemStack item) {
		if (item == null) return null;
		if (item instanceof SpoutCraftItemStack) {
			return (SpoutCraftItemStack)item;
		}
		return new SpoutCraftItemStack(item.getTypeId(), item.getAmount(), item.getDurability());
	}

}
