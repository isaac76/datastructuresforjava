package com.datastructures.algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class ChainedHashTableTest {
    
    private ChainedHashTable<Integer> intTable;
    private ChainedHashTable<String> stringTable;
    
    @BeforeEach
    public void setUp() {
        intTable = new ChainedHashTable<>(10);
        stringTable = new ChainedHashTable<>(10);
    }
    
    @Test
    public void testInsertAndLookup() {
        intTable.insert(42);
        intTable.insert(17);
        intTable.insert(99);
        
        assertEquals(42, intTable.lookup(42));
        assertEquals(17, intTable.lookup(17));
        assertEquals(99, intTable.lookup(99));
    }
    
    @Test
    public void testLookupNonExistent() {
        intTable.insert(42);
        
        assertNull(intTable.lookup(99));
    }
    
    @Test
    public void testRemove() {
        intTable.insert(42);
        intTable.insert(17);
        intTable.insert(99);
        
        assertTrue(intTable.remove(17));
        assertNull(intTable.lookup(17));
        
        // Verify other elements still exist
        assertEquals(42, intTable.lookup(42));
        assertEquals(99, intTable.lookup(99));
    }
    
    @Test
    public void testRemoveNonExistent() {
        intTable.insert(42);
        
        assertFalse(intTable.remove(99));
    }
    
    @Test
    public void testRemoveFromEmptyTable() {
        assertFalse(intTable.remove(42));
    }
    
    @Test
    public void testRemoveWithStream() {
        intTable.insert(42);
        intTable.insert(17);
        
        assertTrue(intTable.removeWithStream(42));
        assertNull(intTable.lookup(42));
        assertEquals(17, intTable.lookup(17));
        
        assertFalse(intTable.removeWithStream(99));
    }
    
    @Test
    public void testWithStrings() {
        stringTable.insert("apple");
        stringTable.insert("banana");
        stringTable.insert("cherry");
        
        assertEquals("apple", stringTable.lookup("apple"));
        assertEquals("banana", stringTable.lookup("banana"));
        assertEquals("cherry", stringTable.lookup("cherry"));
        
        assertTrue(stringTable.remove("banana"));
        assertNull(stringTable.lookup("banana"));
    }
    
    @Test
    public void testWithCustomObjects() {
        ChainedHashTable<Person> personTable = new ChainedHashTable<>(10);
        
        Person alice = new Person("Alice", 25);
        Person bob = new Person("Bob", 30);
        Person charlie = new Person("Charlie", 35);
        
        personTable.insert(alice);
        personTable.insert(bob);
        personTable.insert(charlie);
        
        assertEquals(alice, personTable.lookup(alice));
        assertEquals(bob, personTable.lookup(bob));
        assertEquals(charlie, personTable.lookup(charlie));
        
        assertTrue(personTable.remove(bob));
        assertNull(personTable.lookup(bob));
        
        assertEquals(alice, personTable.lookup(alice));
        assertEquals(charlie, personTable.lookup(charlie));
    }
    
    @Test
    public void testHashCollisions() {
        // Create a small table to force collisions
        ChainedHashTable<Integer> smallTable = new ChainedHashTable<>(3);
        
        // These numbers will likely collide in a 3-bucket table
        smallTable.insert(0);   // hash % 3 = 0
        smallTable.insert(3);   // hash % 3 = 0 (collision!)
        smallTable.insert(6);   // hash % 3 = 0 (collision!)
        smallTable.insert(1);   // hash % 3 = 1
        smallTable.insert(4);   // hash % 3 = 1 (collision!)
        
        // All elements should still be retrievable
        assertEquals(0, smallTable.lookup(0));
        assertEquals(3, smallTable.lookup(3));
        assertEquals(6, smallTable.lookup(6));
        assertEquals(1, smallTable.lookup(1));
        assertEquals(4, smallTable.lookup(4));
        
        // Remove an element from a bucket with collisions
        assertTrue(smallTable.remove(3));
        assertNull(smallTable.lookup(3));
        
        // Other elements in the same bucket should still be there
        assertEquals(0, smallTable.lookup(0));
        assertEquals(6, smallTable.lookup(6));
    }
    
    @Test
    public void testDuplicateInserts() {
        intTable.insert(42);
        intTable.insert(42);  // Duplicate
        intTable.insert(42);  // Another duplicate
        
        // Should be able to lookup (though duplicates may exist in list)
        assertEquals(42, intTable.lookup(42));
        
        // Remove should remove at least one occurrence
        assertTrue(intTable.remove(42));
    }
    
    @Test
    public void testLargeNumberOfElements() {
        ChainedHashTable<Integer> largeTable = new ChainedHashTable<>(100);
        
        // Insert many elements
        for (int i = 0; i < 1000; i++) {
            largeTable.insert(i);
        }
        
        // Verify some random elements
        assertEquals(0, largeTable.lookup(0));
        assertEquals(500, largeTable.lookup(500));
        assertEquals(999, largeTable.lookup(999));
        
        // Remove some elements
        assertTrue(largeTable.remove(500));
        assertNull(largeTable.lookup(500));
        
        // Verify others still exist
        assertEquals(499, largeTable.lookup(499));
        assertEquals(501, largeTable.lookup(501));
    }
    
    @Test
    public void testInsertRemoveInsert() {
        intTable.insert(42);
        assertEquals(42, intTable.lookup(42));
        
        assertTrue(intTable.remove(42));
        assertNull(intTable.lookup(42));
        
        intTable.insert(42);
        assertEquals(42, intTable.lookup(42));
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
