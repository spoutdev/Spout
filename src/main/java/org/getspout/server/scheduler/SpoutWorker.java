package org.getspout.server.scheduler;

import org.getspout.api.plugin.Plugin;
import org.getspout.api.scheduler.Worker;

public class SpoutWorker implements Worker, Runnable {
	private final int id;
	private final Plugin owner;
	private final SpoutTask task;
	private Thread thread = null;
	private boolean shouldContinue = true;

	protected SpoutWorker(final SpoutTask task, final SpoutScheduler scheduler) {
		id = task.getTaskId();
		owner = task.getOwner();
		this.task = task;
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				task.pulse();
				scheduler.workerComplete(SpoutWorker.this);
			}
		});
		thread.start();
	}

	@Override
	public int getTaskId() {
		return id;
	}

	@Override
	public Plugin getOwner() {
		return owner;
	}

	@Override
	public Thread getThread() {
		return thread;
	}

	public SpoutTask getTask() {
		return task;
	}

	public boolean shouldContinue() {
		return shouldContinue;
	}

	public void cancel() {
		if (thread == null) {
			return;
		}
		if (!thread.isAlive()) {
			thread.interrupt();
			return;
		}
		task.stop();
	}

	@Override
	public void run() {
		shouldContinue = task.pulse();
	}
}

