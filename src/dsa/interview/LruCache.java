package dsa.interview;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MOCK INTERVIEW  —  FIND THE BUG
 *
 * Scenario: a teammate wrote this LRU cache. It compiles. It passes the test
 * suite at the bottom of this file — go on, run it:
 *
 *     ./run.sh dsa.interview.LruCache
 *
 * It is still wrong. Somewhere in the class below is exactly one bug.
 *
 * YOUR JOB
 *   1. Read the code and say what an LRU cache is supposed to guarantee.
 *   2. Find the bug by reasoning, not by guessing.
 *   3. Prove it: write a sequence of calls where this code gives a wrong answer.
 *   4. Fix it.
 *   5. Then say why the existing tests never caught it. This is the real question.
 *
 * When you want a machine to check your work — ideally after you already have a
 * suspect — there is a fuzzer:
 *
 *     ./run.sh dsa.interview.LruCache fuzz
 *
 * It compares this cache against a trusted reference on random call sequences,
 * and shrinks any failure it finds down to the shortest one that still breaks.
 * Reach for it second. The point of the exercise is the reasoning, and the
 * fuzzer will hand you the answer.
 *
 * See docs/bughunt-lru.md for hints, in increasing order of how much they give
 * away. The answer lives in a separate file so you can't read it by accident.
 *
 * ------------------------------------------------------------------------
 * What an LRU cache is
 *
 * A fixed-capacity key/value store. When it is full and a NEW key arrives, it
 * evicts the least recently used entry to make room. "Used" means touched by
 * the caller — every get and every put on a key counts as using it.
 *
 * Both operations are meant to be O(1), which is why this is a HashMap (for
 * lookup) laid over a doubly-linked list (for recency order) rather than
 * something that has to scan.
 */
public class LruCache {

    /** One entry, living in both the map and the recency list. */
    private static final class Node {
        final int key;
        int value;
        Node prev, next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> map = new HashMap<>();

    // Sentinels, so insert and remove never have to special-case the ends.
    // The list runs from most-recently-used to least:
    //
    //     head <-> [most recent] <-> ... <-> [least recent] <-> tail
    //
    // so the eviction victim is always tail.prev.
    private final Node head = new Node(0, 0);
    private final Node tail = new Node(0, 0);

    public LruCache(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("capacity must be at least 1");
        }
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    /** Returns the value for key, or -1 if it isn't cached. */
    public int get(int key) {
        Node node = map.get(key);
        if (node == null) {
            return -1;
        }
        moveToFront(node);
        return node.value;
    }

    /** Inserts or updates key, evicting the least recently used entry if full. */
    public void put(int key, int value) {
        Node existing = map.get(key);
        if (existing != null) {
            existing.value = value;
            return;
        }

        if (map.size() == capacity) {
            Node victim = tail.prev;
            unlink(victim);
            map.remove(victim.key);
        }

        Node node = new Node(key, value);
        map.put(key, node);
        addFirst(node);
    }

    public int size() {
        return map.size();
    }

    // ---- the linked list ----

    /** Splices a node out of the list. */
    private void unlink(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.prev = null;
        node.next = null;
    }

    /** Puts a node at the most-recently-used end. */
    private void addFirst(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    /** Marks a node as the most recently used. */
    private void moveToFront(Node node) {
        unlink(node);
        addFirst(node);
    }

    // ======================================================================
    // Below here is test code. You don't need to change it — but you should
    // absolutely read it, because part of the exercise is working out what it
    // fails to check.
    // ======================================================================

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("fuzz")) {
            fuzz();
        } else {
            runTests();
        }
    }

    // ---- the suite the teammate wrote, and which passes ----

    private static int checks = 0;
    private static int failures = 0;

    private static void check(String what, int got, int want) {
        checks++;
        if (got != want) {
            failures++;
            System.out.println("  FAIL  " + what + ": got " + got + ", wanted " + want);
        }
    }

    private static void runTests() {
        System.out.println("running the existing test suite...\n");

        // 1. The example everybody uses.
        LruCache c = new LruCache(2);
        c.put(1, 1);
        c.put(2, 2);
        check("get(1) after putting 1 and 2", c.get(1), 1);
        c.put(3, 3); // capacity 2, and 2 is least recently used -> 2 is evicted
        check("get(2) after 2 was evicted", c.get(2), -1);
        check("get(3) is the new entry", c.get(3), 3);
        c.put(4, 4); // now 1 is least recently used -> 1 is evicted
        check("get(1) after 1 was evicted", c.get(1), -1);
        check("get(3) survived", c.get(3), 3);
        check("get(4) is the new entry", c.get(4), 4);

        // 2. A miss on an empty cache.
        check("get on an empty cache", new LruCache(2).get(99), -1);

        // 3. Updating an existing key.
        LruCache u = new LruCache(2);
        u.put(1, 1);
        u.put(1, 10);
        check("put on an existing key overwrites the value", u.get(1), 10);
        check("...and does not grow the cache", u.size(), 1);

        // 4. get() counts as a use.
        LruCache g = new LruCache(2);
        g.put(1, 1);
        g.put(2, 2);
        g.get(1);    // 1 is now the most recently used, so 2 should be next out
        g.put(3, 3);
        check("get() protected 1 from eviction", g.get(1), 1);
        check("...so 2 was evicted instead", g.get(2), -1);

        // 5. Capacity is respected.
        LruCache s = new LruCache(3);
        for (int k = 0; k < 10; k++) {
            s.put(k, k);
        }
        check("never grows past capacity", s.size(), 3);

        System.out.println(failures == 0
                ? "all " + checks + " checks passed\n"
                : "\n" + failures + " of " + checks + " checks FAILED\n");

        if (failures == 0) {
            System.out.println("So it's correct, right?");
            System.out.println("There is a bug. Find a sequence of calls that exposes it.");
            System.out.println("Stuck? docs/bughunt-lru.md   Want a machine to look? ./run.sh dsa.interview.LruCache fuzz");
        }
    }

