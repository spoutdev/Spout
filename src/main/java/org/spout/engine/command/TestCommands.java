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
package org.spout.engine.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.spout.api.Client;
import org.spout.api.Platform;
import org.spout.api.Spout;
import org.spout.api.audio.Sound;
import org.spout.api.audio.SoundManager;
import org.spout.api.audio.SoundSource;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.component.impl.AnimationComponent;
import org.spout.api.component.impl.InteractComponent;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.exception.CommandException;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.gui.component.SliderComponent;
import org.spout.api.gui.component.SpinnerComponent;
import org.spout.api.gui.component.TextFieldComponent;
import org.spout.api.gui.component.TexturedRectComponent;
import org.spout.api.gui.component.button.ButtonComponent;
import org.spout.api.gui.component.button.CheckBoxComponent;
import org.spout.api.gui.component.button.RadioComponent;
import org.spout.api.gui.component.list.ComboBoxComponent;
import org.spout.api.gui.component.list.ItemListComponent;
import org.spout.api.material.BlockMaterial;
import org.spout.api.math.Vector3;
import org.spout.api.model.Model;
import org.spout.api.model.animation.Animation;
import org.spout.api.model.animation.Skeleton;
import org.spout.api.plugin.CommonPluginManager;
import org.spout.api.plugin.Plugin;

import org.spout.engine.SpoutClient;
import org.spout.engine.SpoutEngine;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.entity.component.EntityRendererComponent;
import org.spout.engine.util.thread.AsyncExecutorUtils;

public class TestCommands {
	private final SpoutEngine engine;

	public TestCommands(SpoutEngine engine) {
		this.engine = engine;
	}

	@Command(aliases = "widget", usage = "<button|checkbox|radio|combo|list|label|slider|spinner|textfield|rect>",
			desc = "Renders a widget on your screen.", min = 1, max = 1)
	public void widget(CommandContext args, CommandSource source) throws CommandException {
		if (!(engine instanceof Client)) {
			throw new CommandException("This command is only available on the client.");
		}

		Client client = (Client) engine;
		Screen screen = new Screen();
		Widget widget = client.getScreenStack().createWidget();
		String flag = args.getString(0);
		if (flag.equalsIgnoreCase("button")) {
			widget.add(ButtonComponent.class);
		} else if (flag.equalsIgnoreCase("checkbox")) {
			widget.add(CheckBoxComponent.class);
		} else if (flag.equalsIgnoreCase("radio")) {
			widget.add(RadioComponent.class);
		} else if (flag.equalsIgnoreCase("combo")) {
			widget.add(ComboBoxComponent.class);
		} else if (flag.equalsIgnoreCase("list")) {
			widget.add(ItemListComponent.class);
		} else if (flag.equalsIgnoreCase("slider")) {
			widget.add(SliderComponent.class);
		} else if (flag.equalsIgnoreCase("spinner")) {
			widget.add(SpinnerComponent.class);
		} else if (flag.equalsIgnoreCase("textfield")) {
			widget.add(TextFieldComponent.class);
		} else if (flag.equalsIgnoreCase("rect")) {
			widget.add(TexturedRectComponent.class);
		} else {
			throw new CommandException("Component not found.");
		}
		screen.attachWidget(((CommonPluginManager) Spout.getPluginManager()).getMetaPlugin(), widget);
		client.getScreenStack().openScreen(screen);
	}

	@Command(aliases = "dw", usage = "<play|stop|pause>", desc = "Plays a tune at your position.", min = 1, max = 1)
	public void dw(CommandContext args, CommandSource source) throws CommandException {
		if (!(engine instanceof Client)) {
			return;
		}
		Client client = (Client) engine;
		Point pos = client.getActivePlayer().getScene().getPosition();
		SoundManager sm = client.getSoundManager();
		String action = args.getString(0);
		SoundSource s = sm.getSource("dw");

		if (s == null) {
			if (action.equalsIgnoreCase("play")) {
				s = sm.createSource((Sound) client.getFilesystem().getResource("sound://Spout/fallbacks/dw.wav"), "dw");
			} else {
				throw new CommandException("Nothing to " + action + ".");
			}
		}

		sm.getListener().setPosition(pos);
		s.setPosition(pos);

		if (action.equalsIgnoreCase("play")) {
			s.play();
			source.sendMessage(ChatStyle.BRIGHT_GREEN, "Playing...");
		} else if (action.equalsIgnoreCase("stop")) {
			s.stop();
			source.sendMessage(ChatStyle.BRIGHT_GREEN, "Stopping...");
		} else if (action.equalsIgnoreCase("pause")) {
			s.pause();
			source.sendMessage(ChatStyle.BRIGHT_GREEN, "Pausing...");
		} else {
			throw new CommandException("Unknown action: " + action);
		}
	}

