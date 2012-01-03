package org.getspout.server.util;

public class StackTrace {
	public static void printStackTrace() {
		try {
			throw new RuntimeException("Stack trace");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
