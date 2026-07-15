# Answer — Find the Bug (LRU Cache)

> Spoilers. Go do [the exercise](bughunt-lru.md) first — the reasoning is the
> whole point, and you only get one first attempt at it.

---

## The bug

In `put`, when the key is already in the cache:

```java
Node existing = map.get(key);
if (existing != null) {
    existing.value = value;
    return;                  // <-- the entry is never moved to the front
}
```

The value is overwritten, and the entry keeps its old position in the recency
list.

## The contract it breaks

An LRU cache evicts the least recently **used** entry. A `put` on an existing key
*is a use of that entry* — you just touched it. So it has to become the most
recently used, exactly as `get` does.

The code treats a write as if it weren't a use. `get` refreshes recency; `put`
refreshes it only for keys it has never seen. So a key that is written over and
over but never read drifts to the least-recently-used end and gets evicted, even
though it is the hottest key in the cache. That is the precise opposite of what an
LRU cache is for.

## The repro

Capacity 2:

| Call | Recency order (most → least) | | A correct LRU |
|------|------------------------------|---|---------------|
| `put(1, 1)` | `[1]` | | `[1]` |
| `put(2, 2)` | `[2, 1]` | | `[2, 1]` |
| `put(1, 99)` | `[2, 1]` ← **1 was not promoted** | | `[1, 2]` |
| `put(3, 3)` | evicts `1` | | evicts `2` |
| `get(1)` | **`-1`** | | `99` |
| `get(2)` | **`2`** | | `-1` |

Both of those last two answers are wrong, and they're wrong in the worst way: the
cache threw away the entry you had *just written*, and kept the one you hadn't
touched in ages.

The fuzzer finds this on its own and shrinks it to five calls:

```
put(2, 61)
put(1, 9)
put(2, 97)     <-- 2 is written again, but not promoted
put(0, 5)      <-- so 2 is evicted, when 1 should have been
get(1)  ->  9      WRONG. A correct LRU returns -1 here.
```

## The fix

One line:

```java
if (existing != null) {
    existing.value = value;
    moveToFront(existing);   // a write is a use
    return;
}
```

With that, the fuzzer runs 20,000 random sequences against `LinkedHashMap` and
finds no divergence.

## Why twelve passing tests missed it — the real question

Test 3 is called "Updating an existing key." It runs the buggy line. It passes.

```java
LruCache u = new LruCache(2);
u.put(1, 1);
u.put(1, 10);
check("put on an existing key overwrites the value", u.get(1), 10);
check("...and does not grow the cache", u.size(), 1);
```

Look at what it asserts: the **value** is right, and the **size** is right. Both of
those *are* right — the bug doesn't touch either. What the bug corrupts is the
**recency order**, and the recency order is unobservable from the outside until an
eviction has to choose a victim. This test uses a cache of capacity 2 and puts one
key in it. Nothing is ever evicted. So the corrupted state is created, and then the
test ends before anything could possibly reveal it.

The other tests don't help either. Tests 1 and 4 exercise eviction properly, but
they never overwrite an existing key, so they never enter the buggy branch. The
suite has one test that reaches the bug and no test that can see it, and one set of
tests that could see it but never reach it. Between them they cover every line and
catch nothing.

That's the lesson, and it generalizes well past this file:

- **Line coverage is not behaviour coverage.** The buggy line was executed by the
  test suite. Executing a line only proves it didn't crash.
- **Test the property, not the symptom.** The property here is "the least recently
  *used* entry is the one evicted." Almost none of the tests assert anything about
  eviction *order* — they assert values and sizes, because values and sizes are
  easy to assert.
- **Bugs live where two representations must agree.** Every entry exists in the map
  *and* in the list. The bug is in the one place that updates one and forgets the
  other. When you review code with redundant state, go straight to the writes and
  check that every one of them updates both.
- **The state that's hardest to assert is where bugs hide.** Recency is internal
  and has no getter, so nobody tested it. Bugs collect in exactly the places that
  are awkward to check.

## The test that should have been there

```java
// Overwriting a key must protect it from eviction, exactly as reading it does.
LruCache c = new LruCache(2);
c.put(1, 1);
c.put(2, 2);
c.put(1, 99);   // a write is a use: 1 is now the most recently used
c.put(3, 3);    // so 2 is the least recently used, and 2 is what should go
check("the overwritten key survived", c.get(1), 99);
check("the untouched key was evicted", c.get(2), -1);
```

Note the shape of it: it asserts on eviction *order*, and it makes the cache
actually evict something. Compare it with test 4, which asserts precisely this
property for `get` — and then nobody wrote the `put` half. The bug is sitting in
the gap between a test that exists and its missing twin.

## If you're asked to go further

Good follow-ups an interviewer might push you toward:

- **`get` on a missing key must not count as a use.** It doesn't here — `get`
  returns early on `null` — but say so, because it's the symmetric mistake.
- **Capacity 0.** The constructor rejects it, and that guard is load-bearing.
  Take it out and the first `put` sees `map.size() == capacity` (both zero), decides
  it must evict, and picks `tail.prev` — which, on an empty list, is the `head`
  sentinel. `unlink(head)` then dereferences `head.prev`, which is `null`:

  ```
  Exception in thread "main" java.lang.NullPointerException
        at dsa.interview.LruCache.unlink(LruCache.java:120)
        at dsa.interview.LruCache.put(LruCache.java:103)
  ```

  Rejecting the argument is a fine answer, and so is caching nothing. The point to
  make is that the sentinel trick removes the special case for *ends of the list*,
  not for *an empty cache*, and it's worth knowing which special case your
  cleverness actually eliminated.
- **Thread safety.** This is not safe for concurrent use, and the interesting answer
  isn't "add `synchronized`" — it's that an LRU's recency list is a write on *every*
  read, so a naive lock makes the cache a global serialization point even for a
  pure-read workload. That's why real caches often approximate LRU instead.
