package org.spout.api.io.bytearrayarray;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BAAOpenInProgress implements ByteArrayArray {

	@Override
	public DataInputStream getInputStream(int i) throws IOException {
		return null;
	}

	@Override
	public DataOutputStream getOutputStream(int i) throws IOException {
		return null;
	}

	@Override
	public boolean attemptClose() throws IOException {
		return false;
	}

	@Override
	public boolean isTimedOut() {
		return false;
	}

	@Override
	public void closeIfTimedOut() throws IOException {		
	}

	@Override
	public boolean isClosed() {
		return false;
	}

}
