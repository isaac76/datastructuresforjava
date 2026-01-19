package com.datastructures.algorithms;

import java.util.Optional;

/**
 * A hash map implementation that stores key-value pairs using chaining for collision resolution.
 * 
 * This implementation uses separate chaining where each bucket contains a linked list of entries.
 * Keys are hashed to determine which bucket to use, and values are retrieved by their associated keys.
 * 
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class ChainedHashMap<K, V> {
    private int buckets;
    private int size;
    private List<Entry<K, V>>[] table;

    /**
     * Entry class to hold key-value pairs.
     */
    public static class Entry<K, V> implements Comparable<Entry<K, V>> {
        private final K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public int compareTo(Entry<K, V> other) {
            // Compare based on key hashCode
            return Integer.compare(this.key.hashCode(), other.key.hashCode());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Entry)) return false;
            Entry<?, ?> other = (Entry<?, ?>) obj;
            return key.equals(other.key);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    @SuppressWarnings("unchecked")
    public ChainedHashMap(int buckets) {
        this.buckets = buckets;
        this.size = 0;
        this.table = new List[buckets];
        for (int i = 0; i < buckets; i++) {
            table[i] = new List<>();
        }
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old value is replaced.
     * 
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no mapping
     */
    public V put(K key, V value) {
        int index = hash(key);
        List<Entry<K, V>> bucket = table[index];
        
        // Check if key already exists and update
        Optional<Entry<K, V>> existing = bucket.stream()
            .filter(entry -> entry.getKey().equals(key))
            .findFirst();
        
        if (existing.isPresent()) {
            V oldValue = existing.get().getValue();
            existing.get().setValue(value);
            return oldValue;
        }
        
        // Insert new entry
        bucket.insertNext(new Entry<>(key, value));
        size++;
        return null;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this map
     * contains no mapping for the key.
     * 
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null
     */
    public V get(K key) {
        int index = hash(key);
        List<Entry<K, V>> bucket = table[index];
        
        Optional<Entry<K, V>> result = bucket.stream()
            .filter(entry -> entry.getKey().equals(key))
            .findFirst();
        
        return result.map(Entry::getValue).orElse(null);
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     * 
     * @param key the key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     */
    public boolean containsKey(K key) {
        int index = hash(key);
        List<Entry<K, V>> bucket = table[index];
        
        return bucket.stream()
            .anyMatch(entry -> entry.getKey().equals(key));
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * 
     * @param key the key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no mapping
     */
    public V remove(K key) {
        int index = hash(key);
        List<Entry<K, V>> bucket = table[index];
        
        // Find the entry first to get the value
        Optional<Entry<K, V>> result = bucket.stream()
            .filter(entry -> entry.getKey().equals(key))
            .findFirst();
        
        if (result.isPresent()) {
            V value = result.get().getValue();
            bucket.remove(result.get());
            size--;
            return value;
        }
        
        return null;
    }

    /**
     * Returns the number of key-value mappings in this map.
     * 
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return size;
    }

    /**
     * Returns true if this map contains no key-value mappings.
     * 
     * @return true if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Computes the hash bucket index for a given key.
     */
    private int hash(K key) {
        return Math.abs(key.hashCode()) % buckets;
    }
}
