package com.datastructures.algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;

public class ListTest {
    
    private List<Integer> intList;
    private List<String> stringList;
    
    @BeforeEach
    public void setUp() {
        intList = new List<>();
        stringList = new List<>();
    }
    
    @Test
    public void testInsertAndGetSize() {
        assertEquals(0, intList.getSize());
        assertTrue(intList.isEmpty());
        
        intList.insertNext(10);
        assertEquals(1, intList.getSize());
        assertFalse(intList.isEmpty());
        
        intList.insertNext(20);
        intList.insertNext(30);
        assertEquals(3, intList.getSize());
    }
    
    @Test
    public void testContains() {
        intList.insertNext(10);
        intList.insertNext(20);
        intList.insertNext(30);
        
        assertTrue(intList.contains(10));
        assertTrue(intList.contains(20));
        assertTrue(intList.contains(30));
        assertFalse(intList.contains(40));
    }
    
    @Test
    public void testRemove() {
        intList.insertNext(10);
        intList.insertNext(20);
        intList.insertNext(30);
        
        assertTrue(intList.remove(20));
        assertEquals(2, intList.getSize());
        assertFalse(intList.contains(20));
        
        assertTrue(intList.remove(10));
        assertEquals(1, intList.getSize());
        assertFalse(intList.contains(10));
        
        assertTrue(intList.remove(30));
        assertEquals(0, intList.getSize());
        assertTrue(intList.isEmpty());
        
        // Remove from empty list
        assertFalse(intList.remove(40));
    }
    
    @Test
    public void testRemoveHead() {
        intList.insertNext(10);
        intList.insertNext(20);
        intList.insertNext(30);
        
        // Remove first element
        assertTrue(intList.remove(10));
        assertEquals(2, intList.getSize());
        assertFalse(intList.contains(10));
        assertTrue(intList.contains(20));
        assertTrue(intList.contains(30));
    }
    
    @Test
    public void testRemoveTail() {
        intList.insertNext(10);
        intList.insertNext(20);
        intList.insertNext(30);
        
        // Remove last element
        assertTrue(intList.remove(30));
        assertEquals(2, intList.getSize());
        assertFalse(intList.contains(30));
        assertTrue(intList.contains(10));
        assertTrue(intList.contains(20));
    }
    
    @Test
    public void testRemoveNonExistent() {
        intList.insertNext(10);
        intList.insertNext(20);
        
        assertFalse(intList.remove(30));
        assertEquals(2, intList.getSize());
    }
    
    @Test
    public void testRemoveWithStream() {
        intList.insertNext(10);
        intList.insertNext(20);
        intList.insertNext(30);
        
        assertTrue(intList.removeWithStream(20));
        assertEquals(2, intList.getSize());
        assertFalse(intList.contains(20));
        
        assertFalse(intList.removeWithStream(40));
        assertEquals(2, intList.getSize());
    }
    
    @Test
    public void testIterator() {
        intList.insertNext(10);
        intList.insertNext(20);
        intList.insertNext(30);
        
        Iterator<Integer> iter = intList.iterator();
        assertTrue(iter.hasNext());
        assertEquals(10, iter.next());
        assertTrue(iter.hasNext());
        assertEquals(20, iter.next());
        assertTrue(iter.hasNext());
        assertEquals(30, iter.next());
        assertFalse(iter.hasNext());
    }
    
    @Test
    public void testStream() {
        intList.insertNext(10);
        intList.insertNext(20);
        intList.insertNext(30);
        intList.insertNext(40);
        
        // Count elements
        assertEquals(4, intList.stream().count());
        
        // Filter elements
        long countGreaterThan15 = intList.stream()
            .filter(n -> n > 15)
            .count();
        assertEquals(3, countGreaterThan15);
        
        // Find element
        assertTrue(intList.stream().anyMatch(n -> n == 20));
        assertFalse(intList.stream().anyMatch(n -> n == 50));
    }
    
    @Test
    public void testWithStrings() {
        stringList.insertNext("apple");
        stringList.insertNext("banana");
        stringList.insertNext("cherry");
        
        assertEquals(3, stringList.getSize());
        assertTrue(stringList.contains("banana"));
        
        assertTrue(stringList.remove("banana"));
        assertEquals(2, stringList.getSize());
        assertFalse(stringList.contains("banana"));
        
        assertTrue(stringList.contains("apple"));
        assertTrue(stringList.contains("cherry"));
    }
    
    @Test
    public void testWithCustomObjects() {
        List<Person> personList = new List<>();
        
        Person alice = new Person("Alice", 25);
        Person bob = new Person("Bob", 30);
        Person charlie = new Person("Charlie", 35);
        
        personList.insertNext(alice);
        personList.insertNext(bob);
        personList.insertNext(charlie);
        
        assertEquals(3, personList.getSize());
        assertTrue(personList.contains(bob));
        
        assertTrue(personList.remove(bob));
        assertEquals(2, personList.getSize());
        assertFalse(personList.contains(bob));
        
        // Test stream operations
        long countOver30 = personList.stream()
            .filter(p -> p.age > 30)
            .count();
        assertEquals(1, countOver30);
    }
    
    /**
     * Simple test class for custom objects.
     */
    private static class Person implements Comparable<Person> {
        String name;
        int age;
        
        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        @Override
        public int compareTo(Person other) {
            return this.name.compareTo(other.name);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Person)) return false;
            Person other = (Person) obj;
            return name.equals(other.name) && age == other.age;
        }
        
        @Override
        public int hashCode() {
            return name.hashCode() + age;
        }
    }
}
