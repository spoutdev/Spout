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
package org.spout.api.lang;

import java.io.Writer;
import java.util.List;

import org.spout.api.command.CommandSource;
import org.spout.api.plugin.Plugin;

public interface PluginDictionary {

	public void save(Writer writer);

	/**
	 * Returns the translation of source into the receivers preferred language
	 * 
	 * @param source
	 *            the string to translate
	 * @param receiver
	 *            the receiver who will see the message
	 * @param args
	 *            any object given will be inserted into the target string for
	 *            each %0, %1 asf
	 * @param foundClass
	 *            the class that called the translation (used for determining
	 *            which translation is correct)
	 * @return the translation
	 */
	public String tr(String source, CommandSource receiver, String foundClass,
			Object[] args);

	public Plugin getPlugin();

	public LanguageDictionary getDictionary(Locale locale);

	public void setKey(String source, String clazz, int id);

	public int getKey(String source, String clazz);

	public void broadcast(String source, CommandSource[] receivers,
			String clazz, Object[] args);

	public int getNextKey();

	public List<Integer> getIdList();

	public void setDictionary(Locale locale, LanguageDictionary dictionary);

	public String getCodedSource(int id);

}