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

import java.util.HashMap;
import java.util.Map;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.gui.DebugHud;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.gui.component.LabelComponent;
import org.spout.api.meta.SpoutMetaPlugin;
import org.spout.api.plugin.CommonPluginManager;
import org.spout.api.plugin.Plugin;
import org.spout.api.render.SpoutRenderMaterials;

/**
 * DebugHud for Spout (client). Its purpose, by default, is to render debug developer info when a client player
 * presses F3. The internal debug screen is exposed so plugins can add their own widgets to this.
 * <p/>
 * I'll take a moment to briefly explain some of the more confusing methods found in this class (namely
 * how setSprite and setSource works).
 * <p/>
 * Firstly you must understand how Rectangle works in-regards to drawing onscreen. Here is some things to remember.
 * Ex. setSprite(new Rectangle(x, y, width, height))
 * <p/>
 * X = Where to start drawing X-wise. -1 is western bounds of the screen, 1 is eastern bounds of the screen.
 * Y = Where to start drawing Y-wise. -1 is bottom bounds of the screen, 1 is top bounds of the screen.
 * Width = The width of the sprite. Negative values will drawn leftwards, positive values draw rightwards.
 * Height = The height of the sprite. Negative values will drawn downwards, positive values draw upwards.
 * <p/>
 * Secondly you must understand how setSource works. Its almost the same but we are now looking at grabbing a certain texture
 * from a sprite sheet (texture atlas). Its the same concept with rectangles: choose where to start with x, y and then the width and height
 * of the image to grab.
 * <p/>
 * Ex. You have a sprite sheet with images (think terrain.png) that is 256 x 256. You want to grab a specific texture from it. You would
 * take the texture and use an application like paint to find the exact pixel this image starts at. It comes down to basic coordinate planes.
 * In short, you want the "origin" to start at within the image, it is the top left corner of the pixel where it starts drawing (both x and y).
 * The following demonstrates how you would perceive a sprite sheet to find the appropriate values.
 * <p/>
 * 0
 * ^
 * |
 * |
 * 0 <----------|----------> 256
 * |
 * |
 * v
 * 256
 * <p/>
 * In our example, lets say our image starts at 120x and is at the top of the sprite sheet so 0y. Now its a matter of finding out the width and height
 * of the image. An application like paint allows you to select an image and find out its width and height. We will assume its a 16x16. Here is the
 * final code line to grab the image:
 * <p/>
 * setSource(new Rectangle(120f/256f, 0f, 16f/256f, 16f/256f));
 * <p/>
 * Finally some tricks I've found. I'll add on to this as I further complete this component...
 * - If you want to just draw a rectangle with a solid color on the screen, setSprite(new Color(r, g, b, a)) to whatever you like and
 * setSource(new Rectangle(0f, 0f, 0f, 0f)). This will render your entire sprite with the color you chose!
 */

public class DebugScreen extends Screen implements DebugHud {
		// The Internal Spout-dummy plugin
		private final SpoutMetaPlugin plugin;
		// Spout's debug messages
		private final Map<Integer, Widget> spoutMessages = new HashMap<Integer, Widget>();
		// The hashmap that contains the plugin's debug messages
		private final Map<Plugin, Widget> messages = new HashMap<Plugin, Widget>();

		public DebugScreen() {
			plugin = ((CommonPluginManager) Spout.getPluginManager()).getMetaPlugin();
			init();
		}

		public void open() {
			((Client) Spout.getEngine()).getScreenStack().openScreen(this);
		}

		public void close() {
			((Client) Spout.getEngine()).getScreenStack().closeScreen(this);
		}

		public void reset() {
			removeWidgets();
			init();
			spoutMessages.clear();
			messages.clear();
		}
		
		/**
		 * Spout can display more than one line
		 * @param id, arg
		 */
		public void spoutUpdate(int id, ChatArguments arg) {
			if (spoutMessages.containsKey(id)) {
				LabelComponent lbl = spoutMessages.get(id).get(LabelComponent.class);
				if (!arg.toFormatString().equals(lbl.getText().toFormatString())) {
					lbl.setText(arg);
				}
			} else {
				Widget w = new SpoutWidget();
				w.getTransform().setPosition(-0.95f, 0.9f-id*0.1f);
				LabelComponent lbl = w.add(LabelComponent.class);
				lbl.setFont(SpoutRenderMaterials.DEFAULT_FONT);
				lbl.setText(arg);
				spoutMessages.put(id, w);
				attachWidget(plugin, w);
			}
		}
		
		public void updateParameter(Plugin plug, ChatArguments arg) {
			if (messages.containsKey(plug)) {
				LabelComponent lbl = messages.get(plug).get(LabelComponent.class);
				if (!arg.toFormatString().equals(lbl.getText().toFormatString())) {
					lbl.setText(arg);
				}
			} else {
				Widget w = new SpoutWidget();
				w.getTransform().setPosition(0, 0.9f-messages.size()*0.1f);
				LabelComponent lbl = w.add(LabelComponent.class);
				lbl.setFont(SpoutRenderMaterials.DEFAULT_FONT);
				lbl.setText(arg);
				messages.put(plug, w);
				attachWidget(plug, w);
			}
		}

		/**
		 * Constructs the default Spout debug HUD
		 */
		private void init() {
			setGrabsMouse(true);
			setTakesInput(false);
		}
}
