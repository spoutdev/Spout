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
package org.spout.engine.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.command.CommandArguments;
import org.spout.api.command.CommandBatch;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.CommandDescription;
import org.spout.api.command.annotated.Filter;
import org.spout.api.command.annotated.Permissible;
import org.spout.api.command.annotated.Platform;
import org.spout.api.command.filter.PlayerFilter;
import org.spout.api.entity.Player;
import org.spout.api.exception.ArgumentParseException;
import org.spout.api.exception.CommandException;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.math.vector.Vector3;
import org.spout.api.meta.SpoutMetaPlugin;
import org.spout.api.plugin.Plugin;
import org.spout.engine.SpoutEngine;
import org.spout.engine.component.entity.MovementValidatorComponent;
import org.spout.math.imaginary.Quaternion;

public class CommonCommands {
	private final SpoutEngine engine;

	public CommonCommands(SpoutEngine engine) {
		this.engine = engine;
	}

	public SpoutEngine getEngine() {
		return engine;
	}

	@CommandDescription (aliases = {"bat", "batch"}, usage = "<file>", desc = "Executes a Spout batch file.")
	@Permissible ("spout.command.batch")
	public void batch(CommandSource source, CommandArguments args) throws CommandException {
		String fileName = args.popString("file");
		args.assertCompletelyParsed();

		if (!(source.hasPermission("spout.command.batch." + fileName))) {
			throw new CommandException("You do not have permission to execute " + fileName);
		}
		CommandBatch bat = engine.getFileSystem().getResource("batch://Spout/batches/" + fileName);
		bat.execute(source);
		source.sendMessage("Executed " + fileName + ".");
	}

	@CommandDescription (aliases = "stop", usage = "[message]", desc = "Stop the server!")
	@Permissible ("spout.command.stop")
	public void stop(CommandSource source, CommandArguments args) throws ArgumentParseException {
		String message = "Engine halting";
		switch (engine.getPlatform()) {
			case CLIENT:
				message = "Client halting";
				break;
			case PROXY:
				message = "Proxy halting";
				break;
			case SERVER:
				message = "Server halting";
				break;
		}
		message = args.popRemainingStrings("message", message);
		engine.stop(message);
	}

	@CommandDescription (aliases = "stackdump", desc = "Writes the stack trace of all active threads to the logs")
	@Permissible ("spout.command.dumpstack")
	public void dumpstack(CommandSource source, CommandArguments args) {
		Map<Thread, StackTraceElement[]> dump = Thread.getAllStackTraces();
		Iterator<Entry<Thread, StackTraceElement[]>> i = dump.entrySet().iterator();
		engine.getLogger().info("[--------------Thread Stack Dump--------------]");
		while (i.hasNext()) {
			Entry<Thread, StackTraceElement[]> e = i.next();
			engine.getLogger().info("Thread: " + e.getKey().getName());
			for (StackTraceElement element : e.getValue()) {
				engine.getLogger().info("    " + element.toString());
			}
			engine.getLogger().info("");
		}
		engine.getLogger().info("[---------------End Stack Dump---------------]");
	}

	@CommandDescription (aliases = "reload", usage = "[plugin]", desc = "Reload engine and/or plugins")
	@Permissible ("spout.command.reload")
	public void reload(CommandSource source, CommandArguments args) throws CommandException {
		if (!args.hasMore()) {
			source.sendMessage("Reloading engine...");

			for (Plugin plugin : getEngine().getPluginManager().getPlugins()) {
				if (plugin.getDescription().allowsReload()) {
					plugin.onReload();
				}
			}

			source.sendMessage("Reloaded.");
		} else {
			String pluginName = args.popString("plugin");
			args.assertCompletelyParsed();
			if (getEngine().getPluginManager().getPlugin(pluginName) == null) {
				throw new CommandException("'" + pluginName + "' is not a valid plugin name.");
			}

			Plugin plugin = getEngine().getPluginManager().getPlugin(pluginName);
			if (!plugin.getDescription().allowsReload()) {
				throw new CommandException("The plugin '" + pluginName + "' does not allow reloads.");
			}
			plugin.onReload();
			source.sendMessage("Reloaded '" + pluginName + "'.");
		}
	}

	@CommandDescription (aliases = {"plugins", "pl"}, desc = "List all plugins on the engine")
	@Permissible ("spout.command.plugins")
	public void plugins(CommandSource source, CommandArguments args) throws ArgumentParseException {
		args.assertCompletelyParsed();
		List<Plugin> plugins = getEngine().getPluginManager().getPlugins();
		StringBuilder pluginListString = new StringBuilder();
		pluginListString.append(Arrays.<Object>asList("Plugins (", plugins.size() - 1, "): "));

		for (int i = 0; i < plugins.size(); i++) {
			Plugin plugin = plugins.get(i);
			if (plugin instanceof SpoutMetaPlugin) {
				continue;
			}

			/*pluginListString.append(plugin.isEnabled() ? ChatStyle.BRIGHT_GREEN : ChatStyle.RED)
					.append(plugin.getName());*/

			/*if (i != plugins.size() - 1) {
				pluginListString.append(ChatStyle.RESET).append(", ");
			}*/
		}
		source.sendMessage(pluginListString.toString());
	}

