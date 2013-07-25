/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.junit.Test;

import org.spout.api.exception.InvalidDescriptionFileException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PluginDescriptionFileTest {
	private InputStream getFile(String name) {
		InputStream stream = PluginDescriptionFileTest.class.getResourceAsStream(name);
		if (stream == null) {
			stream = PluginDescriptionFileTest.class.getResourceAsStream(name);
		}
		return stream;
	}

	@Test
	public void testValid() throws InvalidDescriptionFileException, IOException {
		InputStream stream = getFile("/valid_properties.yml");
		assertTrue("Failed to find properties", stream != null);
		PluginDescriptionFile desc = new PluginDescriptionFile(stream);
		assertEquals("Name does not match", "TestProperties", desc.getName());
		assertEquals("version does not match", "1.0", desc.getVersion());
		assertEquals("Fullname does not match", "TestProperties v1.0", desc.getFullName());
		assertEquals("Website does not match", "www.example.com", desc.getWebsite());
		assertEquals("Author does not match", "JohnDoe", desc.getAuthors().get(0));
		assertEquals("Main does not match", "com.example", desc.getMain());
		assertEquals("LoadOrder does not match", LoadOrder.STARTUP, desc.getLoad());
		assertEquals("Data does not match", "testData", desc.getData("test_key"));
		stream.close();
	}

	@Test
	public void testFullProperties() throws InvalidDescriptionFileException, IOException {
		InputStream stream = getFile("/full_properties.yml");
		assertTrue("Failed to find properties", stream != null);
		PluginDescriptionFile desc = new PluginDescriptionFile(stream);
		assertEquals("Name does not match", "TestProperties12", desc.getName());
		assertEquals("version does not match", "9999.9999", desc.getVersion());
		assertEquals("Fullname does not match", "TestProperties12 v9999.9999", desc.getFullName());
		assertEquals("Description does not match", "This is a really long description...", desc.getDescription());
		assertEquals("Author does not match", "JaneDoe", desc.getAuthors().get(0));
		assertEquals("Author does not match", "JohnDoe", desc.getAuthors().get(1));
		assertEquals("Author does not match", "Foo", desc.getAuthors().get(2));
		assertEquals("Author does not match", "Bar", desc.getAuthors().get(3));
		assertEquals("Author does not match", "Baz", desc.getAuthors().get(4));
		assertEquals("Website does not match", "www.example.com?param=test", desc.getWebsite());
		assertEquals("Reload does not match", true, desc.allowsReload());
		assertEquals("LoadOrder does not match", LoadOrder.POSTWORLD, desc.getLoad());
		assertEquals("Main does not match", "com.example.main.Main", desc.getMain());
		assertEquals("Depends does not match", "foo", desc.getDepends().get(0));
		assertEquals("Depends does not match", "bar", desc.getDepends().get(1));
		assertEquals("Softdepends does not match", "baz", desc.getSoftDepends().get(0));
		assertEquals("Softdepends does not match", "foo", desc.getSoftDepends().get(1));
		assertEquals("Softdepends does not match", "bar", desc.getSoftDepends().get(2));
		assertEquals("Locale does not match", Locale.GERMANY.getLanguage(), desc.getCodedLocale().getLanguage());
		assertEquals("Data does not match", "testData", desc.getData("test_key"));
		assertEquals("Data does not match", "1234", desc.getData("other_key"));
		assertEquals("Data does not match", "true", desc.getData("false"));
		stream.close();
	}

	@Test
	public void testMisspelling() throws InvalidDescriptionFileException, IOException {
		InputStream stream = getFile("/misspelled_properties.yml");
		assertTrue("Failed to find properties", stream != null);
		PluginDescriptionFile desc = new PluginDescriptionFile(stream);
		assertEquals("Name does not match", "TestProperties12", desc.getName());
		assertEquals("version does not match", "9999.9999", desc.getVersion());
		assertEquals("Fullname does not match", "TestProperties12 v9999.9999", desc.getFullName());
		assertEquals("Description does not match", "This is a really long description...", desc.getDescription());
		assertEquals("Author does not match", "JaneDoe", desc.getAuthors().get(0));
		assertEquals("Author does not match", "JohnDoe", desc.getAuthors().get(1));
		assertEquals("Author does not match", "Foo", desc.getAuthors().get(2));
		assertEquals("Author does not match", "Bar", desc.getAuthors().get(3));
		assertEquals("Author does not match", "Baz", desc.getAuthors().get(4));
		assertEquals("Reload does not match", true, desc.allowsReload());
		assertEquals("Main does not match", "com.example.main.Main", desc.getMain());
		assertEquals("Data does not match", "testData", desc.getData("test_key"));
		assertEquals("Data does not match", "1234", desc.getData("other_key"));
		assertEquals("Data does not match", "true", desc.getData("false"));
		stream.close();
	}

	@Test
	public void testMissingName() throws IOException {
		InputStream stream = getFile("/missing_name_properties.yml");
		assertTrue("Failed to find properties", stream != null);
		try {
			new PluginDescriptionFile(stream);
		} catch (InvalidDescriptionFileException ex) {
			return;
		} finally {
			stream.close();
		}
		fail("Parsed an invalid properties!");
	}

	@Test
	public void testMissingVersion() throws IOException {
		InputStream stream = getFile("/missing_version_properties.yml");
		assertTrue("Failed to find properties", stream != null);
		try {
			new PluginDescriptionFile(stream);
		} catch (InvalidDescriptionFileException ex) {
			return;
		} finally {
			stream.close();
		}
		fail("Parsed an invalid properties!");
	}

	@Test
	public void testMissingAuthor() throws IOException {
		InputStream stream = getFile("/missing_author_properties.yml");
		assertTrue("Failed to find properties", stream != null);
		try {
			new PluginDescriptionFile(stream);
		} catch (InvalidDescriptionFileException ex) {
			return;
		} finally {
			stream.close();
		}
		fail("Parsed an invalid properties!");
	}

	@Test
	public void testMissingMain() throws IOException {
		InputStream stream = getFile("/missing_main_properties.yml");
		assertTrue("Failed to find properties", stream != null);
		try {
			new PluginDescriptionFile(stream);
		} catch (InvalidDescriptionFileException ex) {
			return;
		} finally {
			stream.close();
		}
		fail("Parsed an invalid properties!");
	}

	@Test
	public void testMissingPlatform() throws IOException {
		InputStream stream = getFile("/missing_platform_properties.yml");
		assertTrue("Failed to find properties", stream != null);
		try {
			new PluginDescriptionFile(stream);
		} catch (InvalidDescriptionFileException ex) {
			return;
		} finally {
			stream.close();
		}
		fail("Parsed an invalid properties!");
	}
}
