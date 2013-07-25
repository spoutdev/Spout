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
package org.spout.engine.filesystem;

import org.spout.api.Engine;
import org.spout.api.Spout;
import org.spout.api.command.CommandSource;
import org.spout.api.scheduler.TaskPriority;

public class ServerFileSystem extends CommonFileSystem {
	@Override
	public void init() {
		super.init();

		// notify the console about the updates every 5 minutes
		long delay = 1000 * 60 * 5; // five min
		final Engine engine = Spout.getEngine();
		Spout.getScheduler().scheduleSyncRepeatingTask(engine, new Runnable() {
			@Override
			public void run() {
				notifyInstalls();
			}
		}, delay, delay, TaskPriority.NORMAL);
	}

	public void notifyInstalls() {
		int installs = requestedInstallations.size();
		if (installs > 0) {
			CommandSource source = Spout.getEngine().getCommandSource();
			source.sendMessage("There are " + installs + " requested installations available.");
			source.sendMessage("Type '/install list' to view all requested installations.");
			source.sendMessage("Type '/install <allow|deny> all' to allow or disallow all to be installed.");
			source.sendMessage("Type '/install <allow|deny> <plugin>' to allow or disallow individual plugins to be installed.");
		}
	}
}
