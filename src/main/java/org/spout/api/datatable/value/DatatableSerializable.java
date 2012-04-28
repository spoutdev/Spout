package org.spout.api.datatable.value;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DatatableSerializable extends DatatableObject {
	
	public DatatableSerializable(int key) {
		super(key);
	}
	
	public DatatableSerializable(int key, Serializable data) {
		super(key, data);
	}

	@Override
	public byte[] compress() {
		Serializable value = super.get();
		if (value instanceof ByteArrayWrapper) {
			return ((ByteArrayWrapper)value).getArray();
		} else {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

			try {
				ObjectOutputStream objOut = new ObjectOutputStream(byteOut);

				objOut.writeObject(super.get());
				objOut.flush();
				objOut.close();
			} catch (IOException e) {
				return null;
			}

			return byteOut.toByteArray();
		}
	}

	@Override
	public void decompress(byte[] compressed) {
		super.set(new ByteArrayWrapper(compressed));
	}
	
	@Override
	public Serializable get() {
		while (true) {
			Serializable s = super.get();
			if (s instanceof ByteArrayWrapper) {
				try {
					ByteArrayWrapper w = (ByteArrayWrapper)s;
					ObjectInputStream inObj = new ObjectInputStream(new ByteArrayInputStream(w.getArray()));
					Object o;
					try {
						o = inObj.readObject();
					} finally {
						// Overkill
						inObj.close();
					}
					Serializable deserialized = (Serializable)o;
					if (super.compareAndSet(s, deserialized)) {
						return deserialized;
					} else {
						continue;
					}
				} catch (IOException e) {
					return null;
				} catch (ClassNotFoundException e) {
					return null;
				} catch (ClassCastException e) {
					return null;
				}
			} else {
				return super.get();
			}
		}
	}
	
	public boolean isUnknownClass() {
		Serializable s = super.get();
		if (s == null) {
			return false;
		} else {
			return s instanceof ByteArrayWrapper;
		}
	}
	
	private static class ByteArrayWrapper implements Serializable {
		private byte[] array;
		
		public ByteArrayWrapper(byte[] array) {
			this.array = array;
		}
		
		public byte[] getArray() {
			return array;
		}
		
	}

}
