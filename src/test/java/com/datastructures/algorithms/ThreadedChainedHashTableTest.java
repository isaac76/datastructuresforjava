package com.datastructures.algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadedChainedHashTableTest {
    
    private ThreadedChainedHashTable<Integer> threadSafeTable;
    private ChainedHashTable<Integer> unsafeTable;
    
    @BeforeEach
    public void setUp() {
        threadSafeTable = new ThreadedChainedHashTable<>(100);
        unsafeTable = new ChainedHashTable<>(100);
    }
    
    @Test
    public void testBasicOperations() {
        threadSafeTable.insert(42);
        threadSafeTable.insert(17);
        
        assertEquals(42, threadSafeTable.lookup(42));
        assertEquals(17, threadSafeTable.lookup(17));
        
        assertTrue(threadSafeTable.remove(42));
        assertNull(threadSafeTable.lookup(42));
    }
    
    @Test
    public void testConcurrentInserts() throws InterruptedException {
        int numThreads = 10;
        int insertsPerThread = 100;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    for (int j = 0; j < insertsPerThread; j++) {
                        int value = threadId * insertsPerThread + j;
                        threadSafeTable.insert(value);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
            thread.start();
        }
        
        startLatch.countDown(); // Start all threads simultaneously
        assertTrue(doneLatch.await(10, TimeUnit.SECONDS));
        
        // Verify all elements were inserted
        for (int i = 0; i < numThreads * insertsPerThread; i++) {
            assertEquals(i, threadSafeTable.lookup(i), "Element " + i + " should be present");
        }
    }
    
    @Test
    public void testConcurrentReads() throws InterruptedException {
        // Pre-populate the table
        for (int i = 0; i < 1000; i++) {
            threadSafeTable.insert(i);
        }
        
        int numThreads = 20;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(() -> {
                try {
                    startLatch.await();
                    // Each thread reads all values
                    for (int j = 0; j < 1000; j++) {
                        Integer value = threadSafeTable.lookup(j);
                        if (value != null && value == j) {
                            successCount.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
            thread.start();
        }
        
        startLatch.countDown();
        assertTrue(doneLatch.await(10, TimeUnit.SECONDS));
        
        // All reads should have succeeded
        assertEquals(numThreads * 1000, successCount.get());
    }
    
    @Test
    public void testConcurrentInsertsAndRemoves() throws InterruptedException {
        int numThreads = 10;
        int operationsPerThread = 100;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numThreads * 2);
        
        // Insert threads
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < operationsPerThread; j++) {
                        int value = threadId * operationsPerThread + j;
                        threadSafeTable.insert(value);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
            thread.start();
        }
        
        // Remove threads (trying to remove same values)
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                try {
                    startLatch.await();
                    Thread.sleep(10); // Give inserts a head start
                    for (int j = 0; j < operationsPerThread; j++) {
                        int value = threadId * operationsPerThread + j;
                        threadSafeTable.remove(value);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
            thread.start();
        }
        
        startLatch.countDown();
        assertTrue(doneLatch.await(10, TimeUnit.SECONDS));
        
        // Table should be empty or nearly empty (some removes might have happened before inserts)
        assertTrue(threadSafeTable.getSize() < 100, "Most elements should be removed");
    }
    
    @Test
    public void testPerformanceSequentialVsThreaded() throws InterruptedException {
        int numOperations = 10000;
        
        System.out.println("\n=== Performance Comparison: Sequential vs Threaded Hash Table ===");
        
        // Sequential operations
        ChainedHashTable<Integer> seqTable = new ChainedHashTable<>(100);
        long seqStart = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            seqTable.insert(i);
        }
        for (int i = 0; i < numOperations; i++) {
            seqTable.lookup(i);
        }
        long seqTime = (System.nanoTime() - seqStart) / 1_000_000;
        System.out.println("Sequential operations: " + seqTime + " ms");
        
        // Threaded operations with platform threads
        ThreadedChainedHashTable<Integer> threadedTable = new ThreadedChainedHashTable<>(100);
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        long threadedStart = System.nanoTime();
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int t = 0; t < numThreads; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    int opsPerThread = numOperations / numThreads;
                    int start = threadId * opsPerThread;
                    int end = start + opsPerThread;
                    
                    // Insert
                    for (int i = start; i < end; i++) {
                        threadedTable.insert(i);
                    }
                    // Lookup
                    for (int i = start; i < end; i++) {
                        threadedTable.lookup(i);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executor.shutdown();
        long threadedTime = (System.nanoTime() - threadedStart) / 1_000_000;
        System.out.println("Threaded operations (" + numThreads + " threads): " + threadedTime + " ms");
        
        double speedup = (double) seqTime / threadedTime;
        System.out.println("Speedup: " + String.format("%.2f", speedup) + "x");
        
        // Verify correctness
        for (int i = 0; i < numOperations; i++) {
            assertNotNull(threadedTable.lookup(i), "Element " + i + " should be present");
        }
    }
    
    @Test
    public void testPerformanceWithVirtualThreads() throws InterruptedException {
        int numOperations = 10000;
        // For CPU-bound work, optimal thread count matches CPU cores (not higher)
        // Virtual threads still run on carrier threads (one per core)
        // Using more threads for CPU-bound work doesn't help and adds scheduling overhead
        int numThreads = Runtime.getRuntime().availableProcessors();
        
        System.out.println("\n=== Performance Comparison: Platform Threads vs Virtual Threads ===");
        
        // Platform threads
        ThreadedChainedHashTable<Integer> platformTable = new ThreadedChainedHashTable<>(100);
        int platformPoolSize = Runtime.getRuntime().availableProcessors();
        ExecutorService platformExecutor = Executors.newFixedThreadPool(platformPoolSize);
        
        long platformStart = System.nanoTime();
        CountDownLatch platformLatch = new CountDownLatch(numThreads);
        
        for (int t = 0; t < numThreads; t++) {
            final int threadId = t;
            platformExecutor.submit(() -> {
                try {
                    int opsPerThread = numOperations / numThreads;
                    int start = threadId * opsPerThread;
                    int end = start + opsPerThread;
                    
                    for (int i = start; i < end; i++) {
                        platformTable.insert(i);
                        platformTable.lookup(i);
                    }
                } finally {
                    platformLatch.countDown();
                }
            });
        }
        
        platformLatch.await();
        platformExecutor.shutdown();
        long platformTime = (System.nanoTime() - platformStart) / 1_000_000;
        System.out.println("Platform threads (" + platformPoolSize + " thread pool, " + numThreads + " tasks): " + platformTime + " ms");
        
        // Virtual threads
        ThreadedChainedHashTable<Integer> virtualTable = new ThreadedChainedHashTable<>(100);
        
        long virtualStart = System.nanoTime();
        CountDownLatch virtualLatch = new CountDownLatch(numThreads);
        
        for (int t = 0; t < numThreads; t++) {
            final int threadId = t;
            Thread.ofVirtual().start(() -> {
                try {
                    int opsPerThread = numOperations / numThreads;
                    int start = threadId * opsPerThread;
                    int end = start + opsPerThread;
                    
                    for (int i = start; i < end; i++) {
                        virtualTable.insert(i);
                        virtualTable.lookup(i);
                    }
                } finally {
                    virtualLatch.countDown();
                }
            });
        }
        
        virtualLatch.await();
        long virtualTime = (System.nanoTime() - virtualStart) / 1_000_000;
        System.out.println("Virtual threads (" + numThreads + " threads): " + virtualTime + " ms");
        
        System.out.println("Virtual thread overhead: " + 
            String.format("%.2f", ((double) virtualTime / platformTime)) + "x");
        
        // Verify correctness - only check elements that were actually inserted
        int opsPerThread = numOperations / numThreads;
        int totalInserted = numThreads * opsPerThread;
        for (int i = 0; i < totalInserted; i++) {
            assertNotNull(virtualTable.lookup(i), "Element " + i + " should be present");
        }
    }
    
    @Test
    public void testDifferentBucketsParallelism() throws InterruptedException {
        System.out.println("\n=== Testing Parallelism: Different Buckets vs Same Bucket ===");
        
        int numOps = 1000;
        int numThreads = 10;
        
        // Test 1: All threads work on different buckets (should be very parallel)
        ThreadedChainedHashTable<Integer> diffBuckets = new ThreadedChainedHashTable<>(100);
        long diffStart = System.nanoTime();
        
        CountDownLatch diffLatch = new CountDownLatch(numThreads);
        for (int t = 0; t < numThreads; t++) {
            final int threadId = t;
            Thread.ofVirtual().start(() -> {
                try {
                    // Each thread works on values that hash to different buckets
                    for (int i = 0; i < numOps; i++) {
                        int value = threadId + i * numThreads;
                        diffBuckets.insert(value);
                        diffBuckets.lookup(value);
                    }
                } finally {
                    diffLatch.countDown();
                }
            });
        }
        diffLatch.await();
        long diffTime = (System.nanoTime() - diffStart) / 1_000_000;
        System.out.println("Different buckets (high parallelism): " + diffTime + " ms");
        
        // Test 2: All threads work on same bucket (should have more contention)
        ThreadedChainedHashTable<Integer> sameBucket = new ThreadedChainedHashTable<>(100);
        long sameStart = System.nanoTime();
        
        CountDownLatch sameLatch = new CountDownLatch(numThreads);
        for (int t = 0; t < numThreads; t++) {
            final int threadId = t;
            Thread.ofVirtual().start(() -> {
                try {
                    // All threads work on values that hash to bucket 0
                    for (int i = 0; i < numOps; i++) {
                        int value = threadId * numOps + i;
                        // Multiply by 100 to force same bucket (value % 100 = 0)
                        sameBucket.insert(value * 100);
                        sameBucket.lookup(value * 100);
                    }
                } finally {
                    sameLatch.countDown();
                }
            });
        }
        sameLatch.await();
        long sameTime = (System.nanoTime() - sameStart) / 1_000_000;
        System.out.println("Same bucket (high contention): " + sameTime + " ms");
        
        System.out.println("Contention slowdown: " + 
            String.format("%.2f", ((double) sameTime / diffTime)) + "x");
        
        assertTrue(sameTime > diffTime, 
            "Same bucket should be slower due to lock contention");
    }
    
    @Test
    public void testThreadSafetyOfSize() throws InterruptedException {
        int numThreads = 20;
        int insertsPerThread = 500;
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            Thread.ofVirtual().start(() -> {
                try {
                    for (int j = 0; j < insertsPerThread; j++) {
                        int value = threadId * insertsPerThread + j;
                        threadSafeTable.insert(value);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        
        // Size should be exactly correct (no lost updates due to race conditions)
        assertEquals(numThreads * insertsPerThread, threadSafeTable.getSize(),
            "Size counter should be accurate with concurrent inserts");
    }
}
