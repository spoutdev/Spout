/*
 * This file is part of SpoutAPI (http://wwwi.getspout.org/).
 * 
 * SpoutcraftAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpoutcraftAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spout.api.gui;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.spout.api.ClientOnly;

public class GenericListWidgetItem extends PropertyObject implements PropertyInterface, ListWidgetItem {
	private String title = null, text = null, iconUrl = null;
	private ListWidget parent;

	public GenericListWidgetItem(String title, String text, String iconUrl) {
		initProperties();
		setTitle(title);
		setText(text);
		setIconUrl(iconUrl);
	}

	private void initProperties() {
		addProperty("title", new Property() {
			public void set(Object value) {
				setTitle((String) value);
			}

			public Object get() {
				return getTitle();
			}
		});
		addProperty("text", new Property() {
			public void set(Object value) {
				setText((String) value);
			}

			public Object get() {
				return getText();
			}
		});
		addProperty("iconurl", new Property() {
			public void set(Object value) {
				setIconUrl((String) value);
			}

			public Object get() {
				return getIconUrl();
			}
		});
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

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof GenericListWidgetItem)) {
			return false;
		}
		GenericListWidgetItem li = (GenericListWidgetItem) other;
		return (new EqualsBuilder()).append(this.text, li.text).append(this.title, li.title).append(this.iconUrl, li.iconUrl).isEquals();
	}

	@Override
	public int hashCode() {
		return (new HashCodeBuilder()).append(text).append(title).append(iconUrl).toHashCode();
	}

	public int getHeight() {
		return 20;
	}

	public void setListWidget(ListWidget widget) {
		parent = widget;
	}

	public ListWidget getListWidget() {
		return parent;
	}

	public void onClick(int x, int y, boolean d) {
		System.out.println(getTitle() + " got clicked at "+x+", "+y + (d?" (double":""));
	}

	@Override
	@ClientOnly
	public void render() {
//		Spoutcraft.getClient().getRenderDelegate().render(this);
	}

	@ClientOnly
	public void render(int x, int y, int width, int height) {
//		Spoutcraft.getRenderDelegate().render(this, x, y, width, height);
	}
}
