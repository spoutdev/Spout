package org.getspout.commons.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class FlatFileStore<T> {
	
	private final File file;
	private final Map<String, T> map;
	private final Map<T, String> reverseMap;
	private boolean dirty = false;
	private final Class<?> clazz; // preserve class, so parser knows what to do
	
	public FlatFileStore(File file, Class<?> clazz) {
		this.clazz = clazz;
		map = new HashMap<String, T>();
		reverseMap = new HashMap<T, String>();
		this.file = file;
		if (file != null) {
			if (!file.exists()) {
				if (!FileUtil.createFile(file)) {
					return;
				}
			}
		}
	}
	
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
	
	public Collection<String> getKeys() {
		return map.keySet();
	}
	
	public T get(String key) {
		return map.get(key);
	}
	
	public String reverseGet(T value) {
		return reverseMap.get(value);
	}
	
	public T get(String key, T def) {
		T value = get(key);
		if (value == null) {
			return def;
		} else {
			return value;
		}
	}
	
	public T remove(String key) {
		T value = map.remove(key);
		if (value != null) {
			reverseMap.remove(value);
			dirty = true;
		}
		return value;
	}
	
	public T set(String key, T value) {
		dirty = true;
		T oldValue = map.put(key, value);
		if (oldValue != null) {
			reverseMap.remove(oldValue);
		}
		reverseMap.put(value, key);
		return oldValue;
	}
	
	private Collection<String> getStrings() {
		Iterator<Entry<String, T>> itr = map.entrySet().iterator();
		ArrayList<String> strings = new ArrayList<String>(map.size());
		while (itr.hasNext()) {
			Entry<String, T> entry = itr.next();
			String encodedKey = encode(entry.getKey());
			T value = entry.getValue();
			strings.add(value + ":" + encodedKey);
		}
		return strings;
	}
	
	private boolean processStrings(Collection<String> strings) {
		map.clear();
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
			return (T)(Object)Integer.parseInt(string);
		} else if (clazz == String.class) {
			return (T)(Object)string;
		} else {
			throw new IllegalArgumentException("Unable to parse clazzes of type " + clazz.getName());
		}
	}

}
