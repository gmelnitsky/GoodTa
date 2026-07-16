package dsa.interview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LruCache} — the "find the bug" exercise.
 *
 * The cache has exactly one intentional bug: {@code put} on an *existing* key
 * overwrites the value but does NOT refresh the entry's recency (see
 * docs/bughunt-lru-answer.md). A write is a use, so the overwritten key should
 * become most-recently-used; this implementation leaves it where it was and can
 * evict the hottest key.
 *
 * The tests below split cleanly into two groups:
 *   1. Behaviours the (buggy) implementation gets RIGHT — these are live and
 *      must pass. They pin down the correct contract for everything except the
 *      recency-on-overwrite corner.
 *   2. One @Disabled test, {@code writeIsAUse_recencyOnOverwrite}, that asserts
 *      the true LRU contract for the buggy corner. It is the "missing twin" of
 *      the existing get()-refreshes-recency test. It is disabled ON PURPOSE:
 *      the exercise is to find the bug, not to have the implementation silently
 *      changed to pass. Enabling it against the unfixed code fails, which is the
 *      whole point. Do NOT edit LruCache to make it green.
 */
class LruCacheTest {

    // ---------- Group 1: correct behaviours (must pass) ----------

    @Test
    @DisplayName("evicts the least recently used entry when capacity overflows")
    void evictsLeastRecentlyUsed() {
        LruCache c = new LruCache(2);
        c.put(1, 1);
        c.put(2, 2);
        c.put(3, 3); // 1 is least recently used -> evicted
        assertEquals(-1, c.get(1));
        assertEquals(2, c.get(2));
        assertEquals(3, c.get(3));
    }

    @Test
    @DisplayName("get() counts as a use and protects a key from eviction")
    void getRefreshesRecency() {
        LruCache c = new LruCache(2);
        c.put(1, 1);
        c.put(2, 2);
        assertEquals(1, c.get(1)); // 1 is now most recently used
        c.put(3, 3);               // so 2 (untouched) is evicted
        assertEquals(1, c.get(1));
        assertEquals(-1, c.get(2));
        assertEquals(3, c.get(3));
    }

    @Test
    @DisplayName("get() on a missing key returns -1 and is not a use")
    void missReturnsMinusOne() {
        LruCache c = new LruCache(2);
        assertEquals(-1, c.get(99));
        c.put(1, 1);
        c.put(2, 2);
        assertEquals(-1, c.get(42)); // a miss must not promote anything
        c.put(3, 3);                 // 1 is still LRU -> evicted
        assertEquals(-1, c.get(1));
        assertEquals(2, c.get(2));
    }

    @Test
    @DisplayName("put on an existing key overwrites the value without growing the cache")
    void overwriteUpdatesValue() {
        LruCache c = new LruCache(2);
        c.put(1, 1);
        c.put(1, 10);
        assertEquals(10, c.get(1));
        assertEquals(1, c.size());
    }

    @Test
    @DisplayName("size never exceeds capacity")
    void respectsCapacity() {
        LruCache c = new LruCache(3);
        for (int k = 0; k < 10; k++) {
            c.put(k, k);
        }
        assertEquals(3, c.size());
    }

    @Test
    @DisplayName("the three most recently inserted keys survive a flood")
    void keepsMostRecentAfterFlood() {
        LruCache c = new LruCache(3);
        for (int k = 0; k < 10; k++) {
            c.put(k, k * 100);
        }
        // Keys 7, 8, 9 were inserted last and never overwritten, so they remain.
        assertEquals(700, c.get(7));
        assertEquals(800, c.get(8));
        assertEquals(900, c.get(9));
        assertEquals(-1, c.get(6));
    }

    @Test
    @DisplayName("capacity < 1 is rejected")
    void rejectsNonPositiveCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new LruCache(0));
        assertThrows(IllegalArgumentException.class, () -> new LruCache(-3));
    }

    // ---------- Group 2: the intended contract the bug violates ----------

    @Test
    @Disabled("BUG-HUNT: documents the intended LRU contract — a put() on an "
            + "existing key is a use and must refresh recency. The exercise's "
            + "implementation intentionally violates this (see "
            + "docs/bughunt-lru-answer.md). Enabling this test against the "
            + "unfixed code fails BY DESIGN; that failure is the lesson. Do not "
            + "change LruCache to make it pass.")
    @DisplayName("write-is-a-use: overwriting a key must protect it from eviction")
    void writeIsAUse_recencyOnOverwrite() {
        LruCache c = new LruCache(2);
        c.put(1, 1);
        c.put(2, 2);
        c.put(1, 99); // a write is a use: 1 becomes most recently used
        c.put(3, 3);  // so 2 is least recently used and should be evicted
        assertEquals(99, c.get(1), "the overwritten (hottest) key must survive");
        assertEquals(-1, c.get(2), "the untouched key must be the eviction victim");
    }
}
