package org.spout.engine.util;

import java.util.LinkedList;

import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Transform;
import org.spout.api.math.Quaternion;
import org.spout.api.math.Vector3;
import org.spout.api.util.sanitation.SafeCast;

import org.spout.nbt.CompoundMap;
import org.spout.nbt.FloatTag;
import org.spout.nbt.Tag;
import org.spout.nbt.exception.InvalidTagException;

public class NBTMapper {
	/**
	 * This method takes a linked list and constructs a transform from it. Ordering is VERY IMPORTANT
	 * @param floatsList the list of floats representing a transform.
	 * @return the constructed transform from NBT tags
	 */
	public static Transform nbtToTransform(World world, LinkedList<FloatTag> floatsList) {
		//Position
		float px = SafeCast.toFloat(floatsList.get(0), 1f);
		float py = SafeCast.toFloat(floatsList.get(1), 85f);
		float pz = SafeCast.toFloat(floatsList.get(2), 1f);
		//Rotation
		float rw = SafeCast.toFloat(floatsList.get(3), 1f);
		float rx = SafeCast.toFloat(floatsList.get(4), 1f);
		float ry = SafeCast.toFloat(floatsList.get(5), 1f);
		float rz = SafeCast.toFloat(floatsList.get(6), 1f);
		//Scale
		float sx = SafeCast.toFloat(floatsList.get(7), 1f);
		float sy = SafeCast.toFloat(floatsList.get(8), 1f);
		float sz = SafeCast.toFloat(floatsList.get(9), 1f);
		return new Transform(new Point(world, px, py, pz), new Quaternion(rw, rx, ry, rz), new Vector3(sx, sy, sz));
	}

	/**
	 * This method takes a transform and converts it into a series of float tags representing a transform.
	 * @param transform The transform to convert to tags
	 * @return compound map composing of tags
	 */
	public static LinkedList<FloatTag> transformToNBT(Transform transform) {
		LinkedList<FloatTag> floatsList = new LinkedList<FloatTag>();
		Vector3 point = transform.getPosition();
		Quaternion rotation = transform.getRotation();
		Vector3 scale = transform.getScale();
		//Position
		floatsList.add(new FloatTag("px", point.getX()));
		floatsList.add(new FloatTag("py", point.getY()));
		floatsList.add(new FloatTag("pz", point.getZ()));
		//Rotation
		floatsList.add(new FloatTag("rw", rotation.getW()));
		floatsList.add(new FloatTag("rx", rotation.getX()));
		floatsList.add(new FloatTag("ry", rotation.getY()));
		floatsList.add(new FloatTag("rz", rotation.getZ()));
		//Scale
		floatsList.add(new FloatTag("sx", scale.getX()));
		floatsList.add(new FloatTag("sy", scale.getY()));
		floatsList.add(new FloatTag("sz", scale.getZ()));
		//Return list of floats
		return floatsList;
	}

	/**
	 * Takes in an NBT tag, sanely checks null status, and then returns its value.
	 * @param t Tag to get value from
	 * @return tag value as an object or null if no value
	 */
	public static Object toTagValue(Tag t) {
		if (t == null) {
			return null;
		} else {
			return t.getValue();
		}
	}
}
