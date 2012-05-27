package org.spout.engine.scheduler;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class TaskPriorityQueue extends PriorityQueue<SpoutTask> {

	private static final long serialVersionUID = 1L;
	
	private final Thread taskThread;

	public TaskPriorityQueue() {
		this(Thread.currentThread());
	}
	
	public TaskPriorityQueue(Thread t) {
		super(20, new Comparator<SpoutTask>() {
			@Override
			public int compare(SpoutTask task1, SpoutTask task2) {
				long diff = task1.getNextCallTime() - task2.getNextCallTime();
				return diff < 0 ? -1 :
					   diff > 0 ? +1 :
						           0;
			}
			
		});
		taskThread = t;
	}
	
	/**
	 * Gets the first pending task on the queue.  A task is considered pending if its next call time is less than or equal to the given current time.<br>
	 * <br>
	 * NOTE: This method should only be called from a single thread.  
	 * 
	 * @param currentTime the current time
	 * @return the first pending task, or null if no task is pending
	 */
	public SpoutTask getPendingTask(long currentTime) {
		if (Thread.currentThread() != taskThread) {
			throw new IllegalStateException("getPendingTask() may only be called from the thread that created the TaskPriorityQueue");
		}
		SpoutTask task = peek();
		if (task == null || task.getNextCallTime() > currentTime) {
			return null;
		} else {
			task = poll();
			if (task.getNextCallTime() > currentTime) {
				add(task);
				return null;
			} else {
				return task;
			}
		}
	}
	
	/**
	 * Indicates if there are any pending tasks on the queue.  A task is considered pending if its next call time is less than or equal to the given current time.<br>
	 * 
	 * @param currentTime the current time
	 * @return true if there are any pending tasks
	 */
	public boolean hasPendingTasks(long currentTime) {
		SpoutTask task = peek();
		if (task == null || task.getNextCallTime() > currentTime) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public boolean add(SpoutTask task) {
		if (task != null) {
			task.lockNextCallTime();
		}
		// FIXME: PriorityQueue doesn't support null elements, so TaskPriorityQueue probably shouldn't, either.
		return super.add(task);
	}
	
	@Override
	public SpoutTask poll() {
		SpoutTask task = super.poll();
		if (task != null) {
			task.unlockNextCallTime();
		}
		return task;
	}
	
	@Override
	public boolean remove(Object task) {
		if (super.remove(task)) {
			if (task instanceof SpoutTask) {
				((SpoutTask)task).unlockNextCallTime();
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void clear() {
		throw new UnsupportedOperationException("Not supported");
	}
	
	@Override
	public String toString() {
		Iterator<SpoutTask> i = iterator();
		StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		while (i.hasNext()) {
			SpoutTask t = i.next();
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append("{" + t.getTaskId() + ":" + t.getNextCallTime() + "}");
		}
		return sb.append("}").toString();
	}
}

