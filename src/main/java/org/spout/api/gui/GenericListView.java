package org.spout.api.gui;

public class GenericListView extends GenericListWidget implements ListWidget {
	
	private AbstractListModel model = null;
	private int selected = -1;
	
	public GenericListView(AbstractListModel model) {
		setModel(model);
	}

	public void setModel(AbstractListModel model) {
		if(this.model != null) this.model.removeView(this);
		this.model = model;
		if(this.model != null) this.model.addView(this);
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
		for(int i = 0; i < model.getSize(); i++) {
			items[i] = model.getItem(i);
		}
		return items;
	}

	@Override
	public ListWidgetItem getItem(int n) {
		return model.getItem(n);
	}

	public ListWidget addItem(ListWidgetItem item) {
		return this;
	}

	public boolean removeItem(ListWidgetItem item) {
		return false;
	}

	public void clear() {
	}

	public ListWidgetItem getSelectedItem() {
		if(getSelectedRow() < 0 || getSelectedRow() > getSize()) return null;
		return model.getItem(getSelectedRow());
	}

	public int getSelectedRow() {
		return selected;
	}

	public ListWidget setSelection(int n) {
		if(n < -1) {
			n = -1;
		}
		if(n >= model.getSize()) {
			n = model.getSize() - 1;
		}
		selected = n;
		if(n != -1) {
			ensureVisible(getItemRect(n));
		}
		
		return this;
	}

	public ListWidget clearSelection() {
		selected = -1;
		return this;
	}

	public boolean isSelected(int n) {
		return n == selected;
	}

	public boolean isSelected(ListWidgetItem item) {
		return item == getSelectedItem();
	}

	public ListWidget shiftSelection(int n) {
		if(selected + n < 0) n = 0;
		setSelection(selected + n);
		return this;
	}

	@Override
	public void onSelected(int item, boolean doubleClick) {
		model.onSelected(item, doubleClick);
	}
	
	public void sizeChanged() {
		cachedTotalHeight = -1;
		if(selected + 1 > model.getSize()) {
			selected = model.getSize() - 1;
			setScrollPosition(Orientation.VERTICAL, getMaximumScrollPosition(Orientation.VERTICAL));
		}
		autoDirty();
	}

}
