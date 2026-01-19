package com.datastructures.algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MergeSort class.
 */
class MergeSortTest {

    private MergeSort<Integer> integerSorter;
    private MergeSort<String> stringSorter;
    private MergeSort<IndexedItem> itemSorter;

    @BeforeEach
    void setUp() {
        integerSorter = new MergeSort<>();
        stringSorter = new MergeSort<>();
        itemSorter = new MergeSort<>();
    }

    @Test
    void testSortEmptyArray() {
        Integer[] array = {};
        integerSorter.sort(array);
        assertArrayEquals(new Integer[]{}, array);
    }

    @Test
    void testSortSingleElement() {
        Integer[] array = {42};
        integerSorter.sort(array);
        assertArrayEquals(new Integer[]{42}, array);
    }

    @Test
    void testSortAlreadySorted() {
        Integer[] array = {1, 2, 3, 4, 5};
        integerSorter.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, array);
    }

    @Test
    void testSortReverseSorted() {
        Integer[] array = {5, 4, 3, 2, 1};
        integerSorter.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, array);
    }

    @Test
    void testSortWithDuplicates() {
        Integer[] array = {3, 1, 4, 1, 5, 9, 2, 6, 5};
        integerSorter.sort(array);
        assertArrayEquals(new Integer[]{1, 1, 2, 3, 4, 5, 5, 6, 9}, array);
    }

    @Test
    void testSortRandomOrder() {
        Integer[] array = {64, 34, 25, 12, 22, 11, 90};
        integerSorter.sort(array);
        assertArrayEquals(new Integer[]{11, 12, 22, 25, 34, 64, 90}, array);
    }

    @Test
    void testSortNegativeNumbers() {
        Integer[] array = {-5, 3, -2, 8, -10, 0, 6};
        integerSorter.sort(array);
        assertArrayEquals(new Integer[]{-10, -5, -2, 0, 3, 6, 8}, array);
    }

    @Test
    void testSortLargeArray() {
        Integer[] array = new Integer[1000];
        for (int i = 0; i < 1000; i++) {
            array[i] = 1000 - i;
        }
        integerSorter.sort(array);
        
        // Verify it's sorted
        for (int i = 0; i < 999; i++) {
            assertTrue(array[i] <= array[i + 1]);
        }
    }

    @Test
    void testSortStrings() {
        String[] array = {"banana", "apple", "cherry", "date", "elderberry"};
        stringSorter.sort(array);
        assertArrayEquals(new String[]{"apple", "banana", "cherry", "date", "elderberry"}, array);
    }

    @Test
    void testSortStringsWithDuplicates() {
        String[] array = {"dog", "cat", "bird", "cat", "ant"};
        stringSorter.sort(array);
        assertArrayEquals(new String[]{"ant", "bird", "cat", "cat", "dog"}, array);
    }

    @Test
    void testSortCustomObjects() {
        IndexedItem[] array = {
            new IndexedItem(5, "Fifth"),
            new IndexedItem(2, "Second"),
            new IndexedItem(8, "Eighth"),
            new IndexedItem(1, "First"),
            new IndexedItem(3, "Third")
        };
        
        itemSorter.sort(array);
        
        assertEquals(1, array[0].getIndex());
        assertEquals(2, array[1].getIndex());
        assertEquals(3, array[2].getIndex());
        assertEquals(5, array[3].getIndex());
        assertEquals(8, array[4].getIndex());
    }

    @Test
    void testSortCustomObjectsWithDuplicateIndices() {
        IndexedItem[] array = {
            new IndexedItem(3, "First3"),
            new IndexedItem(1, "First1"),
            new IndexedItem(3, "Second3"),
            new IndexedItem(2, "First2")
        };
        
        itemSorter.sort(array);
        
        assertEquals(1, array[0].getIndex());
        assertEquals(2, array[1].getIndex());
        assertEquals(3, array[2].getIndex());
        assertEquals(3, array[3].getIndex());
    }

    /**
     * A simple class to test sorting custom objects.
     * Objects are compared by their index value.
     */
    private static class IndexedItem implements Comparable<IndexedItem> {
        private final int index;
        private final String name;

        public IndexedItem(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        @Override
        public int compareTo(IndexedItem other) {
            return Integer.compare(this.index, other.index);
        }

        @Override
        public String toString() {
            return "IndexedItem{index=" + index + ", name='" + name + "'}";
        }
    }
}

