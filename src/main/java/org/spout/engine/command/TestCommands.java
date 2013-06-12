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
import java.util.Map.Entry;
import java.util.Set;

import org.spout.api.Client;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.audio.Sound;
import org.spout.api.audio.SoundManager;
import org.spout.api.audio.SoundSource;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.Filter;
import org.spout.api.component.entity.AnimationComponent;
import org.spout.api.component.entity.InteractComponent;
import org.spout.api.component.widget.RenderPartComponent;
import org.spout.api.component.widget.SliderComponent;
import org.spout.api.component.widget.SpinnerComponent;
import org.spout.api.component.widget.TextFieldComponent;
import org.spout.api.component.widget.button.ButtonComponent;
import org.spout.api.component.widget.button.CheckBoxComponent;
import org.spout.api.component.widget.button.RadioComponent;
import org.spout.api.component.widget.list.ComboBoxComponent;
import org.spout.api.component.widget.list.ItemListComponent;
import org.spout.api.command.annotated.Permissible;
import org.spout.api.command.annotated.Platform;
import org.spout.api.command.filter.PlayerFilter;
import org.spout.api.entity.Entity;
import org.spout.api.entity.Player;
import org.spout.api.exception.CommandException;
import org.spout.api.generator.Populator;
import org.spout.api.generator.biome.Biome;
import org.spout.api.generator.biome.Decorator;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.material.BlockMaterial;
import org.spout.api.math.Vector3;
import org.spout.api.model.Model;
import org.spout.api.model.animation.Animation;
import org.spout.api.model.animation.Skeleton;
import org.spout.api.plugin.CommonPluginManager;
import org.spout.api.plugin.Plugin;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.protocol.ServerNetworkSynchronizer;
import org.spout.api.protocol.Session;

import org.spout.engine.SpoutClient;
import org.spout.engine.SpoutEngine;
import org.spout.engine.component.entity.SpoutModelComponent;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.protocol.builtin.message.CommandMessage;
import org.spout.engine.util.thread.AsyncExecutorUtils;
import org.spout.engine.world.SpoutChunk;

public class TestCommands {
	private final SpoutEngine engine;

	public TestCommands(SpoutEngine engine) {
		this.engine = engine;
	}

	private SoundSource getSoundSource(int id) throws CommandException {
		SoundSource source = ((Client) engine).getSoundManager().getSource(id);
		if (source == null) {
			throw new CommandException("Source with id " + id + " does not exist.");
		}
		return source;
	}

	@Command(aliases = "sound", usage = "<load|play|pause|stop|rewind|dispose|pitch|gain|loop> <path|id> [value]",
			desc = "Test command for sound management.", min = 2, max = 3)
	public void sound(CommandSource source, CommandArguments args) throws CommandException {
		if (!(engine instanceof Client)) {
			throw new CommandException("Sounds can only be managed on the client.");
		}

		SoundManager sm = ((Client) engine).getSoundManager();
		String action = args.getString(0);
		if (action.equalsIgnoreCase("load")) {
			String path = args.getString(1);
			Sound sound = engine.getFileSystem().getResource("sound://" + path);
			if (sound == null) {
				throw new CommandException("Could not get sound at path " + path);
			}
			int sourceId = sm.createSource(sound).getId();
			source.sendMessage("Created new source at your position with id: " + sourceId);
		} else if (action.equalsIgnoreCase("play")) {
			SoundSource soundSource = getSoundSource(args.getInteger(1));
			soundSource.play();
			source.sendMessage("Playing...");
		} else if (action.equalsIgnoreCase("pause")) {
			SoundSource soundSource = getSoundSource(args.getInteger(1));
			soundSource.pause();
			source.sendMessage("Paused.");
		} else if (action.equalsIgnoreCase("stop")) {
			SoundSource soundSource = getSoundSource(args.getInteger(1));
			soundSource.stop();
			source.sendMessage("Stopped.");
		} else if (action.equalsIgnoreCase("rewind")) {
			SoundSource soundSource = getSoundSource(args.getInteger(1));
			soundSource.rewind();
			source.sendMessage("Rewinding...");
		} else if (action.equalsIgnoreCase("dispose")) {
			sm.removeSource(getSoundSource(args.getInteger(1)));
			source.sendMessage("Source dispoed.");
		} else if (action.equalsIgnoreCase("pitch")) {
			SoundSource soundSource = getSoundSource(args.getInteger(1));
			float pitch = (float) args.getDouble(2);
			soundSource.setPitch(pitch);
			source.sendMessage("Set pitch to " + pitch);
		} else if (action.equalsIgnoreCase("gain")) {
			SoundSource soundSource = getSoundSource(args.getInteger(1));
			float gain = (float) args.getDouble(2);
			soundSource.setGain(gain);
			source.sendMessage("Set gain to " + gain);
		} else if (action.equalsIgnoreCase("loop")) {
			SoundSource soundSource = getSoundSource(args.getInteger(1));
			boolean loop = Boolean.valueOf(args.getString(2));
			source.sendMessage("Set to loop: " + loop);
		} else {
			throw new CommandException("Unknown action: " + action);
		}
	}

