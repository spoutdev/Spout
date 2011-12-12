package net.glowstone.util.thread;

public abstract class Managed {
    
    private final ManagementThread manager;
    
    public Managed(ManagementThread manager) {
        manager.addManaged(this);
        this.manager = manager;
    }

    /**
     * Checks if the current thread is managing this object
     * 
     * @returns true if the object is managed by the current thread
     */    
    public boolean isModifiable () {
        return Thread.currentThread() == manager;
    }
    
    /**
     * Returns the thread that is managing this object
     * 
     * @returns the management thread
     */
    public ManagementThread getManagementThread () {
        return manager;
    }
    
    
}
