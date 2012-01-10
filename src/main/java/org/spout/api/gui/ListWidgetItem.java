/*
 * This file is part of SpoutAPI (http://wwwi.getspout.org/).
 *
 * Spout API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.gui;

/**
 * This is a single item for the ListWidget.
 */
public class ListWidgetItem {

	String title;
	String text;
	String iconUrl = "";
	ListWidget listWidget = null;
	
	public ListWidgetItem() {
	}

	public ListWidgetItem(String title, String text) {
		this.title = title;
		this.text = text;
	}
	
	public ListWidgetItem(String title, String text, String iconUrl) {
		this.title = title;
		this.text = text;
		this.iconUrl = iconUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public void setListWidget(ListWidget list) {
		if (listWidget != null && list != null && !listWidget.equals(list)) {
			listWidget.removeItem(this);
		}
		listWidget = list;
	}
	
	public int getHeight() {
		return 24;
	}
}
