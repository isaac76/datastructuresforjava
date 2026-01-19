package com.datastructures.algorithms;

import java.util.Arrays;

/**
 * Implementation of the Merge Sort algorithm.
 * Merge sort is a divide-and-conquer algorithm that divides the input array into two halves,
 * recursively sorts them, and then merges the two sorted halves.
 * Time Complexity: O(n log n)
 * Space Complexity: O(n)
 *
 * @param <T> the type of elements to be sorted, must implement Comparable
 */
public class MergeSort<T extends Comparable<T>> {

    /**
     * Sorts an array of comparable elements using the merge sort algorithm.
     *
     * @param array the array to be sorted
     */
    public void sort(T[] array) {
        if (array == null || array.length < 2) {
            return; // Array is already sorted
        }
        mergeSort(array, 0, array.length - 1);
    }

    /**
     * Sorts a portion of an array using the merge sort algorithm.
     *
     * @param array the array to be sorted
     * @param left  the starting index of the portion to sort
     * @param right the ending index of the portion to sort
     */
    protected void mergeSort(T[] array, int p, int r) {
        if (p < r) {
            int q = (p + r) / 2;
            mergeSort(array, p, q);
            mergeSort(array, q + 1, r);
            merge(array, p, q, r);
        }
    }

    /**
     * Merges two sorted subarrays into a single sorted subarray.
     * The first subarray is array[p..q] and the second is array[q+1..r].
     * 
     * Algorithm:
     * 1. Copy both subarrays into temporary arrays (left and right)
     * 2. Merge by comparing elements from left and right, choosing the smaller one
     * 3. Copy any remaining elements from either subarray
     *
     * @param array the array containing the subarrays
     * @param p     the starting index of the first subarray
     * @param q     the ending index of the first subarray (midpoint)
     * @param r     the ending index of the second subarray
     */
    protected void merge(T[] array, int p, int q, int r) {
        int n1 = q - p + 1;
        int n2 = r - q;

        // Copy data to temp arrays (Arrays.copyOfRange handles this correctly)
        T[] left = Arrays.copyOfRange(array, p, q + 1);
        T[] right = Arrays.copyOfRange(array, q + 1, r + 1);

        // Merge the temp arrays back into array[p..r]
        int i = 0; // Initial index of first subarray
        int j = 0; // Initial index of second subarray
        int k = p; // Initial index of merged subarray

        while (i < n1 && j < n2) {
            if (left[i].compareTo(right[j]) <= 0) {
                array[k] = left[i];
                i++;
            } else {
                array[k] = right[j];
                j++;
            }
            k++;
        }

        // Copy remaining elements of left[] if any
        while (i < n1) {
            array[k] = left[i];
            i++;
            k++;
        }

        // Copy remaining elements of right[] if any
        while (j < n2) {
            array[k] = right[j];
            j++;
            k++;
        }
    }
}
