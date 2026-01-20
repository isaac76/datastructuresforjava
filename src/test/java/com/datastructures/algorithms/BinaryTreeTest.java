package com.datastructures.algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class BinaryTreeTest {
    private BinaryTree<Integer> tree;

    @BeforeEach
    public void setUp() {
        tree = new BinaryTree<>();
    }

    @Test
    public void testInOrderTraversal_EmptyTree() {
        List<Integer> result = tree.inOrderTraversal();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testInOrderTraversal_SingleNode() {
        tree.insertLeft(null, 5);
        List<Integer> result = tree.inOrderTraversal();
        
        assertEquals(1, result.getSize());
        assertEquals(5, result.iterator().next());
    }

    @Test
    public void testInOrderTraversal_CompleteTree() {
        // Build tree by testing through public methods only
        // We'll build and verify using traversals
        tree.insertLeft(null, 1);          // root = 1
        tree.insertLeft(tree.getRoot(), 2);   // left child of root = 2
        tree.insertRight(tree.getRoot(), 3);  // right child of root = 3
        
        // Get reference to left child and add its children
        // Since we can't access TreeNode directly, we'll build a simpler tree
        List<Integer> result = tree.inOrderTraversal();
        assertEquals(3, result.getSize());
        
        // In-order: 2, 1, 3
        Integer[] expected = {2, 1, 3};
        int i = 0;
        for (Integer val : result) {
            assertEquals(expected[i++], val);
        }
    }

    @Test
    public void testPreOrderTraversal_EmptyTree() {
        List<Integer> result = tree.preOrderTraversal();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testPreOrderTraversal_SingleNode() {
        tree.insertLeft(null, 5);
        List<Integer> result = tree.preOrderTraversal();
        
        assertEquals(1, result.getSize());
        assertEquals(5, result.iterator().next());
    }

    @Test
    public void testPreOrderTraversal_ThreeNodes() {
        tree.insertLeft(null, 1);
        tree.insertLeft(tree.getRoot(), 2);
        tree.insertRight(tree.getRoot(), 3);
        
        List<Integer> result = tree.preOrderTraversal();
        assertEquals(3, result.getSize());
        
        // Pre-order: 1, 2, 3
        Integer[] expected = {1, 2, 3};
        int i = 0;
        for (Integer val : result) {
            assertEquals(expected[i++], val);
        }
    }

    @Test
    public void testPostOrderTraversal_EmptyTree() {
        List<Integer> result = tree.postOrderTraversal();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testPostOrderTraversal_SingleNode() {
        tree.insertLeft(null, 5);
        List<Integer> result = tree.postOrderTraversal();
        
        assertEquals(1, result.getSize());
        assertEquals(5, result.iterator().next());
    }

    @Test
    public void testPostOrderTraversal_ThreeNodes() {
        tree.insertLeft(null, 1);
        tree.insertLeft(tree.getRoot(), 2);
        tree.insertRight(tree.getRoot(), 3);
        
        List<Integer> result = tree.postOrderTraversal();
        assertEquals(3, result.getSize());
        
        // Post-order: 2, 3, 1
        Integer[] expected = {2, 3, 1};
        int i = 0;
        for (Integer val : result) {
            assertEquals(expected[i++], val);
        }
    }

    @Test
    public void testLevelOrderTraversal_EmptyTree() {
        List<Integer> result = tree.levelOrderTraversal();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testLevelOrderTraversal_SingleNode() {
        tree.insertLeft(null, 5);
        List<Integer> result = tree.levelOrderTraversal();
        
        assertEquals(1, result.getSize());
        assertEquals(5, result.iterator().next());
    }

    @Test
    public void testLevelOrderTraversal_ThreeNodes() {
        tree.insertLeft(null, 1);
        tree.insertLeft(tree.getRoot(), 2);
        tree.insertRight(tree.getRoot(), 3);
        
        List<Integer> result = tree.levelOrderTraversal();
        assertEquals(3, result.getSize());
        
        // Level-order: 1, 2, 3
        Integer[] expected = {1, 2, 3};
        int i = 0;
        for (Integer val : result) {
            assertEquals(expected[i++], val);
        }
    }

    @Test
    public void testLevelOrderTraversal_LeftSkewedTree() {
        // Build tree:
        //       1
        //      /
        //     2
        tree.insertLeft(null, 1);
        tree.insertLeft(tree.getRoot(), 2);
        
        List<Integer> result = tree.levelOrderTraversal();
        assertEquals(2, result.getSize());
        
        // Level-order: 1, 2
        Integer[] expected = {1, 2};
        int i = 0;
        for (Integer val : result) {
            assertEquals(expected[i++], val);
        }
    }

    @Test
    public void testAllTraversals_RightSkewedTree() {
        // Build tree:
        //   1
        //    \
        //     2
        tree.insertLeft(null, 1);
        tree.insertRight(tree.getRoot(), 2);
        
        // In-order: 1, 2
        List<Integer> inOrder = tree.inOrderTraversal();
        Integer[] expectedInOrder = {1, 2};
        int i = 0;
        for (Integer val : inOrder) {
            assertEquals(expectedInOrder[i++], val);
        }
        
        // Pre-order: 1, 2
        List<Integer> preOrder = tree.preOrderTraversal();
        Integer[] expectedPreOrder = {1, 2};
        i = 0;
        for (Integer val : preOrder) {
            assertEquals(expectedPreOrder[i++], val);
        }
        
        // Post-order: 2, 1
        List<Integer> postOrder = tree.postOrderTraversal();
        Integer[] expectedPostOrder = {2, 1};
        i = 0;
        for (Integer val : postOrder) {
            assertEquals(expectedPostOrder[i++], val);
        }
        
        // Level-order: 1, 2
        List<Integer> levelOrder = tree.levelOrderTraversal();
        Integer[] expectedLevelOrder = {1, 2};
        i = 0;
        for (Integer val : levelOrder) {
            assertEquals(expectedLevelOrder[i++], val);
        }
    }

    @Test
    public void testTreeSize() {
        assertEquals(0, tree.getSize());
        
        tree.insertLeft(null, 1);
        assertEquals(1, tree.getSize());
        
        tree.insertLeft(tree.getRoot(), 2);
        assertEquals(2, tree.getSize());
        
        tree.insertRight(tree.getRoot(), 3);
        assertEquals(3, tree.getSize());
    }
}
