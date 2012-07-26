/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.api.gui.attribute;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;

import org.spout.api.math.MathHelper;

public class SimpleAttributeStore implements AttributeStore {
	private HashMap<String, Attribute> attachedAttributes = new HashMap<String, Attribute>();

	@Override
	public boolean hasAttribute(String name) {
		return attachedAttributes.containsKey(name);
	}

	@Override
	public Attribute getAttribute(String name) {
		return attachedAttributes.get(name);
	}

	@Override
	public void setAttribute(Attribute value) {
		attachedAttributes.put(value.getName(), value);
	}

	@Override
	public Error[] setStylesheet(String css) {
		LinkedList<Error> errors = new LinkedList<Error>();
		css = css.replaceAll("\n", "");
		String attrs[] = css.split(";");
		for (String attr : attrs) {
			attr = attr.trim();
			String splt[] = attr.split(":");
			if (splt.length == 2) {
				String name = splt[0];
				AttributeUnit unit = null;
				for (String text : AttributeUnit.getAllTexts()) {
					if (text.isEmpty()) {
						continue;
					}
					if (splt[1].endsWith(text)) {
						unit = AttributeUnit.getByText(text);
						// Remove the unit from the text to parse the value
						splt[1] = splt[1].replaceAll(text + "^", ""); // TODO
																		// check
																		// if
																		// this
																		// works
						break;
					}
				}
				if (unit == null) {
					unit = AttributeUnit.NONE;
				}
				Object value = fromCSSString(splt[1]);
				setAttribute(new Attribute(name, new AttributeValue(value),
						unit));
			} else {
				errors.add(new Error(
						"More than 1 ':' found in attribute assignment"));
			}
		}
		return errors.toArray(new Error[0]);
	}

	@Override
	public String getStylesheet() {
		StringBuffer stylesheet = new StringBuffer();
		for (Attribute attr : attachedAttributes.values()) {

			stylesheet.append(attr.getName()).append( ": ");
			stylesheet.append(toCSSString(attr.getValue().getValue()));
			stylesheet.append(attr.getUnit().getText()[0]);
			stylesheet.append(";\n");
		}
		return stylesheet.toString();
	}

	public static Object fromCSSString(String attribute) {
		try {
			return Integer.getInteger(attribute);
		} catch (NumberFormatException e) {
		}
		try {
			return Double.parseDouble(attribute);
		} catch (NumberFormatException e) {
		}
		// TODO add colors
		if (attribute.startsWith("#")) {

		}
		if (attribute.matches("rgb\\([0-9]{1,3},[0-9]{1,3},[0-9]{1,3}\\)")) {

		}
		if (attribute
				.matches("rgba\\([0-9]{1,3},[0-9]{1,3},[0-9]{1,3},[0-9]{1,3}\\)")) {

		}
		return attribute;
	}

	public static String toCSSString(Object value) {
		if (value instanceof Color) {
			Color color = (Color) value;
			if (color.getAlpha() == 0) {
				return "#" + MathHelper.decToHex(color.getRed(), 2)
						+ MathHelper.decToHex(color.getGreen(), 2)
						+ MathHelper.decToHex(color.getBlue(), 2);
			} else {
				return "rgba(" + color.getRed() + ", " + color.getGreen()
						+ ", " + color.getBlue() + ", " + color.getAlpha()
						+ ")";
			}
		}
		return value.toString();
	}
}
