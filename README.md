# Data Structures for Java

A library of common data structures and algorithms implemented in Java 21, exploring both sequential and parallel approaches.

## Algorithms

### Merge Sort

Three implementations of merge sort demonstrating different concurrency models:

#### 1. MergeSort (Sequential)
Classic sequential merge sort implementation.

- **Time Complexity**: O(n log n)
- **Space Complexity**: O(n)
- **Best for**: Small to medium datasets, single-threaded environments

**Pseudocode:**
```
MERGE-SORT(A, p, r)
  if p < r
      q = (p + r) / 2
      MERGE-SORT(A, p, q)
      MERGE-SORT(A, q+1, r)
      MERGE(A, p, q, r)
```

#### 2. ThreadedMergeSort (Fork/Join Framework)
Parallel implementation using Java's Fork/Join framework with platform threads.

- **Time Complexity**: O(n log n)
- **Space Complexity**: O(n)
- **Best for**: Large datasets, CPU-bound tasks with recursive divide-and-conquer
- **Threshold**: 10,000 elements (switches to sequential below this)

**Pseudocode:**
```
THREADED-MERGE-SORT(A, p, r, threshold)
  if r - p + 1 <= threshold
      SEQUENTIAL-MERGE-SORT(A, p, r)
  else if p < r
      q = (p + r) / 2
      left_task = ASYNC THREADED-MERGE-SORT(A, p, q, threshold)
      right_task = ASYNC THREADED-MERGE-SORT(A, q+1, r, threshold)
      WAIT(left_task)
      WAIT(right_task)
      MERGE(A, p, q, r)
```

**Performance Results (1M elements, 12 cores):**
- Sequential: 125ms
- ThreadedMergeSort: 36ms
- **Speedup:  3. 47x**

#### 3. VirtualThreadedMergeSort (Virtual Threads - Java 21)
Parallel implementation using Java's virtual threads (Project Loom) with structured concurrency.

- **Time Complexity**: O(n log n)
- **Space Complexity**: O(n)
- **Best for**: High concurrency scenarios, simpler code than Fork/Join
- **Threshold**:  10,000 elements (switches to sequential below this)
- **Virtual Threads**: Lightweight (~1KB), JVM-managed, millions can run concurrently

**Pseudocode:**
```
VIRTUAL-THREADED-MERGE-SORT(A, p, r, threshold)
  if r - p + 1 <= threshold
      SEQUENTIAL-MERGE-SORT(A, p, r)
  else if p < r
      q = (p + r) / 2
      scope = CREATE-STRUCTURED-SCOPE()
      left_task = scope.FORK(() -> VIRTUAL-THREADED-MERGE-SORT(A, p, q, threshold))
      right_task = scope.FORK(() -> VIRTUAL-THREADED-MERGE-SORT(A, q+1, r, threshold))
      scope.JOIN()
      scope. THROW-IF-FAILED()
      MERGE(A, p, q, r)
```

**Performance Results (1M elements, 12 cores):**
- Sequential: 225ms
- VirtualThreadedMergeSort: 49ms
- **Speedup:  4.59x**

### Performance Comparison

| Implementation | Time (1M elements) | Speedup | Technology |
|----------------|-------------------|---------|------------|
| Sequential | 125-225ms | 1.0x | Single thread |
| Fork/Join | 36ms | 3.47x | Platform threads, work-stealing |
| Virtual Threads | 49ms | 4.59x | Virtual threads, structured concurrency |

**Key Findings:**
- Both parallel implementations provide significant speedup over sequential
- Virtual threads offer simpler, more readable code than Fork/Join
- Optimal threshold:  5,000-10,000 elements to balance parallelism vs overhead
- Below 200K elements, overhead dominates and sequential may be faster

## Data Structures

### Binary Tree

A generic binary tree implementation with support for various tree traversal algorithms. 

**Structure:**
- **Time Complexity (Insert/Remove)**: O(1) for direct node operations
- **Space Complexity**: O(n) where n is the number of nodes
- **Best for**: Building custom tree structures, hierarchical data representation
- **Operations**: `insertLeft(node, data)`, `insertRight(node, data)`, `removeLeft(node)`, `removeRight(node)`, `mergeTree(merge, left, right, data)`

