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
package org.spout.api.gui.widget;

import org.spout.api.gui.TextProperties;
import org.spout.api.gui.WidgetType;

/**
 * Defines a container that has a border around it and a title on the top. It looks something like this:
 * <pre>
 * | Group title ------|
 * | (widgets)         |
 * |-------------------|
 * </pre>
 *
 */
public class Group extends AbstractWidgetContainer implements Label {
	
	private TextProperties properties = new TextProperties();
	private String text = "";

	@Override
	public WidgetType getWidgetType() {
		return WidgetType.GROUP;
	}

	@Override
	public void render() {
		super.render();
		//TODO render the text and the border
	}

	@Override
	public TextProperties getTextProperties() {
		return properties;
	}

	@Override
	public Label setTextProperties(TextProperties p) {
		this.properties = p;
		return this;
	}

	@Override
	public Label setText(String text) {
		this.text = text;
		return this;
	}

	@Override
	public String getText() {
		return text;
	}

}
