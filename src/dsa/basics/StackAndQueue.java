package dsa.basics;

import java.util.Scanner;

/**
 * Stack vs Queue  —  a runnable playground to SEE the difference.
 *
 * Both are "add things, take things out" containers. The ONLY difference is the
 * order they hand things back:
 *
 *   STACK  = LIFO  (Last In, First Out).   Like a stack of plates: you take off
 *            the one you put on most recently. Operations: push / pop.
 *
 *   QUEUE  = FIFO  (First In, First Out).   Like a line at a store: whoever got
 *            in line first is served first. Operations: enqueue / dequeue.
 *
 * This file has:
 *   1. Small, fully-working IntStack and IntQueue classes (read them — they're
 *      short and commented, so you can see how each one stores its data).
 *   2. A narrated demo that runs automatically.
 *   3. Two practical examples (a bracket checker on a stack, a print queue).
 *   4. An INTERACTIVE mode: type numbers and watch a stack and a queue react
 *      side by side. This is the part to play with.
 *
 * Run with:  ./run.sh dsa.basics.StackAndQueue
 */
public class StackAndQueue {

    // ==================================================================
    // 1a. STACK  (LIFO) — array-backed. We always add/remove at the END
    //     of the array, because that's the cheap end to grow and shrink.
    // ==================================================================
    static class IntStack {
        private int[] data = new int[8];
        private int size = 0; // number of elements; the "top" is at index size-1

        /** Put a value ON TOP of the stack. */
        void push(int value) {
            if (size == data.length) grow();
            data[size] = value; // write at the end...
            size++;             // ...and that end is now the new top
        }

        /** Remove and return the TOP value (the most recently pushed). */
        int pop() {
            if (isEmpty()) throw new IllegalStateException("stack is empty");
            size--;             // shrink first...
            return data[size];  // ...then the old top is what we return
        }

        /** Look at the top without removing it. */
        int peek() {
            if (isEmpty()) throw new IllegalStateException("stack is empty");
            return data[size - 1];
        }

        boolean isEmpty() { return size == 0; }
        int size() { return size; }

        private void grow() {
            int[] bigger = new int[data.length * 2];
            System.arraycopy(data, 0, bigger, 0, data.length);
            data = bigger;
        }

        /** Draw the stack with the TOP on the left, marked. */
        @Override public String toString() {
            StringBuilder sb = new StringBuilder("top -> [");
            for (int i = size - 1; i >= 0; i--) { // walk top-to-bottom
                sb.append(data[i]);
                if (i > 0) sb.append(", ");
            }
            return sb.append("]").toString();
        }
    }

    // ==================================================================
    // 1b. QUEUE (FIFO) — a "circular" array. We add at the tail and remove
    //     from the head. Both indices march forward and WRAP AROUND to 0
    //     when they run off the end, so we reuse freed slots instead of
    //     shifting everything down every time (which would be slow).
    // ==================================================================
    static class IntQueue {
        private int[] data = new int[8];
        private int head = 0;  // index of the front element (next to leave)
        private int size = 0;  // number of elements currently stored

        /** Add a value to the BACK of the line. */
        void enqueue(int value) {
            if (size == data.length) grow();
            int tail = (head + size) % data.length; // wrap around with modulo
            data[tail] = value;
            size++;
        }

        /** Remove and return the FRONT value (the one waiting longest). */
        int dequeue() {
            if (isEmpty()) throw new IllegalStateException("queue is empty");
            int value = data[head];
            head = (head + 1) % data.length; // front marches forward, wrapping
            size--;
            return value;
        }

        /** Look at the front without removing it. */
        int peek() {
            if (isEmpty()) throw new IllegalStateException("queue is empty");
            return data[head];
        }

        boolean isEmpty() { return size == 0; }
        int size() { return size; }

        private void grow() {
            int[] bigger = new int[data.length * 2];
            for (int i = 0; i < size; i++) {        // copy in logical order,
                bigger[i] = data[(head + i) % data.length]; // un-wrapping it
            }
            data = bigger;
            head = 0; // reset so it's laid out simply again
        }

        /** Draw the queue with the FRONT on the left, marked. */
        @Override public String toString() {
            StringBuilder sb = new StringBuilder("front -> [");
            for (int i = 0; i < size; i++) { // walk front-to-back
                sb.append(data[(head + i) % data.length]);
                if (i < size - 1) sb.append(", ");
            }
            return sb.append("]").toString();
        }
    }

