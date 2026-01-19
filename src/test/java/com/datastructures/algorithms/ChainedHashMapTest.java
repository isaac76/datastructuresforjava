package com.datastructures.algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class ChainedHashMapTest {
    
    private ChainedHashMap<String, Integer> nameAgeMap;
    private ChainedHashMap<Integer, String> idNameMap;
    
    @BeforeEach
    public void setUp() {
        nameAgeMap = new ChainedHashMap<>(10);
        idNameMap = new ChainedHashMap<>(10);
    }
    
    @Test
    public void testPutAndGet() {
        nameAgeMap.put("Alice", 25);
        nameAgeMap.put("Bob", 30);
        nameAgeMap.put("Charlie", 35);
        
        assertEquals(25, nameAgeMap.get("Alice"));
        assertEquals(30, nameAgeMap.get("Bob"));
        assertEquals(35, nameAgeMap.get("Charlie"));
    }
    
    @Test
    public void testGetNonExistent() {
        nameAgeMap.put("Alice", 25);
        
        assertNull(nameAgeMap.get("Bob"));
    }
    
    @Test
    public void testPutUpdateExistingKey() {
        // Put initial value
        assertNull(nameAgeMap.put("Alice", 25));
        assertEquals(25, nameAgeMap.get("Alice"));
        
        // Update with new value - should return old value
        assertEquals(25, nameAgeMap.put("Alice", 26));
        assertEquals(26, nameAgeMap.get("Alice"));
        
        // Update again
        assertEquals(26, nameAgeMap.put("Alice", 27));
        assertEquals(27, nameAgeMap.get("Alice"));
    }
    
    @Test
    public void testContainsKey() {
        nameAgeMap.put("Alice", 25);
        nameAgeMap.put("Bob", 30);
        
        assertTrue(nameAgeMap.containsKey("Alice"));
        assertTrue(nameAgeMap.containsKey("Bob"));
        assertFalse(nameAgeMap.containsKey("Charlie"));
    }
    
    @Test
    public void testRemove() {
        nameAgeMap.put("Alice", 25);
        nameAgeMap.put("Bob", 30);
        nameAgeMap.put("Charlie", 35);
        
        // Remove returns the old value
        assertEquals(30, nameAgeMap.remove("Bob"));
        assertNull(nameAgeMap.get("Bob"));
        assertFalse(nameAgeMap.containsKey("Bob"));
        
        // Other keys should still exist
        assertEquals(25, nameAgeMap.get("Alice"));
        assertEquals(35, nameAgeMap.get("Charlie"));
    }
    
    @Test
    public void testRemoveNonExistent() {
        nameAgeMap.put("Alice", 25);
        
        assertNull(nameAgeMap.remove("Bob"));
    }
    
    @Test
    public void testSizeAndIsEmpty() {
        assertTrue(nameAgeMap.isEmpty());
        assertEquals(0, nameAgeMap.size());
        
        nameAgeMap.put("Alice", 25);
        assertFalse(nameAgeMap.isEmpty());
        assertEquals(1, nameAgeMap.size());
        
        nameAgeMap.put("Bob", 30);
        assertEquals(2, nameAgeMap.size());
        
        // Update existing key shouldn't change size
        nameAgeMap.put("Alice", 26);
        assertEquals(2, nameAgeMap.size());
        
        nameAgeMap.remove("Alice");
        assertEquals(1, nameAgeMap.size());
        
        nameAgeMap.remove("Bob");
        assertTrue(nameAgeMap.isEmpty());
        assertEquals(0, nameAgeMap.size());
    }
    
    @Test
    public void testWithIntegerKeys() {
        idNameMap.put(101, "Alice");
        idNameMap.put(102, "Bob");
        idNameMap.put(103, "Charlie");
        
        assertEquals("Alice", idNameMap.get(101));
        assertEquals("Bob", idNameMap.get(102));
        assertEquals("Charlie", idNameMap.get(103));
        
        assertEquals("Bob", idNameMap.remove(102));
        assertNull(idNameMap.get(102));
    }
    
    @Test
    public void testWithCustomObjectKeys() {
        ChainedHashMap<Person, String> personRoleMap = new ChainedHashMap<>(10);
        
        Person alice = new Person("Alice", 25);
        Person bob = new Person("Bob", 30);
        Person charlie = new Person("Charlie", 35);
        
        personRoleMap.put(alice, "Engineer");
        personRoleMap.put(bob, "Manager");
        personRoleMap.put(charlie, "Designer");
        
        assertEquals("Engineer", personRoleMap.get(alice));
        assertEquals("Manager", personRoleMap.get(bob));
        assertEquals("Designer", personRoleMap.get(charlie));
        
        // Update role
        assertEquals("Manager", personRoleMap.put(bob, "Senior Manager"));
        assertEquals("Senior Manager", personRoleMap.get(bob));
        
        // Remove
        assertEquals("Engineer", personRoleMap.remove(alice));
        assertNull(personRoleMap.get(alice));
    }
    
    @Test
    public void testHashCollisions() {
        // Create a small map to force collisions
        ChainedHashMap<String, Integer> smallMap = new ChainedHashMap<>(3);
        
        // These keys should cause collisions in a 3-bucket map
        smallMap.put("key0", 0);
        smallMap.put("key3", 3);
        smallMap.put("key6", 6);
        smallMap.put("key1", 1);
        smallMap.put("key4", 4);
        
        // All entries should still be retrievable
        assertEquals(0, smallMap.get("key0"));
        assertEquals(3, smallMap.get("key3"));
        assertEquals(6, smallMap.get("key6"));
        assertEquals(1, smallMap.get("key1"));
        assertEquals(4, smallMap.get("key4"));
        
        // Remove an entry from a bucket with collisions
        assertEquals(3, smallMap.remove("key3"));
        assertNull(smallMap.get("key3"));
        
        // Other entries in the same bucket should still be there
        assertEquals(0, smallMap.get("key0"));
        assertEquals(6, smallMap.get("key6"));
    }
    
    @Test
    public void testNullValues() {
        nameAgeMap.put("Alice", 25);
        nameAgeMap.put("Bob", null);
        
        assertEquals(25, nameAgeMap.get("Alice"));
        assertNull(nameAgeMap.get("Bob"));
        
        // Distinguish between key not present and null value
        assertTrue(nameAgeMap.containsKey("Bob"));
        assertFalse(nameAgeMap.containsKey("Charlie"));
    }
    
    @Test
    public void testLargeNumberOfEntries() {
        ChainedHashMap<Integer, String> largeMap = new ChainedHashMap<>(100);
        
        // Insert many entries
        for (int i = 0; i < 1000; i++) {
            largeMap.put(i, "Value" + i);
        }
        
        assertEquals(1000, largeMap.size());
        
        // Verify some random entries
        assertEquals("Value0", largeMap.get(0));
        assertEquals("Value500", largeMap.get(500));
        assertEquals("Value999", largeMap.get(999));
        
        // Remove some entries
        assertEquals("Value500", largeMap.remove(500));
        assertNull(largeMap.get(500));
        assertEquals(999, largeMap.size());
        
        // Verify others still exist
        assertEquals("Value499", largeMap.get(499));
        assertEquals("Value501", largeMap.get(501));
    }
    
    @Test
    public void testPutRemovePut() {
        nameAgeMap.put("Alice", 25);
        assertEquals(25, nameAgeMap.get("Alice"));
        assertEquals(1, nameAgeMap.size());
        
        assertEquals(25, nameAgeMap.remove("Alice"));
        assertNull(nameAgeMap.get("Alice"));
        assertEquals(0, nameAgeMap.size());
        
        nameAgeMap.put("Alice", 30);
        assertEquals(30, nameAgeMap.get("Alice"));
        assertEquals(1, nameAgeMap.size());
    }
    
    @Test
    public void testMultipleUpdates() {
        nameAgeMap.put("Alice", 25);
        
        for (int age = 26; age <= 35; age++) {
            Integer oldAge = nameAgeMap.put("Alice", age);
            assertEquals(age - 1, oldAge);
        }
        
        assertEquals(35, nameAgeMap.get("Alice"));
        assertEquals(1, nameAgeMap.size());
    }
    
    /**
     * Simple test class for custom object keys.
     */
    private static class Person {
        String name;
        int age;
        
        Person(String name, int age) {
            this.name = name;
            this.age = age;
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
