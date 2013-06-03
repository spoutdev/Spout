/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spout.engine.gui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.spout.api.gui.FocusReason;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.component.widget.ControlComponent;
import org.spout.api.plugin.Plugin;

public class FocusTest {
	Screen screen;
	Widget widget1, widget2, widget3;
	
	
	@Before
	public void setup() {
		screen = new Screen();
		widget1 = new SpoutWidget();
		widget1.add(ControlComponent.class).setTabIndex(0);
		widget2 = new SpoutWidget();
		widget2.add(ControlComponent.class).setTabIndex(1);
		widget3 = new SpoutWidget();
		widget3.add(ControlComponent.class).setTabIndex(6);
		Plugin plugin = PowerMockito.mock(Plugin.class);
		screen.attachWidget(plugin, widget1);
		screen.attachWidget(plugin, widget2);
		screen.attachWidget(plugin, widget3);
	}
	
	@Test
	public void testTabOrder() {
		screen.setFocus(widget1);
		screen.nextFocus(FocusReason.PROGRAMMED);
		
		Assert.assertEquals(widget2, screen.getFocusedWidget());
		
		screen.previousFocus(FocusReason.PROGRAMMED);
		
		Assert.assertEquals(widget1, screen.getFocusedWidget());
	}
	
	@Test
	public void testSanityChecks() {
		widget1.detach(ControlComponent.class);
		screen.removeWidget(widget2);
		screen.setFocus(widget1);
		Assert.assertNotEquals(widget1, screen.getFocusedWidget());
		screen.setFocus(widget2);
		Assert.assertNotEquals(widget2, screen.getFocusedWidget());
	}
}
