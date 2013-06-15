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
