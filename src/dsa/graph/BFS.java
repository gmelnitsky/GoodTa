package dsa.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * BFS LAB  —  three parts, each harder than the last. Implement them in order;
 * the test harness at the bottom grades each part separately so you see progress.
 *
 * BFS explores a graph in RINGS out from the start (nearest first), driven by a
 * QUEUE (FIFO). The golden rule: a node is discovered exactly ONCE — the first
 * time you reach it — and that first arrival is always the shortest way there.
 *
 *   PART 1  bfsOrder     — return the order nodes are visited. (core loop + "seen")
 *   PART 2  shortestHops — fewest hops start->target, or -1.    (add distance)
 *   PART 3  shortestPath — the actual list of nodes on a shortest route. (stretch)
 *
 * Run with:  ./run.sh dsa.graph.BFS
 */
public class BFS {

    // ==================================================================
    // PART 1 — bfsOrder
    // Return the nodes in the order BFS visits them, starting from `start`.
    // On the city map from "A", the answer is [A, B, C, D, E, F] (Z is an
    // island, never reached).
    // ==================================================================
    public static List<String> bfsOrder(Graph g, String start) {
        List<String> order = new ArrayList<>();   // fill this in visit order
        Queue<String> q = new ArrayDeque<>();     // q.add = enqueue, q.remove = dequeue
        Set<String> seen = new HashSet<>();       // seen.contains(x) / seen.add(x)

        // TODO:
        //   - start by discovering `start` (mark it seen, put it on the queue)
        //   - while the queue isn't empty: take the front node, add it to `order`,
        //     then discover each of its g.neighbors(...) you haven't seen yet
        //   - return `order`
        throw new UnsupportedOperationException("bfsOrder not implemented yet");
    }

    // ==================================================================
    // PART 2 — shortestHops
    // Fewest hops from start to target (a node to itself = 0). -1 if unreachable.
    // Same loop as Part 1, but now track how far each node is from the start.
    // Tip: a Map<String,Integer> of distances can double as your "seen" check
    // (if a node is a key, you've seen it).
    // ==================================================================
    public static int shortestHops(Graph g, String start, String target) {
        Queue<String> q = new ArrayDeque<>();
        Map<String, Integer> dist = new HashMap<>();

        // TODO:
        //   - seed: dist.put(start, 0) and enqueue start
        //   - loop: pull the front node; if it's the target, return its distance;
        //     otherwise give each unseen neighbor distance (current + 1) and enqueue it
        //   - if the queue empties, return -1
        throw new UnsupportedOperationException("shortestHops not implemented yet");
    }

    // ==================================================================
    // PART 3 (stretch) — shortestPath
    // Return the actual list of nodes on a shortest route start..target,
    // e.g. ["A","B","D","E","F"]. Return an EMPTY list if unreachable.
    // A path from a node to itself is just [that node].
    //
    // The trick: when you discover a neighbor, remember WHO you came from in a
    // Map<String,String> parent. Once you reach the target, walk parents backward
    // (target -> ... -> start), then reverse it. (Collections.reverse(list) helps.)
    // ==================================================================
    public static List<String> shortestPath(Graph g, String start, String target) {
        Queue<String> q = new ArrayDeque<>();
        Map<String, String> parent = new HashMap<>(); // node -> the node we reached it from

        // TODO:
        //   - BFS as usual, but when you discover a neighbor, record parent.put(neighbor, cur)
        //   - when you dequeue the target, rebuild the path by following parent links
        //     back to start, then reverse. If BFS finishes without reaching target,
        //     return an empty list.
        throw new UnsupportedOperationException("shortestPath not implemented yet");
    }

    // ------------------------------------------------------------------
    // Test harness — you don't need to edit below this line.
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        Graph city = Graph.cityMap();
        System.out.println("City map:");
        System.out.print(city);
        System.out.println("==================================================");

        gradePart1(city);
        gradePart2(city);
        gradePart3(city);
    }

    private static void gradePart1(Graph city) {
        System.out.println("PART 1 — bfsOrder(city, \"A\")");
        try {
            List<String> got = bfsOrder(city, "A");
            List<String> want = new ArrayList<>();
            Collections.addAll(want, "A", "B", "C", "D", "E", "F");
            boolean ok = got.equals(want);
            System.out.println("   got:  " + got);
            System.out.println("   want: " + want);
            System.out.println(ok ? "   PASS ✅\n" : "   not matching yet\n");
        } catch (UnsupportedOperationException e) {
            System.out.println("   not implemented yet\n");
        }
    }

    private static void gradePart2(Graph city) {
        System.out.println("PART 2 — shortestHops");
        String[][] cases = {
            {"A", "A", "0"}, {"A", "B", "1"}, {"A", "D", "2"}, {"A", "E", "3"},
            {"A", "F", "4"}, {"B", "C", "2"}, {"F", "A", "4"}, {"A", "Z", "-1"},
        };
        try {
            int pass = 0;
            for (String[] c : cases) {
                int got = shortestHops(city, c[0], c[1]);
                int want = Integer.parseInt(c[2]);
                boolean ok = got == want;
                if (ok) pass++;
                System.out.printf("   %s->%s got %d want %d  [%s]%n", c[0], c[1], got, want, ok ? "ok" : "WRONG");
            }
            System.out.println(pass == cases.length ? "   PASS ✅\n" : "   " + pass + "/" + cases.length + "\n");
        } catch (UnsupportedOperationException e) {
            System.out.println("   not implemented yet\n");
        }
    }

    private static void gradePart3(Graph city) {
        System.out.println("PART 3 — shortestPath (stretch)");
        Object[][] cases = {
            {"A", "A", list("A")},
            {"A", "C", list("A", "C")},
            {"A", "D", list("A", "B", "D")},
            {"A", "F", list("A", "B", "D", "E", "F")},
            {"A", "Z", list()}, // unreachable -> empty
        };
        try {
            int pass = 0;
            for (Object[] c : cases) {
                @SuppressWarnings("unchecked")
                List<String> want = (List<String>) c[2];
                List<String> got = shortestPath(city, (String) c[0], (String) c[1]);
                boolean ok = want.equals(got);
                if (ok) pass++;
                System.out.printf("   %s->%s got %s want %s  [%s]%n", c[0], c[1], got, want, ok ? "ok" : "WRONG");
            }
            System.out.println(pass == cases.length ? "   PASS ✅\n" : "   " + pass + "/" + cases.length + "\n");
        } catch (UnsupportedOperationException e) {
            System.out.println("   not implemented yet\n");
        }
    }

    private static List<String> list(String... xs) {
        List<String> out = new ArrayList<>();
        Collections.addAll(out, xs);
        return out;
    }
}
