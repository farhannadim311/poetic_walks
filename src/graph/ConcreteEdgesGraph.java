package graph;

import java.util.*;

/**
 * An implementation of Graph using a vertex set and a flat list of immutable edges.
 */
public class ConcreteEdgesGraph<L> implements Graph<L> {

    // ----- Rep -----
    private final Set<L> vertices = new HashSet<>();
    private final List<Edge<L>> edges = new ArrayList<>();

    // ----- AF / RI / Safety -----
    // AF(vertices, edges) = directed, weighted graph G = (V, E)
    //   V = exactly the labels in `vertices`
    //   E = { (e.source, e.target, e.weight) | e in `edges` with weight > 0 }
    //
    // RI:
    //   - vertices != null, edges != null
    //   - no null vertex; no null edge
    //   - each edge has non-null endpoints and weight > 0
    //   - every edge endpoint is in vertices
    //   - no duplicate parallel edges (at most one per (source,target))
    //
    // Safety:
    //   - never return the mutable reps directly
    //   - vertices() returns a defensive copy
    //   - sources()/targets() return fresh maps
    //   - Edge is immutable

    // ----- Constructors -----

    /** Empty graph. */
    public ConcreteEdgesGraph() { }

    /** Optional convenience constructor (defensive copy). */
    public ConcreteEdgesGraph(Set<L> vertices, List<Edge<L>> edges) {
        if (vertices != null) this.vertices.addAll(vertices);
        if (edges != null)    this.edges.addAll(edges);
        checkRep();
    }

    // ----- checkRep -----
    private void checkRep() {
        assert this.vertices != null : "vertices set is null";
        assert this.edges != null    : "edges list is null";

        for (L v : this.vertices) {
            assert v != null : "null vertex";
        }

        Set<Pair<L, L>> seenPairs = new HashSet<>();
        for (Edge<L> e : this.edges) {
            assert e != null : "null edge";
            assert e.getSource() != null : "null edge source";
            assert e.getTarget() != null : "null edge target";
            assert e.getWeight() > 0     : "nonpositive weight stored: " + e.getWeight();
            assert this.vertices.contains(e.getSource()) : "edge source not in vertices";
            assert this.vertices.contains(e.getTarget()) : "edge target not in vertices";
            Pair<L, L> key = new Pair<>(e.getSource(), e.getTarget());
            assert seenPairs.add(key) : "duplicate edge " + e.getSource() + "->" + e.getTarget();
        }
    }

    // ----- Graph<L> methods -----

    @Override
    public boolean add(L vertex) {
        assert vertex != null : "vertex must be non-null";
        if (this.vertices.contains(vertex)) {
            return false;
        }
        this.vertices.add(vertex);
        checkRep();
        return true;
    }

    @Override
    public int set(L source, L target, int weight) {
        assert source != null : "source must be non-null";
        assert target != null : "target must be non-null";
        assert weight >= 0    : "weight must be nonnegative (0 means remove)";

        // Look for existing (source -> target)
        Iterator<Edge<L>> it = this.edges.iterator();
        while (it.hasNext()) {
            Edge<L> e = it.next();
            if (e.getSource().equals(source) && e.getTarget().equals(target)) {
                int prev = e.getWeight();
                if (weight == 0) {
                    // remove edge
                    it.remove();
                    checkRep();
                    return prev;
                } else {
                    // update edge (replace to keep Edge immutable)
                    it.remove();
                    this.vertices.add(source);
                    this.vertices.add(target);
                    this.edges.add(new Edge<>(source, target, weight));
                    checkRep();
                    return prev;
                }
            }
        }

        // No existing edge
        if (weight == 0) {
            checkRep();
            return 0; // no-op
        } else {
            // add new edge; auto-add missing vertices
            this.vertices.add(source);
            this.vertices.add(target);
            this.edges.add(new Edge<>(source, target, weight));
            checkRep();
            return 0;
        }
    }

    @Override
    public boolean remove(L vertex) {
        assert vertex != null : "vertex must be non-null";
        if (!this.vertices.contains(vertex)) {
            return false;
        }
        // remove incident edges
        Iterator<Edge<L>> it = this.edges.iterator();
        while (it.hasNext()) {
            Edge<L> e = it.next();
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
    public Set<L> vertices() {
        return new HashSet<>(this.vertices);
    }

    @Override
    public Map<L, Integer> sources(L target) {
        assert target != null : "target must be non-null";
        Map<L, Integer> result = new HashMap<>();
        for (Edge<L> e : this.edges) {
            if (e.getTarget().equals(target)) {
                result.put(e.getSource(), e.getWeight());
            }
        }
        return result;
    }

    @Override
    public Map<L, Integer> targets(L source) {
        assert source != null : "source must be non-null";
        Map<L, Integer> result = new HashMap<>();
        for (Edge<L> e : this.edges) {
            if (e.getSource().equals(source)) {
                result.put(e.getTarget(), e.getWeight());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Vertices: " + this.vertices + "\nEdges: " + this.edges;
    }

    // ----- Immutable Edge value object -----
    public static final class Edge<L> {
        private final L source;
        private final L target;
        private final int weight;

        public Edge(L source, L target, int weight) {
            assert source != null : "Edge source cannot be null";
            assert target != null : "Edge target cannot be null";
            assert weight > 0     : "Edge weight must be positive";
            this.source = source;
            this.target = target;
            this.weight = weight;
        }

        public L getSource()  { return source; }
        public L getTarget()  { return target; }
        public int getWeight(){ return weight; }

        @Override
        public String toString() {
            return source + " -> " + target + " (" + weight + ")";
        }
    }

    // ----- Generic Pair for duplicate detection -----
    public static final class Pair<T1, T2> {
        public final T1 first;
        public final T2 second;

        public Pair(T1 first, T2 second) {
            this.first  = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pair)) return false; // raw type on instanceof is required
            Pair<?, ?> other = (Pair<?, ?>) o;
            return Objects.equals(this.first, other.first)
                && Objects.equals(this.second, other.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.first, this.second);
        }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }
    }
}
