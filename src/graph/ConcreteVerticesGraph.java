/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashMap;

import graph.ConcreteEdgesGraph.Edge;

import java.util.HashSet;
import java.util.Iterator;
/**
 * An implementation of Graph.
 * 
 * <p>PS2 instructions: you MUST use the provided rep.
 */
public class ConcreteVerticesGraph implements Graph<String> {
    
    private final List<Vertex> vertices = new ArrayList<>();
    
 // Abstraction function:
//  AF(this) = a single vertex in the graph with label = this.label,
//             and with outgoing edges defined by this.targets:
//             for each (v, w) in targets, there is a directed edge
//             from this.label -> v with weight w (> 0).

//Representation invariant:
//  - label != null
//  - targets != null
//  - for every key k in targets: k != null
//  - for every value w in targets: w > 0
//  - no duplicate keys in targets (enforced by Map)

//Safety from rep exposure:
//  - All fields are private.
//  - label is immutable (final String).
//  - targets is mutable but never returned directly;
//    the targets() observer returns a defensive copy.
//  - No mutator accepts or exposes external collections;
//    edges can only be changed via setTarget().
    
    public ConcreteVerticesGraph() {}
    public ConcreteVerticesGraph(List<Vertex> verticesIn) {
        this.vertices.clear(); // ensure starting empty
        for (Vertex v : verticesIn) {
            if (v == null) {
                throw new IllegalArgumentException("Vertex cannot be null");
            }
            this.vertices.add(v.copy()); // deep copy: same label + copy of targets
        }
        checkRep(); // verify rep invariant after initialization
    }
    
    // TODO checkRep
    
    private void checkRep() {
    	Set<String> labels = new HashSet<>();
    	for(Vertex v : this.vertices) {
    		assert v!= null : "vertex can't be null";
    		assert v.getLabel() != null : "label can't be null";
    		assert labels.add(v.getLabel()): "duplicates in labels";
    		for(Map.Entry<String, Integer> entry : v.getTargets().entrySet()) {
    			String key = entry.getKey();
    			Integer weight = entry.getValue();
    			assert key != null : "key can't be null";
    			assert weight != null : "weight can't be null";
    			assert weight > 0 : "weight has to be positive";
    		}
    	}
    }
    
    @Override
    public boolean add(String vertex) {
        assert vertex != null : "vertex must be non-null";
        for (Vertex v : this.vertices) {
            if (v.getLabel().equals(vertex)) {
                return false; // already present
            }
        }
        this.vertices.add(new Vertex(vertex));
        checkRep();
        return true;
    }

    
    @Override
    public int set(String source, String target, int weight) {
        // Preconditions
    	assert source != null;
    	assert target != null;
    	assert weight >= 0;
    	Vertex src = null, tgt = null;
    	for(Vertex v : this.vertices) {
    		if(v.getLabel().equals(source)) src = v;
    		if(v.getLabel().equals(target)) tgt = v;
    	}
    	if(src == null && weight == 0) {
    		return 0;
    	}
    	if(src == null) {
    		src = new Vertex(source);
    		this.vertices.add(src);
    	}
    	if(weight > 0 && tgt == null) {
    		tgt = new Vertex(target);
    		this.vertices.add(tgt);
    	}
    	int prev = src.setTarget(target, weight);
    	checkRep();
    	return prev;
    }

    @Override
    public boolean remove(String vertex) {
    	assert vertex != null;
    	boolean found = false;
    	for(Vertex v: this.vertices) {
    		v.setTarget(vertex, 0);
    		if(v.getLabel().equals(vertex)) {
    			found = true;
    		}
    	}
    	if(!found) {
    		return false;
    	}
    	Iterator<Vertex> it = this.vertices.iterator();
    	while(it.hasNext()) {
    		Vertex v = it.next();
    		if(v.getLabel().equals(vertex)) {
    			it.remove();
    			break;
    		}
    	}
    	checkRep();
    	return true;
    }

    
    @Override public Set<String> vertices() {
        Set<String> result = new HashSet<>();
        for (Vertex v: this.vertices) {
        	result.add(v.getLabel());
        }
        return result;
    }
    
    @Override public Map<String, Integer> sources(String target) {
      assert target != null: "target can't be null";
      Map<String, Integer> result = new HashMap<>();
      for (Vertex v: this.vertices) {
    	  Map<String, Integer> m = v.getTargets();
    	  Integer w = m.get(target);
    	  if(w != null) {
    		  result.put(v.getLabel(), w);
    	  }
      }
      return result;
    }
    