	@Command(aliases = "break", desc = "Debug command to break a block")
	public void debugBreak(CommandContext args, CommandSource source) throws CommandException {
		if (!(engine instanceof Client)) {
			return;
		}
		Client client = (Client) engine;
		Player player = client.getActivePlayer();
		Block block = player.get(InteractComponent.class).getTargetBlock();

		if (block == null || block.getMaterial().equals(BlockMaterial.AIR)) {
			source.sendMessage("No blocks in range.");
		} else {
			source.sendMessage("Block to break: ", block.toString());
			block.setMaterial(BlockMaterial.AIR);
		}
	}

	@Command(aliases = {"dbg"}, desc = "Debug Output")
	public void debugOutput(CommandContext args, CommandSource source) {
		World world = engine.getDefaultWorld();
		source.sendMessage("World Entity count: ", world.getAll().size());
	}

	@Command(aliases = "dumpthreads", desc = "Dumps a listing of all thread stacks to the console")
	public void dumpThreads(CommandContext args, CommandSource source) throws CommandException {
		AsyncExecutorUtils.dumpAllStacks();
	}

	@Command(aliases = "testmsg", desc = "Test extracting chat styles from a message and printing them")
	public void testMsg(CommandContext args, CommandSource source) throws CommandException {
		source.sendMessage(args.getJoinedString(0));
	}

