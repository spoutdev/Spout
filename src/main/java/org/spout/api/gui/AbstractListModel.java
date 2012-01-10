/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractListModel {
	
	private HashSet<GenericListView> views = new HashSet<GenericListView>();

	public abstract ListWidgetItem getItem(int row);

	public abstract int getSize();

	public abstract void onSelected(int item, boolean doubleClick);
	
	public void addView(GenericListView view) {
		views.add(view);
	}
	
	public void removeView(GenericListView view) {
		views.remove(view);
	}
	
	public void sizeChanged() {
		for(GenericListView view:views) {
			view.sizeChanged();
		}
	}
	
	public Set<GenericListView> getViews() {
		return Collections.unmodifiableSet(views);
	}

}
