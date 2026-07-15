# Merge Sort

**Your code:** [`src/dsa/sorting/MergeSort.java`](../src/dsa/sorting/MergeSort.java)
**Run / test it:** `./run.sh`

> The file has a stub `sort()` for you to fill in. When you run it, a built-in
> self-test checks your result against Java's `Arrays.sort` over 1000 random
> arrays and prints pass/fail. Implement, run, repeat.

---

## The idea: divide and conquer

Merge sort sorts an array by breaking the problem into smaller copies of itself:

1. **Divide** — split the array into two halves.
2. **Conquer** — recursively sort each half.
3. **Combine** — *merge* the two sorted halves into one sorted array.

The base case is a sub-array of length 0 or 1 — already sorted, so recursion stops.

```
            [5 2 9 1 | 5 6 3 8]
           /                   \
      [5 2 | 9 1]          [5 6 | 3 8]
       /       \            /       \
   [5|2]     [9|1]      [5|6]     [3|8]
    \ /       \ /        \ /       \ /
   [2 5]     [1 9]      [5 6]     [3 8]      <- sorted pairs
       \      /             \      /
      [1 2 5 9]            [3 5 6 8]         <- merge
            \                 /
        [1 2 3 5 5 6 8 9]                    <- final merge
```

## The part to think hardest about: the merge

You'll be given two halves that are *each already sorted*. How do you combine
them into one sorted run in a single linear pass? A few questions to guide you:

- If you walk a pointer down each half, which element do you take next?
- What happens when one half runs out before the other?
- When the two front elements are equal, which do you take — and why might
  that choice matter? (Hint: look up "stable sort".)

## Complexity (good to reason about before you code)

| | |
|---|---|
| Time (best / avg / worst) | **O(n log n)** |
| Space | **O(n)** scratch buffer |
| Stable | Yes (if you break ties correctly) |
| In-place | No |

Why `n log n`? There are about `log n` levels of splitting, and each level does
O(n) total work to merge.

## Once it passes, try these

- Print the array at each merge to *watch* the divide-and-conquer order.
- Make it generic: sort `Comparable[]` instead of `int[]`.
- Write a **bottom-up** (iterative) version with no recursion.
- Switch to insertion sort for tiny sub-arrays and measure the speedup.