	@Command(aliases = "plugins-tofile", usage = "[filename]", desc = "Creates a file containing all loaded plugins and their version", min = 0, max = 1)
	@CommandPermissions("spout.command.pluginstofile")
	public void getPluginDetails(CommandContext args, CommandSource source) throws CommandException {

		// File and filename
		String filename = "";
		String standpath = "pluginreports";
		File file = null;

		// Getting date
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		String parse = dateFormat.format(date);

		// Create file with passed filename or current date and time as name
		if (args.length() == 1) {
			filename = args.getString(0);
			file = new File(standpath.concat("/" + replaceInvalidCharsWin(filename)));
		} else {
			file = new File(standpath.concat("/" + replaceInvalidCharsWin(parse)).concat(".txt"));
		}

		// Delete the file if existent
		if (file.exists()) {
			file.delete();
		}

		String linesep = System.getProperty("line.separator");

		// Create a new file
		try {
			new File("pluginreports").mkdirs();
			file.createNewFile();
		} catch (IOException e) {
			throw new CommandException("Couldn't create report-file!" + linesep + "Please make sure to only use valid chars in the filename.");
		}

		// Content Builder
		StringBuilder sbuild = new StringBuilder();
		sbuild.append("# This file was created on the " + dateFormat.format(date).concat(linesep));
		sbuild.append("# Plugin Name | Version | Authors".concat(linesep));

		// Plugins to write down
		List<Plugin> plugins = engine.getPluginManager().getPlugins();

		// Getting plugin informations
		for (Plugin plugin : plugins) {

			// Name and Version
			sbuild.append(plugin.getName().concat(" | "));
			sbuild.append(plugin.getDescription().getVersion());

			// Authors
			List<String> authors = plugin.getDescription().getAuthors();
			StringBuilder authbuilder = new StringBuilder();
			if (authors != null && authors.size() > 0) {
				int size = authors.size();
				int count = 0;
				for (String s : authors) {
					count++;
					if (count != size) {
						authbuilder.append(s + ", ");
					} else {
						authbuilder.append(s);
					}
				}
				sbuild.append(" | ".concat(authbuilder.toString()).concat(linesep));
			} else {
				sbuild.append(linesep);
			}
		}

		BufferedWriter writer = null;

		// Write to file
		if (file != null) {
			try {
				writer = new BufferedWriter(new FileWriter(file));
				writer.write(sbuild.toString());
			} catch (IOException e) {
				throw new CommandException("Couldn't write to report-file!");
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		source.sendMessage("Plugins-report successfully created! " + linesep + "Stored in: " + standpath);
	}

	@Command(aliases = {"move"}, desc = "Move a entity with his Id", min = 4, max = 4)
	public void moveEntity(CommandContext args, CommandSource source) throws CommandException {
		SpoutPlayer player;
		if (!(source instanceof Player)) {
			if (engine.getPlatform() == Platform.CLIENT) {
				player = ((SpoutClient) engine).getActivePlayer();
			} else {
				player = (SpoutPlayer) source;
			}
		} else {
			throw new CommandException("Can only run this as a player!");
		}

		int id = args.getInteger(0);
		float x = args.getFloat(1);
		float y = args.getFloat(2);
		float z = args.getFloat(3);

		Entity e = player.getWorld().getEntity(id);

		if (e == null) {
			return;
		}

		e.getScene().setPosition(new Point(e.getWorld(), x, y, z));

		engine.getLogger().info("Entity " + id + " move to " + x + " " + y + " " + z);
	}

	@Command(aliases = {"rotate"}, desc = "Rotate a entity with his Id", min = 4, max = 4)
	public void rotateEntity(CommandContext args, CommandSource source) throws CommandException {
		SpoutPlayer player;
		if (!(source instanceof Player)) {
			if (engine.getPlatform() == Platform.CLIENT) {
				player = ((SpoutClient) engine).getActivePlayer();
			} else {
				player = (SpoutPlayer) source;
			}
		} else {
			throw new CommandException("Can only run this as a player!");
		}

		int id = args.getInteger(0);
		float pitch = args.getFloat(1);
		float yaw = args.getFloat(2);
		float roll = args.getFloat(3);

		Entity e = player.getWorld().getEntity(id);

		if (e == null) {
			return;
		}
		e.getScene().setRotation(e.getScene().getTransform().getRotation().rotate(0, pitch, yaw, roll));

		engine.getLogger().info("Entity " + id + " rotate to " + pitch + " " + yaw + " " + roll);
	}

	@Command(aliases = {"scale"}, desc = "Scale a entity with his Id", min = 4, max = 4)
	public void scaleEntity(CommandContext args, CommandSource source) throws CommandException {
		SpoutPlayer player;
		if (!(source instanceof Player)) {
			if (engine.getPlatform() == Platform.CLIENT) {
				player = ((SpoutClient) engine).getActivePlayer();
			} else {
				player = (SpoutPlayer) source;
			}
		} else {
			throw new CommandException("Can only run this as a player!");
		}

		int id = args.getInteger(0);
		float x = args.getFloat(1);
		float y = args.getFloat(2);
		float z = args.getFloat(3);

		Entity e = player.getWorld().getEntity(id);

		if (e == null) {
			return;
		}

		Transform transform = e.getScene().getTransform();
		transform.scale(new Vector3(x, y, z));
		e.getScene().setTransform(transform);

		engine.getLogger().info("Entity " + id + " scale to " + x + " " + y + " " + z);
	}

	@Command(aliases = {"animstart"}, desc = "Launch a animation his Id", min = 2, max = 3)
	public void playAnimation(CommandContext args, CommandSource source) throws CommandException {
		SpoutPlayer player;
		if (!(source instanceof Player)) {
			if (engine.getPlatform() == Platform.CLIENT) {
				player = ((SpoutClient) engine).getActivePlayer();
			} else {
				player = (SpoutPlayer) source;
			}
		} else {
			throw new CommandException("Can only run this as a player!");
		}

		int id = args.getInteger(0);

		Entity e = player.getWorld().getEntity(id);

		if (e == null) {
			source.sendMessage("Entity not found");
			return;
		}

		EntityRendererComponent rendererComponent = e.get(EntityRendererComponent.class);

		if (rendererComponent.getModels().isEmpty()) {
			source.sendMessage("No model on this entity");
			return;
		}

		Model model = rendererComponent.getModels().get(0);

		Skeleton skeleton = model.getSkeleton();

		if (skeleton == null) {
			source.sendMessage("No skeleton on this entity");
			return;
		}

		Animation animation = model.getAnimations().get(args.getString(1));

		if (animation == null) {
			source.sendMessage("No animation with " + args.getString(1) + ", see the list :");
			for (String a : model.getAnimations().keySet()) {
				source.sendMessage(a);
			}
			return;
		}

		AnimationComponent ac = e.get(AnimationComponent.class);

		ac.playAnimation(model, animation, args.length() > 2 ? args.getString(2).equalsIgnoreCase("on") : false);

		source.sendMessage("Entity " + id + " play " + animation.getName());
	}

	@Command(aliases = {"animstop"}, desc = "Stop all animation on a entity", min = 1, max = 1)
	public void stopAnimation(CommandContext args, CommandSource source) throws CommandException {
		SpoutPlayer player;
		if (!(source instanceof Player)) {
			if (engine.getPlatform() == Platform.CLIENT) {
				player = ((SpoutClient) engine).getActivePlayer();
			} else {
				player = (SpoutPlayer) source;
			}
		} else {
			throw new CommandException("Can only run this as a player!");
		}

		int id = args.getInteger(0);

		Entity e = player.getWorld().getEntity(id);

		if (e == null) {
			source.sendMessage("Entity not found");
			return;
		}

		AnimationComponent ac = e.get(AnimationComponent.class);

		if (ac == null) {
			source.sendMessage("No AnimationComponent on this entity");
			return;
		}

		ac.stopAnimations();

		source.sendMessage("Entity " + id + " animation stopped ");
	}

	/**
	 * Replaces chars which are not allowed in filenames on windows with "-".
	 */
	private String replaceInvalidCharsWin(String s) {
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			return s.replaceAll("[\\/:*?\"<>|]", "-");
		} else {
			return s;
		}
	}
}
