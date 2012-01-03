package org.getspout.api.io.store;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Basic Store interface.
 *
 * It is used to store maps that map Strings to classes of type T
 *
 * Subclasses can implement backing to the file system.
 * 
 * Implementing classes must be synchronized
 *
 * This is a two-way map, so each value only maps back to one key
 */

public interface SimpleStore<T> {

	/**
	 * Save the map to the persistence system associated with the store.
	 *
	 * If the store is a memory based volatile map, then this method will have
	 * no effect and will always return true.
	 *
	 * @return returns true if the save was successful
	 */
	public boolean save();

	/**
	 * Loads the map from the persistence system associated with the store.
	 *
	 * If the store is a memory based volatile map, then this method will have
	 * no effect and will always return true.
	 *
	 * @return returns true if the load was successful
	 */
	public boolean load();

	/**
	 * Returns a collection of all keys for all key, value pairs within the
	 * Store
	 *
	 * @return returns a Collection containing all the keys
	 */
	public Collection<String> getKeys();

	/**
	 * Returns an entry set containing all the key, value pairs within the Store
	 *
	 * @return returns a Set containing all the keys, value entries
	 */
	public Set<Entry<String, T>> getEntrySet();

	/**
	 * Returns the number of key, value pairs within the Store
	 *
	 * @return the size of the store
	 */
	public int getSize();

	/**
	 * Wipes all the key value pairs for the store
	 */
	public boolean clear();

	/**
	 * Gets the value associated with a key
	 *
	 * @return returns the value associated with the key
	 */
	public T get(String key);

	/**
	 * Gets the value associated with a key and allows a default to be set
	 *
	 * @return returns the value associated with the key, or the default if
	 *         there is no mapping
	 */
	public T get(String key, T def);

	/**
	 * Gets the key associated with a value
	 *
	 * @return returns the key associated with the value
	 */
	public String reverseGet(T value);

	/**
	 * Removes a key, value pair
	 *
	 * @return returns the old value associated with the key, or null if the
	 *         key, value pair doesn't exist
	 */
	public T remove(String key);

	/**
	 * Sets the value associated with a key
	 *
	 * @return returns the old value associated with the key
	 */
	public T set(String key, T value);
	
	/**
	 * Sets the value associated with a key, but only if neither the key or value are in use
	 *
	 * @return true if the key/value pair was set
	 */
	public boolean setIfAbsent(String key, T value);

}