**Basic Operations Pseudocode:**
```
TREE-INSERT-LEFT(T, node, data)
  newNode = CREATE-NODE(data)
  if node == null
      T.root = newNode
  else
      node.left = newNode
  T.size++

TREE-INSERT-RIGHT(T, node, data)
  newNode = CREATE-NODE(data)
  if node == null
      T.root = newNode
  else
      node.right = newNode
  T.size++

TREE-MERGE(merge, left, right, data)
  merge.root = CREATE-NODE(data)
  merge.root.left = left.root
  merge.root.right = right.root
  merge.size = left.size + right.size + 1
```

#### Tree Traversal Algorithms

The BinaryTree class implements four standard traversal algorithms:

##### 1. In-Order Traversal (Left, Root, Right)

Visits nodes in ascending order for binary search trees.

- **Time Complexity**: O(n)
- **Space Complexity**:  O(h) where h is tree height (recursion stack), O(n) for result list
- **Best for**: Getting sorted sequence from BST, expression tree evaluation
- **Order**: Left subtree → Root → Right subtree

**Pseudocode:**
```
IN-ORDER-TRAVERSAL(node, result)
  if node == null
      return
  IN-ORDER-TRAVERSAL(node.left, result)
  result.ADD(node.data)
  IN-ORDER-TRAVERSAL(node.right, result)
```

##### 2. Pre-Order Traversal (Root, Left, Right)

Visits root before its children.

- **Time Complexity**: O(n)
- **Space Complexity**: O(h) where h is tree height (recursion stack), O(n) for result list
- **Best for**: Copying trees, prefix expression evaluation, serialization
- **Order**: Root → Left subtree → Right subtree

**Pseudocode:**
```
PRE-ORDER-TRAVERSAL(node, result)
  if node == null
      return
  result.ADD(node.data)
  PRE-ORDER-TRAVERSAL(node.left, result)
  PRE-ORDER-TRAVERSAL(node.right, result)
```

##### 3. Post-Order Traversal (Left, Right, Root)

Visits children before root.

- **Time Complexity**: O(n)
- **Space Complexity**: O(h) where h is tree height (recursion stack), O(n) for result list
- **Best for**: Deleting trees, postfix expression evaluation, calculating tree properties
- **Order**: Left subtree → Right subtree → Root

**Pseudocode:**
```
POST-ORDER-TRAVERSAL(node, result)
  if node == null
      return
  POST-ORDER-TRAVERSAL(node.left, result)
  POST-ORDER-TRAVERSAL(node.right, result)
  result.ADD(node.data)
```

##### 4. Level-Order Traversal (Breadth-First)

Visits nodes level by level from top to bottom.

- **Time Complexity**: O(n)
- **Space Complexity**: O(w) where w is maximum width of tree (queue size), O(n) for result list
- **Best for**: Finding shortest path, level-by-level processing, serialization
- **Order**: All nodes at depth 0, then depth 1, then depth 2, etc.

**Pseudocode:**
```
LEVEL-ORDER-TRAVERSAL(root)
  if root == null
      return empty list
  result = CREATE-LIST()
  queue = CREATE-QUEUE()
  queue.ENQUEUE(root)
  
  while queue is not empty
      current = queue.DEQUEUE()
      result.ADD(current.data)
      
      if current. left != null
          queue. ENQUEUE(current.left)
      if current.right != null
          queue.ENQUEUE(current.right)
  
  return result
```

**Traversal Comparison:**

| Traversal | Order | Recursion | Best Use Case |
|-----------|-------|-----------|---------------|
| In-Order | L-Root-R | Yes | BST sorting, expression evaluation |
| Pre-Order | Root-L-R | Yes | Tree copying, serialization |
| Post-Order | L-R-Root | Yes | Tree deletion, calculating sizes |
| Level-Order | By levels | No (uses queue) | Shortest paths, level processing |

### Hash Tables

Three implementations demonstrating chaining for collision resolution and thread-safe concurrent access: 

#### 1. ChainedHashTable<T> (Set-like Collection)
Hash table storing values with separate chaining for collision resolution.

- **Time Complexity**: O(1) average for insert/lookup/remove, O(n) worst case
- **Space Complexity**:  O(n + m) where n is elements, m is buckets
- **Best for**: Set operations, membership testing
- **Operations**: `insert(T)`, `lookup(T)`, `remove(T)`

**Pseudocode:**
```
HASH-INSERT(T, data)
  index = HASH(data) mod T.buckets
  LIST-INSERT(T.table[index], data)
  T.size++

HASH-LOOKUP(T, data)
  index = HASH(data) mod T.buckets
  return LIST-SEARCH(T.table[index], data)

HASH-REMOVE(T, data)
  index = HASH(data) mod T.buckets
  if LIST-REMOVE(T.table[index], data)
      T.size--
      return true
  return false
```

