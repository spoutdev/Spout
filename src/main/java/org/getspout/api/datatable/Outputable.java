package org.getspout.api.datatable;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Outputable {
	void output(OutputStream out ) throws IOException;
	void input(InputStream in) throws IOException;	
}
