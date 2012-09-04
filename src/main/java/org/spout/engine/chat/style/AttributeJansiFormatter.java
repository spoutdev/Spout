/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.chat.style;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.apache.commons.lang3.Validate;
import org.fusesource.jansi.Ansi;

public class AttributeJansiFormatter extends JansiStyleFormatter {
	private static final TIntObjectHashMap<Ansi.Attribute> ATTRIBUTE_ID_MAP = new TIntObjectHashMap<Ansi.Attribute>();
	static {
		for (Ansi.Attribute attr : Ansi.Attribute.values()) {
			ATTRIBUTE_ID_MAP.put(attr.value(), attr);
		}
	}

	private final Ansi.Attribute enableAttribute;
	private final Ansi.Attribute disableAttribute;

	public AttributeJansiFormatter(Ansi.Attribute enableAttribute) {
		this.enableAttribute = enableAttribute;
		this.disableAttribute = ATTRIBUTE_ID_MAP.get(enableAttribute.value() + 20);
		Validate.notNull(disableAttribute, "No corresponding disable attribute for " + enableAttribute);
	}

	@Override
	public void format(Ansi ansi, String text) {
		ansi.a(enableAttribute).a(text).a(disableAttribute);
	}
}
