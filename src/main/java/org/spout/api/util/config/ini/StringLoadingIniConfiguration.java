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
package org.spout.api.util.config.ini;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * A subclass of IniConfiguration that loads from a String
 *
 * @author zml2008
 */
public class StringLoadingIniConfiguration extends IniConfiguration {
	private String value;
	private StringWriter writer;
	public StringLoadingIniConfiguration(String value) {
		super(null);
		this.value = value;
	}

	@Override
	protected Reader getReader() {
		return new StringReader(value);
	}

	/**
	 * Set the value to load from.  {@link #load()} needs to be called separately for
	 * the value passed in this method to affect the actual configuration data.
	 *
	 * @param value The configuration value
	 */
	public void setValue(String value) {
		this.value = value == null ? "" : value;
	}

	public String getValue() {
		if (writer != null) {
			value = writer.toString();
			writer = null;
		}
		return value;
	}

	@Override
	protected Writer getWriter() {
		return writer = new StringWriter();
	}
}