	@CommandDescription (aliases = {"setspawn", "ss"}, desc = "Sets the spawnpoint for a world")
	@Permissible ("spout.command.setspawn")
	@Platform (org.spout.api.Platform.SERVER)
	public void setspawn(CommandSource source, CommandArguments args) throws CommandException {
		Point point = args.popPoint("spawnpoint", source);
		args.assertCompletelyParsed();

		//Finally set the spawn point
		point.getWorld().setSpawnPoint(new Transform(point, Quaternion.IDENTITY, Vector3.ONE));
		//Notify the source
		source.sendMessage("Set the spawnpoint of world: " + point.getWorld().getName() + " to x: "
				+ point.getBlockX() + ", y: " + point.getBlockY() + ", z: " + point.getBlockZ());
	}

	@CommandDescription (aliases = {"whatisspawn", "wis"}, desc = "Tells you the spawnpoint of a world")
	@Permissible ("spout.command.tellspawn")
	@Platform (org.spout.api.Platform.SERVER)
	public void tellspawn(CommandSource source, CommandArguments args) throws CommandException {
		Point point = args.popWorld("world", source).getSpawnPoint().getPosition();
		args.assertCompletelyParsed();

		source.sendMessage("The spawnpoint of world: " + point.getWorld().getName() + " is x: "
				+ point.getBlockX() + ", y: " + point.getBlockY() + ", z: " + point.getBlockZ());
	}

	@CommandDescription (aliases = {"worldinfo"}, desc = "Provides info about known worlds", usage = "[world]")
	@Permissible ("spout.command.worldinfo")
	public void worldInfo(CommandSource source, CommandArguments args) throws CommandException {
		if (!args.hasMore() && getEngine() instanceof Server) {
			Collection<World> worlds = ((Server) engine).getWorlds();
			StringBuilder output = new StringBuilder("Worlds (" + worlds.size() + "): ");
			for (Iterator<World> i = worlds.iterator(); i.hasNext(); ) {
				output.append(i.next().getName());
				if (i.hasNext()) {
					output.append(", ");
				}
			}
			source.sendMessage(output.toString());
		} else {
			World world = args.popWorld("world");
			args.assertCompletelyParsed();
			source.sendMessage("World: " + world.getName());
			source.sendMessage("==========================");
			source.sendMessage("Age: " + world.getAge());
			source.sendMessage("UUID: " + world.getUID());
			source.sendMessage("Seed: " + world.getSeed());
		}
	}

	@CommandDescription (aliases = {"regioninfo"}, desc = "Provides info about regions in a given world", usage = "[world]")
	@Permissible ("spout.command.regioninfo")
	public void chunkInfo(CommandSource source, CommandArguments args) throws CommandException {
		World world = args.popWorld("world");
		args.assertCompletelyParsed();

		source.sendMessage("World: " + world.getName());
		source.sendMessage("==========================");
		int chunks = 0;
		int regions = 0;
		for (Region r : world.getRegions()) {
			regions++;
			chunks += r.getNumLoadedChunks();
		}
		source.sendMessage("Regions:" + regions);
		source.sendMessage("chunks: " + chunks);
	}

	@CommandDescription (aliases = {"tp", "teleport"}, usage = "[player] <target>", desc = "Teleport to a location")
	@Permissible ("spout.command.tp")
	public void tp(CommandSource source, CommandArguments args) throws CommandException {
		Player player = args.popPlayerOrMe("source", source);
		Point point = args.popPoint("target", source);
		args.assertCompletelyParsed();

		point.getWorld().getChunkFromBlock(point);
		player.teleport(point);

		/*if (target != null) { // TODO: players in popPoint
			player.sendMessage("You teleported to " + target.getName() + ".");
			target.sendMessage(player.getName() + " teleported to you.");
			return;
		}*/
		player.sendMessage("You were teleported to " + point.getWorld().getName() + ", X: " + point.getX()
				+ ", Y: " + point.getY() + ", Z: " + point.getZ() + ".");
	}

	@CommandDescription (aliases = "validate_movement", desc = "Toggle the validating of movement.")
	@Filter (PlayerFilter.class)
	public void validateInput(Player player, CommandArguments args) throws CommandException {
		if (!InputCommands.isPressed(args)) {
			return;
		}

		if (engine.getPlatform() == org.spout.api.Platform.SERVER) {
			player.getData().put(MovementValidatorComponent.VALIDATE_MOVEMENT, !player.getData().get(MovementValidatorComponent.VALIDATE_MOVEMENT));
		}
	}

	@CommandDescription (aliases = {"ver", "version"}, desc = "Display the version of Spout this server is running.")
	@Permissible ("spout.command.version")
	public void version(CommandSource source, CommandArguments args) throws CommandException {
		source.sendMessage("This server is running Spout #" + Spout.getAPIVersion().replace("dev b", "") + ".");
	}
}
