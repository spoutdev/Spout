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

import java.awt.Color;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.gui.component.LabelComponent;
import org.spout.api.gui.component.RenderPartsHolderComponent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.gui.render.RenderPartPack;
import org.spout.api.math.Rectangle;
import org.spout.api.meta.SpoutMetaPlugin;
import org.spout.api.plugin.CommonPluginManager;
import org.spout.api.render.Font;
import org.spout.api.render.SpoutRenderMaterials;

public class DevConsole extends Screen {
	// The Internal Spout-dummy plugin
	private final SpoutMetaPlugin plugin;

	private final Widget background = new SpoutWidget();
	private final Widget textfield = new SpoutWidget();
	private final Font font;
	
	private DateFormat dateFormat;
	private List<Widget> lines = new ArrayList<Widget>();
	private float scroll = 0;

	public DevConsole(Font font) {
		this.plugin = ((CommonPluginManager) Spout.getPluginManager()).getMetaPlugin();
		this.font = font;
		init();
	}
	
	public void clearConsole() {
		this.removeWidgets();
		this.lines.clear();
		init();
		scroll = 0;
	}

	public void init() {
		setGrabsMouse(true);
		setTakesInput(false);
		final RenderPartsHolderComponent bg = background.add(RenderPartsHolderComponent.class);
		final RenderPartPack bg_pack = new RenderPartPack(SpoutRenderMaterials.GUI_COLOR);
		
		// The display messages background
		final RenderPart text_bg = new RenderPart();
		text_bg.setColor(new Color(0f, 0f, 0f, 0.5f)); //Black with opacity of 50%
		text_bg.setSprite(new Rectangle(-0.975f, -0.825f, 0.9f, 0.6f));
		text_bg.setSource(new Rectangle(0f, 0f, 0f, 0f));
		bg_pack.add(text_bg, 0);

		// The textfield background
		final RenderPart textfield_bg = new RenderPart();
		textfield_bg.setColor(new Color(0f, 0f, 0f, 0.5f)); //Black with opacity of 50%
		textfield_bg.setSprite(new Rectangle(-0.975f, -0.95f, 0.9f, 0.075f));
		textfield_bg.setSource(new Rectangle(0f, 0f, 0f, 0f));
		bg_pack.add(textfield_bg, 1);

		bg.add(bg_pack);
		
		//Finally attach widget so we can draw
		attachWidget(plugin, background);
		
		// Create the textfield
		textfield.getTransform().setPosition(-0.965f, -0.945f);
		LabelComponent lbl = textfield.add(LabelComponent.class);
		lbl.setFont(font);
		lbl.setColor(Color.WHITE);
		lbl.setText(new ChatArguments("_"));
		attachWidget(plugin, textfield);
	}

	public boolean isInitialized() {
		return true;
	}

	public void setDateFormat(DateFormat format) {
		this.dateFormat = format;
	}

	public void addMessage(ChatArguments message) {
		Widget wid = new SpoutWidget();
		wid.getTransform().setPosition(-0.965f, -0.3f - scroll);
		LabelComponent txt = wid.add(LabelComponent.class);

		txt.setColor(Color.WHITE);
		txt.setFont(font);
		ChatArguments outputText = new ChatArguments();
		if (dateFormat != null) {
			outputText.append("[").append(dateFormat.format(new Date())).append("] ");
		}
		outputText.append(message.getExpandedPlaceholders());
		txt.setText(outputText);

		scroll += 0.06f;
		lines.add(wid);
		attachWidget(plugin, wid);
	}
}
