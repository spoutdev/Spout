package org.spout.api.io.store.map;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

public interface SimpleStoreMap<K, V> {
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
	public Collection<K> getKeys();

	/**
	 * Returns an entry set containing all the key, value pairs within the Store
	 *
	 * @return returns a Set containing all the keys, value entries
	 */
	public Set<Entry<K, V>> getEntrySet();

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
	public V get(K key);

	/**
	 * Gets the value associated with a key and allows a default to be set
	 *
	 * @return returns the value associated with the key, or the default if
	 *         there is no mapping
	 */
	public V get(K key, V def);

	/**
	 * Gets the key associated with a value
	 *
	 * @return returns the key associated with the value
	 */
	public K reverseGet(V value);

	/**
	 * Removes a key, value pair
	 *
	 * @return returns the old value associated with the key, or null if the
	 *         key, value pair doesn't exist
	 */
	public V remove(K key);

	/**
	 * Sets the value associated with a key
	 *
	 * @return returns the old value associated with the key
	 */
	public V set(K key, V value);

	/**
	 * Sets the value associated with a key, but only if neither the key or
	 * value are in use
	 *
	 * @return true if the key/value pair was set
	 */
	public boolean setIfAbsent(K key, V value);
}
