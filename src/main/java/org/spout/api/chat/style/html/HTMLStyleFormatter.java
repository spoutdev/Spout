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
package org.spout.api.chat.style.html;

import java.util.HashMap;
import java.util.Map;

import org.spout.api.chat.style.StyleFormatter;

/**
 * A StyleFormatter that formats with HTML tags
 */
public class HTMLStyleFormatter implements StyleFormatter {
	private final String tag;
	private String openTag, closeTag;
	private final Map<String, String> attributes = new HashMap<String, String>();

	public HTMLStyleFormatter(String tag, String... attributePairs) {
		this.tag = tag;
		if ((attributePairs.length & 1) != 0) {
			throw new IllegalArgumentException("Attributes must be given as key-value pairs!");
		}
		for (int i = 0; i < attributePairs.length; i += 2) {
			attributes.put(attributePairs[i], attributePairs[i + 1]);
		}
		buildTag();
	}

	public void addAttribute(String key, String value) {
		attributes.put(key, value);
		buildTag();
	}

	public String removeAttribute(String key) {
		final String ret = attributes.remove(key);
		buildTag();
		return ret;
	}

	public String getAttribute(String key) {
		return attributes.get(key);
	}

	public boolean hasAttribute(String attr) {
		return attributes.containsKey(attr);
	}

	protected void buildTag() {
		StringBuilder openBuilder = new StringBuilder("<");
		openBuilder.append(tag);
		if (attributes.size() > 0) {
			for (Map.Entry<String, String> entry : attributes.entrySet()) {
				openBuilder.append(" ").append(entry.getKey());
				if (entry.getValue() != null) {
					openBuilder.append("=\"").append(entry.getValue()).append("\"");
				}
			}
		}
		openBuilder.append(">");
		openTag = openBuilder.toString();
		closeTag = "</" + tag + ">";
	}

	@Override
	public String format(String text) {
		return openTag + text.replace("&", "&amp;").replace("<", "&lt;") + closeTag;
	}
}
