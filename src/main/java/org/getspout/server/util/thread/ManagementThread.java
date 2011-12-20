package org.getspout.server.util.thread;

import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.getspout.server.util.thread.coretasks.CopySnapshotTask;
import org.getspout.server.util.thread.coretasks.StartTickTask;

/**
 * This is a thread that is responsible for managing various objects.
 */
public abstract class ManagementThread extends PulsableThread implements AsyncExecutor {
	private WeakHashMap<Managed, Boolean> managedSet = new WeakHashMap<Managed, Boolean>();
	private ConcurrentLinkedQueue<ManagementTask> taskQueue = new ConcurrentLinkedQueue<ManagementTask>();
	private AtomicBoolean wakePending = new AtomicBoolean(false);
	private AtomicInteger wakeCounter = new AtomicInteger(0);
	private CopySnapshotTask copySnapshotTask = new CopySnapshotTask();
	private StartTickTask startTickTask = new StartTickTask();

	/**
	 * Sets this thread as manager for a given object
	 *
	 * @param managed the object to give responsibility for
	 */
	@Override
	public final void addManaged(Managed managed) {
		ThreadsafetyManager.checkManagerThread(this);
		managedSet.put(managed, Boolean.TRUE);
	}

	/**
	 * Adds a task to this thread's queue
	 *
	 * @param task the runnable to execute
	 */
	@Override
	public final void addToQueue(ManagementTask task) {
		taskQueue.add(task);
	}

	/**
	 * Adds a task to this thread's queue and wakes it if necessary
	 *
	 * @param task the runnable to execute
	 */
	@Override
	public final void addToQueueAndWake(ManagementTask task) {
		taskQueue.add(task);
		pulse();
	}

	/**
	 * Executes any tasks on the queue.
	 *
	 * Other tasks can be processed while event processing steps that require
	 * waiting for other threads are waiting.
	 */
	public final void executeOtherTasks() throws InterruptedException {
		ThreadsafetyManager.checkManagerThread(this);
		ManagementTask task;
		while ((task = taskQueue.poll()) != null) {
			task.run(this);
		}
		// TODO - need a way to wait for return values from other threads, but still allow this one to be woken, when new tasks arrive.
	}

	/**
	 * Instructs the thread to copy all updated data to its snapshot
	 *
	 * @return false if the thread was already pulsing
	 */
	@Override
	public final boolean copySnapshot() {
		ThreadsafetyManager.checkMainThread();
		taskQueue.add(copySnapshotTask);
		return pulse();
	}

	/**
	 * Instructs the thread to start a new tick
	 *
	 * @return false if the thread was already pulsing
	 */
	@Override
	public final boolean startTick(long ticks) {
		ThreadsafetyManager.checkMainThread();
		taskQueue.add(startTickTask.setTicks(ticks));
		return pulse();
	}

	/**
	 * Returns if this thread has completed its pulse and all submitted tasks
	 * associated with it
	 *
	 * @return true if the pulse was completed
	 */
	@Override
	public final boolean isPulseFinished() {
		try {
			disableWake();
			return !isPulsing() && taskQueue.isEmpty();
		} finally {
			enableWake();
		}
	}

	/**
	 * Prevents this thread from being woken up.
	 *
	 * This functionality is implemented using a counter, so every call to
	 * disableWake must be matched by a call to enableWake.
	 */
	@Override
	public final void disableWake() {
		wakeCounter.incrementAndGet();
	}

	/**
	 * Allows this thread to be woken up.
	 *
	 * This functionality is implemented using a counter, so every call to
	 * enableWake must be matched by a call to disableWake.
	 */
	@Override
	public final void enableWake() {
		int localCounter = wakeCounter.decrementAndGet();
		if (localCounter == 0 && wakePending.compareAndSet(true, false)) {
			pulse();
		}
	}

	/**
	 * All tasks on the queue are executed whenever the thread is pulsed
	 */
	@Override
	protected final void pulsedRun() throws InterruptedException {
		ThreadsafetyManager.checkManagerThread(this);
		ManagementTask task;
		while ((task = taskQueue.poll()) != null) {
			task.run(this);
		}
	}

	/**
	 * This method is called once at the end of every tick
	 *
	 * This method should be overridden.
	 */
	@Override
	public abstract void copySnapshotRun() throws InterruptedException;

	/**
	 * This method is called once at the start of every tick
	 *
	 * This method should be overridden.
	 */
	public abstract void startTickRun() throws InterruptedException;

}
