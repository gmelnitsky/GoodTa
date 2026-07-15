# Mock Interview — Find the Bug (LRU Cache)

**The code:** [`src/dsa/interview/LruCache.java`](../src/dsa/interview/LruCache.java)
**Run it:** `./run.sh dsa.interview.LruCache`
**Fuzz it:** `./run.sh dsa.interview.LruCache fuzz`
**Answer:** [bughunt-lru-answer.md](bughunt-lru-answer.md) — separate file, so you can't spoil it by scrolling.

---

## The setup

You're in a code review. A teammate hands you an LRU cache and says it's ready to
merge: it compiles, and the tests pass. Run it yourself — they do pass, all twelve
of them.

There is exactly one bug in the class.

This is one of the most common styles of real interview, and it is *not* a puzzle
about spotting a typo. It's about whether you can hold "what this code is supposed
to guarantee" in your head and check the code against that, instead of reading the
code and nodding along because each line looks reasonable. Every line does look
reasonable. That's the point.

## Time it like the real thing

Give yourself **25 minutes**, and work in this order:

1. **State the contract (2 min).** Before reading a single line of the
   implementation, say out loud what an LRU cache promises. Be precise about the
   word *used*. If you skip this step you have nothing to check the code against,
   and you'll just be proofreading.
2. **Read the code against the contract (8 min).** For each operation, ask: does
   this maintain what I just said? Don't hunt for weird syntax. Hunt for a promise
   that isn't kept.
3. **Prove it (5 min).** Write a concrete sequence of calls where the code returns
   something an LRU cache never would. A bug you can't demonstrate is a guess.
4. **Fix it (2 min).** It's a small fix.
5. **Explain the test gap (8 min).** *Why did twelve passing tests miss this?*
   This is the part interviewers actually care about, and the part most candidates
   skip. Look hard at test 3 in `runTests()`.

## What a good answer sounds like

Not just "line 104 is wrong." A strong answer has four parts:

- **The contract it breaks** — which promise, stated as a rule.
- **The repro** — a specific call sequence, with the expected and actual result.
- **The fix** — and why it belongs where you're putting it.
- **The test gap** — what property the existing suite never checks, and the test
  you'd add. The interesting answer here isn't "add a test for this bug." It's
  noticing that the existing tests check *values* and barely check *order*, and
  that the one test which touches the buggy path asserts the wrong thing about it.

## Hints

Read one at a time, only when properly stuck.

<details>
<summary><b>Hint 1</b> — where to point the microscope</summary>

The bug is not in the linked-list plumbing. `unlink`, `addFirst`, and `moveToFront`
are all correct, and so is `get`.

Every entry lives in two places at once: the map, and the recency list. The map
part is fine everywhere. So: where does the code touch an entry and update the map
side of it, but not the list side?
</details>

<details>
<summary><b>Hint 2</b> — the contract, said more carefully</summary>

"Least recently **used**."

Write down every operation a caller can perform that counts as *using* an entry.
There are more than one. Now check: does the code refresh recency on **all** of
them?
</details>

<details>
<summary><b>Hint 3</b> — the shape of the failing sequence</summary>

To expose it you need a key that is written more than once and then never read.
The bug is invisible unless a key gets *overwritten* while the cache is under
memory pressure.

Try, on a cache of capacity 2:

```
put(1, 1)
put(2, 2)
put(1, 99)   // 1 is now the most recently used... isn't it?
put(3, 3)    // something gets evicted here. Which one? Which one SHOULD?
get(2)
get(1)
```

Work out by hand what a correct LRU returns for those last two calls, then run it.
</details>

<details>
<summary><b>Hint 4</b> — why the tests missed it</summary>

Look at test 3, "Updating an existing key." It calls `put` on a key that's already
there — the exact buggy path — and it passes.

It passes because it only checks that the **value** was overwritten. The bug
doesn't corrupt the value. It corrupts the **recency order**, and the order is
invisible until an eviction has to choose a victim. That test never fills the
cache, so no eviction ever happens, so the corruption never gets a chance to show.

A test can execute the buggy line and still tell you nothing.
</details>

## The fuzzer

Once you have a suspect — or if you're properly stuck — run:

```sh
./run.sh dsa.interview.LruCache fuzz
```

It generates random call sequences, replays each against both this cache and a
trusted reference, and stops at the first disagreement. Then it **shrinks** the
failure: it drops any call that isn't needed for the failure to still happen, over
and over, until every remaining call is load-bearing. A twelve-call failure tells
you almost nothing. The four-call one it prints tells you what the bug *is*.

Two things in there are worth stealing for your own work:

**The reference is borrowed, not rewritten.** If you write your own "correct" LRU
to test against, you'll write it with the same misunderstanding you had the first
time, the two will agree, and you'll conclude everything is fine. So the reference
is `LinkedHashMap` in access-order mode, which is an LRU cache that ships with the
JDK and was not written by you today.

**Shrinking is the whole game.** Finding *a* failure is easy. Finding the *smallest*
failure is what converts "something's wrong" into "here's the bug."

This is the same trick `MergeSort`'s self-test uses — check your implementation
against a trusted one over many random inputs — and it is the single most useful
testing habit in this repo.
