package com.datastructures.algorithms;

/**
 * A Queue implementation using a simple linked list.
 * Provides FIFO (First-In-First-Out) semantics.
 * 
 * @param <T> the type of elements held in this queue
 */
public class Queue<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public Queue() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Adds an element to the rear of the queue.
     * 
     * @param data the element to add
     */
    public void enqueue(T data) {
        Node<T> newNode = new Node<>(data);
        
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    /**
     * Removes and returns the element at the front of the queue.
     * 
     * @return the element at the front of the queue
     * @throws IllegalStateException if the queue is empty
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        
        T data = head.data;
        head = head.next;
        
        if (head == null) {
            tail = null;
        }
        
        size--;
        return data;
    }

    /**
     * Returns the element at the front of the queue without removing it.
     * 
     * @return the element at the front of the queue
     * @throws IllegalStateException if the queue is empty
     */
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        
        return head.data;
    }

    /**
     * Checks if the queue is empty.
     * 
     * @return true if the queue contains no elements, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements in the queue.
     * 
     * @return the number of elements in the queue
     */
    public int size() {
        return size;
    }

    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }
}
