package org.spout.api.datatable.procedures;

import gnu.trove.procedure.TIntObjectProcedure;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;

import org.spout.api.datatable.GenericDatatableMap;
import org.spout.api.datatable.value.DatatableObject;
import org.spout.api.util.VarInt;

public class GDMCompressProcedure implements TIntObjectProcedure<DatatableObject> {

	private final HashSet<String> stringKeys = new HashSet<String>();
	private final OutputStream stringOutput;
	private final OutputStream objectOutput;

	public int strings = 0;
	public int objects = 0;

	private final GenericDatatableMap map;

	public GDMCompressProcedure(GenericDatatableMap map, OutputStream stringOutput, OutputStream objectOutput) {
		this.map = map;
		this.stringOutput = stringOutput;
		this.objectOutput = objectOutput;
	}

	@Override
	public boolean execute(int a, DatatableObject b) {
		String stringKey = map.getStringKey(a);
		if (stringKey != null) {
			if (stringKeys.add(stringKey)) {
				try {
					VarInt.writeInt(stringOutput, a);
					VarInt.writeString(stringOutput, stringKey);
					strings++;
				} catch (IOException e) {
					return false;
				}
			}
			try {
				b.output(objectOutput);
				objects++;
			} catch (IOException e) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}
};
