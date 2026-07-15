package dsa.basics;

import java.util.ArrayDeque;
import java.util.Random;

/**
 * Stack & Queue  —  PRACTICE.  YOUR job: implement the TODO methods yourself.
 *
 * You already understand these conceptually:
 *   STACK = LIFO (Last In, First Out)   — push / pop / peek
 *   QUEUE = FIFO (First In, First Out)   — enqueue / dequeue / peek
 *
 * Now code them. The self-test in main() checks your work against Java's own
 * trusted structures and prints pass/fail. Suggested order:
 *   1. IntStack  (easier — you only ever touch the END of the array)
 *   2. IntQueue  (a little trickier — a "circular" array, see its hints)
 *
 * If you get truly stuck, the FINISHED version lives in StackAndQueue.java in
 * this same folder — but try it yourself first; that's where the learning is.
 *
 * Run with:  ./run.sh dsa.basics.StackQueuePractice
 */
public class StackQueuePractice {

    // ==================================================================
    // 1. STACK (LIFO). Array-backed. The "top" is the LAST used slot.
    //    Given to you for free: the array, size, isEmpty/size, and grow().
    //    You implement: push, pop, peek.
    // ==================================================================
    static class IntStack {
        private int[] data = new int[8];
        private int size = 0; // number of elements; top lives at index size-1

        /** Put a value ON TOP of the stack. */
        void push(int value) {
            if (size == data.length) grow(); // make room first (free helper)
            data[size] = value;
            size++;
        }

        /** Remove and return the TOP value (the most recently pushed). */
        int pop() {
            if (isEmpty()) throw new IllegalStateException("stack is empty");
            int send = data[size - 1];
            size--;
            return send;
        }

        /** Look at the top value WITHOUT removing it. */
        int peek() {
            if (isEmpty()) throw new IllegalStateException("stack is empty");
            return data[size - 1];
        }

        // ---- given to you for free ----
        boolean isEmpty() { return size == 0; }
        int size() { return size; }
        private void grow() {
            int[] bigger = new int[data.length * 2];
            System.arraycopy(data, 0, bigger, 0, data.length);
            data = bigger;
        }
    }

    // ==================================================================
    // 2. QUEUE (FIFO). A "circular" array so we can reuse freed slots
    //    instead of shifting everything down.
    //      - `head`  = index of the FRONT element (the next one to leave).
    //      - the BACK slot to write into is  (head + size) % data.length.
    //      - "% data.length" makes an index WRAP AROUND to 0 when it runs
    //        off the end — that's the whole "circular" trick.
    //    Given to you for free: the array, head, size, isEmpty/size, grow().
    //    You implement: enqueue, dequeue, peek.
    // ==================================================================
    static class IntQueue {
        private int[] data = new int[8];
        private int head = 0; // index of the front element
        private int size = 0; // number of elements currently stored

        /** Add a value to the BACK of the line. */
        void enqueue(int value) {
            if (size == data.length) grow(); // make room first (free helper)
            int tail = (head + size) % data.length;
            data[tail] = value;
            size++;
        }

        /** Remove and return the FRONT value (the one that has waited longest). */
        int dequeue() {
            if (isEmpty()) throw new IllegalStateException("queue is empty");
            int ret = data[head];
            head = (head + 1) % data.length;
            size--;
            return ret;
        }

        /** Look at the front value WITHOUT removing it. */
        int peek() {
            if (isEmpty()) throw new IllegalStateException("queue is empty");
            return data[head];
        }

        // ---- given to you for free ----
        boolean isEmpty() { return size == 0; }
        int size() { return size; }
        private void grow() {
            int[] bigger = new int[data.length * 2];
            for (int i = 0; i < size; i++) {
                bigger[i] = data[(head + i) % data.length]; // un-wrap into order
            }
            data = bigger;
            head = 0;
        }
    }

    // ------------------------------------------------------------------
    // Test harness — you don't need to edit below this line.
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        boolean stackOk = testStack();
        boolean queueOk = testQueue();

        System.out.println("------------------------------------------");
        if (stackOk && queueOk) {
            System.out.println("ALL PASS — nicely done. You coded a stack and a queue. 🎉");
        } else {
            System.out.println("Not done yet:");
            if (!stackOk) System.out.println("  - IntStack: implement push / pop / peek");
            if (!queueOk) System.out.println("  - IntQueue: implement enqueue / dequeue / peek");
        }
    }

    /** Returns true if the stack passes; false (with a message) otherwise. */
    private static boolean testStack() {
        System.out.println("=== testing IntStack (LIFO) ===");
        try {
            Random rng = new Random(1);
            for (int t = 0; t < 1000; t++) {
                IntStack mine = new IntStack();
                ArrayDeque<Integer> ref = new ArrayDeque<>(); // used as a stack
                int ops = rng.nextInt(60);
                for (int k = 0; k < ops; k++) {
                    if (ref.isEmpty() || rng.nextBoolean()) {
                        int v = rng.nextInt(1000);
                        mine.push(v);
                        ref.push(v); // ArrayDeque.push = add to head (LIFO)
                    } else {
                        if (mine.peek() != ref.peek()) return report("stack", "peek", mine.peek(), ref.peek());
                        int got = mine.pop(), want = ref.pop();
                        if (got != want) return report("stack", "pop", got, want);
                    }
                    if (mine.size() != ref.size()) return report("stack", "size", mine.size(), ref.size());
                }
            }
            System.out.println("  all 1000 random trials passed ✅\n");
            return true;
        } catch (UnsupportedOperationException e) {
            System.out.println("  not implemented yet (" + e.getMessage() + ")\n");
            return false;
        }
    }

    /** Returns true if the queue passes; false (with a message) otherwise. */
    private static boolean testQueue() {
        System.out.println("=== testing IntQueue (FIFO) ===");
        try {
            Random rng = new Random(2);
            for (int t = 0; t < 1000; t++) {
                IntQueue mine = new IntQueue();
                ArrayDeque<Integer> ref = new ArrayDeque<>(); // used as a queue
                int ops = rng.nextInt(60);
                for (int k = 0; k < ops; k++) {
                    if (ref.isEmpty() || rng.nextBoolean()) {
                        int v = rng.nextInt(1000);
                        mine.enqueue(v);
                        ref.addLast(v); // add to back
                    } else {
                        if (mine.peek() != ref.peekFirst()) return report("queue", "peek", mine.peek(), ref.peekFirst());
                        int got = mine.dequeue(), want = ref.removeFirst(); // remove from front
                        if (got != want) return report("queue", "dequeue", got, want);
                    }
                    if (mine.size() != ref.size()) return report("queue", "size", mine.size(), ref.size());
                }
            }
            System.out.println("  all 1000 random trials passed ✅\n");
            return true;
        } catch (UnsupportedOperationException e) {
            System.out.println("  not implemented yet (" + e.getMessage() + ")\n");
            return false;
        }
    }

    private static boolean report(String which, String op, int got, int expected) {
        System.out.println("  FAILED " + which + " during " + op
                + ": got " + got + ", expected " + expected + "\n");
        return false;
    }
}
