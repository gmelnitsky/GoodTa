# GoodTa — Data Structures & Algorithms practice (Java)

[![CI](https://github.com/gmelnitsky/GoodTa/actions/workflows/ci.yml/badge.svg)](https://github.com/gmelnitsky/GoodTa/actions/workflows/ci.yml)

A place to learn DSA topics by **writing the code yourself** and running it.

## How it works

- Each topic has a stub file under `src/` for you to implement, plus a built-in
  self-test in its `main()` that checks your work.
- Each topic also has a concept page under `docs/`.

## Running

```bash
./run.sh                        # runs the default class (mergesort)
./run.sh dsa.sorting.MergeSort  # run a specific class by full name
```

`run.sh` compiles everything under `src/` into `out/` and runs the class you name.

## Topics

| Topic | Code | Notes |
|-------|------|-------|
| Merge sort | [`src/dsa/sorting/MergeSort.java`](src/dsa/sorting/MergeSort.java) | [docs/mergesort.md](docs/mergesort.md) |

_Add new rows here as you go._

## Mock interviews

Different exercise: the code is already written, and it's wrong. Read it, find the
bug, and — the part interviewers actually care about — explain why the passing test
suite never caught it.

| Question | Code | Prompt |
|----------|------|--------|
| Find the bug: LRU cache | [`src/dsa/interview/LruCache.java`](src/dsa/interview/LruCache.java) | [docs/bughunt-lru.md](docs/bughunt-lru.md) |

```sh
./run.sh dsa.interview.LruCache        # the suite the teammate wrote. It passes.
./run.sh dsa.interview.LruCache fuzz   # a machine looks instead. Use it second.
```

## Tests

Alongside the per-topic `main()` self-tests, there is a real **JUnit 5** suite
under [`test/`](test/) (mirroring the `src/` package layout) that runs in
[CI](.github/workflows/ci.yml) on every push and pull request.

**What it proves.** The suite covers the topics that are actually *implemented*:

- **Stack / Queue** (`StackAndQueue`, `StackQueuePractice`) — LIFO / FIFO order,
  `peek` without removal, empty-container underflow (`IllegalStateException`),
  array growth past the initial capacity, circular-buffer wrap-around, and a
  differential test against `java.util.ArrayDeque` over hundreds of randomized
  op sequences.
- **Graph** (`Graph`) — directed edges are one-way, undirected edges are
  symmetric, isolated nodes, unknown-node queries don't mutate the graph, and
  the documented structure of the `studyGuideExample` / `cityMap` factories
  (including the island node `Z`).
- **LRU cache** (`LruCache`) — eviction of the least-recently-used entry, `get`
  refreshing recency, misses returning `-1`, value overwrite, and capacity
  bounds.

**The bug-hunt.** `LruCacheTest` also contains one **`@Disabled`** test,
`writeIsAUse_recencyOnOverwrite`, that asserts the *intended* LRU contract for
the corner the exercise's implementation deliberately gets wrong (a `put` on an
existing key is a use and must refresh recency — see
[`docs/bughunt-lru-answer.md`](docs/bughunt-lru-answer.md)). It is disabled on
purpose: the point of the exercise is to find the bug, not to quietly patch the
code so the test passes. Enabling it against the unfixed implementation fails by
design.

> The unimplemented practice stubs (`MergeSort`, `BFS`, `MinHeap`) are **not**
> tested — they still throw `UnsupportedOperationException`, and asserting that a
> stub throws proves nothing about an algorithm. Add tests as you implement them.

### Running the tests locally

No Maven or Gradle required — just a JDK and the single JUnit standalone jar:

```bash
# 1. Fetch the JUnit Platform Console Standalone jar into a gitignored lib/
mkdir -p lib
curl -L -o lib/junit-console.jar \
  https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.5/junit-platform-console-standalone-1.10.5.jar

# 2. Compile the sources and tests together
mkdir -p out
javac -cp lib/junit-console.jar -d out $(find src test -name '*.java')

# 3. Run the suite
java -jar lib/junit-console.jar execute --class-path out --scan-class-path
```
