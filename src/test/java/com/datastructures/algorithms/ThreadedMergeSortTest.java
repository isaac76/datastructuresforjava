package com.datastructures.algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ThreadedMergeSort class.
 */
class ThreadedMergeSortTest {

    private ThreadedMergeSort<Integer> sorter;
    private MergeSort<Integer> sequentialSorter;

    @BeforeEach
    void setUp() {
        sorter = new ThreadedMergeSort<>(10000, true);
        sequentialSorter = new MergeSort<>();
    }

    @Test
    void testSortLargeArray() {
        // Create array larger than threshold (500) to trigger multithreading
        int size = 50000;
        Integer[] array = new Integer[size];
        
        // Fill with reverse sorted values
        for (int i = 0; i < size; i++) {
            array[i] = size - i;
        }
        
        // Sort the array
        sorter.sort(array);
        
        // Verify it's sorted
        for (int i = 0; i < size - 1; i++) {
            assertTrue(array[i] <= array[i + 1], 
                "Array not sorted at index " + i + ": " + array[i] + " > " + array[i + 1]);
        }
        
        // Verify first and last elements
        assertEquals(1, array[0]);
        assertEquals(size, array[size - 1]);
    }

    @Test
    void testCompareThreadedVsSequential() {
        int size = 1000000;
        Integer[] originalArray = new Integer[size];
        
        // Fill with reverse sorted values
        for (int i = 0; i < size; i++) {
            originalArray[i] = size - i;
        }
        
        // Create two copies for fair comparison
        Integer[] threadedArray = Arrays.copyOf(originalArray, size);
        Integer[] sequentialArray = Arrays.copyOf(originalArray, size);
        
        System.out.println("\n=== Performance Comparison ===");
        System.out.println("Array size: " + size);
        System.out.println("Threshold: 10000\n");
        
        // Test sequential version
        System.out.println("--- Sequential MergeSort ---");
        long startSeq = System.currentTimeMillis();
        sequentialSorter.sort(sequentialArray);
        long endSeq = System.currentTimeMillis();
        long sequentialTime = endSeq - startSeq;
        System.out.println("Sequential time: " + sequentialTime + " ms\n");
        
        // Test threaded version
        System.out.println("--- Threaded MergeSort ---");
        long startThread = System.currentTimeMillis();
        sorter.sort(threadedArray);
        long endThread = System.currentTimeMillis();
        long threadedTime = endThread - startThread;
        System.out.println("\n=== Results ===");
        System.out.println("Sequential time: " + sequentialTime + " ms");
        System.out.println("Threaded time:   " + threadedTime + " ms");
        
        if (threadedTime < sequentialTime) {
            double speedup = (double) sequentialTime / threadedTime;
            System.out.println("Speedup:         " + String.format("%.2fx", speedup) + " faster");
        } else {
            double slowdown = (double) threadedTime / sequentialTime;
            System.out.println("Slowdown:        " + String.format("%.2fx", slowdown) + " slower");
        }
        System.out.println("=============================\n");
        
        // Verify both arrays are correctly sorted and identical
        assertArrayEquals(sequentialArray, threadedArray, "Both arrays should be sorted identically");
    }
}
