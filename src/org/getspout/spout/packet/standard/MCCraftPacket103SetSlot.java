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

package org.getspout.spout.packet.standard;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet103SetSlot;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.packet.standard.MCPacket103SetSlot;

/**
 * Implementation of MCPacket103SetSlot
 * @author Richard Robertson
 */
public class MCCraftPacket103SetSlot extends MCCraftPacket implements MCPacket103SetSlot
{
	private static Field windowField, slotField, itemStackField;
	
	private int rawWindow, rawSlot;
	private net.minecraft.server.ItemStack notchStack;
	private ItemStack bukkitStack;
	private Slot slot;
	private Window window;
	
	// Glom onto Notch's packet
	static
	{
		Class<Packet103SetSlot> packetclass = Packet103SetSlot.class;
		try
		{
			windowField = packetclass.getDeclaredField("a");
			slotField = packetclass.getDeclaredField("b");
			itemStackField = packetclass.getDeclaredField("c");
			
			windowField.setAccessible(true);
			slotField.setAccessible(true);
			itemStackField.setAccessible(true);
		}
		catch (Exception ex)
		{
			Logger.getLogger("Minecraft").log(Level.WARNING, "org.getspout.spout.packet.standard.MCCraftPacket103SetSlot\nError accessing net.minecraft.server.Packet103SetSlot.");
		}
	}
	
	@Override
	public Slot getSlot()
	{
		return slot;
	}

	@Override
	public void setSlot(Slot slot)
	{
		this.slot = slot;
		try
		{
			rawWindow = slot.rawWindowId;
			window = Window.getWindowById(rawWindow);
			rawSlot = slot.rawSlotId;
			windowField.set(packet, rawWindow);
			slotField.set(packet, rawSlot);
		}
		catch (Exception ex)
		{
		}
	}

	@Override
	public Window getWindow()
	{
		return window;
	}

	@Override
	public int getRawSlot()
	{
		return rawSlot;
	}

	@Override
	public void setRawSlot(int slot)
	{
		rawSlot = slot;
		try
		{
			slotField.set(packet, slot);
			this.slot = Slot.getSlotByRawValues(rawWindow, rawSlot);
		}
		catch (Exception ex)
		{
		}
	}

	@Override
	public int getRawWindow()
	{
		return rawWindow;
	}

	@Override
	public void setRawWindow(int window)
	{
		rawWindow = window;
		try
		{
			windowField.set(packet, window);
			this.window = Window.getWindowById(window);
			slot = Slot.getSlotByRawValues(rawWindow, rawSlot);
		}
		catch (Exception ex)
		{
		}
	}

	@Override
	public ItemStack getItemStack()
	{
		return bukkitStack;
	}

	@Override
	public void setItemStack(ItemStack itemStack)
	{
		bukkitStack = itemStack;
		if (bukkitStack == null)
			notchStack = null;
		else
			notchStack = new net.minecraft.server.ItemStack(bukkitStack.getTypeId(), bukkitStack.getAmount(), bukkitStack.getDurability());
		try
		{
			itemStackField.set(packet, notchStack);
		}
		catch (Exception ex)
		{
		}
	}
	
	@Override
	public void setPacket(Packet packet, int packetId)
	{
		super.setPacket(packet, packetId);
		try
		{
			rawWindow = ((Integer)windowField.get(packet)).intValue();
			window = Window.getWindowById(rawWindow);
			rawSlot = ((Integer)slotField.get(packet)).intValue();
			slot = Slot.getSlotByRawValues(rawWindow, rawSlot);
			notchStack = (net.minecraft.server.ItemStack)itemStackField.get(packet);
			if (notchStack != null)
				bukkitStack = new ItemStack(notchStack.id, notchStack.count, (short)notchStack.damage);
		}
		catch (Exception ex)
		{
		}
	}
	
	@Override
	public String toString()
	{
		if (notchStack == null)
			return "{" + slot.toString() + ", null}";
		else
			return "{" + slot.toString() + ", " + bukkitStack.toString() + "}";
	}
}
