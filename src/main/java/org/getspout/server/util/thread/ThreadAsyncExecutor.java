package org.getspout.server.util.thread;

import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.getspout.server.util.thread.coretasks.CopySnapshotTask;
import org.getspout.server.util.thread.coretasks.KillTask;
import org.getspout.server.util.thread.coretasks.StartTickTask;
import org.getspout.server.util.thread.future.ManagedFuture;

/**
 * This is a thread that executes tasks
 */
public final class ThreadAsyncExecutor extends PulsableThread implements AsyncExecutor {
	private ConcurrentLinkedQueue<ManagementTask> taskQueue = new ConcurrentLinkedQueue<ManagementTask>();
	private AtomicBoolean wakePending = new AtomicBoolean(false);
	private AtomicInteger wakeCounter = new AtomicInteger(0);
	private AtomicReference<Object> waitingMonitor = new AtomicReference<Object>();
	private CopySnapshotTask copySnapshotTask = new CopySnapshotTask();
	private StartTickTask startTickTask = new StartTickTask();
	private KillTask killTask = new KillTask();
	private AsyncManager manager = null;
	
	public void setManager(AsyncManager manager) {
		if (this.manager != null) {
			throw new IllegalStateException("The manager for an AsyncExecutor may not be set more than once");
		}
		this.manager = manager;
		this.start();
	}
	
	public final Future<Serializable> addToQueue(ManagementTask task) throws InterruptedException {
		System.out.println("Task added: " + task.getClass().getName());
		if (Thread.currentThread() == this) {
			executeTask(task);
		} else {
			taskQueue.add(task);
			pulse();
		}
		ManagedFuture<Serializable> future = task.getFuture();
		if (future != null) {
			future.setManager(manager);
		}
		return future;
	}
	
	public final void waitForFuture(ManagedFuture<Serializable> future) throws InterruptedException {
		ThreadsafetyManager.checkCurrentThread(this);
		checkFuture(future);
		
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
	 * Checks if the future is associated with this executor
	 * 
	 * @param future the future
	 */
	private void checkFuture(ManagedFuture<Serializable> future) {
		if (future.getManager().getExecutor() != this) {
			throw new IllegalArgumentException("AsyncExecutors may only wait on futures linked with the executor");
		}
	}

	/**
	 * Executes all tasks on the queue
	 * 
	 * @throws InterruptedException
	 */
	private final void executeAllTasks() throws InterruptedException {
		ThreadsafetyManager.checkCurrentThread(this);
		ManagementTask task;
		while ((task = taskQueue.poll()) != null) {
			executeTask(task);
		}
	}
	
	private final void executeTask(ManagementTask task) throws InterruptedException {
		Serializable returnValue = task.call(this);
		ManagedFuture<Serializable> future = task.getFuture();
		if (future != null) {
			future.set(returnValue);
		}
	}

	@Override
	public final boolean copySnapshot() {
		ThreadsafetyManager.checkMainThread();
		taskQueue.add(copySnapshotTask);
		return pulse();
	}

	@Override
	public final boolean startTick(long delta) {
		ThreadsafetyManager.checkMainThread();
		taskQueue.add(startTickTask.setDelta(delta));
		return pulse();
	}
	
	@Override
	public final boolean kill() {
		ThreadsafetyManager.checkMainThread();
		taskQueue.add(killTask);
		return pulse();
	}

	@Override
	public final boolean isPulseFinished() {
		try {
			disableWake();
			return !isPulsing() && taskQueue.isEmpty();
		} finally {
			enableWake();
		}
	}

	@Override
	public final void disableWake() {
		wakeCounter.incrementAndGet();
	}

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
		ThreadsafetyManager.checkCurrentThread(this);
		executeAllTasks();
	}

	@Override
	public AsyncManager getManager() {
		return manager;
	}

	@Override
	public void syncKill() throws InterruptedException {
		executeAllTasks();
		throw new InterruptedException("Executor killed");
	}

}
