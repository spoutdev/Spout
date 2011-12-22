package org.getspout.unchecked.api.io.store;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import org.getspout.unchecked.api.io.FileUtil;

/**
 * This implements a SimpleStore that is stored in memory. The save and load
 * methods can be used to write the map to a File.
 */

public class FlatFileStore<T> extends MemoryStore<T> implements SimpleStore<T> {

	private final File file;
	private boolean dirty = false;
	private final Class<?> clazz; // preserve class, so parser knows what to do

	public FlatFileStore(File file, Class<?> clazz) {
		super();
		this.clazz = clazz;
		this.file = file;
		if (file != null) {
			if (!file.exists()) {
				if (!FileUtil.createFile(file)) {
					return;
				}
			}
		}
	}

	@Override
	public boolean clear() {
		dirty = true;
		return super.clear();
	}

	@Override
	public boolean save() {
		if (dirty) {
			Collection<String> strings = getStrings();
			boolean saved = FileUtil.stringToFile(strings, file);
			if (saved) {
				dirty = false;
			}
			return saved;
		} else {
			return true;
		}
	}

	@Override
	public boolean load() {
		Collection<String> strings = FileUtil.fileToString(file);
		if (strings == null) {
			return false;
		}
		boolean loaded = processStrings(strings);
		if (loaded) {
			dirty = false;
		}
		return loaded;
	}

	@Override
	public T remove(String key) {
		T value = super.remove(key);
		if (value != null) {
			dirty = true;
		}
		return value;
	}

	@Override
	public T set(String key, T value) {
		dirty = true;
		return super.set(key, value);
	}

	private Collection<String> getStrings() {
		Iterator<Entry<String, T>> itr = super.getEntrySet().iterator();
		ArrayList<String> strings = new ArrayList<String>(super.getSize());
		while (itr.hasNext()) {
			Entry<String, T> entry = itr.next();
			String encodedKey = encode(entry.getKey());
			T value = entry.getValue();
			strings.add(value + ":" + encodedKey);
		}
		return strings;
	}

	private boolean processStrings(Collection<String> strings) {
		super.clear();
		for (String string : strings) {
			String[] split = string.trim().split(":");
			if (split.length != 2) {
				return false;
			}
			T value;
			try {
				value = parse(split[0]);
			} catch (NumberFormatException nfe) {
				return false;
			}
			String key = decode(split[1]);
			set(key, value);
		}
		return true;
	}

	private static String encode(String key) {
		String encoded = key;
		encoded = encoded.replace("\\", "\\\\");
		encoded = encoded.replace("\n", "\\n");
		encoded = encoded.replace(":", "\\:");
		return encoded;
	}

	private static String decode(String encoded) {
		String key = encoded;
		key = key.replace("\\:", ":");
		key = key.replace("\\n", "\n");
		key = key.replace("\\\\", "\\");
		return encoded;
	}

	@SuppressWarnings("unchecked")
	private T parse(String string) {
		if (clazz == Integer.class) {
			return (T) (Object) Integer.parseInt(string);
		} else if (clazz == String.class) {
			return (T) string;
		} else {
			throw new IllegalArgumentException("Unable to parse clazzes of type " + clazz.getName());
		}
	}

}
