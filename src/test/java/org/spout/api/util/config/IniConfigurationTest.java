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
package org.spout.api.util.config;

import org.junit.Test;
import org.spout.api.exception.ConfigurationException;
import org.spout.api.util.config.commented.CommentedConfigurationNodeBase;
import org.spout.api.util.config.ini.StringLoadingIniConfiguration;

import static org.spout.api.util.config.commented.CommentedConfigurationNodeBase.LINE_SEPARATOR;
import static org.junit.Assert.*;

/**
 * @author zml2008
 */
public class IniConfigurationTest {
	@Test
	public void testBasicLoading() throws ConfigurationException {
		StringLoadingIniConfiguration subject = new StringLoadingIniConfiguration(
				"[section]" + LINE_SEPARATOR +
				"node = value" + LINE_SEPARATOR);
		subject.load();
		ConfigurationNode sectionNode = subject.getNode("section");
		assertNotNull(sectionNode);
		assertTrue(sectionNode.isAttached());
		assertTrue(sectionNode.hasChildren());
		assertEquals("value", subject.getNode("section.node").getString());
	}

	@Test
	public void testBasicSaving() throws ConfigurationException {
		StringLoadingIniConfiguration subject = new StringLoadingIniConfiguration(null);
		subject.getNode("section.node").setValue("value");
		subject.save();
		assertEquals("[section]" + LINE_SEPARATOR +
		"node=value" + LINE_SEPARATOR, subject.getValue());
	}

	@Test
	public void testCommentLoading() throws ConfigurationException {
		StringLoadingIniConfiguration subject = new StringLoadingIniConfiguration(
				"# This is the first section!" + LINE_SEPARATOR +
				"[section]" + LINE_SEPARATOR +
				"# This is a node!" + LINE_SEPARATOR +
				"# With a multiline comment!" + LINE_SEPARATOR +
				"node=value" + LINE_SEPARATOR);
		subject.load();
		ConfigurationNode node = subject.getNode("section");
		assertArrayEquals(new String[] {"This is the first section!"}, ((CommentedConfigurationNodeBase) node).getComment());
		node = subject.getNode("section.node");
		assertArrayEquals(new String[] {"This is a node!", "With a multiline comment!"}, ((CommentedConfigurationNodeBase) node).getComment());
	}

	@Test
	public void testCommentSaving() throws ConfigurationException {
		StringLoadingIniConfiguration subject = new StringLoadingIniConfiguration(null);
		subject.getNode("section").setComment("Hello", "World");
		subject.getNode("section", "node").setValue("value");
		subject.save();
		assertEquals("# Hello" + LINE_SEPARATOR +
		"# World" + LINE_SEPARATOR +
		"[section]" + LINE_SEPARATOR +
		"node=value" + LINE_SEPARATOR, subject.getValue());
	}
}