    // ==================================================================
    // 2. Narrated demo: push the SAME numbers into both, then take them
    //    all out, so you can watch LIFO vs FIFO happen.
    // ==================================================================
    private static void narratedDemo() {
        System.out.println("========== DEMO: same numbers in, different order out ==========");
        int[] nums = {10, 20, 30, 40};

        IntStack stack = new IntStack();
        IntQueue queue = new IntQueue();

        for (int n : nums) {
            stack.push(n);
            queue.enqueue(n);
            System.out.println("added " + n
                    + "   | stack " + stack + "   | queue " + queue);
        }

        System.out.println("\nNow take everything out of each:\n");
        System.out.println("STACK pops (LIFO — newest first):");
        while (!stack.isEmpty()) {
            System.out.println("   popped " + stack.pop() + "   remaining " + stack);
        }
        System.out.println("\nQUEUE dequeues (FIFO — oldest first):");
        while (!queue.isEmpty()) {
            System.out.println("   dequeued " + queue.dequeue() + "   remaining " + queue);
        }
        System.out.println("\nSame input {10,20,30,40}: stack gave 40,30,20,10 — queue gave 10,20,30,40.\n");
    }

    // ==================================================================
    // 3a. Practical STACK example: check that brackets are balanced.
    //     Every time we see an opener we PUSH it; every closer must match
    //     the most-recent opener — exactly what a stack's "top" gives us.
    // ==================================================================
    private static boolean bracketsBalanced(String s) {
        IntStack openers = new IntStack();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(' || c == '[' || c == '{') {
                openers.push(c); // char auto-converts to its int code
            } else if (c == ')' || c == ']' || c == '}') {
                if (openers.isEmpty()) return false;          // closer with no opener
                char open = (char) openers.pop();             // most recent opener
                if (!matches(open, c)) return false;          // wrong kind
            }
        }
        return openers.isEmpty(); // leftover openers => not balanced
    }

    private static boolean matches(char open, char close) {
        return (open == '(' && close == ')')
                || (open == '[' && close == ']')
                || (open == '{' && close == '}');
    }

    private static void bracketDemo() {
        System.out.println("========== PRACTICAL: stack checks balanced brackets ==========");
        String[] tests = {"(a[b]{c})", "([)]", "(((", "{[()]}", "]"};
        for (String t : tests) {
            System.out.printf("   %-12s -> %s%n", t, bracketsBalanced(t) ? "balanced" : "NOT balanced");
        }
        System.out.println();
    }

    // ==================================================================
    // 3b. Practical QUEUE example: a print queue. Jobs come out in the
    //     exact order they were submitted — that's what FIFO guarantees.
    // ==================================================================
    private static void printQueueDemo() {
        System.out.println("========== PRACTICAL: queue runs print jobs in order ==========");
        IntQueue printer = new IntQueue();
        System.out.println("   Alice submits job 101, Bob 102, then Alice again 103.");
        printer.enqueue(101);
        printer.enqueue(102);
        printer.enqueue(103);
        while (!printer.isEmpty()) {
            System.out.println("   printing job #" + printer.dequeue());
        }
        System.out.println("   -> First submitted, first printed. Fair!\n");
    }

    // ==================================================================
    // 4. INTERACTIVE playground. Feeds your input to a stack AND a queue
    //    at once so you can compare them live.
    // ==================================================================
    private static void interactive() {
        System.out.println("========== PLAYGROUND (type commands, watch both react) ==========");
        System.out.println("Commands:");
        System.out.println("   <number>   add it to BOTH the stack and the queue");
        System.out.println("   r          remove one from each (pop vs dequeue) and compare");
        System.out.println("   p          peek the next-out of each (without removing)");
        System.out.println("   q          quit");
        System.out.println("(If you're not in an interactive terminal, this section just ends.)\n");

        IntStack stack = new IntStack();
        IntQueue queue = new IntQueue();
        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            String tok = in.next().trim();

            if (tok.equalsIgnoreCase("q")) {
                System.out.println("bye!");
                return;
            } else if (tok.equalsIgnoreCase("r")) {
                if (stack.isEmpty()) {
                    System.out.println("   both are empty — add some numbers first");
                } else {
                    int fromStack = stack.pop();
                    int fromQueue = queue.dequeue();
                    System.out.println("   stack popped " + fromStack
                            + " (newest)   |   queue dequeued " + fromQueue + " (oldest)");
                }
            } else if (tok.equalsIgnoreCase("p")) {
                if (stack.isEmpty()) {
                    System.out.println("   both are empty");
                } else {
                    System.out.println("   next out -> stack: " + stack.peek()
                            + "   |   queue: " + queue.peek());
                }
            } else {
                try {
                    int value = Integer.parseInt(tok);
                    stack.push(value);
                    queue.enqueue(value);
                } catch (NumberFormatException e) {
                    System.out.println("   '" + tok + "'? type a number, or r / p / q");
                    continue;
                }
            }
            System.out.println("   stack " + stack + "   |   queue " + queue);
        }
    }

    public static void main(String[] args) {
        narratedDemo();
        bracketDemo();
        printQueueDemo();
        interactive();
    }
}
