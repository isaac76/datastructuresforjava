package com.datastructures.algorithms;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Concurrent implementation of Merge Sort using Java's Fork/Join framework.
 * 
 * This implementation leverages parallel processing to sort arrays more efficiently
 * on multi-core systems by dividing the work across multiple threads.
 * 
 * Algorithm Overview:
 * 1. Split the array into two halves
 * 2. If the array is large enough, fork tasks to sort each half in parallel
 * 3. Wait for both halves to complete (join)
 * 4. Merge the two sorted halves
 * 
 * Performance Characteristics:
 * - Time Complexity: O(n log n) - same as sequential merge sort
 * - Space Complexity: O(n) - requires temporary arrays for merging
 * - Parallel Speedup: Depends on number of cores and array size
 * - Threshold: Small arrays use sequential sort to avoid thread overhead
 * 
 * Pseudocode:
 * 
 * THREADED-MERGE-SORT(A, p, r, threshold)
 *   if r - p + 1 <= threshold
 *       // Use sequential merge sort for small arrays
 *       SEQUENTIAL-MERGE-SORT(A, p, r)
 *   else if p < r
 *       q = (p + r) / 2
 *       
 *       // Fork left half - execute in parallel thread
 *       left_task = ASYNC THREADED-MERGE-SORT(A, p, q, threshold)
 *       
 *       // Fork right half - execute in parallel thread  
 *       right_task = ASYNC THREADED-MERGE-SORT(A, q+1, r, threshold)
 *       
 *       // Wait for both tasks to complete
 *       WAIT(left_task)
 *       WAIT(right_task)
 *       
 *       // Merge the sorted halves
 *       MERGE(A, p, q, r)
 * 
 * MERGE(A, p, q, r)
 *   n1 = q - p + 1
 *   n2 = r - q
 *   
 *   // Create temporary arrays
 *   left[1..n1] = A[p..q]
 *   right[1..n2] = A[q+1..r]
 *   
 *   // Merge process (same as sequential)
 *   i = 1, j = 1, k = p
 *   while i <= n1 and j <= n2
 *       if left[i] <= right[j]
 *           A[k] = left[i]
 *           i++
 *       else
 *           A[k] = right[j]
 *           j++
 *       k++
 *   
 *   // Copy remaining elements
 *   while i <= n1
 *       A[k] = left[i]
 *       i++, k++
 *   
 *   while j <= n2
 *       A[k] = right[j]
 *       j++, k++
 * 
 * Usage:
 * <pre>
 * ThreadedMergeSort<Integer> sorter = new ThreadedMergeSort<>();
 * Integer[] array = {5, 2, 8, 1, 9};
 * sorter.sort(array);
 * </pre>
 * 
 * @param <T> the type of elements to be sorted, must implement Comparable
 */
public class ThreadedMergeSort<T extends Comparable<T>> extends MergeSort<T> {
    
    /**
     * Default threshold for switching to sequential sort.
     * Arrays smaller than this will use sequential merge sort to avoid
     * the overhead of creating threads for small datasets.
     */
    private static final int DEFAULT_THRESHOLD = 1000;
    
    /**
     * Enable debug output.
     */
    private final boolean debug;
    
    /**
     * Counter for tracking number of tasks created.
     */
    private int taskCounter = 0;
    
    /**
     * The threshold for this sorter instance.
     */
    private final int threshold;
    
    /**
     * Fork/Join pool for parallel execution.
     */
    private final ForkJoinPool pool;
    
    /**
     * Creates a ThreadedMergeSort with default threshold and common pool.
     */
    public ThreadedMergeSort() {
        this(DEFAULT_THRESHOLD, false);
    }
    
    /**
     * Creates a ThreadedMergeSort with specified threshold.
     * 
     * @param threshold the array size below which sequential sort is used
     */
    public ThreadedMergeSort(int threshold) {
        this(threshold, false);
    }
    
    /**
     * Creates a ThreadedMergeSort with specified threshold and debug mode.
     * 
     * @param threshold the array size below which sequential sort is used
     * @param debug     whether to enable debug output
     */
    public ThreadedMergeSort(int threshold, boolean debug) {
        this.threshold = threshold;
        this.pool = ForkJoinPool.commonPool();
        this.debug = debug;
        
        if (debug) {
            int cpuCores = Runtime.getRuntime().availableProcessors();
            int poolSize = pool.getParallelism();
            System.out.println("\n=== ThreadedMergeSort Debug Info ===");
            System.out.println("CPU Cores: " + cpuCores);
            System.out.println("ForkJoinPool Parallelism: " + poolSize);
            System.out.println("Threshold: " + threshold);
            System.out.println("====================================\n");
        }
    }
    
    /**
     * Sorts an array using parallel merge sort.
     * 
     * @param array the array to be sorted
     */
    @Override
    public void sort(T[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        if (debug) {
            System.out.println("Starting sort of array with " + array.length + " elements");
            taskCounter = 0;
        }
        long startTime = System.currentTimeMillis();
        pool.invoke(new MergeSortTask(array, 0, array.length - 1));
        long endTime = System.currentTimeMillis();
        if (debug) {
            System.out.println("\nSort completed in " + (endTime - startTime) + " ms");
            System.out.println("Total tasks created: " + taskCounter);
        }
    }

    protected void threadedMergeSort(T[] array, int p, int r) {
        int size = r - p + 1;
        String threadName = Thread.currentThread().getName();
        
        if (size <= threshold) {
            if (debug) {
                System.out.println("[" + threadName + "] Sequential sort [" + p + ".." + r + "] size=" + size);
            }
            super.mergeSort(array, p, r);
        } else {
            if (p < r) {
                int q = (p + r) / 2;
                
                if (debug) {
                    System.out.println("[" + threadName + "] Forking [" + p + ".." + r + "] size=" + size + " into [" + p + ".." + q + "] and [" + (q+1) + ".." + r + "]");
                }
                
                MergeSortTask leftTask = new MergeSortTask(array, p, q);
                MergeSortTask rightTask = new MergeSortTask(array, q + 1, r);
                
                // Fork left, compute right in current thread (more efficient)
                leftTask.fork();
                rightTask.compute();
                
                if (debug) {
                    System.out.println("[" + threadName + "] Waiting for left task [" + p + ".." + q + "] to complete");
                }
                leftTask.join();
                
                if (debug) {
                    System.out.println("[" + threadName + "] Merging [" + p + ".." + r + "]");
                }
                super.merge(array, p, q, r);
            }
        }
    }
    
    /**
     * RecursiveAction that performs the parallel merge sort.
     * This is the task that gets forked and executed in the Fork/Join pool.
     */
    private class MergeSortTask extends RecursiveAction {
        private final T[] array;
        private final int p;
        private final int r;
        private final int taskId;

        public MergeSortTask(T[] array, int p, int r) {
            this.array = array;
            this.p = p;
            this.r = r;
            this.taskId = ++taskCounter;
            if (debug) {
                System.out.println("[" + Thread.currentThread().getName() + "] Created Task #" + taskId + " for [" + p + ".." + r + "]");
            }
        }

        @Override
        protected void compute() {
            if (debug) {
                System.out.println("[" + Thread.currentThread().getName() + "] Task #" + taskId + " starting compute [" + p + ".." + r + "]");
            }
            threadedMergeSort(array, p, r);
            if (debug) {
                System.out.println("[" + Thread.currentThread().getName() + "] Task #" + taskId + " completed [" + p + ".." + r + "]");
            }
        }
    }
}
