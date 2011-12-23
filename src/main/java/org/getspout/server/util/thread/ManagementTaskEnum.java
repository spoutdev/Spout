package org.getspout.server.util.thread;

import java.util.HashSet;
import java.util.concurrent.Callable;

import org.getspout.server.util.thread.coretasks.CopySnapshotTask;
import org.getspout.server.util.thread.coretasks.StartTickTask;

public enum ManagementTaskEnum {
	
	COPY_SNAPSHOT(1, new Callable<CopySnapshotTask>() {
		@Override
		public CopySnapshotTask call() {
			return new CopySnapshotTask();
		}
	}),
	START_TICK(2, new Callable<StartTickTask>() {
		@Override
		public StartTickTask call() {
			return new StartTickTask();
		}
	});
	
	private static final int maxId = 2;
	private static final HashSet<Integer> ids = new HashSet<Integer>();
	
	static {
		for (ManagementTaskEnum e : ManagementTaskEnum.values()) {
			reserveId(e.getId());
		}
	}
	
	private int id;
	private Callable<? extends ManagementTask> create;
	
	private ManagementTaskEnum(int id, Callable<? extends ManagementTask> create) {
		this.id = id;
		this.create = create;
	}
	
	public int getId() {
		return id;
	}
	
	public ManagementTask getInstance() {
		try {
			return create.call();
		} catch (Exception e) {
			throw new IllegalStateException("Unable to create class for management task", e);
		}
	}
	
	private static void reserveId(int id) {
		if (id > maxId) {
			throw new IllegalArgumentException("Task id exceeds the maximum id value, please update the ManagementTask class");
		}
		if (!ids.add(id)) {
			throw new IllegalArgumentException("The task id of " + id + " was registered more than once");
		} 
	}
	
	/**
	 * Gets the highest registered id
	 * 
	 * @return the highest id
	 */
	public static int getMaxId() {
		return maxId;
	}

}
