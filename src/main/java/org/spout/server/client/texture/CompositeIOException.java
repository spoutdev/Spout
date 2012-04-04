package org.spout.server.client.texture;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A collection of IOException that failed image data loading
 * 
 * @author kevin
 */
@SuppressWarnings("serial")
public class CompositeIOException extends IOException {
	/** The list of exceptions causing this one */
	private ArrayList<Exception> exceptions = new ArrayList<Exception>();
	
	/**
	 * Create a new composite IO Exception
	 */
	public CompositeIOException() {
		super();
	}
	
	/**
	 * Add an exception that caused this exceptino
	 * 
	 * @param e The exception
	 */
	public void addException(Exception e) {
		exceptions.add(e);
	}
	
	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		String msg = "Composite Exception: \n";
		for (int i=0;i<exceptions.size();i++) {
			msg += "\t"+((IOException) exceptions.get(i)).getMessage()+"\n";
		}
		
		return msg;
	}
}
