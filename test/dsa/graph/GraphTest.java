package dsa.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Graph}, the finished adjacency-list graph.
 *
 * (The BFS class that consumes this graph is an unimplemented stub and is
 * deliberately not tested here — testing a method that throws
 * UnsupportedOperationException proves nothing about an algorithm.)
 */
class GraphTest {

    @Test
    @DisplayName("directed edges point one way only")
    void directedEdgeIsOneWay() {
        Graph g = new Graph(true);
        g.addEdge("A", "B");
        assertEquals(Arrays.asList("B"), g.neighbors("A"));
        assertTrue(g.neighbors("B").isEmpty(), "B should not point back to A in a directed graph");
        assertTrue(g.hasNode("B"), "addEdge must create the destination node too");
    }

    @Test
    @DisplayName("undirected edges connect both ways")
    void undirectedEdgeIsSymmetric() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        assertTrue(g.neighbors("A").contains("B"));
        assertTrue(g.neighbors("B").contains("A"));
    }

    @Test
    @DisplayName("addNode creates an isolated node with no neighbors")
    void isolatedNode() {
        Graph g = new Graph(false);
        g.addNode("Z");
        assertTrue(g.hasNode("Z"));
        assertTrue(g.neighbors("Z").isEmpty());
    }

    @Test
    @DisplayName("neighbors of an unknown node is empty, and the node is not created")
    void unknownNodeHasNoNeighbors() {
        Graph g = new Graph(false);
        assertTrue(g.neighbors("nope").isEmpty());
        assertFalse(g.hasNode("nope"), "querying neighbors must not implicitly create a node");
    }

    @Test
    @DisplayName("studyGuideExample has the documented directed structure")
    void studyGuideStructure() {
        Graph g = Graph.studyGuideExample();
        assertEquals(new HashSet<>(Arrays.asList("A", "B", "C", "D")), new HashSet<>(g.nodes()));
        assertEquals(new HashSet<>(Arrays.asList("B", "C")), new HashSet<>(g.neighbors("A")));
        assertEquals(Arrays.asList("D"), g.neighbors("B"));
        assertEquals(Arrays.asList("D"), g.neighbors("C"));
        assertTrue(g.neighbors("D").isEmpty(), "D is a sink with no outgoing edges");
    }

    @Test
    @DisplayName("cityMap is undirected with the documented adjacency and an island Z")
    void cityMapStructure() {
        Graph city = Graph.cityMap();
        Set<String> expectedNodes =
                new HashSet<>(Arrays.asList("A", "B", "C", "D", "E", "F", "Z"));
        assertEquals(expectedNodes, new HashSet<>(city.nodes()));

        // Undirected: every edge is mirrored. Spot-check the hub node D.
        Set<String> dNeighbors = new HashSet<>(city.neighbors("D"));
        assertEquals(new HashSet<>(Arrays.asList("B", "C", "E")), dNeighbors);

        // A is connected to B and C only.
        assertEquals(new HashSet<>(Arrays.asList("B", "C")), new HashSet<>(city.neighbors("A")));

        // F is a leaf reachable only from E.
        assertEquals(Arrays.asList("E"), city.neighbors("F"));

        // Z is an island: no edges either direction.
        assertTrue(city.neighbors("Z").isEmpty());
        for (String n : city.nodes()) {
            assertFalse(city.neighbors(n).contains("Z"), "nothing should point at the island Z");
        }
    }

    @Test
    @DisplayName("parallel edges are kept (adjacency list, not a set)")
    void parallelEdgesRetained() {
        Graph g = new Graph(true);
        g.addEdge("A", "B");
        g.addEdge("A", "B");
        List<String> neighbors = g.neighbors("A");
        assertEquals(2, neighbors.size(), "adjacency list stores each addEdge, including duplicates");
    }
}
