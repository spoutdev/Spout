package org.getspout.server.entity;

public interface Angerable {
    /**
     * Returns whether this entity is angry
     *
     * @return
     */
    public boolean isAngry();

    /**
     * Sets if this entity is angry.
     *
     * @param angry
     */
    public void setAngry(boolean angry);
}