	@Command(aliases = "widget", usage = "<button|checkbox|radio|combo|list|label|slider|spinner|textfield|rect>",
			desc = "Renders a widget on your screen.", min = 1, max = 1)
	@Platform(org.spout.api.Platform.CLIENT)
	public void widget(CommandSource source, CommandArguments args) throws CommandException {
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
			widget.add(RenderPartComponent.class);
		} else {
			throw new CommandException("Component not found.");
		}
		screen.attachWidget(((CommonPluginManager) Spout.getPluginManager()).getMetaPlugin(), widget);
		client.getScreenStack().openScreen(screen);
	}

	@Command(aliases = "break", desc = "Debug command to break a block")
	@Platform(org.spout.api.Platform.CLIENT)
	public void debugBreak(CommandSource source, CommandArguments args) throws CommandException {
		Client client = (Client) engine;
		Player player = client.getPlayer();
		Block block = player.get(InteractComponent.class).getTargetBlock();

		if (block == null || block.getMaterial().equals(BlockMaterial.AIR)) {
			source.sendMessage("No blocks in range.");
		} else {
			source.sendMessage("Block to break: " + block.toString());
			block.setMaterial(BlockMaterial.AIR);
		}
	}

	@Command(aliases = {"dbg"}, desc = "Debug Output")
	public void debugOutput(CommandSource source, CommandArguments args) {
		World world = engine.getDefaultWorld();
		source.sendMessage("World Entity count: " + world.getAll().size());
	}

	@Command(aliases = "dumpthreads", desc = "Dumps a listing of all thread stacks to the console")
	public void dumpThreads(CommandSource source, CommandArguments args) throws CommandException {
		AsyncExecutorUtils.dumpAllStacks();
	}

	@Command(aliases = "testmsg", desc = "Test extracting chat styles from a message and printing them")
	public void testMsg(CommandSource source, CommandArguments args) throws CommandException {
		source.sendMessage(args.getJoinedString(0));
	}

	@Command(aliases = "plugins-tofile", usage = "[filename]", desc = "Creates a file containing all loaded plugins and their version", min = 0, max = 1)
	@Permissible("spout.command.pluginstofile")
	public void getPluginDetails(CommandSource source, CommandArguments args) throws CommandException {

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
		sbuild.append("# This file was created on the ").append(dateFormat.format(date).concat(linesep));
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
						authbuilder.append(s).append(", ");
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
	public void moveEntity(CommandSource source, CommandArguments args) throws CommandException {
		SpoutPlayer player;
		if (!(source instanceof Player)) {
			if (engine.getPlatform() == org.spout.api.Platform.CLIENT) {
				player = ((SpoutClient) engine).getPlayer();
			} else {
				player = (SpoutPlayer) source;
			}
		} else {
			throw new CommandException("Can only run this as a player!");
		}

		int id = args.getInteger(0);
		float x = (float) args.getDouble(1);
		float y = (float) args.getDouble(2);
		float z = (float) args.getDouble(3);

		Entity e = player.getWorld().getEntity(id);

		if (e == null) {
			return;
		}

		e.getScene().setPosition(new Point(e.getWorld(), x, y, z));

		engine.getLogger().info("Entity " + id + " move to " + x + " " + y + " " + z);
	}

	@Command(aliases = {"rotate"}, desc = "Rotate a entity with his Id", min = 4, max = 4)
	public void rotateEntity(CommandSource source, CommandArguments args) throws CommandException {
		SpoutPlayer player;
		if (!(source instanceof Player)) {
			if (engine.getPlatform() == org.spout.api.Platform.CLIENT) {
				player = ((SpoutClient) engine).getPlayer();
			} else {
				player = (SpoutPlayer) source;
			}
		} else {
			throw new CommandException("Can only run this as a player!");
		}

		int id = args.getInteger(0);
		float pitch = (float) args.getDouble(1);
		float yaw = (float) args.getDouble(2);
		float roll = (float) args.getDouble(3);

		Entity e = player.getWorld().getEntity(id);

		if (e == null) {
			return;
		}
		e.getScene().setRotation(e.getScene().getTransform().getRotation().rotate(0, pitch, yaw, roll));

		engine.getLogger().info("Entity " + id + " rotate to " + pitch + " " + yaw + " " + roll);
	}

	@Command(aliases = {"scale"}, desc = "Scale a entity with his Id", min = 4, max = 4)
	public void scaleEntity(CommandSource source, CommandArguments args) throws CommandException {
		SpoutPlayer player;
		if (!(source instanceof Player)) {
			if (engine.getPlatform() == org.spout.api.Platform.CLIENT) {
				player = ((SpoutClient) engine).getPlayer();
			} else {
				player = (SpoutPlayer) source;
			}
		} else {
			throw new CommandException("Can only run this as a player!");
		}

		int id = args.getInteger(0);
		float x = (float) args.getDouble(1);
		float y = (float) args.getDouble(2);
		float z = (float) args.getDouble(3);

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
	public void playAnimation(CommandSource source, CommandArguments args) throws CommandException {
		SpoutPlayer player;
		if (!(source instanceof Player)) {
			if (engine.getPlatform() == org.spout.api.Platform.CLIENT) {
				player = ((SpoutClient) engine).getPlayer();
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

		SpoutModelComponent rendererComponent = e.get(SpoutModelComponent.class);

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

		ac.playAnimation(model, animation, args.length() > 2 && args.getString(2).equalsIgnoreCase("on"));

		source.sendMessage("Entity " + id + " play " + animation.getName());
	}

	@Command(aliases = {"animstop"}, desc = "Stop all animation on a entity", min = 1, max = 1)
	public void stopAnimation(CommandSource source, CommandArguments args) throws CommandException {
		SpoutPlayer player;
		if (!(source instanceof Player)) {
			if (engine.getPlatform() == org.spout.api.Platform.CLIENT) {
				player = ((SpoutClient) engine).getPlayer();
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

	@Command(aliases = {"profpop"}, desc = "Prints the populator profiler results to console", min = 0, max = 0)
	public void profilePopulator(CommandSource source, CommandArguments args) throws CommandException {
		Spout.getLogger().info("");
		Spout.getLogger().info("Populator profiler results");
		long totalPopulator = 0;
		for (Entry<Class<? extends Populator>, Long> e : SpoutChunk.getProfileResults()) {
			totalPopulator += e.getValue();
		}
		for (Entry<Class<? extends Populator>, Long> e : SpoutChunk.getProfileResults()) {
			Spout.getLogger().info(e.getKey().getSimpleName() + " " + e.getValue() + " (" + (0.10 * ((e.getValue() * 1000) / totalPopulator)) + ")");
		}
		Spout.getLogger().info("Total " + totalPopulator);
		Spout.getLogger().info("");
		Spout.getLogger().info("Decorator profiler results");
		long totalDecorator = 0;
		for (Entry<Class<? extends Decorator>, Long> e : Biome.getProfileResults()) {
			totalDecorator += e.getValue();
		}
		for (Entry<Class<? extends Decorator>, Long> e : Biome.getProfileResults()) {
			Spout.getLogger().info(e.getKey().getSimpleName() + " " + e.getValue() + " (" + (0.10 * ((e.getValue() * 1000) / totalDecorator)) + ")");
		}
		Spout.getLogger().info("Total " + totalDecorator);
	}

	@Command(aliases = {"resend"}, usage = "[all|one]", desc = "Resends chunks to players", min = 0, max = 1)
	public void resendChunks(CommandSource source, CommandArguments args) throws CommandException {
		if (args.length() > 0) {
			if (engine.getPlatform() != org.spout.api.Platform.SERVER) {
				throw new CommandException("This command must be used on the server");
			}
			boolean one = false;
			if (args.getString(0).equalsIgnoreCase("one")) {
				one = true;
			}
			int count = 0;
			outer:
			for (Player player : ((Server) engine).getOnlinePlayers()) {
				ServerNetworkSynchronizer network = (ServerNetworkSynchronizer) player.getNetworkSynchronizer();
				Set<Chunk> chunks = network.getActiveChunks();
				for (Chunk c : chunks) {
					count++;
					network.sendChunk(c);
					if (one) {
						break outer;
					}
				}
			}

			source.sendMessage("Resent " + count + " chunks");
		} else {
			throw new CommandException("That's not implemented. Sorry");
			//player.getNetworkSynchronizer().sendChunk(player.getChunk());
			//source.sendMessage("Chunk resent")
		}
	}

	@Command(aliases = {"testnetwork"}, desc = "Checks that the session is open and connected", min = 0, max = 0)
	public void testNetwork(CommandSource source, CommandArguments args) throws CommandException {
		switch (engine.getPlatform()) {
			case CLIENT:
				Session session = ((SpoutClient) engine).getSession();
				if (session.isConnected()) {
					source.sendMessage("Network is open and connected");
				} else {
					source.sendMessage("Network is down. Stopping.");
				engine.stop();
				}
				break;
			case SERVER:
				for (Player player : ((Server) engine).getOnlinePlayers()) {
					player.getSession().send(new CommandMessage(Spout.getCommandManager().getCommand("say"), "Network Works"));
				}
				break;
		}
	}

	@Command(aliases = "respawn", usage = "", desc = "Forces the client to respawn", max = 0)
	@Platform(org.spout.api.Platform.SERVER)
	@Filter(PlayerFilter.class)
	public void respawn(CommandSource source, CommandArguments args) throws CommandException {
		((ServerNetworkSynchronizer) ((Player) source).getNetworkSynchronizer()).setRespawned();
	}

	/**
	 * Replaces chars which are not allowed in filenames on windows with "-".
	 */
	private String replaceInvalidCharsWin(String s) {
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			return s.replaceAll("[\\/:*?\"<>|]", "-");
		} else {
			return s;
		}
	}
}
