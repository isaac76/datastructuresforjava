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
- **Speedup: 3.47x**

#### 3. VirtualThreadedMergeSort (Virtual Threads - Java 21)
Parallel implementation using Java's virtual threads (Project Loom) with structured concurrency.

- **Time Complexity**: O(n log n)
- **Space Complexity**: O(n)
- **Best for**: High concurrency scenarios, simpler code than Fork/Join
- **Threshold**: 10,000 elements (switches to sequential below this)
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
      scope.THROW-IF-FAILED()
      MERGE(A, p, q, r)
```

**Performance Results (1M elements, 12 cores):**
- Sequential: 225ms
- VirtualThreadedMergeSort: 49ms
- **Speedup: 4.59x**

### Performance Comparison

| Implementation | Time (1M elements) | Speedup | Technology |
|----------------|-------------------|---------|------------|
| Sequential | 125-225ms | 1.0x | Single thread |
| Fork/Join | 36ms | 3.47x | Platform threads, work-stealing |
| Virtual Threads | 49ms | 4.59x | Virtual threads, structured concurrency |

**Key Findings:**
- Both parallel implementations provide significant speedup over sequential
- Virtual threads offer simpler, more readable code than Fork/Join
- Optimal threshold: 5,000-10,000 elements to balance parallelism vs overhead
- Below 200K elements, overhead dominates and sequential may be faster

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
