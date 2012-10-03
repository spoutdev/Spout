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

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.spout.api.chat.ChatArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.data.ValueHolder;
import org.spout.api.geo.World;

import static org.junit.Assert.assertEquals;
public class LanguageTest {
	TestPluginDictionary dict;
	TestCommandSource source;
	private static final String CLASS = "org.spout.api.lang.LanguageTest";

	@Before
	public void setup() {
		dict = new TestPluginDictionary();
		source = new TestCommandSource();
	}

	@Test
	public void testNumber() {
		assertEquals("Wir haben 3 Einheiten", dict.tr("We have got %n item(s)", source, CLASS, new Integer[] {3})); // test plural
		assertEquals("Wir haben 1 Einheit", dict.tr("We have got %n item(s)", source, CLASS, new Integer[] {1})); // test singular
	}

	@Test
	public void testLanguage() throws IOException {
		assertEquals("Teste org.spout.api.lang!", dict.tr("Testing %0!", source, CLASS, new String[] {"org.spout.api.lang"}));
	}

	public static class TestPluginDictionary extends CommonPluginDictionary {
		
		public TestPluginDictionary() {
			load();
		}
		
		@Override
		protected InputStream openLangResource(String filename) {
			return getClass().getResourceAsStream(filename);
		}

		@Override
		protected void loadLanguages() {
			loadLanguage(openLangResource("lang-DE_DE.yml"), "lang-DE_DE.yml");
		}
		
	}
	
	public static class TestCommandSource implements CommandSource {
		
		@Override
		public boolean hasPermission(String node) {
			return false;
		}
		
		@Override
		public boolean hasPermission(World world, String node) {
			return false;
		}
		
		@Override
		public boolean isInGroup(String group) {
			return false;
		}
		
		@Override
		public boolean isInGroup(World world, String group) {
			return false;
		}
		
		@Override
		public String[] getGroups() {
			return null;
		}
		
		@Override
		public String[] getGroups(World world) {
			return null;
		}
		
		@Override
		public ValueHolder getData(String node) {
			return null;
		}
		
		@Override
		public ValueHolder getData(World world, String node) {
			return null;
		}
		
		@Override
		public boolean hasData(String node) {
			return false;
		}
		
		@Override
		public boolean hasData(World world, String node) {
			return false;
		}
		
		@Override
		public String getName() {
			return null;
		}
		
		@Override
		public boolean sendMessage(Object... message) {
			return false;
		}
		
		@Override
		public void sendCommand(String command, ChatArguments arguments) {
			
		}
		
		@Override
		public void processCommand(String command, ChatArguments arguments) {
			
		}
		
		@Override
		public boolean sendMessage(ChatArguments message) {
			return false;
		}
		
		@Override
		public boolean sendRawMessage(Object... message) {
			return false;
		}
		
		@Override
		public boolean sendRawMessage(ChatArguments message) {
			return false;
		}
		
		@Override
		public Locale getPreferredLocale() {
			return Locale.GERMAN_DE;
		}
		
	}
}
