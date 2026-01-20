package com.datastructures.algorithms;

public class BinaryTree<T extends Comparable<T>> {
    private int size;
    private TreeNode root;

    public void insertLeft(TreeNode node, T data) {
        TreeNode newNode = new TreeNode(data);

        if (node == null) {
            if (size > 0) {
                return;
            }

            this.root = newNode;
        } else {
            if (node.getLeft() != null) {
                return;
            }

            node.setLeft(newNode);
        }

        size++;
    }

    public TreeNode getRoot() {
        return this.root;
    }

    public void insertRight(TreeNode node, T data) {
        TreeNode newNode = new TreeNode(data);

        if (node == null) {
            if (size > 0) {
                return;
            }

            this.root = newNode;
        } else {
            if (node.getRight() != null) {
                return;
            }
            node.setRight(newNode);
        }

        size++;
    }

    public void removeLeft(TreeNode node) {
        if (size == 0) {
            return;
        }

        TreeNode position;
        if (node == null) {
            position = this.root;
            this.root = null;
        } else {
            position = node.getLeft();
            node.setLeft(null);
        }

        if (position != null) {
            int removedCount = countAndRemoveSubtree(position);
            size -= removedCount;
        }
    }

    public void removeRight(TreeNode node) {
        if (size == 0) {
            return;
        }

        TreeNode position;
        if (node == null) {
            position = this.root;
            this.root = null;
        } else {
            position = node.getRight();
            node.setRight(null);
        }

        if (position != null) {
            int removedCount = countAndRemoveSubtree(position);
            size -= removedCount;
        }
    }

    /**
     * Helper method to count and remove all nodes in a subtree.
     * Returns the number of nodes removed.
     */
    private int countAndRemoveSubtree(TreeNode node) {
        if (node == null) {
            return 0;
        }
        
        int count = 1; // Count this node
        count += countAndRemoveSubtree(node.getLeft());
        count += countAndRemoveSubtree(node.getRight());
        
        return count;
    }

    public static <T extends Comparable<T>> void mergeTree(BinaryTree<T> merge, BinaryTree<T> left, BinaryTree<T> right, T data) {
        merge.root = merge.new TreeNode(data);

        merge.root.setLeft(left.root);
        merge.root.setRight(right.root);

        merge.size = left.size + right.size + 1;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isLeaf(TreeNode node) {
        return node.getLeft() == null && node.getRight() == null;
    }

    /**
     * Performs an in-order traversal of the tree (left, root, right).
     * @return list of elements in in-order sequence
     */
    public List<T> inOrderTraversal() {
        List<T> result = new List<>();
        inOrderHelper(root, result);
        return result;
    }

    private void inOrderHelper(TreeNode node, List<T> result) {
        if (node == null) {
            return;
        }
        inOrderHelper(node.getLeft(), result);
        result.insertNext(node.getData());
        inOrderHelper(node.getRight(), result);
    }

    /**
     * Performs a pre-order traversal of the tree (root, left, right).
     * @return list of elements in pre-order sequence
     */
    public List<T> preOrderTraversal() {
        List<T> result = new List<>();
        preOrderHelper(root, result);
        return result;
    }

    private void preOrderHelper(TreeNode node, List<T> result) {
        if (node == null) {
            return;
        }
        result.insertNext(node.getData());
        preOrderHelper(node.getLeft(), result);
        preOrderHelper(node.getRight(), result);
    }

    /**
     * Performs a post-order traversal of the tree (left, right, root).
     * @return list of elements in post-order sequence
     */
    public List<T> postOrderTraversal() {
        List<T> result = new List<>();
        postOrderHelper(root, result);
        return result;
    }

    private void postOrderHelper(TreeNode node, List<T> result) {
        if (node == null) {
            return;
        }
        postOrderHelper(node.getLeft(), result);
        postOrderHelper(node.getRight(), result);
        result.insertNext(node.getData());
    }

    /**
     * Performs a level-order traversal of the tree (breadth-first).
     * @return list of elements in level-order sequence
     */
    public List<T> levelOrderTraversal() {
        List<T> result = new List<>();
        if (root == null) {
            return result;
        }

        Queue<TreeNode> queue = new Queue<>();
        queue.enqueue(root);

        while (!queue.isEmpty()) {
            TreeNode current = queue.dequeue();
            result.insertNext(current.getData());

            if (current.getLeft() != null) {
                queue.enqueue(current.getLeft());
            }
            if (current.getRight() != null) {
                queue.enqueue(current.getRight());
            }
        }

        return result;
    }

    private class TreeNode {
        private T data;
        private TreeNode left;
        private TreeNode right;
        
        TreeNode(T data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }

        public T getData() {
            return data;
        }

        public TreeNode getLeft() {
            return left;
        }

        public TreeNode getRight() {
            return right;
        }

        public void setLeft(TreeNode left) {
            this.left = left;
        }

        public void setRight(TreeNode right) {
            this.right = right;
        }
    }
}
