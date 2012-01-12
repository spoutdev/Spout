/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.spout.api.ClientOnly;
import org.spout.api.inventory.ItemStack;

public class GenericItemWidget extends GenericWidget implements ItemWidget {

	protected int material = -1;
	protected short data = -1;
	protected int depth = 8;

	public GenericItemWidget() {
	}

	public GenericItemWidget(ItemStack item) {
		this.material = item.getMaterial().getId();
		this.data = item.getMaterial().getData();
	}

	@Override
	public int getVersion() {
		return super.getVersion() + 0;
	}

	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 10;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		this.setTypeId(input.readInt());
		this.setData(input.readShort());
		this.setDepth(input.readInt());
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeInt(getTypeId());
		output.writeShort(getData());
		output.writeInt(getDepth());
	}

	@Override
	public ItemWidget setTypeId(int id) {
		if (getTypeId() != id) {
			this.material = id;
			autoDirty();
		}
		return this;
	}

	@Override
	public int getTypeId() {
		return material;
	}

	@Override
	public ItemWidget setData(short data) {
		if (getData() != data) {
			this.data = data;
			autoDirty();
		}
		return this;
	}

	@Override
	public short getData() {
		return data;
	}

	@Override
	public ItemWidget setDepth(int depth) {
		if (getDepth() != depth) {
			this.depth = depth;
			autoDirty();
		}
		return this;
	}

	@Override
	public int getDepth() {
		return depth;
	}

	@Override
	public ItemWidget setHeight(int height) {
		super.setHeight(height);
		return this;
	}

	@Override
	public ItemWidget setWidth(int width) {
		super.setWidth(width);
		return this;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.ItemWidget;
	}

	@Override
	public ItemWidget copy() {
		return ((ItemWidget) super.copy()).setTypeId(getTypeId()).setData(getData()).setDepth(getDepth());
	}

	@Override
	@ClientOnly
	public void render() {
//		Spoutcraft.getClient().getRenderDelegate().render(this);
	}
}
