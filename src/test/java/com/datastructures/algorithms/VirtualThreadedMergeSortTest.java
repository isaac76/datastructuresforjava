package com.datastructures.algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

public class VirtualThreadedMergeSortTest {
    
    private VirtualThreadedMergeSort<Integer> virtualSorter;
    private MergeSort<Integer> sequentialSorter;
    
    @BeforeEach
    public void setUp() {
        virtualSorter = new VirtualThreadedMergeSort<>(10000);
        sequentialSorter = new MergeSort<>();
    }
    
    @Test
    public void testSortEmptyArray() {
        Integer[] array = {};
        virtualSorter.sort(array);
        assertEquals(0, array.length);
    }
    
    @Test
    public void testSortSingleElement() {
        Integer[] array = {42};
        virtualSorter.sort(array);
        assertArrayEquals(new Integer[]{42}, array);
    }
    
    @Test
    public void testSortSmallArray() {
        Integer[] array = {5, 2, 8, 1, 9};
        virtualSorter.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 5, 8, 9}, array);
    }
    
    @Test
    public void testSortWithDuplicates() {
        Integer[] array = {5, 2, 8, 2, 9, 5};
        virtualSorter.sort(array);
        assertArrayEquals(new Integer[]{2, 2, 5, 5, 8, 9}, array);
    }
    
    @Test
    public void testSortAlreadySorted() {
        Integer[] array = {1, 2, 3, 4, 5};
        virtualSorter.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, array);
    }
    
    @Test
    public void testSortReverseSorted() {
        Integer[] array = {5, 4, 3, 2, 1};
        virtualSorter.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, array);
    }
    
    @Test
    public void testSortLargeArray() {
        int size = 100000;
        Integer[] array = generateRandomArray(size);
        Integer[] expected = array.clone();
        
        virtualSorter.sort(array);
        sequentialSorter.sort(expected);
        
        assertArrayEquals(expected, array, "Virtual threaded sort should produce same result as sequential");
    }
    
    @Test
    public void testCompareVirtualThreadedVsSequential() {
        int size = 1000000;
        Integer[] array1 = generateRandomArray(size);
        Integer[] array2 = array1.clone();
        
        System.out.println("\n=== Performance Comparison: Virtual Threads vs Sequential ===");
        System.out.println("Array size: " + size);
        
        // Sequential sort
        long sequentialStart = System.nanoTime();
        sequentialSorter.sort(array1);
        long sequentialTime = (System.nanoTime() - sequentialStart) / 1_000_000;
        System.out.println("Sequential sort time: " + sequentialTime + " ms");
        
        // Virtual threaded sort with debug
        virtualSorter.setDebug(true);
        long virtualStart = System.nanoTime();
        virtualSorter.sort(array2);
        long virtualTime = (System.nanoTime() - virtualStart) / 1_000_000;
        System.out.println("Virtual threaded sort time: " + virtualTime + " ms");
        
        // Compare results
        assertArrayEquals(array1, array2, "Both sorts should produce identical results");
        
        // Performance analysis
        double speedup = (double) sequentialTime / virtualTime;
        System.out.println("\nSpeedup: " + String.format("%.2f", speedup) + "x");
        
        if (speedup > 1.0) {
            System.out.println("Virtual threaded version is faster!");
        } else {
            System.out.println("Sequential version is faster. Virtual threads may not benefit CPU-bound tasks.");
        }
    }
    
    @Test
    public void testCompareThresholds() {
        int size = 500000;
        Integer[] baseArray = generateRandomArray(size);
        
        System.out.println("\n=== Threshold Comparison for Virtual Threads ===");
        System.out.println("Array size: " + size);
        
        int[] thresholds = {1000, 5000, 10000, 50000};
        
        for (int threshold : thresholds) {
            Integer[] array = baseArray.clone();
            VirtualThreadedMergeSort<Integer> sorter = new VirtualThreadedMergeSort<>(threshold);
            
            long start = System.nanoTime();
            sorter.sort(array);
            long time = (System.nanoTime() - start) / 1_000_000;
            
            System.out.println("Threshold " + threshold + ": " + time + " ms");
            
            // Verify correctness
            for (int i = 1; i < array.length; i++) {
                assertTrue(array[i - 1].compareTo(array[i]) <= 0, 
                    "Array should be sorted at index " + i);
            }
        }
    }
    
    @Test
    public void testVirtualThreadedWithStrings() {
        VirtualThreadedMergeSort<String> stringSorter = new VirtualThreadedMergeSort<>(100);
        String[] array = {"zebra", "apple", "mango", "banana", "cherry"};
        
        stringSorter.sort(array);
        
        assertArrayEquals(new String[]{"apple", "banana", "cherry", "mango", "zebra"}, array);
    }
    
    private Integer[] generateRandomArray(int size) {
        Random random = new Random(42); // Fixed seed for reproducibility
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size);
        }
        return array;
    }
}