    @Override
    public Map<String, Integer> targets(String source) {
        assert source != null : "source can't be null";

        // default: empty result if the source vertex doesn't exist
        Map<String, Integer> result = new HashMap<>();

        for (Vertex v : this.vertices) {
            if (v.getLabel().equals(source)) {
                // defensive copy to avoid exposing the Vertex's internal map
                result = new HashMap<>(v.getTargets());
                break; // labels are unique; we can stop here
            }
        }
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph with vertices:\n");
        for (Vertex v : this.vertices) {
            sb.append("  ")
              .append(v.getLabel())
              .append(" -> ")
              .append(v.getTargets())
              .append("\n");
        }
        return sb.toString();
    }
}

/**
 * TODO specification
 * Mutable.
 * This class is internal to the rep of ConcreteVerticesGraph.
 * 
 * <p>PS2 instructions: the specification and implementation of this class is
 * up to you.
 */
class Vertex {
    
    // TODO fields
	private final String label;                // the vertex's name
	private final Map<String, Integer> targets; // outgoing edges: neighbor -> weight

    
//  AF(this) = a graph vertex v
//    - v has name/label = this.label
//    - for each (u, w) in this.targets,
//        there is a directed edge from v to vertex u
//        with nonzero weight w (>0).
	// Representation invariant:
//  - label != null
//  - targets != null
//  - For every entry (u, w) in targets:
//      * u != null
//      * w != null
//      * w > 0              // only positive weights are stored
//  - No duplicate keys in targets (ensured by Map)
//  - (Self-loops allowed unless the overall Graph spec forbids them;
//     if forbidden, additionally require: for all u in targets, !u.equals(label))

//Safety from rep exposure:
//  - All fields are private; references to them are not leaked.
//  - label is immutable (String) and the field reference is final.
//  - targets is a private mutable Map, but:
//      * observers (e.g., getTargets()) return a defensive copy,
//        never the internal map itself.
//      * mutators (e.g., setTarget/removeTarget) validate inputs and
//        update the internal map without exposing it.
//  - No mutator accepts or stores external mutable collections directly;
//    if a constructor or copy method takes a map, it must defensively copy it.
//  - Any copy/clone operation of Vertex performs a deep copy of `targets`
//    (new Map with the same (neighbor → weight) pairs).
	
	public Vertex(String label) {
	    assert label != null : "label cannot be null";
	    this.label = label;
	    this.targets = new HashMap<>();
	    checkRep();
	}

	
	public Vertex(String label, Map<String, Integer> targets) {
	    assert label != null : "label cannot be null";
	    assert targets != null : "targets cannot be null";

	    this.label = label;
	    this.targets = new HashMap<>();

	    for (Map.Entry<String, Integer> e : targets.entrySet()) {
	        assert e.getKey() != null : "target key cannot be null";
	        assert e.getValue() != null : "weight cannot be null";
	        assert e.getValue() > 0 : "weight must be positive";

	        this.targets.put(e.getKey(), e.getValue());
	    }

	    checkRep();
	}

	private void checkRep() {
	    assert this.label != null : "label cannot be null";
	    assert this.targets != null : "targets cannot be null";

	    for (Map.Entry<String, Integer> e : this.targets.entrySet()) {
	        assert e.getKey() != null : "target key cannot be null";
	        assert e.getValue() != null : "weight cannot be null";
	        assert e.getValue() > 0 : "weight must be positive";
	     
	    }
	}

	
    public String getLabel() {
    	return this.label;
    }
    
    public Map<String, Integer> getTargets(){
    	Map<String, Integer> results = new HashMap<>();
    	for(Map.Entry<String, Integer> e : this.targets.entrySet()) {
    		results.put(e.getKey(), e.getValue());
    	}
    	return results;
    }
    
 // In Vertex
    public int setTarget(String target, int weight) {
        assert target != null : "target cannot be null";
        assert weight >= 0 : "weight must be nonnegative (0 means remove)";

        Integer prev = this.targets.get(target);

        if (weight == 0) {
            // remove edge if present
            if (prev != null) {
                this.targets.remove(target);
                checkRep();
                return prev;
            } else {
                // nothing to remove
                checkRep();
                return 0;
            }
        }

        // weight > 0: add or update
        this.targets.put(target, weight);
        checkRep();
        return prev == null ? 0 : prev;
    }
    
    
    public Vertex copy() {
    	return new Vertex(this.label, this.targets);
    }
    
    
    @Override
    public String toString() {
        return label + " -> " + targets;
    }

    
}