#### 2. ChainedHashMap<K, V> (Key-Value Map)
True hash map storing key-value pairs using Entry objects. 

- **Time Complexity**: O(1) average for put/get/remove, O(n) worst case
- **Space Complexity**: O(n + m) where n is entries, m is buckets
- **Best for**: Dictionary operations, key-value associations
- **Operations**: `put(K, V)`, `get(K)`, `remove(K)`, `containsKey(K)`
- **Features**: Updates existing values on duplicate keys, tracks size correctly

**Pseudocode:**
```
HASH-PUT(M, key, value)
  index = HASH(key) mod M.buckets
  entry = LIST-SEARCH(M.table[index], key)
  if entry != null
      oldValue = entry.value
      entry.value = value
      return oldValue
  else
      newEntry = CREATE-ENTRY(key, value)
      LIST-INSERT(M.table[index], newEntry)
      M.size++
      return null

HASH-GET(M, key)
  index = HASH(key) mod M.buckets
  entry = LIST-SEARCH(M. table[index], key)
  if entry != null
      return entry.value
  return null

HASH-REMOVE(M, key)
  index = HASH(key) mod M.buckets
  if LIST-REMOVE(M.table[index], key)
      M.size--
      return true
  return false
```

#### 3. ThreadedChainedHashTable<T> (Thread-Safe)
Thread-safe hash table with fine-grained locking for high concurrency.

- **Locking Strategy**: One ReadWriteLock per bucket
- **Concurrency**: Operations on different buckets execute in parallel
- **Read Operations**: Multiple threads can read same bucket simultaneously
- **Write Operations**:  Exclusive access per bucket, synchronized size counter

**Pseudocode:**
```
THREADED-HASH-INSERT(T, data)
  index = HASH(data) mod T.buckets
  ACQUIRE-WRITE-LOCK(T.locks[index])
  try
      LIST-INSERT(T. table[index], data)
      SYNCHRONIZED(T)
          T.size++
  finally
      RELEASE-WRITE-LOCK(T.locks[index])

THREADED-HASH-LOOKUP(T, data)
  index = HASH(data) mod T.buckets
  ACQUIRE-READ-LOCK(T.locks[index])
  try
      return LIST-SEARCH(T.table[index], data)
  finally
      RELEASE-READ-LOCK(T.locks[index])

THREADED-HASH-REMOVE(T, data)
  index = HASH(data) mod T.buckets
  ACQUIRE-WRITE-LOCK(T.locks[index])
  try
      if LIST-REMOVE(T. table[index], data)
          SYNCHRONIZED(T)
              T.size--
          return true
      return false
  finally
      RELEASE-WRITE-LOCK(T.locks[index])
```

**Concurrency Issues Demonstrated:**
- **Race Conditions**: Without synchronization, size counter loses updates
- **Lock Contention**: All threads on same bucket causes 41x slowdown
- **Fine-Grained Locking**: Different buckets achieve near-linear speedup
- **Deadlock Prevention**: Lock ordering strategy documented
- **Thread Starvation**: Fair vs unfair lock policies discussed

### Performance Results (10,000 operations)

**Sequential vs Threaded (10 threads, 12 cores):**
- Sequential: 7ms
- Threaded (platform threads): 3ms
- **Speedup: 2.33x**

**Platform Threads vs Virtual Threads (100 concurrent tasks):**
- Platform thread pool (10 threads): 13ms
- Virtual threads (100 threads): 8ms
- **Virtual threads 1.6x faster** due to lower overhead

**Lock Contention Analysis:**
| Scenario | Time | Description |
|----------|------|-------------|
| Different buckets | 2ms | Operations on different buckets (high parallelism) |
| Same bucket | 82ms | All operations on one bucket (lock contention) |
| **Slowdown** | **41x** | Impact of lock contention on same bucket |

**Key Findings:**
- Fine-grained locking (per-bucket) enables true parallel execution
- Lock contention on same bucket causes dramatic performance degradation (41x)
- Virtual threads show lower overhead than platform threads for concurrent operations
- Thread-safe size counter maintains accuracy with 10,000 concurrent inserts
- ReadWriteLock allows multiple concurrent readers without blocking

## Building

```bash
mvn clean install
```

## Testing

```bash
mvn test
```

## Requirements

- Java 21 or higher (for virtual threads support)
- Maven 3.6 or higher
