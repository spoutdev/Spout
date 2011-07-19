package org.bukkitcontrib.packet.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.Packet;

public class PacketListenerHandler {
    // Hashmap for Integer (Packet ID) and ArrayList of PacketListeners allowed to listen to that packet 
    private static final Map<Integer, ArrayList<PacketListener>> listenerMap = new HashMap<Integer, ArrayList<PacketListener>>();
	
    
    
    //Method to add a listener, with PacketListener pl and the packetId it can listen on
    public static void addListener(int packetId, PacketListener pl) {
        //No other listeners are registered to this packet type, so we make a new arraylist for it.
    	if(!listenerMap.containsKey(packetId)) {
        	listenerMap.put(packetId, new ArrayList<PacketListener>());
        }
        //Now we add the packetlistener to the packetid its registered to.
    	listenerMap.get(packetId).add(pl);
    }
    
    
    
    //Method to remove a listener, with PacketListener pl and the packetId it can listen on, because one packet listener could listen on multiple packets, in theory.
    public static boolean removeListener(int packetId, PacketListener pl) {
		//Checks if the anything is registered on that packetid
    	if (!listenerMap.containsKey(packetId)) {
			return false;
		}
		//Removes packet listener, checks if that packetid registry is empty, then removes it if it is empty so we dont conflict in addlistener
    	if (listenerMap.get(packetId).remove(pl)) {
			if (listenerMap.get(packetId).isEmpty()) {
				listenerMap.remove(packetId);
			}
			return true;
		}
		return false;
    }

    
    public static boolean hasListeners() {
    	return !listenerMap.isEmpty();
    }
    
    //Method to check if a packetlistner pl can listen to packet
    public static boolean checkPacket(Packet packet) {
    	return false;
    }

 }
