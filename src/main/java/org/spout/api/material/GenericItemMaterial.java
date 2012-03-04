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
package org.spout.api.material;

import org.spout.api.entity.Entity;
import org.spout.api.event.player.PlayerInteractEvent.Action;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.block.BlockFace;
import org.spout.api.model.Model;

public class GenericItemMaterial implements ItemMaterial {
	private final short id;
	private final short data;
	private final boolean subtypes;
	private final String name;
	private Model model;
	private String displayName;
	private int maxStackSize = 64;

	public GenericItemMaterial(String name, int id, int data, boolean subtypes) {
		this.name = name.replace(' ', '_');
		displayName = name;
		this.id = (short) id;
		this.data = (short) data;
		this.subtypes = subtypes;

		MaterialData.registerMaterial(this);
	}

	protected GenericItemMaterial(String name, int id, int data) {
		this(name, id, data, false);
	}

	public GenericItemMaterial(String name, int id) {
		this(name, id, 0, false);
	}

	public short getId() {
		return id;
	}

	public short getData() {
		return data;
	}

	public boolean hasSubtypes() {
		return subtypes;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Material setModel(Model model) {
		this.model = model;
		return this;
	}

	public Model getModel() {
		return model;
	}

	public void onInventoryRender() {
	}

	@Override
	public void onInteract(Entity entity, Point position, Action type, BlockFace clickedFace) {
	}

	@Override
	public int getMaxStackSize() {
		return maxStackSize;
	}

	@Override
	public void setMaxStackSize(int newValue) {
		maxStackSize = newValue;
	}
}
