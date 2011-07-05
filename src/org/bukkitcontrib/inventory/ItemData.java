package org.bukkitcontrib.inventory;

public class ItemData {
	public final int id;
	public final byte data;
	protected ItemData(int id) {
		this.id = id;
		this.data = 0;
	}
	
	protected ItemData(int id, int data) {
		this.id = id;
		this.data = (byte)data;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof ItemData) {
			ItemData temp = (ItemData)obj;
			return temp.id == id && temp.data == data;
		}
		return false;
	}
	
	public int hashCode() {
		return 37 * id * 7 * (2 + data);
	}

}
