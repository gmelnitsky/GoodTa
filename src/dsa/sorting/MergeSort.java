package dsa.sorting;

import java.util.Arrays;
import java.util.Random;

/**
 * Merge sort  —  YOUR job: implement sort(int[]) so it sorts ascending.
 *
 * Reminder of the idea (divide and conquer):
 *   1. DIVIDE  the array into two halves.
 *   2. CONQUER by recursively sorting each half.
 *   3. COMBINE by merging the two sorted halves back together.
 *
 * Run with:  ./run.sh
 * The main() method below runs a self-test and tells you pass/fail.
 */
public class MergeSort {

    /** Sorts the given array in ascending order. */
    public static void sort(int[] a) {
        // TODO: implement merge sort here.
        // Tip: a recursive helper sort(a, aux, lo, hi) plus a merge(...) is the
        // classic structure, but write it however you like.

        








    }

    // ------------------------------------------------------------------
    // Test harness — you don't need to edit below this line.
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        // 1) A small, visible example.
        int[] demo = {5, 2, 9, 1, 5, 6, 3, 8, 0, 7};
        System.out.println("before: " + Arrays.toString(demo));
        sort(demo);
        System.out.println("after:  " + Arrays.toString(demo));

        // 2) Randomized self-test against Java's built-in sort.
        Random rng = new Random(42); // fixed seed -> reproducible runs
        int trials = 1000;
        for (int t = 0; t < trials; t++) {
            int n = rng.nextInt(50);
            int[] mine = new int[n];
            for (int k = 0; k < n; k++) {
                mine[k] = rng.nextInt(200) - 100; // values in [-100, 99]
            }
            int[] expected = mine.clone();
            Arrays.sort(expected); // trusted reference result

            sort(mine);
            if (!Arrays.equals(mine, expected)) {
                System.out.println("FAILED on trial " + t);
                System.out.println("  got:      " + Arrays.toString(mine));
                System.out.println("  expected: " + Arrays.toString(expected));
                return;
            }
        }
        System.out.println("self-test: all " + trials + " random trials passed");
    }
}
