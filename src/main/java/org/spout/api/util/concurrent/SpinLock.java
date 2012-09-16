package org.spout.api.util.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SpinLock implements Lock {
	
	private static int MAX_SPINS = 100;
	
	private AtomicBoolean locked = new AtomicBoolean();
	private AtomicInteger waiting = new AtomicInteger();

	@Override
	public void lock() {
		for (int i = 0; i < MAX_SPINS; i++) {
			if (tryLock()) {
				return;
			}
		}
		
		boolean interrupted = false;
		boolean success = false;
		
		while (!success) {
			try {
				waitLock();
				success = true;
			} catch (InterruptedException ie) {
				interrupted = true;
			}
		}
		if (interrupted) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		for (int i = 0; i < MAX_SPINS; i++) {
			if (tryLock()) {
				return;
			}
		}
		waitLock();
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		long endTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(time, unit) + 1;
		boolean timedOut = false;
		while (!tryLock()) {
			timedOut = System.currentTimeMillis() >= endTime;
			if (timedOut) {
				break;
			}
		}
		return !timedOut;
	}

	@Override
	public Condition newCondition() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean tryLock() {
		return locked.compareAndSet(false, true);
	}
	
	private void waitLock() throws InterruptedException {
		waiting.incrementAndGet();
		try {
			while (!tryLock()) {
				waiting.wait();
			}
		} finally {
			waiting.decrementAndGet();
		}
	}
	
	@Override
	public void unlock() {
		if (!locked.compareAndSet(true, false)) {
			throw new IllegalStateException("Attempt to unlock lock when it isn't locked");
		}
		if (!waiting.compareAndSet(0, 0)) {
			synchronized (waiting) {
				waiting.notifyAll();
			}
		}
	}

}
