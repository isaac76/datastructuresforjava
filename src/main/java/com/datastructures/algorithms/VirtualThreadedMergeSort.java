package com.datastructures.algorithms;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

/**
 * Concurrent implementation of Merge Sort using Java's Virtual Threads (Project Loom).
 * 
 * This implementation leverages parallel processing using lightweight virtual threads
 * to sort arrays more efficiently on multi-core systems by dividing the work across
 * multiple concurrent tasks.
 * 
 * What are Virtual Threads?
 * Virtual threads were introduced as a stable feature in Java 21 (preview in Java 19-20).
 * They are lightweight threads managed by the JVM rather than the operating system.
 * 
 * Key Characteristics:
 * - Lightweight: Virtual threads have minimal memory footprint (~1KB) compared to 
 *   platform threads (~1-2MB), allowing millions of concurrent virtual threads
 * - Cheap to create: Creating a virtual thread is orders of magnitude faster than 
 *   creating a platform thread
 * - Carrier threads: Virtual threads run on a small pool of platform threads called
 *   carrier threads (typically one per CPU core)
 * - Automatic yielding: When a virtual thread blocks (I/O, locks, etc.), it's 
 *   automatically unmounted from its carrier thread, allowing other virtual threads to run
 * - No pooling needed: Unlike platform threads, virtual threads are so cheap that 
 *   you create them on-demand rather than using thread pools
 * 
 * Virtual Threads vs Fork/Join:
 * - Fork/Join: Best for CPU-bound recursive divide-and-conquer tasks with work-stealing
 *   Uses a fixed pool of platform threads
 * - Virtual Threads: Best for I/O-bound tasks or when you need massive concurrency
 *   For CPU-bound tasks like sorting, virtual threads compete for the same carrier threads,
 *   so performance may be similar to Fork/Join but with simpler, more readable code
 * 
 * StructuredTaskScope:
 * This implementation uses StructuredTaskScope.ShutdownOnFailure, which provides:
 * - Structured concurrency - ensures all subtasks complete before the scope exits
 * - Automatic cancellation - if one task fails, all others are cancelled
 * - Clean resource management - all threads are properly cleaned up
 * 
 * Algorithm Overview:
 * 1. Split the array into two halves
 * 2. If the array is large enough, fork virtual threads to sort each half in parallel
 * 3. Wait for both halves to complete (structured concurrency)
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
 * VIRTUAL-THREADED-MERGE-SORT(A, p, r, threshold)
 *   if r - p + 1 <= threshold
 *       // Use sequential merge sort for small arrays
 *       SEQUENTIAL-MERGE-SORT(A, p, r)
 *   else if p < r
 *       q = (p + r) / 2
 *       
 *       // Create structured task scope for managed concurrency
 *       scope = CREATE-STRUCTURED-SCOPE()
 *       
 *       // Fork left half - execute in virtual thread
 *       left_task = scope.FORK(() -> VIRTUAL-THREADED-MERGE-SORT(A, p, q, threshold))
 *       
 *       // Fork right half - execute in virtual thread  
 *       right_task = scope.FORK(() -> VIRTUAL-THREADED-MERGE-SORT(A, q+1, r, threshold))
 *       
 *       // Wait for both tasks to complete (structured concurrency)
 *       scope.JOIN()
 *       scope.THROW-IF-FAILED()
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
 * VirtualThreadedMergeSort<Integer> sorter = new VirtualThreadedMergeSort<>(10000);
 * Integer[] array = {5, 2, 8, 1, 9};
 * sorter.sort(array);
 * </pre>
 * 
 * @param <T> the type of elements to be sorted, must implement Comparable
 */
public class VirtualThreadedMergeSort<T extends Comparable<T>> extends MergeSort<T> {
    
    private final int threshold;
    private boolean debug = false;
    
    /**
     * Creates a VirtualThreadedMergeSort with the specified threshold.
     * When the subarray size falls below this threshold, the algorithm
     * switches to sequential sorting to avoid excessive thread creation overhead.
     * 
     * @param threshold the minimum size for parallel sorting
     */
    public VirtualThreadedMergeSort(int threshold) {
        this.threshold = threshold;
    }
    
    /**
     * Enables or disables debug output showing virtual thread execution details.
     * 
     * @param debug true to enable debug output
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    /**
     * Sorts the given array using virtual threads for parallel execution.
     * 
     * @param array the array to be sorted
     */
    @Override
    public void sort(T[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        
        if (debug) {
            System.out.println("=== Virtual Thread Merge Sort ===");
            System.out.println("Array size: " + array.length);
            System.out.println("Threshold: " + threshold);
            System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
        }
        
        long startTime = System.nanoTime();
        
        try {
            virtualThreadedMergeSort(array, 0, array.length - 1);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Sorting failed", e);
        }
        
        if (debug) {
            long duration = (System.nanoTime() - startTime) / 1_000_000;
            System.out.println("Virtual threaded sort completed in " + duration + " ms");
        }
    }
    
    /**
     * Recursively sorts the array using virtual threads when size exceeds threshold.
     * 
     * @param array the array to sort
     * @param left the starting index
     * @param right the ending index
     */
    private void virtualThreadedMergeSort(T[] array, int left, int right) 
            throws InterruptedException, ExecutionException {
        
        if (left >= right) {
            return;
        }
        
        int size = right - left + 1;
        
        // Switch to sequential for small subarrays
        if (size < threshold) {
            mergeSort(array, left, right);
            return;
        }
        
        int mid = left + (right - left) / 2;
        
        if (debug) {
            System.out.println("Creating virtual threads for range [" + left + ", " + right + "]");
        }
        
        // Use StructuredTaskScope for structured concurrency
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            
            // Fork left and right subtasks as virtual threads
            var leftTask = scope.fork(() -> {
                virtualThreadedMergeSort(array, left, mid);
                return null;
            });
            
            var rightTask = scope.fork(() -> {
                virtualThreadedMergeSort(array, mid + 1, right);
                return null;
            });
            
            // Wait for both tasks to complete
            scope.join();
            scope.throwIfFailed();
            
            // Merge the sorted halves
            merge(array, left, mid, right);
        }
    }
}
