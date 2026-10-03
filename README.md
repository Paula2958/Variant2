# Big-O Complexity — Variant 2

## Scaling Limits in Practice

This project explores the practical scaling limits of algorithms with
different time complexities under a fixed execution-time budget.

Three complexity classes are evaluated:

| Experiment | Complexity |
|---|---|
| Linear Workload | O(n) |
| Merge Sort | O(n log n) |
| Bubble Sort | O(n²) |

## Objective

The objective is to determine the largest tested input size that each
algorithm can process within a **5-second time budget**.

Execution time is measured using Java's `System.nanoTime()`.

The experiments progressively increase the input size until the
execution time exceeds the defined budget.

## Implementation

The experiments are implemented in:

`Variant2Benchmark.java`

The benchmark includes:

- JVM warm-up executions
- Execution-time measurement with `System.nanoTime()`
- Progressive input scaling
- A fixed 5-second time budget
- Deterministic synthetic datasets for the sorting experiments
- Input generation outside the timed section
- Detection of the largest tested feasible input
- Detection of the first tested input that exceeds the budget

## Experimental Results

| Experiment | Complexity | Largest tested feasible n | Time |
|---|---|---:|---:|
| Linear Workload | O(n) | 2,250,000,000 | 4.574 s |
| Merge Sort | O(n log n) | 32,000,000 | 4.570 s |
| Bubble Sort | O(n²) | 90,000 | 3.759 s |

The measured limits are specific to the tested environment and should
not be interpreted as universal limits for these complexity classes.

## Running the Benchmark

Compile:

```bash
javac Variant2Benchmark.java
