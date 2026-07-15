package dsa.heap;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Min-heap priority queue  —  YOUR job: implement insert / peek / poll so the
 * smallest value is always the one that comes out first.
 *
 * The idea (array-backed complete binary tree):
 *   - The tree lives in an array. This one is 1-indexed: the root is at index 1,
 *     and for a node at index i:
 *        parent(i) = i / 2
 *        left(i)   = 2 * i
 *        right(i)  = 2 * i + 1
 *   - HEAP INVARIANT (min-heap): every parent is <= both of its children.
 *     That means heap[1] is always the minimum.
 *
 * Two helper moves keep the invariant true:
 *   - bubbleUp(i)   : a node that is too SMALL for its spot moves UP toward the root,
 *                     swapping with its parent. Used after insert, when the new value
 *                     lands at the bottom and may need to climb.
 *   - bubbleDown(i) : a node that is too BIG for its spot moves DOWN toward the leaves,
 *                     swapping with its smaller child. Used after poll, when the last
 *                     value is moved up to the root and may need to descend.
 *
 * Run with:  ./run.sh dsa.heap.MinHeap
 * The main() method below runs a self-test and tells you pass/fail.
 */
public class MinHeap {

    // 1-indexed storage. heap[0] is intentionally unused so the parent/child
    // index math above stays clean. size = number of elements currently in the heap.
    private int[] heap;
    private int size;

    public MinHeap() {
        heap = new int[16]; // starts at index 1, so capacity for 15 elements
        size = 0;
    }

    /** How many elements are in the heap. */
    public int size() {
        return size;
    }

    /** True when the heap has no elements. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Adds a value to the heap. */
    public void insert(int value) {
        // TODO:
        //   1. Make sure there's room (call ensureCapacity(size + 1)).
        //   2. Place `value` at the next open slot (index size + 1) and grow size.
        //   3. bubbleUp() that new node until the heap invariant holds again.
        throw new UnsupportedOperationException("insert not implemented yet");
    }

    /** Returns the smallest value WITHOUT removing it. */
    public int peek() {
        // TODO: the minimum of a min-heap always lives at the root (index 1).
        //       Throw if the heap is empty (see the guard style in poll()).
        throw new UnsupportedOperationException("peek not implemented yet");
    }

    /** Removes and returns the smallest value. */
    public int poll() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException("heap is empty");
        }
        // TODO:
        //   1. Remember the root (heap[1]) — that's the min you'll return.
        //   2. Move the LAST element (heap[size]) into the root, then shrink size.
        //   3. bubbleDown() the new root until the heap invariant holds again.
        //   4. Return the min you saved in step 1.
        throw new UnsupportedOperationException("poll not implemented yet");
    }

    // ------------------------------------------------------------------
    // Helper moves — you implement these; insert()/poll() lean on them.
    // (These used to be called "swim" and "sink" — same idea, clearer names.)
    // ------------------------------------------------------------------

    /** Move the node at index i UP while it is smaller than its parent. */
    private void bubbleUp(int i) {
        // TODO: while i > 1 and heap[i] < heap[parent], swap them and move up.
        throw new UnsupportedOperationException("bubbleUp not implemented yet");
    }

    /** Move the node at index i DOWN while it is larger than its smaller child. */
    private void bubbleDown(int i) {
        // TODO:
        //   - Find i's children (2i and 2i+1), staying within `size`.
        //   - Pick the SMALLER child. If the node is <= that child, stop.
        //   - Otherwise swap with the smaller child and keep going down.
        throw new UnsupportedOperationException("bubbleDown not implemented yet");
    }

    // ------------------------------------------------------------------
    // Plumbing you can use for free (array growth + swap). Not the interesting
    // part of a heap — focus your energy on insert/peek/poll/bubbleUp/bubbleDown above.
    // ------------------------------------------------------------------

    /** Grows the backing array if it can't hold `needed` elements (1-indexed). */
    private void ensureCapacity(int needed) {
        if (needed >= heap.length) {
            heap = Arrays.copyOf(heap, Math.max(heap.length * 2, needed + 1));
        }
    }

    /** Swaps the elements at indices i and j. */
    private void swap(int i, int j) {
        int tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    // ------------------------------------------------------------------
    // Test harness — you don't need to edit below this line.
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        // 1) A small, visible example: insert a few values, drain them out.
        MinHeap demo = new MinHeap();
        int[] toAdd = {5, 2, 9, 1, 5, 6, 3, 8, 0, 7};
        System.out.println("inserting: " + Arrays.toString(toAdd));
        for (int v : toAdd) {
            demo.insert(v);
        }
        StringBuilder drained = new StringBuilder("[");
        while (!demo.isEmpty()) {
            drained.append(demo.poll());
            if (!demo.isEmpty()) drained.append(", ");
        }
        drained.append("]");
        System.out.println("drained:   " + drained + "   (should come out sorted ascending)");

        // 2) Randomized self-test against java.util.PriorityQueue (a trusted min-heap).
        Random rng = new Random(42); // fixed seed -> reproducible runs
        int trials = 1000;
        for (int t = 0; t < trials; t++) {
            MinHeap mine = new MinHeap();
            PriorityQueue<Integer> reference = new PriorityQueue<>();

            // Interleave a random mix of inserts and polls, checking they agree.
            int ops = rng.nextInt(60);
            for (int k = 0; k < ops; k++) {
                boolean doInsert = reference.isEmpty() || rng.nextBoolean();
                if (doInsert) {
                    int value = rng.nextInt(200) - 100; // values in [-100, 99]
                    mine.insert(value);
                    reference.add(value);
                } else {
                    // peek and poll should both match the reference's minimum.
                    int expectedMin = reference.peek();
                    if (mine.peek() != expectedMin) {
                        fail(t, "peek", mine.peek(), expectedMin);
                        return;
                    }
                    int got = mine.poll();
                    int want = reference.poll();
                    if (got != want) {
                        fail(t, "poll", got, want);
                        return;
                    }
                }
                if (mine.size() != reference.size()) {
                    fail(t, "size", mine.size(), reference.size());
                    return;
                }
            }

            // Drain whatever's left; it must come out in ascending order.
            int prev = Integer.MIN_VALUE;
            while (!reference.isEmpty()) {
                int got = mine.poll();
                int want = reference.poll();
                if (got != want || got < prev) {
                    fail(t, "drain", got, want);
                    return;
                }
                prev = got;
            }
            if (!mine.isEmpty()) {
                System.out.println("FAILED on trial " + t + ": heap not empty after draining");
                return;
            }
        }
        System.out.println("self-test: all " + trials + " random trials passed");
    }

    private static void fail(int trial, String op, int got, int expected) {
        System.out.println("FAILED on trial " + trial + " during " + op);
        System.out.println("  got:      " + got);
        System.out.println("  expected: " + expected);
    }
}
