package com.datastructures.algorithms;

public class ChainedHashTable<T extends Comparable<T>> {
    int buckets;
    int size;
    List<T>[] table;

    public ChainedHashTable(int buckets) {
        this.buckets = buckets;
        this.size = 0;
        this.table = new List[buckets];
        for (int i = 0; i < buckets; i++) {
            table[i] = new List<>();
        }
    }

    public void insert(T data) {
        int index = chainTableLookup(data);
        table[index].insertNext(data);
        size++;
    }

    /**
     * Removes the specified element from the hash table.
     * Returns true if the element was found and removed, false otherwise.
     */
    public boolean remove(T data) {
        int index = chainTableLookup(data);
        List<T> bucket = table[index];
        
        if (bucket.remove(data)) {
            size--;
            return true;
        }
        return false;
    }

    /**
     * Removes an element using a stream-based approach.
     * Demonstrates using streams for searching before removal.
     */
    public boolean removeWithStream(T data) {
        int index = chainTableLookup(data);
        List<T> bucket = table[index];
        
        if (bucket.removeWithStream(data)) {
            size--;
            return true;
        }
        return false;
    }

    public T lookup(T data) {
        int index = chainTableLookup(data);
        List<T> bucket = table[index];
        return bucket.lookup(data);
    }

    protected int chainTableLookup(T data) {
        int hash = data.hashCode();
        return Math.abs(hash) % buckets;
    }
}
