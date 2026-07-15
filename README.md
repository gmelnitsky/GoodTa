# GoodTa — Data Structures & Algorithms practice (Java)

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
