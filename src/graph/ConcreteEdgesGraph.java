/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import java.util.*;

/**
 * An implementation of Graph.
 *
 * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteEdgesGraph implements Graph<String> {

    // ----- Rep -----
    private final Set<String> vertices = new HashSet<>();
    private final List<Edge> edges = new ArrayList<>();

    // ----- AF / RI / Safety -----
    // Abstraction Function:
    //   AF(vertices, edges) = a directed, weighted graph G=(V,E)
    //     where V = exactly the strings in `vertices`,
    //     and E = { (e.source, e.target, e.weight) | e in `edges`, weight > 0 }.
    //   Every endpoint named by an edge is also in V. Self-loops allowed.

    // Representation Invariant:
    //   1) vertices != null, edges != null
    //   2) no nulls in vertices; no null edges; edge endpoints non-null
    //   3) every edge has weight > 0
    //   4) every edge’s endpoints are in vertices
    //   5) no parallel duplicates: at most one edge per (source,target)

    // Safety from rep exposure:
    //   * Fields are private; we never return them directly.
    //   * vertices() returns a defensive copy.
    //   * sources()/targets() return fresh maps.
    //   * Edge is immutable (final fields, no setters).

    // ----- Constructors -----

    /** No-arg constructor for an empty graph. */
    public ConcreteEdgesGraph() { }

    /** Optional convenience constructor: copies inputs defensively. */
    public ConcreteEdgesGraph(Set<String> vertices, List<Edge> edges) {
        if (vertices != null) this.vertices.addAll(vertices);
        if (edges != null)    this.edges.addAll(edges);
        checkRep();
    }

    // ----- checkRep -----
    private void checkRep() {
        assert this.vertices != null : "vertices set is null";
        assert this.edges != null : "edges list is null";

        for (String v : this.vertices) {
            assert v != null : "null vertex";
        }

        Set<String> seenPairs = new HashSet<>();
        for (Edge e : this.edges) {
            assert e != null : "null edge";
            assert e.getSource() != null : "null edge source";
            assert e.getTarget() != null : "null edge target";
            assert e.getWeight() > 0 : "nonpositive weight stored: " + e.getWeight();
            assert this.vertices.contains(e.getSource()) : "edge source not in vertices";
            assert this.vertices.contains(e.getTarget()) : "edge target not in vertices";
            String key = e.getSource() + "\u0001" + e.getTarget();
            assert seenPairs.add(key) : "duplicate edge " + e.getSource() + "->" + e.getTarget();
        }
    }

    // ----- Graph<String> methods -----

    @Override
    public boolean add(String vertex) {
        assert vertex != null : "vertex must be non-null";
        if (this.vertices.contains(vertex)) {
            return false;
        }
        this.vertices.add(vertex);
        checkRep();
        return true;
    }

    @Override
    public int set(String source, String target, int weight) {
        // Preconditions
        assert source != null : "source must be non-null";
        assert target != null : "target must be non-null";
        assert weight >= 0 : "weight must be nonnegative (0 means remove)";

        // Look for existing (source -> target)
        Iterator<Edge> it = this.edges.iterator();
        while (it.hasNext()) {
            Edge e = it.next();
            if (e.getSource().equals(source) && e.getTarget().equals(target)) {
                int prev = e.getWeight();
                if (weight == 0) {
                    // removal
                    it.remove();
                    checkRep();
                    return prev;
                } else {
                    // update: replace (keep Edge immutable)
                    it.remove();
                    this.vertices.add(source);
                    this.vertices.add(target);
                    this.edges.add(new Edge(source, target, weight));
                    checkRep();
                    return prev;
                }
            }
        }

        // Edge not found
        if (weight == 0) {
            // no-op
            checkRep();
            return 0;
        } else {
            // add new edge (and auto-add missing vertices)
            this.vertices.add(source);
            this.vertices.add(target);
            this.edges.add(new Edge(source, target, weight));
            checkRep();
            return 0;
        }
    }

    @Override
    public boolean remove(String vertex) {
        assert vertex != null : "vertex must be non-null";
        if (!this.vertices.contains(vertex)) {
            return false;
        }
        // remove incident edges
        Iterator<Edge> it = this.edges.iterator();
        while (it.hasNext()) {
            Edge e = it.next();
            if (vertex.equals(e.getSource()) || vertex.equals(e.getTarget())) {
                it.remove();
            }
        }
        // remove the vertex
        this.vertices.remove(vertex);
        checkRep();
        return true;
    }

    @Override
    public Set<String> vertices() {
        return new HashSet<>(this.vertices); // defensive copy
    }

    @Override
    public Map<String, Integer> sources(String target) {
        assert target != null : "target must be non-null";
        Map<String, Integer> result = new HashMap<>();
        for (Edge e : this.edges) {
            if (e.getTarget().equals(target)) {
                result.put(e.getSource(), e.getWeight());
            }
        }
        return result; // fresh map
    }

    @Override
    public Map<String, Integer> targets(String source) {
        assert source != null : "source must be non-null";
        Map<String, Integer> result = new HashMap<>();
        for (Edge e : this.edges) {
            if (e.getSource().equals(source)) {
                result.put(e.getTarget(), e.getWeight());
            }
        }
        return result; // fresh map
    }

    @Override
    public String toString() {
        return "Vertices: " + this.vertices + "\nEdges: " + this.edges;
    }

    // ----- Immutable Edge value object -----
    static final class Edge {
        private final String source;
        private final String target;
        private final int weight;

        Edge(String source, String target, int weight) {
            assert source != null : "Edge source cannot be null";
            assert target != null : "Edge target cannot be null";
            assert weight > 0 : "Edge weight must be positive";
            this.source = source;
            this.target = target;
            this.weight = weight;
        }

        String getSource() { return source; }
        String getTarget() { return target; }
        int getWeight()   { return weight; }

        @Override
        public String toString() {
            return source + " -> " + target + " (" + weight + ")";
        }
    }
}