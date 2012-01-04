package org.getspout.server.datatable.value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.getspout.api.datatable.DatatableTuple;
import org.getspout.api.math.Quaternion;
import org.getspout.api.math.Vector2;
import org.getspout.api.math.Vector3;
import org.getspout.server.datatable.SpoutDatatableProto.DatatableEntry;
import org.getspout.server.datatable.SpoutDatatableProto.DatatableValue;
import org.getspout.server.datatable.SpoutDatatableProto.QuaternionBuf;
import org.getspout.server.datatable.SpoutDatatableProto.Vector2Buf;
import org.getspout.server.datatable.SpoutDatatableProto.Vector3Buf;

public class SpoutDatatableObject implements DatatableTuple {
	public static final byte Persist = 0x1;
	public static final byte Sync = 0x2;

	protected int keyID;
	protected byte flags;
	Object data;

	public SpoutDatatableObject(int key) {
		keyID = key;
	}

	public SpoutDatatableObject(int key, Object dat) {
		keyID = key;
		this.data = dat;
	}

	@Override
	public void set(int key, Object value) {
		keyID = key;
		if (!(value instanceof Vector3) || !(value instanceof Vector2) || !(value instanceof Quaternion))
			throw new IllegalArgumentException("Unsuported Metadata type");
		data = value;

	}

	@Override
	public int hashCode() {
		return keyID;
	}

	@Override
	public void setFlags(byte flags) {
		this.flags = flags;

	}

	@Override
	public void setPersistant(boolean value) {
		if (value) flags &= SpoutDatatableObject.Persist;
		else flags &= ~SpoutDatatableObject.Persist;
	}

	@Override
	public void setSynced(boolean value) {
		if (value) flags &= SpoutDatatableObject.Sync;
		else flags &= ~SpoutDatatableObject.Sync;
	}

	@Override
	public Object get() {
		return data;
	}

	@Override
	public int asInt() {
		throw new NumberFormatException("Cannot represent Object as int");
	}

	@Override
	public float asFloat() {
		throw new NumberFormatException("Cannot represent Object as float");
	}

	@Override
	public boolean asBool() {
		throw new NumberFormatException("Cannot represent Object as boolean");
	}

	@Override
	public void output(OutputStream out) throws IOException {

		DatatableValue.Builder value = DatatableValue.newBuilder();
		if (data instanceof Vector3) {
			Vector3 v = (Vector3) data;
			value.setVec3Val(Vector3Buf.newBuilder().setX(v.getX()).setY(v.getY()).setZ(v.getZ()));
		}
		if (data instanceof Vector2) {
			Vector2 v = (Vector2) data;
			value.setVec2Val(Vector2Buf.newBuilder().setX(v.getX()).setY(v.getY()));
		}
		if (data instanceof Quaternion) {
			Quaternion v = (Quaternion) data;
			value.setQuatval(QuaternionBuf.newBuilder().setX(v.getX()).setY(v.getY()).setZ(v.getZ()).setW(v.getW()));
		}
		DatatableEntry entry = DatatableEntry.newBuilder().setKeyHash(keyID).setFlags(flags).setValue(value).build();
		entry.writeTo(out);

	}

	@Override
	public void input(InputStream in) throws IOException {
		DatatableEntry entry = DatatableEntry.parseFrom(in);
		keyID = entry.getKeyHash();
		flags = (byte) entry.getFlags();
		if (entry.getValue().hasQuatval()) data = entry.getValue().getQuatval();
		if (entry.getValue().hasVec3Val()) data = entry.getValue().getVec3Val();
		if (entry.getValue().hasVec2Val()) data = entry.getValue().getVec2Val();

	}

	public static DatatableTuple read(InputStream in) throws IOException {
		//TODO THIS IS UGLY
		//Redo this after test is done
		DatatableEntry entry = DatatableEntry.parseFrom(in);
		int keyID = entry.getKeyHash();
		byte flags = (byte) entry.getFlags();
		if (entry.getValue().hasIntval()) {
			SpoutDatatableInt i = new SpoutDatatableInt(keyID);
			i.setFlags(flags);
			i.data = entry.getValue().getIntval();
			return i;
		} else if (entry.getValue().hasFloatval()) {
			SpoutDatatableFloat i = new SpoutDatatableFloat(keyID);
			i.setFlags(flags);
			i.data = entry.getValue().getFloatval();
			return i;
		} else if (entry.getValue().hasBoolval()) {
			SpoutDatatableBool i = new SpoutDatatableBool(keyID);
			i.setFlags(flags);
			i.data = entry.getValue().getBoolval();
			return i;
		} else if (entry.getValue().hasVec3Val()) {
			SpoutDatatableObject i = new SpoutDatatableObject(keyID);
			i.setFlags(flags);
			i.data = entry.getValue().getVec3Val();
			return i;
		} else if (entry.getValue().hasVec2Val()) {
			SpoutDatatableObject i = new SpoutDatatableObject(keyID);
			i.setFlags(flags);
			i.data = entry.getValue().getVec2Val();
			return i;
		} else if (entry.getValue().hasQuatval()) {
			SpoutDatatableObject i = new SpoutDatatableObject(keyID);
			i.setFlags(flags);
			i.data = entry.getValue().getQuatval();
			return i;
		}
		return null;

	}
}
