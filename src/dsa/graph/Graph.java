package dsa.graph;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Graph  —  a working, reusable graph you can play with. (Phase 0: "see a graph".)
 *
 * A graph is just NODES (things) connected by EDGES (relationships). We store it
 * as an ADJACENCY LIST: a map from each node to the list of nodes it points to.
 * That's the exact structure from your study guide:
 *
 *     A -> [B, C]
 *     B -> [D]
 *     C -> [D]
 *     D -> []
 *
 * This class is DONE — you don't fill anything in here. Read it, run it, and use
 * the playground in main() to build graphs and ask them questions. Later phases
 * (BFS, DFS, ...) will import this same class.
 *
 * Run with:  ./run.sh dsa.graph.Graph
 */
public class Graph {

    // node -> its neighbors. LinkedHashMap keeps insertion order so printouts are stable.
    private final Map<String, List<String>> adj = new LinkedHashMap<>();
    private final boolean directed;

    /** directed = true: edges have a direction (A->B only). false: both ways (A<->B). */
    public Graph(boolean directed) {
        this.directed = directed;
    }

    /** Make sure a node exists (even if it has no edges yet). */
    public void addNode(String node) {
        adj.putIfAbsent(node, new ArrayList<>());
    }

    /** Connect `from` to `to`. In an undirected graph this also connects `to` to `from`. */
    public void addEdge(String from, String to) {
        addNode(from);
        addNode(to);
        adj.get(from).add(to);
        if (!directed) {
            adj.get(to).add(from);
        }
    }

    /** The nodes directly reachable from `node` in one hop. */
    public List<String> neighbors(String node) {
        return adj.getOrDefault(node, java.util.Collections.emptyList());
    }

    /** All nodes in the graph. */
    public Set<String> nodes() {
        return adj.keySet();
    }

    public boolean hasNode(String node) {
        return adj.containsKey(node);
    }

    /** Pretty-print the adjacency list. */
    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(directed ? "(directed)\n" : "(undirected)\n");
        for (String node : adj.keySet()) {
            sb.append("   ").append(node).append(" -> ").append(neighbors(node)).append("\n");
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // A couple of ready-made example graphs the later phases will reuse.
    // ------------------------------------------------------------------

    /** The directed A/B/C/D graph straight from your study guide. */
    public static Graph studyGuideExample() {
        Graph g = new Graph(true); // directed
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "D");
        g.addEdge("C", "D");
        g.addNode("D"); // D has no outgoing edges
        return g;
    }

    /** A small undirected "map of cities" — good for shortest-path stuff (BFS). */
    public static Graph cityMap() {
        Graph g = new Graph(false); // undirected: roads go both ways
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "D");
        g.addEdge("C", "D");
        g.addEdge("D", "E");
        g.addEdge("E", "F");
        g.addNode("Z"); // an island — connected to nothing
        return g;
    }

    // ------------------------------------------------------------------
    // Playground — build graphs and ask them questions.
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("========== the study-guide graph (directed) ==========");
        Graph g = studyGuideExample();
        System.out.print(g);
        System.out.println("neighbors of A: " + g.neighbors("A") + "   (A points to these)");
        System.out.println("neighbors of D: " + g.neighbors("D") + "   (D points to nobody)");
        System.out.println("is there a node 'C'? " + g.hasNode("C"));

        System.out.println("\n========== the city map (undirected) ==========");
        Graph city = cityMap();
        System.out.print(city);
        System.out.println("neighbors of D: " + city.neighbors("D")
                + "   (roads go both ways, so D connects back to B, C, and on to E)");
        System.out.println("neighbors of Z: " + city.neighbors("Z") + "   (an island)");

        System.out.println("\nTry it: edit main() to add your own edges with city.addEdge(\"X\", \"Y\")");
        System.out.println("Then re-run and print it. Next phase: BFS finds the shortest path across this map.");
    }
}
