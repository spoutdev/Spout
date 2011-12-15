package org.getspout.server.msg;


import java.util.Arrays;

import org.getspout.server.inventory.SpoutItemStack;

public final class SetWindowSlotsMessage extends Message {

    private final int id;
    private final SpoutItemStack[] items;

    public SetWindowSlotsMessage(int id, SpoutItemStack[] items) {
        this.id = id;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public SpoutItemStack[] getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "SetWindowSlotsMessage{id=" + id + ",slots=" + Arrays.toString(items) + "}";
    }
}
