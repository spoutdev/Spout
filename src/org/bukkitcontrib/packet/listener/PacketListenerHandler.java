package org.bukkitcontrib.packet.listener;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.Packet;

public class PacketListenerHandler {
    // Hashmap for Integer (Packet ID) and ArrayList of PacketListeners allowed to listen to that packet 
    private static final Map<Integer, PacketListener> listenerMap = new HashMap<Integer, PacketListener>();
	
    
    
    //Method to add a listener, with PacketListener pl and the packetId it can listen on
    public static void addListener(int packetId, PacketListener pl) {
        
    }
    
    
    
    //Method to remove a listener, with PacketListener pl and the packetId it can listen on, because one packet listener could listen on multiple packets, in theory.
    public static void removeListener(int packetId, PacketListener pl) {
        
    }

    
    //Method to check if a packetlistner pl can listen to packet
    public static boolean checkPacket(Packet packet) {
    	return false;
    }

 }
