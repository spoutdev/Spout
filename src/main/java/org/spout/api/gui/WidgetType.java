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
package org.spout.api.gui;

import java.util.HashSet;
import java.util.Set;

/**
 * All widgets need to have a WidgetType entry, this allows the client and
 * server widgets to sync properly. In order to have a network-enabled widget
 * you must have the same Widget subclass on both client and server, and then
 * register it as a network type.
 */
public final class WidgetType {

	/** A custom widget, not network enabled. */
	private static final int CUSTOM_WIDGET = -1;
	/** A network enabled custom widget. */
	private static final int NETWORK_WIDGET = -2;
	/** All registered custom widgets. */
	private static final Set<WidgetType> LOOKUP = new HashSet<WidgetType>();
	/** Spout registered network widgets, save bandwidth by only sending the id. */
	public static final WidgetType WIDGET = new WidgetType(AbstractWidget.class, CUSTOM_WIDGET),
			LABEL = new WidgetType(Label.class, 0),
			HEALTHBAR = new WidgetType(VanillaHealthBar.class, 1),
			BUBBLEBAR = new WidgetType(VanillaBubbleBar.class, 2),
			CHATBAR = new WidgetType(ChatBar.class, 3),
			CHATTEXTBOX = new WidgetType(ChatTextBox.class, 4),
			ARMORBAR = new WidgetType(VanillaArmorBar.class, 5),
			TEXTURE = new WidgetType(GenericTexture.class, 6),
			POPUP = new WidgetType(GenericPopup.class, 7),
			INGAMESCREEN = new WidgetType(InGameScreen.class, 8),
			ITEMWIDGET = new WidgetType(GenericItemWidget.class, 9),
			BUTTON = new WidgetType(GenericButton.class, 10),
			SLIDER = new WidgetType(GenericSlider.class, 11),
			TEXTFIELD = new WidgetType(GenericTextField.class, 12),
			GRADIENT = new WidgetType(Gradient.class, 13),
			CONTAINER = new WidgetType(Gradient.class, 14),
			ENTITYWIDGET = new WidgetType(GenericEntityWidget.class, 15),
			HUNGERBAR = new WidgetType(VanillaHungerBar.class, 17),
			EXPBAR = new WidgetType(VanillaExpBar.class, 18),
			CHECKBOX = new WidgetType(GenericCheckBox.class, 19),
			RADIOBUTTON = new WidgetType(GenericRadioButton.class, 20),
			LISTWIDGET = new WidgetType(GenericListWidget.class, 21),
			DIRTBACKGROUND = new WidgetType(DirtBackground.class, 22),
			SCROLLAREA = new WidgetType(GenericScrollArea.class, 23),
			LISTVIEW = new WidgetType(GenericListView.class, 24),
			COMBOBOX = new WidgetType(GenericComboBox.class, 25),
			POLYGON = new WidgetType(GenericPolygon.class, 26);
	/** The widget id, if below zero then it will not be unique. */
	private final int id;
	/** The widget class for creating new instances etc. */
	private final Class<? extends Widget> widgetClass;

	/**
	 * Create a new WidgetType - only used directly by Spout classes.
	 * @param widget the widget class
	 * @param id of internal class or NETWORK_WIDGET or CUSTOM_WIDGET
	 */
	private WidgetType(final Class<? extends Widget> widget, final int id) {
		widgetClass = widget;
		this.id = id;
		LOOKUP.add(this);
	}

	/**
	 * Get the widget id.
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get the widget class.
	 * @return the class
	 */
	public Class<? extends Widget> getWidgetClass() {
		return widgetClass;
	}

	/**
	 * Check if the widget is network enabled.
	 * @return if network enabled
	 */
	public boolean isNetworkEnabled() {
		return id >= 0 || id == NETWORK_WIDGET;
	}

	/**
	 * Add a private widget class (not network enabled).
	 * This is safe to call multiple times, as it will always return the same
	 * instance.
	 * @param widget the widget class
	 * @return the WidgetType
	 */
	public static WidgetType addType(final Class<? extends Widget> widget) {
		return addType(widget, false);
	}

	/**
	 * Add a private widget class.
	 * This is safe to call multiple times, as it will always return the same
	 * instance.
	 * @param widget the widget class
	 * @param network if it is network enabled
	 * @return the WidgetType
	 */
	public static WidgetType addType(final Class<? extends Widget> widget, final boolean network) {
		WidgetType type = getType(widget);
		if (type == null) {
			type = new WidgetType(widget, network ? NETWORK_WIDGET : CUSTOM_WIDGET);
		}
		return type;
	}

	/**
	 * Get the WidgetType for a private widget.
	 * @param widget the widget class
	 * @return the WidgetType or null
	 */
	public static WidgetType getType(final Class<? extends Widget> widget) {
		for (WidgetType type : LOOKUP) {
			if (type.widgetClass.equals(widget)) {
				return type;
			}
		}
		return null;
	}
}
