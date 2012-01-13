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

public class GenericListView extends GenericListWidget implements ListWidget {

	/** Current version for serialisation and packet handling.*/
	private static final long serialVersionUID = 0L;
	private AbstractListModel model = null;
	private int selected = -1;

	public GenericListView(AbstractListModel model) {
		setModel(model);
	}

	public void setModel(AbstractListModel model) {
		if (this.model != null) {
			this.model.removeView(this);
		}
		this.model = model;
		if (this.model != null) {
			this.model.addView(this);
		}
	}

	public AbstractListModel getModel() {
		return model;
	}

	@Override
	public int getSize() {
		return model.getSize();
	}

	@Override
	public ListWidgetItem[] getItems() {
		ListWidgetItem items[] = new ListWidgetItem[model.getSize()];
		for (int i = 0; i < model.getSize(); i++) {
			items[i] = model.getItem(i);
		}
		return items;
	}

	@Override
	public ListWidgetItem getItem(int n) {
		return model.getItem(n);
	}

	@Override
	public ListWidget addItem(ListWidgetItem item) {
		return this;
	}

	@Override
	public boolean removeItem(ListWidgetItem item) {
		return false;
	}

	@Override
	public void clear() {
	}

	@Override
	public ListWidgetItem getSelectedItem() {
		if (getSelectedRow() < 0 || getSelectedRow() > getSize()) {
			return null;
		}
		return model.getItem(getSelectedRow());
	}

	@Override
	public int getSelectedRow() {
		return selected;
	}

	@Override
	public ListWidget setSelection(int n) {
		if (n < -1) {
			n = -1;
		}
		if (n >= model.getSize()) {
			n = model.getSize() - 1;
		}
		selected = n;
		if (n != -1) {
			ensureVisible(getItemRect(n));
		}

		return this;
	}

	@Override
	public ListWidget clearSelection() {
		selected = -1;
		return this;
	}

	@Override
	public boolean isSelected(int n) {
		return n == selected;
	}

	@Override
	public boolean isSelected(ListWidgetItem item) {
		return item == getSelectedItem();
	}

	@Override
	public ListWidget shiftSelection(int n) {
		if (selected + n < 0) {
			n = 0;
		}
		setSelection(selected + n);
		return this;
	}

	@Override
	public void onSelected(int item, boolean doubleClick) {
		model.onSelected(item, doubleClick);
	}

	public void sizeChanged() {
		cachedTotalHeight = -1;
		if (selected + 1 > model.getSize()) {
			selected = model.getSize() - 1;
			setScrollPosition(Orientation.VERTICAL, getMaximumScrollPosition(Orientation.VERTICAL));
		}
		autoDirty();
	}

	@Override
	public int getVersion() {
		return super.getVersion() + (int) serialVersionUID;
	}
}
