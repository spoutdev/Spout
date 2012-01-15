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
import java.util.ArrayList;
import java.util.List;
import org.spout.api.ClientOnly;
import org.spout.api.packet.PacketUtil;

public class GenericComboBox extends GenericButton implements ComboBox {

	/** Current version for serialisation and packet handling.*/
	private static final long serialVersionUID = 0L;
	private List<String> items = new ArrayList<String>();
	private boolean open = false;
	private int selection = -1;

	public GenericComboBox() {
	}

	public GenericComboBox(int width, int height) {
		super(width, height);
	}

	public GenericComboBox(int X, int Y, int width, int height) {
		super(X, Y, width, height);
	}

	@Override
	public ComboBox setItems(List<String> items) {
		this.items = items;
		return this;
	}

	@Override
	public List<String> getItems() {
		return items;
	}

	@Override
	public ComboBox openList() {
		setOpen(true, true);
		return this;
	}

	@Override
	public ComboBox closeList() {
		setOpen(false, true);
		return null;
	}

	@Override
	public String getSelectedItem() {
		if (selection >= 0 && selection < items.size()) {
			return items.get(selection);
		} else {
			return null;
		}
	}

	public int getSelectedRow() {
		return this.selection;
	}

	public ComboBox setSelection(int row) {
		boolean event = row != selection;
		this.selection = row;
		if (event) {
			onSelectionChanged(row, getSelectedItem());
		}
		return this;
	}

	public void onSelectionChanged(int i, String text) {
	}

	public boolean isOpen() {
		return open;
	}

	/**
	 * Sets the open status.
	 * @param open the state
	 * @param sendPacket if true, send an update packet
	 * @return the instance
	 */
	public ComboBox setOpen(boolean open, boolean sendPacket) {
		if (sendPacket) {
			if (open != this.open) {
				this.open = open;
//				PacketComboBox packet = new PacketComboBox(this);
//				getScreen().getPlayer().sendPacket(packet);
			}
		}

		this.open = open;
		return this;
	}

	@Override
	public WidgetType getType() {
		return WidgetType.COMBOBOX;
	}

	@Override
	public int getVersion() {
		return super.getVersion() + (int) serialVersionUID;
	}

	@Override
	public int getNumBytes() {
		int bytes = 0;
		for (String item : getItems()) {
			bytes += PacketUtil.getNumBytes(item);
		}
		return super.getNumBytes() + 4 + 4 + bytes;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeInt(selection);
		output.writeInt(getItems().size());
		for (String item : getItems()) {
			PacketUtil.writeString(output, item);
		}
	}

	@Override
	@ClientOnly
	public void render() {
//		Spoutcraft.getClient().getRenderDelegate().render(this);
	}
}
