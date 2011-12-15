package org.getspout.server.util.thread;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * A Thread object that can be pulsed
 * 
 */
public abstract class PulsableThread extends Thread {

    private AtomicBoolean pulsing = new AtomicBoolean(false);
    private volatile boolean copySnapshot = false;

    /**
     * Causes the thread to execute one pulse by calling pulseRun();
     * 
     * @param copySnapshot true if the pulse is for copying the snapshot
     * @return false if the thread was already pulsing
     * 
     */
    public boolean pulse(boolean copySnapshot) {
        boolean success = pulsing.compareAndSet(false, true);
        if (!success) {
            return false;
        }
        this.copySnapshot = copySnapshot;
        synchronized (pulsing) {
            pulsing.notifyAll();
        }
        return true;
    }

    /**
     * Puts the current thread to sleep until the current pulse operation has completed
     */
    public void pulseJoin() throws InterruptedException {
        synchronized(pulsing) {
            while (pulsing.get()) {
                pulsing.wait();
            }
        }
    }

    /**
     * Puts the current thread to sleep until the current pulse operation has completed
     * 
     * @param millis the time in milliseconds to wait before throwing a TimeoutException
     */

    public void pulseJoin(long millis) throws InterruptedException, TimeoutException {
        if (millis == 0) {
            pulseJoin();
            return;
        }
        long currentTime = System.currentTimeMillis();
        long endTime = currentTime + millis;
        synchronized(pulsing) {
            while (currentTime < endTime && pulsing.get()) {
                pulsing.wait(endTime - currentTime);
                currentTime = System.currentTimeMillis();
            }
        }
        if (currentTime >= endTime) {
            throw new TimeoutException();
        }
    }

    /**
     * This method indicates if the thread is currently pulsing
     * 
     * @return true if the thread is pulsing
     */
    public boolean isPulsing() {
        return pulsing.get();
    }

    /**
     * This method is executed once per pulse.
     * 
     * Interrupted exceptions MUST be thrown when interrupts happen.
     * 
     * Where InterruptedExceptions are caught for handling, InterruptedExceptions should be chained.
     * 
     * This is required in order to ensure that the thread can be automatically shut down.
     */
    public abstract void pulsedRun(boolean copySnapShot) throws InterruptedException;
    
    /**
     * The thread will continue until it is interrupted
     */
    public final void run() {

        try {
            while (!isInterrupted()) {
                synchronized (pulsing) {
                    while (!pulsing.get()) {
                        pulsing.wait();
                    }
                }

                try {
                    pulsedRun(copySnapshot);
                } finally {
                    synchronized(pulsing) {
                        pulsing.set(false);
                        pulsing.notifyAll();
                    }
                }
            }
        } catch (InterruptedException ie) {
        }

    }

}
