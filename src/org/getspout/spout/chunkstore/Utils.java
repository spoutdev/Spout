/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout.chunkstore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.bukkit.World;
import org.bukkit.World.Environment;

public class Utils {

	public static File getWorldDirectory(World world) {
		
		File dir = new File(world.getName());
		
		if (world.getEnvironment() == Environment.NETHER) {
			dir = new File(dir, "DIM-1");
		}
		
		if (dir.exists()) {
			return dir;
		} else {
			return null;
		}
		
	}
	
	private static ByteArrayOutputStream byteOutput;
	private static ObjectOutputStream objectOutput;
	private static ByteArrayInputStream byteInput;
	private static ObjectInputStream objectInput;
	
	static {

		try {
			byteOutput = new ByteArrayOutputStream();
			objectOutput = new ObjectOutputStream(byteOutput);
		} catch (IOException e) {
			throw new RuntimeException("Unable to create serializer", e);
		}
	}
	
	public static byte[] serialize(Serializable o) {
		
		try {
			byteOutput = new ByteArrayOutputStream();
			objectOutput = new ObjectOutputStream(byteOutput);
			objectOutput.writeObject(o);
			objectOutput.flush();
			byteOutput.flush();
			byte[] b = byteOutput.toByteArray();
			return b;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static Serializable deserialize(byte[] array) {
		if (array == null) {
			return null;
		}
		
		try {
			byteInput = new ByteArrayInputStream(array);
			objectInput = new ObjectInputStream(byteInput);
			Serializable o = (Serializable)objectInput.readObject();
			return o;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
