package net.glowstone.util.thread;

import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is a thread that is responsible for managing various objects.
 */

public abstract class ManagementThread extends PulsableThread {
    
    private WeakHashMap<Managed, Boolean> managedSet = new WeakHashMap<Managed, Boolean>();
    private ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<Runnable>();
    
    /**
     * Sets this thread as manager for a given object
     * 
     * @managed the object to give responsibility for
     */
    public void addManaged(Managed managed) {
        managedSet.put(managed, Boolean.TRUE);
    }

    /**
     * Adds a task to this thread's queue
     * 
     * @task the runnable to execute
     */
    public void addToQueue(Runnable task) {
        taskQueue.add(task);
    }
    
    /**
     * Adds a task to this thread's queue and wakes it if necessary
     * 
     * @task the runnable to execute
     */
    public void addToQueueAndWake(Runnable task) {
        taskQueue.add(task);
        pulse(false);
    }
    
    /**
     * Returns if this thread is managing an object
     * 
     * @managed the object to remove responsibility for
     * @return true if the thread was responsible for the object
     */
    public void isManaging(Managed managed) {
        managedSet.containsKey(managed);
    } 
    
    /**
     * Returns if this thread has completed its pulse and all submitted tasks associated with it
     * 
     * @return true if the thread was responsible for the object
     */
    public boolean isPulseFinished() {
        return managedSet.isEmpty();
    } 

}
