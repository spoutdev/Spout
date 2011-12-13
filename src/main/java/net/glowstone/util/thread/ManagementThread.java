package net.glowstone.util.thread;

import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is a thread that is responsible for managing various objects.
 */

public abstract class ManagementThread extends PulsableThread {
    
    private WeakHashMap<Managed, Boolean> managedSet = new WeakHashMap<Managed, Boolean>();
    private ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<Runnable>();
    private AtomicBoolean wakePending = new AtomicBoolean(false);
    private AtomicInteger wakeCounter = new AtomicInteger(0);
    
    /**
     * Waits for a list of ManagedThreads to complete a pulse
     * 
     * @param threads the threads to join for
     * @param timeout how long to wait
     * 
     */
    public static void pulseJoinAll(List<ManagementThread> threads, long timeout) throws TimeoutException, InterruptedException {

        long currentTime = System.currentTimeMillis();
        long endTime = currentTime + timeout;
        boolean waitForever = timeout == 0;
        
        if (timeout < 0) {
            throw new IllegalArgumentException("Negative timeouts are not allowed (" + timeout + ")");
        }
        
        boolean done = false;
        while (!done && (endTime > currentTime || waitForever)) {
            done = false;
            while (!done && (endTime > currentTime || waitForever)) {
                done = true;
                for (ManagementThread t : threads) {
                    currentTime = System.currentTimeMillis();
                    if (endTime <= currentTime && !waitForever) {
                        break;
                    }
                    if (!t.isPulseFinished()) {
                        done = false;
                        t.pulseJoin(endTime - currentTime);
                    }
                }
            }
            try {
                for (ManagementThread t : threads) {
                    t.disableWake();
                }
                done = true;
                for (ManagementThread t : threads) {
                    if (!t.isPulseFinished()) {
                        done = false;
                        break;
                    }
                }
            } finally {
                for (ManagementThread t : threads) {
                    t.enableWake();
                }
            }
        }
        
        if (endTime <= currentTime && !waitForever) {
            throw new TimeoutException("pulseJoinAll timed out");
        }
        
    }
    
    /**
     * Sets this thread as manager for a given object
     * 
     * @param managed the object to give responsibility for
     */
    public void addManaged(Managed managed) {
        managedSet.put(managed, Boolean.TRUE);
    }

    /**
     * Adds a task to this thread's queue
     * 
     * @param task the runnable to execute
     */
    public void addToQueue(Runnable task) {
        taskQueue.add(task);
    }
    
    /**
     * Adds a task to this thread's queue and wakes it if necessary
     * 
     * @param task the runnable to execute
     */
    public void addToQueueAndWake(Runnable task) {
        taskQueue.add(task);
        pulse(false);
    }
    
    /**
     * Causes the thread to execute one pulse by calling pulseRun();
     * 
     * @param copySnapshot true if the pulse is for copying the snapshot
     * @return false if the thread was already pulsing
     * 
     */
    public boolean pulse(boolean copySnapshot) {
        if (wakeCounter.get() <= 0) {
            return pulse(copySnapshot);
        } else {
            if (copySnapshot) {
                throw new IllegalArgumentException("Wake was disabled when a Management thread started the copy snapshot phase");
            } else {
                wakePending.set(true);
                return false;
            }
        }
    }
    
    /**
     * Returns if this thread is managing an object
     * 
     * @param managed the object to remove responsibility for
     * @return true if the thread was responsible for the object
     */
    public boolean isManaging(Managed managed) {
        return managedSet.containsKey(managed);
    } 
    
    /**
     * Returns if this thread has completed its pulse and all submitted tasks associated with it
     * 
     * @return true if the pulse was completed
     */
    public boolean isPulseFinished() {
        try {
            disableWake();
            return (!isPulsing()) && taskQueue.isEmpty();
        } finally {
            enableWake();
        }
    } 
    
    /**
     * Prevents this thread from being woken up.
     * 
     * This functionality is implemented using a counter, so every call to disableWake must be matched by a call to enableWake.
     */
    public void disableWake() {
        wakeCounter.incrementAndGet();
    }

    /**
     * Allows this thread to be woken up.
     * 
     * This functionality is implemented using a counter, so every call to enableWake must be matched by a call to disableWake.
     */
    public void enableWake() {
        int localCounter = wakeCounter.decrementAndGet();
        if (localCounter == 0 && wakePending.compareAndSet(true, false)) {
            pulse(false);
        }
    }

    
}
