package org.spout.api.datatable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gets a unique sequence number of Datatable updates.
 * 
 * If a record has the same sequence number before and a read, then the read can be considered to have completed correctly.
 */
public class DatatableSequenceNumber {

	private static AtomicInteger sequenceNumber = new AtomicInteger(0);
	
	/**
	 * Sequence number that indicates the record is unstable
	 */
	public static final int UNSTABLE = 1;
	
	/**
	 * Sequence number that indicates that the read was atomic, and so always is valid
	 */
	public static final int ATOMIC = 3;
	
	/**
	 * Gets a unique sequence number.
	 * 
	 * @return the number
	 */
	public static int get() {
		return sequenceNumber.getAndAdd(2);
	}
	
}
