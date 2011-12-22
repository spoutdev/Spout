package org.getspout.server.util.thread;

import java.io.Serializable;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.getspout.api.util.future.SimpleFuture;
import org.getspout.server.util.thread.coretasks.CopySnapshotTask;
import org.getspout.server.util.thread.coretasks.StartTickTask;

/**
 * This is a thread that is responsible for managing various objects.
 */
public abstract class ManagementThread extends PulsableThread implements ManagementAsyncExecutor {
	private WeakHashMap<Managed, Boolean> managedSet = new WeakHashMap<Managed, Boolean>();
	private ConcurrentLinkedQueue<ManagementTask> taskQueue = new ConcurrentLinkedQueue<ManagementTask>();
	private AtomicBoolean wakePending = new AtomicBoolean(false);
	private AtomicInteger wakeCounter = new AtomicInteger(0);
	private AtomicReference<Object> waitingMonitor = new AtomicReference<Object>();
	private CopySnapshotTask copySnapshotTask = new CopySnapshotTask();
	private StartTickTask startTickTask = new StartTickTask();
	protected SimpleFuture<Serializable> cachedFuture = null;

	/**
	 * Sets this thread as manager for a given object
	 *
	 * @param managed the object to give responsibility for
	 */
	public final void addManaged(Managed managed) {
		ThreadsafetyManager.checkManagerThread(this);
		managedSet.put(managed, Boolean.TRUE);
	}

	/**
	 * Adds a task to the thread's queue
	 * 
	 * If this method is called by the thread itself, then the task will be immediately executed.
	 * 
	 * @param task
	 */
	public final Future<Serializable> addToQueue(ManagementTask task) throws InterruptedException {
		if (Thread.currentThread() == this) {
			executeTask(task);
		} else {
			taskQueue.add(task);
			pulse();
		}
		return task.getFuture();
	}
	
	/**
	 * Waits until a future is done.
	 * 
	 * This method will execute any tasks added to the queue while waiting.
	 * 
	 * Other tasks can be processed while event processing steps that require waiting for other threads are waiting. 
	 * 
	 * @param future the future
	 */
	protected final void waitForFuture(SimpleFuture<Serializable> future) throws InterruptedException {
		ThreadsafetyManager.checkManagerThread(this);
		
		Object monitor = future.getMonitor();
		waitingMonitor.set(monitor);
		
		while (!future.isDone()) {
			executeAllTasks();
			synchronized (monitor) {
				if (!taskQueue.isEmpty()) {
					continue;
				}
				future.waitForMonitor();
			}
		}
		
		waitingMonitor.set(null);
	}

	/**
	 * Executes any tasks on the queue.
	 *
	 * Other tasks can be processed while event processing steps that require
	 * waiting for other threads are waiting.
	 */
	protected final void executeAllTasks() throws InterruptedException {
		ThreadsafetyManager.checkManagerThread(this);
		ManagementTask task;
		while ((task = taskQueue.poll()) != null) {
			executeTask(task);
		}
	}
	
	private final void executeTask(ManagementTask task) throws InterruptedException {
		Serializable returnValue = task.call(this);
		SimpleFuture<Serializable> future = task.getFuture();
		if (future != null) {
			future.set(returnValue);
		}
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
	
	@Override
	public boolean pulse() {
		Object monitor = waitingMonitor.get();
		if (monitor != null) {
			synchronized(monitor) {
				monitor.notifyAll();
			}
		}
		return super.pulse();
	}
	
	/**
	 * All tasks on the queue are executed whenever the thread is pulsed
	 */
	@Override
	protected final void pulsedRun() throws InterruptedException {
		ThreadsafetyManager.checkManagerThread(this);
		executeAllTasks();
	}

	public abstract void copySnapshotRun() throws InterruptedException;

	public abstract void startTickRun(long tick) throws InterruptedException;

}
