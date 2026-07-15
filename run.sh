#!/usr/bin/env bash
# Compile everything under src/ and run one class.
#
#   ./run.sh                            -> runs the default (dsa.sorting.MergeSort)
#   ./run.sh dsa.sorting.MergeSort      -> runs a specific class by its full name
#   ./run.sh dsa.interview.LruCache fuzz -> anything after the class name is passed
#                                          through to the program as its args
set -e
mkdir -p out
javac -d out $(find src -name "*.java")

CLASS="${1:-dsa.sorting.MergeSort}"
if [ $# -gt 0 ]; then shift; fi   # drop the class name; the rest are the program's
java -cp out "$CLASS" "$@"
