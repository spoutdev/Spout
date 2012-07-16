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

public interface AttributeStore {

	/**
	 * @param name
	 *            the name of the attribute to look for
	 * @return if an attribute with the given name exists in this store
	 */
	public boolean hasAttribute(String name);

	/**
	 * @param name
	 *            the name of the attribute to look for
	 * @return the attribute for the given name, or null if not found
	 */
	public Attribute getAttribute(String name);

	/**
	 * Sets the attribute value to this store. The name of the attribute is
	 * obtained by value.getName()
	 * 
	 * @param value
	 *            the attribute to set
	 */
	public void setAttribute(Attribute value);

	/**
	 * Sets the attributes as a css-formatted stylesheet which is then parsed.<br/>
	 * If an error occurs, no exception is thrown, instead it keeps on parsing -
	 * if possible - and returns all errors as an array
	 * 
	 * @param css
	 *            the stylesheet to parse
	 * @returns an array of parse errors, if any
	 */
	public Error[] setStylesheet(String css);

	/**
	 * @return the effective stylesheet which contains all attributes<br/>
	 *         Please note, this is NOT neccessarily the stylesheet you set with
	 *         setStylesheet(), but a compilation of all the attributes
	 */
	public String getStylesheet();
}