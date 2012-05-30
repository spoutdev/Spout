package org.spout.engine.scheduler.parallel;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class MarkedWeakReference<T, M> extends WeakReference<T> {
	
	private final M mark;
	
	public MarkedWeakReference(T r, M mark, ReferenceQueue<T> q) {
		super(r, q);
		this.mark = mark;
	}
	
	public MarkedWeakReference(T r, M mark) {
		super(r);
		this.mark = mark;
	}
	
	public M getMark() {
		return mark;
	}
	
}
