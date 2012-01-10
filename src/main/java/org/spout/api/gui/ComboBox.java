package org.spout.api.gui;

import java.util.List;

public interface ComboBox extends Button {
	public ComboBox setItems(List<String> items);
	public List<String> getItems();
	public ComboBox openList();
	public ComboBox closeList();
	public String getSelectedItem();
	public int getSelectedRow();
	public ComboBox setSelection(int row);
	public void onSelectionChanged(int i, String text);
	public boolean isOpen();
}
