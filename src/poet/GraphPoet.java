/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package poet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import graph.ConcreteEdgesGraph;
import graph.Graph;

/**
 * A graph-based poetry generator.
 * 
 * <p>GraphPoet is initialized with a corpus of text, which it uses to derive a
 * word affinity graph.
 * Vertices in the graph are words. Words are defined as non-empty
 * case-insensitive strings of non-space non-newline characters. They are
 * delimited in the corpus by spaces, newlines, or the ends of the file.
 * Edges in the graph count adjacencies: the number of times "w1" is followed by
 * "w2" in the corpus is the weight of the edge from w1 to w2.
 * 
 * <p>For example, given this corpus:
 * <pre>    Hello, HELLO, hello, goodbye!    </pre>
 * <p>the graph would contain two edges:
 * <ul><li> ("hello,") -> ("hello,")   with weight 2
 *     <li> ("hello,") -> ("goodbye!") with weight 1 </ul>
 * <p>where the vertices represent case-insensitive {@code "hello,"} and
 * {@code "goodbye!"}.
 * 
 * <p>Given an input string, GraphPoet generates a poem by attempting to
 * insert a bridge word between every adjacent pair of words in the input.
 * The bridge word between input words "w1" and "w2" will be some "b" such that
 * w1 -> b -> w2 is a two-edge-long path with maximum-weight weight among all
 * the two-edge-long paths from w1 to w2 in the affinity graph.
 * If there are no such paths, no bridge word is inserted.
 * In the output poem, input words retain their original case, while bridge
 * words are lower case. The whitespace between every word in the poem is a
 * single space.
 * 
 * <p>For example, given this corpus:
 * <pre>    This is a test of the Mugar Omni Theater sound system.    </pre>
 * <p>on this input:
 * <pre>    Test the system.    </pre>
 * <p>the output poem would be:
 * <pre>    Test of the system.    </pre>
 * 
 * <p>PS2 instructions: this is a required ADT class, and you MUST NOT weaken
 * the required specifications. However, you MAY strengthen the specifications
 * and you MAY add additional methods.
 * You MUST use Graph in your rep, but otherwise the implementation of this
 * class is up to you.
 */
public class GraphPoet {
    
    private final Graph<String> graph = Graph.empty();
    
 // Abstraction function:
//  AF(this) = a word-affinity graph built from the corpus, where
//    - each vertex is a (case-insensitive) word appearing in the corpus
//    - for each adjacent pair (w1, w2) in the corpus, graph contains a
//      directed edge w1 -> w2 with weight equal to the number of times
//      w1 is immediately followed by w2 in the corpus.
//  The poem(input) operation inserts, between every adjacent pair (a, c)
//  of input words, the bridge word b (lowercase) that maximizes
//  weight(a -> b) + weight(b -> c) over all b present in the graph,
//  if any such two-edge path exists; otherwise inserts nothing.
//
//Representation invariant:
//  - graph != null
//  - All vertex labels in `graph` are nonempty strings as defined by the spec
//    (non-space, non-newline sequences), normalized case-insensitively
//    (i.e., vertices correspond to lowercased forms).
//  - All edge weights in `graph` are strictly positive integers.
//  - For every edge (u -> v) in `graph`, both u and v are vertices in `graph`.
//  - There is at most one stored edge per (u, v) pair; its weight equals
//    the corpus adjacency count for that pair.
//
//Safety from rep exposure:
//  - `graph` is private and final; clients cannot access or reassign it.
//  - No mutator or observer returns the internal `graph` or any of its
//    mutable collections directly; methods that expose data return fresh
//    defensive copies or derived immutable values (Strings, ints).
//  - Construction from the corpus adds vertices/edges only via the Graph API;
//    inputs are parsed and normalized before insertion; no external mutable
//    collections are stored.

    
    /**
     * Create a new poet with the graph from corpus (as described above).
     * 
     * @param corpus text file from which to derive the poet's affinity graph
     * @throws IOException if the corpus file cannot be found or read
     */
    public GraphPoet(File corpus) throws IOException {
    	try (Scanner sc = new Scanner(corpus)){
    		if(!sc.hasNext()) {
    			return;
    		}
    		String prev = sc.next().toLowerCase(Locale.ROOT);
    		while(sc.hasNext()) {
    			String curr = sc.next().toLowerCase(Locale.ROOT);
    			Integer old = graph.targets(prev).get(curr);
    			int newWeight = (old == null ? 1 : old + 1);
    			graph.set(prev, curr, newWeight);
    			prev = curr;
    		}
    	}
        
    }
    
    
    
    // TODO checkRep
    private void checkRep() {
        assert graph != null : "graph must not be null";

        for (String v : graph.vertices()) {
            assert v.equals(v.toLowerCase(Locale.ROOT)) : "vertex labels must be lowercase";
            assert !v.isEmpty() : "vertex labels must not be empty";

            for (Map.Entry<String, Integer> e : graph.targets(v).entrySet()) {
                String target = e.getKey();
                Integer weight = e.getValue();

                assert target != null : "target vertex must not be null";
                assert target.equals(target.toLowerCase(Locale.ROOT)) : "target must be lowercase";
                assert weight != null : "edge weight must not be null";
                assert weight > 0 : "edge weight must be positive";
            }
        }
    }

    /**
     * Generate a poem.
     * 
     * @param input string from which to create the poem
     * @return poem (as described above)
     */
    public String poem(String input) {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return "";

        String[] words = trimmed.split("\\s+");
        StringBuilder out = new StringBuilder();
        out.append(words[0]); // keep original case

        for (int i = 0; i < words.length - 1; i++) {
            String w1Orig = words[i];
            String w2Orig = words[i + 1];
            String w1 = w1Orig.toLowerCase();
            String w2 = w2Orig.toLowerCase();

            String bestBridge = null;
            int bestScore = -1;

            Map<String, Integer> fromW1 = graph.targets(w1);
            if (!fromW1.isEmpty()) {
                for (Map.Entry<String, Integer> e1 : fromW1.entrySet()) {
                    String b = e1.getKey();
                    int w1b = e1.getValue();
                    Integer bw2 = graph.targets(b).get(w2);
                    if (bw2 != null) {
                        int score = w1b + bw2;
                        if (score > bestScore) {
                            bestScore = score;
                            bestBridge = b; // bridge is lowercased
                        }
                    }
                }
            }

            if (bestBridge != null) {
                out.append(' ').append(bestBridge);
            }
            out.append(' ').append(w2Orig); // keep original case
        }

        return out.toString();
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GraphPoet with word-affinity graph:\n");
        for (String v : graph.vertices()) {
            sb.append("  ").append(v).append(" -> ").append(graph.targets(v)).append("\n");
        }
        return sb.toString();
    }
    
}
class Pair<A, B> {
    public final A first;
    public final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair<?,?> p = (Pair<?,?>) o;
        return first.equals(p.first) && second.equals(p.second);
    }

    @Override
    public int hashCode() {
        return first.hashCode() * 31 + second.hashCode();
    }
}
