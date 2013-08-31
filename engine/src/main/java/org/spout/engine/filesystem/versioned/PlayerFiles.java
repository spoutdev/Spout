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
package org.spout.engine.filesystem.versioned;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;

import org.spout.api.Spout;
import org.spout.api.entity.PlayerSnapshot;
import org.spout.api.event.storage.PlayerLoadEvent;
import org.spout.api.event.storage.PlayerSaveEvent;
import org.spout.engine.entity.SpoutPlayer;
import org.spout.engine.entity.SpoutPlayerSnapshot;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

public class PlayerFiles {
	public static void savePlayerData(SpoutPlayer player, boolean async) {
		Runnable saveTask = new SaveTask(player.snapshot());
		if (async) {
			Spout.getEngine().getScheduler().scheduleAsyncTask(Spout.getEngine(), saveTask);
		} else {
			saveTask.run();
		}
	}

	private static class SaveTask implements Runnable {
		private final PlayerSnapshot snapshot;

		public SaveTask(PlayerSnapshot snapshot) {
			this.snapshot = snapshot;
		}

		@Override
		public void run() {
			PlayerSaveEvent event = new PlayerSaveEvent(snapshot);
			Spout.getEngine().getEventManager().callEvent(event);
			if (!event.isSaved()) {
				File playerDir = new File(Spout.getEngine().getDataFolder().toString(), "players");
				//Save data to temp file first
				String fileName = snapshot.getName() + ".dat";
				String tempName = fileName + ".temp";
				File playerData = new File(playerDir, tempName);
				if (!playerData.exists()) {
					try {
						playerData.createNewFile();
					} catch (Exception e) {
						Spout.getLogger().log(Level.SEVERE, "Error creating player data for " + snapshot.getName(), e);
					}
				}
				CompoundTag playerTag = EntityFiles.saveEntity(snapshot);
				NBTOutputStream os = null;
				try {
					os = new NBTOutputStream(new DataOutputStream(new FileOutputStream(playerData)), false);
					os.writeTag(playerTag);
					os.flush();
				} catch (IOException e) {
					Spout.getLogger().log(Level.SEVERE, "Error saving player data for " + snapshot.getName(), e);
					playerData.delete();
					return;
				} finally {
					if (os != null) {
						try {
							os.close();
						} catch (IOException ignore) {
						}
					}
				}
				try {
					//Move the temp data to final location
					File finalData = new File(playerDir, fileName);
					if (finalData.exists()) {
						finalData.delete();
					}
					FileUtils.moveFile(playerData, finalData);
				} catch (IOException e) {
					Spout.getLogger().log(Level.SEVERE, "Error saving player data for " + snapshot.getName(), e);
					playerData.delete();
				}
			}
		}
	}

	/**
	 * Loads player data for the player, if it exists <p> Returns null on failure or if the data could not be loaded. If an exception is thrown or the player data is not in a valid format it will be
	 * backed up and new player data will be created for the player
	 *
	 * @return player, or null if it could not be loaded
	 */
	public static SpoutPlayerSnapshot loadPlayerData(String name) {
		PlayerLoadEvent event = new PlayerLoadEvent(name);
		Spout.getEngine().getEventManager().callEvent(event);
		if (event.getSnapshot() != null) {
			return new SpoutPlayerSnapshot(event.getSnapshot());
		}

		File playerDir = new File(Spout.getEngine().getDataFolder().toString(), "players");
		String fileName = name + ".dat";
		File playerData = new File(playerDir, fileName);
		if (playerData.exists()) {
			NBTInputStream is = null;
			try {
				is = new NBTInputStream(new DataInputStream(new FileInputStream(playerData)), false);
				CompoundTag dataTag = (CompoundTag) is.readTag();
				return EntityFiles.loadPlayerEntity(dataTag, name);
			} catch (Exception e) {
				Spout.getLogger().log(Level.SEVERE, "Error loading player data for " + name, e);

				//Back up the corrupt data, so new data can be saved
				//Back up the file with a unique name, based off the current system time
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = formatter.format(new Date(System.currentTimeMillis()));
				File backup = new File(playerDir, fileName + "_" + time + ".bak");
				if (!playerData.renameTo(backup)) {
					Spout.getLogger().log(Level.SEVERE, "Failed to back up corrupt player data " + name);
				} else {
					Spout.getLogger().log(Level.WARNING, "Successfully backed up corrupt player data for " + name);
				}
			} finally {
				try {
					is.close();
				} catch (IOException ignore) {
				}
			}
		}
		return null;
	}
}
