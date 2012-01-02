package org.getspout.api.datatable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Outputable {
	void writeBytes(DataOutputStream out ) throws IOException;
	Outputable readBytes(DataInputStream in) throws IOException;	
}
