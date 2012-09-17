/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.spout.api.Spout;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;

import org.spout.api.exception.CommandException;
import org.spout.api.geo.World;
import org.spout.api.plugin.Plugin;
import org.spout.engine.SpoutEngine;

public class TestCommands {
	private final SpoutEngine engine;
	public TestCommands(SpoutEngine engine) {
		this.engine = engine;
	}

	@Command(aliases = {"dbg"}, desc = "Debug Output")
	public void debugOutput(CommandContext args, CommandSource source) {
		World world = engine.getDefaultWorld();
		source.sendMessage("World Entity count: ", world.getAll().size());
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
        List<Plugin> plugins = Spout.getEngine().getPluginManager().getPlugins();

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
