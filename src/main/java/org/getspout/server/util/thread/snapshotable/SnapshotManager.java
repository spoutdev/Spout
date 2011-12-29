package org.getspout.server.util.thread.snapshotable;

import java.util.LinkedHashSet;

public class SnapshotManager {
	
	private LinkedHashSet<Snapshotable> managed = new LinkedHashSet<Snapshotable>();
	
	public synchronized void add(Snapshotable s) {
		synchronized(managed) {
			managed.add(s);
		}
	}
	
	public void copyAllSnapshots() {
		synchronized(managed) {
			for (Snapshotable s : managed) {
				s.copySnapshot();
			}
		}
	}

}
