package org.getspout.spout.chunkstore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
	
	public static byte[] serialize(Object o) {
		
		try {
			
			objectOutput.flush();
			objectOutput.reset();
			byteOutput.reset();
			objectOutput.writeObject(o);
			objectOutput.flush();
			byte[] b = byteOutput.toByteArray();
			objectOutput.reset();
			return b;
		} catch (IOException e) {
			return null;
		}
		
	}
	
	public static Object deserialize(byte[] array) {
		if (array == null) {
			return null;
		}
		
		try {
			byteInput = new ByteArrayInputStream(array);
			objectInput = new ObjectInputStream(byteInput);
			Object o = objectInput.readObject();
			return o;
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
		
	}
	
}
