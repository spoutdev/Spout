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

public class GenericListWidget extends GenericScrollable implements ListWidget {

	/** Current version for serialisation and packet handling.*/
	private static final long serialVersionUID = 1L;
	private List<ListWidgetItem> items = new ArrayList<ListWidgetItem>();
	private int selected = -1;
	protected int cachedTotalHeight = -1;

	public WidgetType getType() {
		return WidgetType.ListWidget;
	}

	public ListWidgetItem[] getItems() {
		ListWidgetItem[] sample = {};
		return items.toArray(sample);
	}

	public ListWidgetItem getItem(int i) {
		if (i == -1) {
			return null;
		}
		ListWidgetItem items[] = getItems();
		if (i >= items.length) {
			return null;
		}
		return items[i];
	}

	public ListWidget addItem(ListWidgetItem item) {
		items.add(item);
		item.setListWidget(this);
		cachedTotalHeight = -1;
		return this;
	}

	public ListWidget addItems(ListWidgetItem... items) {
		for (ListWidgetItem item : items) {
			this.addItem(item);
		}
		return this;
	}

	public boolean removeItem(ListWidgetItem item) {
		if (items.contains(item)) {
			items.remove(item);
			item.setListWidget(null);
			cachedTotalHeight = -1;
			return true;
		}
		return false;
	}

	public ListWidgetItem getSelectedItem() {
		return getItem(selected);
	}

	public int getSelectedRow() {
		return selected;
	}

	public ListWidget setSelection(int n) {
		selected = n;
		if (selected < -1) {
			selected = -1;
		}
		if (selected > items.size() - 1) {
			selected = items.size() - 1;
		}

		//Check if selection is visible
		ensureVisible(getItemRect(selected));
		return this;
	}

	protected Rectangle getItemRect(int n) {
		ListWidgetItem item = getItem(n);
		Rectangle result = new Rectangle(0, 0, 0, 0);
		if (item == null) {
			return result;
		}
		result.setX(0);
		result.setY(getItemYOnScreen(n));
		result.setHeight(24);
		result.setWidth(getInnerSize(Orientation.VERTICAL));
		return result;
	}

	protected int getItemYOnScreen(int n) {
		return n * 24;
	}

	public int getSize() {
		return items.size();
	}

	public ListWidget clearSelection() {
		setSelection(-1);
		return this;
	}

	public boolean isSelected(int n) {
		return selected == n;
	}

	public ListWidget setScrollPosition(int position) {
		setScrollPosition(Orientation.VERTICAL, position);
		return this;
	}

	public int getScrollPosition() {
		return getScrollPosition(Orientation.VERTICAL);
	}

	@Override
	public int getInnerSize(Orientation axis) {
		if (axis == Orientation.HORIZONTAL) {
			return getViewportSize(Orientation.HORIZONTAL);
		}
		if (cachedTotalHeight == -1) {
			cachedTotalHeight = getItems().length * 24;
		}
		return cachedTotalHeight + 10;
	}

	public int getTotalHeight() {
		return getInnerSize(Orientation.VERTICAL);
	}

	public int getMaxScrollPosition() {
		return getMaximumScrollPosition(Orientation.VERTICAL);
	}

	public boolean isSelected(ListWidgetItem item) {
		if (getSelectedItem() == null) {
			return false;
		}
		return getSelectedItem().equals(item);
	}

	public ListWidget shiftSelection(int n) {
		if (selected + n < 0) {
			setSelection(0);
		} else {
			setSelection(selected + n);
		}
		return this;
	}

	public void onSelected(int item, boolean doubleClick) {
	}

	public void clear() {
		items.clear();
		cachedTotalHeight = -1;
		selected = -1;
		autoDirty();
	}

	@Override
	public int getNumBytes() {
		int bytes = 0;
		for (ListWidgetItem item : getItems()) {
			bytes += PacketUtil.getNumBytes(item.getTitle());
			bytes += PacketUtil.getNumBytes(item.getText());
			bytes += PacketUtil.getNumBytes(item.getIconUrl());
		}
		return super.getNumBytes() + 4 + 4 + bytes;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		selected = input.readInt();
		int count = input.readInt();
		for (int i = 0; i < count; i++) {
			ListWidgetItem item = new ListWidgetItem(PacketUtil.readString(input), PacketUtil.readString(input), PacketUtil.readString(input));
			addItem(item);
		}
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeInt(selected); // Write which item is selected.
		output.writeInt(getItems().length); // Write number of items first!
		for (ListWidgetItem item : getItems()) {
			PacketUtil.writeString(output, item.getTitle());
			PacketUtil.writeString(output, item.getText());
			PacketUtil.writeString(output, item.getIconUrl());
		}
	}

	@Override
	public int getVersion() {
		return super.getVersion() + (int) serialVersionUID;
	}

	@Override
	@ClientOnly
	public void render() {
//		Spoutcraft.getClient().getRenderDelegate().render(this);
	}
}