    // ---- the fuzzer: differential testing against a trusted reference ----

    /**
     * The reference LRU. Not a reimplementation — reimplementing the thing you
     * are testing tends to reproduce the same misunderstanding, and then the two
     * agree and you learn nothing. This borrows a correct one from the standard
     * library: LinkedHashMap in access-order mode IS an LRU cache, and evicting
     * the eldest entry is a hook it offers for exactly this purpose.
     */
    private static final class ReferenceLru {
        private final LinkedHashMap<Integer, Integer> m;

        ReferenceLru(final int capacity) {
            m = new LinkedHashMap<Integer, Integer>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                    return size() > capacity;
                }
            };
        }

        int get(int key) {
            Integer v = m.get(key);
            return v == null ? -1 : v;
        }

        void put(int key, int value) {
            m.put(key, value);
        }
    }

    /** One call in a generated sequence. */
    private static final class Op {
        final boolean isGet;
        final int key;
        final int value;

        Op(boolean isGet, int key, int value) {
            this.isGet = isGet;
            this.key = key;
            this.value = value;
        }

        boolean isGet() { return isGet; }
        int key()       { return key; }
        int value()     { return value; }

        @Override
        public String toString() {
            return isGet ? "get(" + key + ")" : "put(" + key + ", " + value + ")";
        }
    }

    private static void fuzz() {
        System.out.println("fuzzing against LinkedHashMap in access-order mode...\n");

        Random rng = new Random(7);
        for (int trial = 0; trial < 20_000; trial++) {
            int capacity = 1 + rng.nextInt(4);
            int keySpace = capacity + 2; // small, so keys actually collide and evict

            List<Op> ops = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                boolean isGet = rng.nextInt(3) == 0;
                ops.add(new Op(isGet, rng.nextInt(keySpace), rng.nextInt(100)));
            }

            if (diverges(capacity, ops) != -1) {
                List<Op> minimal = shrink(capacity, ops);
                report(capacity, minimal);
                return;
            }
        }

        System.out.println("20,000 random sequences, no divergence. Either it's fixed, or");
        System.out.println("the generator never produced the shape of call that breaks it.");
    }

    /**
     * Replays a sequence against both caches. Returns the index of the first call
     * where they disagree, or -1 if they agreed the whole way through.
     */
    private static int diverges(int capacity, List<Op> ops) {
        LruCache mine = new LruCache(capacity);
        ReferenceLru theirs = new ReferenceLru(capacity);

        for (int i = 0; i < ops.size(); i++) {
            Op op = ops.get(i);
            if (op.isGet()) {
                if (mine.get(op.key()) != theirs.get(op.key())) {
                    return i;
                }
            } else {
                mine.put(op.key(), op.value());
                theirs.put(op.key(), op.value());
            }
        }
        return -1;
    }

    /**
     * A 12-call failure tells you almost nothing; a 4-call one tells you what the
     * bug IS. So: greedily drop any call that isn't needed to still break it, and
     * keep going until nothing more can be removed.
     */
    private static List<Op> shrink(int capacity, List<Op> ops) {
        List<Op> best = new ArrayList<>(ops);
        boolean shrank = true;

        while (shrank) {
            shrank = false;
            for (int i = 0; i < best.size(); i++) {
                List<Op> candidate = new ArrayList<>(best);
                candidate.remove(i);
                if (diverges(capacity, candidate) != -1) {
                    best = candidate;
                    shrank = true;
                    break;
                }
            }
        }
        return best;
    }

    private static void report(int capacity, List<Op> ops) {
        int at = diverges(capacity, ops);
        LruCache mine = new LruCache(capacity);
        ReferenceLru theirs = new ReferenceLru(capacity);

        System.out.println("Smallest failing sequence, on a cache of capacity " + capacity + ":\n");
        for (int i = 0; i < ops.size(); i++) {
            Op op = ops.get(i);
            String line = "  " + op;

            if (op.isGet()) {
                int got = mine.get(op.key());
                int want = theirs.get(op.key());
                line += "  ->  " + got;
                if (i == at) {
                    line += "      <-- WRONG. A correct LRU returns " + want + " here.";
                }
            } else {
                mine.put(op.key(), op.value());
                theirs.put(op.key(), op.value());
            }
            System.out.println(line);
        }

        System.out.println("\nNow: which call in that sequence did the cache mishandle,");
        System.out.println("and what did it fail to do?");
    }
}
