package org.spout.api.lang;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.spout.api.chat.ChatArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.data.ValueHolder;
import org.spout.api.geo.World;

import static org.junit.Assert.assertEquals;
public class LanguageTest {
	TestPluginDictionary dict = new TestPluginDictionary();
	TestCommandSource source = new TestCommandSource();
	private static final String CLASS = "org.spout.api.lang.LanguageTest";
	
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
			return LanguageTest.class.getResourceAsStream(filename);
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
