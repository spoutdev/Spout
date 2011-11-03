/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetLoginHandler;
import net.minecraft.server.NetworkListenThread;

public class SpoutNetworkAcceptThread extends Thread{

	final MinecraftServer a;
	final NetworkListenThread b;
	Method methodA, methodA2, methodB, methodC;

	@SuppressWarnings("rawtypes")
	SpoutNetworkAcceptThread(NetworkListenThread networklistenthread, String s, MinecraftServer minecraftserver) {
		super(s);
		this.b = networklistenthread;
		this.a = minecraftserver;
		try {
			Class[] params = {NetworkListenThread.class};
			methodA = NetworkListenThread.class.getDeclaredMethod("a", params);
			methodA.setAccessible(true);
			methodB = NetworkListenThread.class.getDeclaredMethod("b", params);
			methodB.setAccessible(true);
			methodC = NetworkListenThread.class.getDeclaredMethod("c", params);
			methodC.setAccessible(true);
			
			Class[] params2 = {NetworkListenThread.class, NetLoginHandler.class};
			methodA2 = NetworkListenThread.class.getDeclaredMethod("a", params2);
			methodA2.setAccessible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void run() {
		while (!this.isInterrupted()) {
			try {
				Socket socket = a(this.b).accept();

				if (socket != null) {
					synchronized (b(this.b)) {
						InetAddress inetaddress = socket.getInetAddress();

						if (b(this.b).containsKey(inetaddress) && System.currentTimeMillis() - ((Long) b(this.b).get(inetaddress)).longValue() < 5000L) {
							b(this.b).put(inetaddress, Long.valueOf(System.currentTimeMillis()));
							socket.close();
							continue;
						}

						b(this.b).put(inetaddress, Long.valueOf(System.currentTimeMillis()));
					}

					int connectionId = c(this.b);
					NetLoginHandler netloginhandler = new SpoutNetLoginHandler(this.a, socket, "Connection #" + connectionId);

					a(this.b, netloginhandler);
				}
				else {
					try {
						sleep(50);
					} catch (InterruptedException e) {
						return;
					}
				}
			} catch (IOException ioexception) {
				ioexception.printStackTrace();
			}
			
		}
	}
	
	@SuppressWarnings("rawtypes")
	public HashMap b(NetworkListenThread thread) {
		try {
			Object[] args = {thread};
			return (HashMap) methodB.invoke(null, args);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ServerSocket a(NetworkListenThread thread) {
		try {
			Object[] args = {thread};
			return (ServerSocket) methodA.invoke(null, args);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int c(NetworkListenThread thread) {
		try {
			Object[] args = {thread};
			return (Integer) methodC.invoke(null, args);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void a(NetworkListenThread thread, NetLoginHandler handler) {
		try {
			Object[] args = {thread, handler};
			methodA2.invoke(null, args);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
