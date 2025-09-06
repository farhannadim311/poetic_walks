/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights
 * reserved. Redistribution of original or derived work requires
 * permission of course staff.
 */
package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of Graph using a vertices-based representation.
 *
 * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteVerticesGraph<L> implements Graph<L> {

    // REP: each Vertex<L> stores its label and a map of outgoing edges
    private final List<Vertex<L>> vertices = new ArrayList<>();

    // Abstraction function (graph-level):
    //   AF(this) = directed weighted graph G whose vertex set is
    //   { v.getLabel() | v ∈ this.vertices }, and whose edge set is the
    //   union of all (v.getLabel() -> u, w) for entries (u, w) in v.getTargets().
    //
    // Representation invariant (graph-level):
    //   - vertices != null, contains no nulls
    //   - all vertex labels are non-null and unique
    //   - for every vertex v and (u, w) in v.getTargets():
    //       * u != null
    //       * w > 0
    //       * u is the label of some vertex in this.vertices (endpoint exists)
    //
    // Safety from rep exposure:
    //   - vertices is private and never returned directly
    //   - observers return defensive copies (sets/maps)
    //   - mutators validate inputs and never expose internal collections

    /** No-arg constructor: start with an empty graph. */
    public ConcreteVerticesGraph() {}

    /** Defensive-copy constructor. */
    public ConcreteVerticesGraph(List<Vertex<L>> verticesIn) {
        this.vertices.clear();
        for (Vertex<L> v : verticesIn) {
            if (v == null) throw new IllegalArgumentException("Vertex<L> cannot be null");
            this.vertices.add(v.copy());
        }
        checkRep();
    }

    /** Graph-level rep check. */
    private void checkRep() {
        Set<L> labels = new HashSet<>();
        // pass 1: collect labels and basic vertex checks
        for (Vertex<L> v : this.vertices) {
            assert v != null : "vertex cannot be null";
            L lab = v.getLabel();
            assert lab != null : "vertex label cannot be null";
            assert labels.add(lab) : "duplicate vertex label: " + lab;
            // vertex-level RI is checked within Vertex; here we also re-check positivity
            for (Map.Entry<L, Integer> e : v.getTargets().entrySet()) {
                L key = e.getKey();
                Integer w = e.getValue();
                assert key != null : "edge key cannot be null";
                assert w != null && w > 0 : "edge weight must be positive";
            }
        }
        // pass 2: every target endpoint must exist as a vertex
        for (Vertex<L> v : this.vertices) {
            for (L tgt : v.getTargets().keySet()) {
                assert labels.contains(tgt)
                        : "edge points to non-existent vertex: " + tgt;
            }
        }
    }

    @Override
    public boolean add(L vertex) {
        assert vertex != null : "vertex must be non-null";
        for (Vertex<L> v : this.vertices) {
            if (v.getLabel().equals(vertex)) {
                return false; // already present
            }
        }
        this.vertices.add(new Vertex<>(vertex));
        checkRep();
        return true;
    }

    @Override
    public int set(L source, L target, int weight) {
        assert source != null : "source must be non-null";
        assert target != null : "target must be non-null";
        assert weight >= 0 : "weight must be nonnegative (0 means remove)";

        Vertex<L> src = null, tgt = null;
        for (Vertex<L> v : this.vertices) {
            if (v.getLabel().equals(source)) src = v;
            if (v.getLabel().equals(target)) tgt = v;
        }

        if (src == null && weight == 0) {
            // no such edge possible
            return 0;
        }
        if (src == null) {
            src = new Vertex<>(source);
            this.vertices.add(src);
        }
        if (weight > 0 && tgt == null) {
            // ensure endpoint exists per spec
            tgt = new Vertex<>(target);
            this.vertices.add(tgt);
        }

        int prev = src.setTarget(target, weight);
        checkRep();
        return prev;
    }

    @Override
    public boolean remove(L vertex) {
        assert vertex != null : "vertex must be non-null";

        boolean found = false;
        // remove incoming edges to `vertex`
        for (Vertex<L> v : this.vertices) {
            v.setTarget(vertex, 0); // no-op if absent; removes if present
            if (v.getLabel().equals(vertex)) {
                found = true;
            }
        }
        if (!found) return false;

        // remove the vertex itself
        Iterator<Vertex<L>> it = this.vertices.iterator();
        while (it.hasNext()) {
            Vertex<L> v = it.next();
            if (v.getLabel().equals(vertex)) {
                it.remove();
                break;
            }
        }
        checkRep();
        return true;
    }

    @Override
    public Set<L> vertices() {
        Set<L> result = new HashSet<>();
        for (Vertex<L> v : this.vertices) {
            result.add(v.getLabel());
        }
        return result;
    }

    @Override
    public Map<L, Integer> sources(L target) {
        assert target != null : "target can't be null";
        Map<L, Integer> result = new HashMap<>();
        for (Vertex<L> v : this.vertices) {
            Map<L, Integer> m = v.getTargets(); // defensive copy
            Integer w = m.get(target);
            if (w != null) {
                result.put(v.getLabel(), w); // v → target
            }
        }
        return result;
    }

    @Override
    public Map<L, Integer> targets(L source) {
        assert source != null : "source can't be null";
        Map<L, Integer> result = new HashMap<>();
        for (Vertex<L> v : this.vertices) {
            if (v.getLabel().equals(source)) {
                // defensive copy to avoid exposing Vertex's internal map
                result = new HashMap<>(v.getTargets());
                break;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph:\n");
        for (Vertex<L> v : this.vertices) {
            sb.append("  ").append(v.toString()).append('\n');
        }
        return sb.toString();
    }
}

/**
 * Mutable vertex used internally by ConcreteVerticesGraph.
 */
class Vertex<L> {

    private final L label;
    private final Map<L, Integer> targets;

    // Abstraction function:
    //   AF(this) = a vertex named `label`, with outgoing edges
    //   { (label -> u, w) | (u, w) ∈ targets, w>0 }.
    //
    // Representation invariant:
    //   - label != null
    //   - targets != null
    //   - for every (u, w) in targets: u != null, w != null, w > 0
    //
    // Safety from rep exposure:
    //   - fields are private; label is immutable; targets never exposed directly
    //   - getters return defensive copies; mutators validate inputs

    public Vertex(L label) {
        assert label != null : "label cannot be null";
        this.label = label;
        this.targets = new HashMap<>();
        checkRep();
    }

    public Vertex(L label, Map<L, Integer> targets) {
        assert label != null : "label cannot be null";
        assert targets != null : "targets cannot be null";
        this.label = label;
        this.targets = new HashMap<>();
        for (Map.Entry<L, Integer> e : targets.entrySet()) {
            assert e.getKey() != null : "target key cannot be null";
            Integer w = e.getValue();
            assert w != null && w > 0 : "weight must be positive";
            this.targets.put(e.getKey(), w);
        }
        checkRep();
    }

    private void checkRep() {
        assert this.label != null : "label cannot be null";
        assert this.targets != null : "targets cannot be null";
        for (Map.Entry<L, Integer> e : this.targets.entrySet()) {
            assert e.getKey() != null : "target key cannot be null";
            Integer w = e.getValue();
            assert w != null && w > 0 : "weight must be positive";
        }
    }

    public L getLabel() { return this.label; }

    /** Defensive copy of outgoing edges map. */
    public Map<L, Integer> getTargets() { return new HashMap<>(this.targets); }

    /**
     * Add/update/remove the edge (this.label -> target) with given weight.
     * weight > 0 => put/update; weight == 0 => remove if present.
     * @return previous weight, or 0 if there was no such edge
     */
    public int setTarget(L target, int weight) {
        assert target != null : "target cannot be null";
        assert weight >= 0 : "weight must be nonnegative (0 means remove)";

        Integer prev = this.targets.get(target);

        if (weight == 0) {
            if (prev != null) {
                this.targets.remove(target);
                checkRep();
                return prev;
            } else {
                checkRep();
                return 0;
            }
        }

        // weight > 0
        this.targets.put(target, weight);
        checkRep();
        return prev == null ? 0 : prev;
    }

    /** Deep copy (new map). */
    public Vertex<L> copy() { return new Vertex<>(this.label, this.targets); }

    @Override
    public String toString() { return label + " -> " + targets; }
}
