package com.datastructures.algorithms;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A thread-safe hash table implementation using chaining for collision resolution.
 * 
 * This implementation uses fine-grained locking with one lock per bucket to maximize
 * concurrency. Multiple threads can safely access different buckets simultaneously,
 * while operations on the same bucket are serialized.
 * 
 * Concurrency Issues Addressed:
 * 
 * 1. Race Condition on Size Counter:
 *    WITHOUT synchronization: Two threads incrementing size simultaneously could cause lost updates.
 *    Example: size=10, Thread1 reads 10, Thread2 reads 10, both write 11, final size=11 (should be 12)
 *    SOLUTION: Use synchronized blocks or AtomicInteger for size updates.
 * 
 * 2. Race Condition on Bucket Lists:
 *    WITHOUT synchronization: Two threads modifying the same linked list could corrupt the structure.
 *    Example: Thread1 inserting while Thread2 removing could result in lost nodes or infinite loops.
 *    SOLUTION: Lock the specific bucket during modifications.
 * 
 * 3. Read-Write Contention:
 *    WITHOUT read-write locks: Reads would block other reads unnecessarily.
 *    SOLUTION: Use ReadWriteLock - multiple readers can proceed, but writers have exclusive access.
 * 
 * 4. Cross-Bucket Operations:
 *    SAFE SCENARIO: Operations on different buckets can proceed in parallel without interference.
 *    This is why per-bucket locking provides better performance than synchronized methods.
 * 
 * Locking Strategy:
 * - Per-bucket ReadWriteLock array for fine-grained locking
 * - Read locks for lookup operations (allows concurrent reads)
 * - Write locks for insert/remove operations (exclusive access)
 * - Synchronized size counter updates
 * 
 * Performance Characteristics:
 * - High concurrency for operations on different buckets
 * - Read operations don't block each other
 * - Write operations block reads and other writes on the same bucket only
 * - Trade-off: More memory for locks, more complex code
 * 
 * @param <T> the type of elements maintained by this hash table
 */
public class ThreadedChainedHashTable<T extends Comparable<T>> extends ChainedHashTable<T> {
    
    /**
     * Array of locks, one per bucket for fine-grained locking.
     * This allows operations on different buckets to proceed in parallel.
     */
    private final ReadWriteLock[] locks;
    
    /**
     * Creates a thread-safe chained hash table with the specified number of buckets.
     * 
     * @param buckets the number of buckets in the hash table
     */
    public ThreadedChainedHashTable(int buckets) {
        super(buckets);
        this.locks = new ReadWriteLock[buckets];
        for (int i = 0; i < buckets; i++) {
            locks[i] = new ReentrantReadWriteLock();
        }
    }
    
    /**
     * Thread-safe insert operation.
     * 
     * CONCURRENCY ISSUES WITHOUT SYNCHRONIZATION:
     * 1. Lost updates to size counter if two threads insert simultaneously
     * 2. Corrupted linked list if two threads insert into same bucket
     * 3. Inconsistent state if one thread reads while another inserts
     * 
     * This implementation:
     * - Acquires write lock for the specific bucket (exclusive access)
     * - Safely inserts into the bucket's linked list
     * - Synchronizes size increment to prevent lost updates
     */
    @Override
    public void insert(T data) {
        int index = chainTableLookup(data);
        locks[index].writeLock().lock();
        try {
            table[index].insertNext(data);
            // Synchronize size update to prevent race conditions
            synchronized (this) {
                size++;
            }
        } finally {
            locks[index].writeLock().unlock();
        }
    }
    
    /**
     * Thread-safe remove operation.
     * 
     * CONCURRENCY ISSUES WITHOUT SYNCHRONIZATION:
     * 1. Race condition on size decrement - could lose updates
     * 2. Corrupted list structure if concurrent remove operations
     * 3. Remove might not find element if concurrent insert/remove on same bucket
     * 
     * This implementation:
     * - Acquires write lock for the bucket (exclusive access)
     * - Safely removes from the linked list
     * - Synchronizes size decrement
     */
    @Override
    public boolean remove(T data) {
        int index = chainTableLookup(data);
        locks[index].writeLock().lock();
        try {
            List<T> bucket = table[index];
            if (bucket.remove(data)) {
                synchronized (this) {
                    size--;
                }
                return true;
            }
            return false;
        } finally {
            locks[index].writeLock().unlock();
        }
    }
    
    /**
     * Thread-safe lookup operation using read lock.
     * 
     * CONCURRENCY ISSUES WITHOUT SYNCHRONIZATION:
     * 1. Might read partial/inconsistent state during concurrent insert
     * 2. Could encounter corrupted list structure during concurrent remove
     * 
     * This implementation:
     * - Acquires read lock (allows concurrent reads, blocks during writes)
     * - Multiple threads can lookup in the same bucket simultaneously
     * - Lookup blocks if a write is in progress on this bucket
     */
    @Override
    public T lookup(T data) {
        int index = chainTableLookup(data);
        locks[index].readLock().lock();
        try {
            List<T> bucket = table[index];
            return bucket.lookup(data);
        } finally {
            locks[index].readLock().unlock();
        }
    }
    
    /**
     * Thread-safe method to get current size.
     * 
     * CONCURRENCY ISSUE WITHOUT SYNCHRONIZATION:
     * - Reading size while other threads modify it could return stale/inconsistent value
     * 
     * This implementation:
     * - Synchronizes read to ensure visibility of latest value
     */
    public synchronized int getSize() {
        return size;
    }
    
    /**
     * Demonstrates potential deadlock scenario (NOT IMPLEMENTED).
     * 
     * DEADLOCK SCENARIO:
     * If we needed to acquire locks on multiple buckets simultaneously:
     * - Thread1: locks bucket[5], waits for bucket[10]
     * - Thread2: locks bucket[10], waits for bucket[5]
     * - DEADLOCK: Both threads wait forever
     * 
     * SOLUTION: Always acquire locks in consistent order (e.g., ascending bucket index)
     * Example for transfer operation:
     *   int first = Math.min(bucket1, bucket2);
     *   int second = Math.max(bucket1, bucket2);
     *   locks[first].writeLock().lock();
     *   locks[second].writeLock().lock();
     *   // perform operation
     *   locks[second].writeLock().unlock();
     *   locks[first].writeLock().unlock();
     */
    
    /**
     * Demonstrates thread starvation scenario (NOT IMPLEMENTED).
     * 
     * STARVATION SCENARIO:
     * Without fairness in locks, a thread could be indefinitely postponed:
     * - Writer threads constantly acquiring write locks
     * - A reader thread waiting for read lock might never get a chance
     * 
     * SOLUTION: Use ReentrantReadWriteLock(true) for fair lock acquisition
     * Trade-off: Fairness reduces throughput but ensures no thread waits forever
     */
}
