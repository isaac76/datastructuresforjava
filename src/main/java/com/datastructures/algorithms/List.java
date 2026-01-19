package com.datastructures.algorithms;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;

public class List<T extends Comparable<T>> implements Iterable<T> {
    private int size;
    private ListNode<T> head;
    private ListNode<T> tail;

    public List() {
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    /**
     * Inserts a new element at the end of the list.
     */
    public void insertNext(T data) {
        ListNode<T> newNode = new ListNode<>(data);
        
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    /**
     * Removes the first occurrence of the specified element from the list.
     * Returns true if the element was found and removed, false otherwise.
     */
    public boolean remove(T data) {
        if (head == null) {
            return false;
        }

        // Special case: removing head
        if (head.data.equals(data)) {
            head = head.next;
            if (head == null) {
                tail = null;
            }
            size--;
            return true;
        }

        // Search for the element
        ListNode<T> current = head;
        while (current.next != null) {
            if (current.next.data.equals(data)) {
                current.next = current.next.next;
                if (current.next == null) {
                    tail = current;
                }
                size--;
                return true;
            }
            current = current.next;
        }

        return false;
    }

    /**
     * Removes an element using a stream-based approach.
     * This demonstrates using streams for searching and removal.
     */
    public boolean removeWithStream(T data) {
        boolean found = stream()
            .anyMatch(item -> item.equals(data));
        
        if (found) {
            return remove(data);
        }
        return false;
    }

    /**
     * Looks up an element in the list.
     * Returns the element if found, null otherwise.
     */
    public T lookup(T data) {
        return stream()
            .filter(item -> item.equals(data))
            .findFirst()
            .orElse(null);
    }

    /**
     * Checks if the list contains the specified element using streams.
     */
    public boolean contains(T data) {
        return stream().anyMatch(item -> item.equals(data));
    }

    /**
     * Returns a stream of elements in this list.
     */
    public Stream<T> stream() {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED),
            false
        );
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private ListNode<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    static class ListNode<T> {
        T data;
        ListNode<T> next;

        ListNode(T data) {
            this.data = data;
            this.next = null;
        }
    }
}
