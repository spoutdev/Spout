package org.bukkitcontrib.packet.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.Packet;

public class PacketListenerHandler {
    // Hashmap for Integer (Packet ID) and ArrayList of PacketListeners allowed
    // to listen to that packet
    private static final Map<Integer, ArrayList<PacketListener>> listenerMap = new HashMap<Integer, ArrayList<PacketListener>>();

    /**
     * Add a packet listener for a specific packet id
     * 
     * @param packetId
     *            packet number i.e 18 being Packet18
     * @param pl
     *            the listener object
     */
    public static void addListener(int packetId, PacketListener pl) {
        if (!listenerMap.containsKey(packetId)) {
            listenerMap.put(packetId, new ArrayList<PacketListener>());
        }
        listenerMap.get(packetId).add(pl);
    }

    /**
     * Remove a packet listener from the list of listeners
     * 
     * @param packetId
     *            packet number i.e 18 being Packet18
     * @param pl
     *            the listener object
     * @return true if the listener was found and removed, false if it was not
     *         found
     */
    public static boolean removeListener(int packetId, PacketListener pl) {
        if (!listenerMap.containsKey(packetId)) {
            return false;
        }
        if (listenerMap.get(packetId).remove(pl)) {
            if (listenerMap.get(packetId).isEmpty()) {
                listenerMap.remove(packetId);
            }
            return true;
        }
        return false;
    }

    /**
     * @return true if there are any packet listeners, false if there are none
     */
    public static boolean hasListeners() {
        return !listenerMap.isEmpty();
    }

    /**
     * @param packetId
     *            packet number i.e 18 being Packet18
     * @return true if there are listeners for the given packet type, false if
     *         there are none
     */
    public static boolean hasListeners(int packetId) {
        return listenerMap.containsKey(packetId);
    }
    
    /**
     * Get a list of packet listeners for a type.
     * 
     * @param packetId
     *            packet number i.e 18 being Packet18
     * @return a list of listeners for the specified type
     */

    public static List<PacketListener> getListeners(int packetId) {
        if (hasListeners(packetId))
            return listenerMap.get(packetId);
        return Collections.emptyList();
    }

    /**
     * Check a packet to see if it can be sent.
     * 
     *  @param packet
     *            the packet to check
     * @return true if the packet is allowed, false if any listener said no
     */
    public static boolean checkPacket(Packet packet) {
        for (PacketListener listener : getListeners(packet.b())) {
            if (!listener.outGoingPacket(packet, packet.b()))
                return false;
        }
        return true;
    }

}
