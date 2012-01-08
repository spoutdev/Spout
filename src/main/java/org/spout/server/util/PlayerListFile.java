/*
 * This file is part of Spout (http://www.spout.org/).
 *
 * Spout is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.server.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.spout.server.SpoutServer;
import org.spout.server.io.StorageOperation;

/**
 * Utility class for storing lists of player names.
 */
public final class PlayerListFile {
	/**
	 * The list as we currently know it.
	 */
	private final ArrayList<String> list = new ArrayList<String>();

	/**
	 * The file the list is associated with.
	 */
	private final File file;

	/**
	 * Initialize the player list from the given file.
	 *
	 * @param path The file to use for this list.
	 */
	public PlayerListFile(String path) {
		this(new File(path));
	}

	/**
	 * Initialize the player list from the given file.
	 *
	 * @param file The file to use for this list.
	 */
	public PlayerListFile(File file) {
		this.file = file;
		load();
	}

	/**
	 * Reloads from the file.
	 */
	public void load() {
		SpoutServer.storeQueue.queue(new StorageOperation() {
			@Override
			public boolean isParallel() {
				return true;
			}

			@Override
			public String getGroup() {
				return file.getName();
			}

			@Override
			public boolean queueMultiple() {
				return true;
			}

			@Override
			public String getOperation() {
				return "playerlistfile-load";
			}

			@Override
			public void run() {
				synchronized (list) {
					list.clear();
					try {
						Scanner input = new Scanner(file);
						while (input.hasNextLine()) {
							String line = input.nextLine().trim().toLowerCase();
							if (line.length() > 0) {
								if (!list.contains(line)) {
									list.add(line);
								}
							}
						}
						Collections.sort(list);
						save();
					} catch (FileNotFoundException ex) {
						save();
					}
				}
			}
		});
	}

	/**
	 * Saves to the file.
	 */
	private void save() {
		SpoutServer.storeQueue.queue(new StorageOperation() {
			@Override
			public boolean isParallel() {
				return true;
			}

			@Override
			public String getGroup() {
				return file.getName();
			}

			@Override
			public boolean queueMultiple() {
				return true;
			}

			@Override
			public String getOperation() {
				return "playerlistfile-save";
			}

			@Override
			public void run() {
				try {
					PrintWriter out = new PrintWriter(new FileWriter(file));
					for (String str : list) {
						out.println(str);
					}
					out.flush();
					out.close();
				} catch (IOException ex) {
					// Pfft.
				}
			}
		});
	}

	/**
	 * Add a player to the list.
	 */
	public void add(String player) {
		if (!contains(player)) {
			list.add(player.trim().toLowerCase());
		}
		Collections.sort(list);
		save();
	}

	/**
	 * Remove a player from the list.
	 */
	public void remove(String player) {
		list.remove(player.trim());
		save();
	}

	/**
	 * Check if a player is in the list.
	 */
	public boolean contains(String player) {
		for (String str : list) {
			if (str.equalsIgnoreCase(player.trim())) {
				return true;
			}
		}
		return false;
	}

	public List<String> getContents() {
		return list;
	}
}
