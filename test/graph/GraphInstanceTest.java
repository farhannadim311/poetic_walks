/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

import java.util.HashSet;

import java.util.Set;

import java.util.Map;

import java.util.HashMap;

import java.util.Arrays;

/**
 * Tests for instance methods of Graph.
 * 
 * <p>PS2 instructions: you MUST NOT add constructors, fields, or non-@Test
 * methods to this class, or change the spec of {@link #emptyInstance()}.
 * Your tests MUST only obtain Graph instances by calling emptyInstance().
 * Your tests MUST NOT refer to specific concrete implementations.
 */
public abstract class GraphInstanceTest {
    
    // Testing strategy
    //   TODO
    
    /**
     * Overridden by implementation-specific test classes.
     * 
     * @return a new empty graph of the particular implementation being tested
     */
    public abstract Graph<String> emptyInstance();
    
    
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testInitialVerticesEmpty() {
        // TODO you may use, change, or remove this test
        assertEquals("expected new graph to have no vertices",
                Collections.emptySet(), emptyInstance().vertices());
    }
    
    @Test
    public void testAddNewVertexReturnsTrue() {
    	Graph <String> g = emptyInstance();
    	boolean added = g.add("A");
    	assertTrue("Adding a new vertex should return true", added);
    	assertEquals("Vertex should contain A after first add", new java.util.HashSet<>(java.util.Arrays.asList("A")), g.vertices());
    		
    	}
    
    @Test
    public void testAddDuplicateVertexReturnsFalse() {
        Graph<String> g = emptyInstance();
        g.add("A");

        boolean addedAgain = g.add("A");

        assertFalse("Adding a duplicate vertex should return false", addedAgain);
    }

    @Test
    public void testDuplicateAddDoesNotModifyVertices() {
        Graph<String> g = emptyInstance();
        g.add("A");
        java.util.Set<String> before = new java.util.HashSet<>(g.vertices());

        boolean addedAgain = g.add("A");
        java.util.Set<String> after = g.vertices();

        assertFalse("Duplicate add should return false", addedAgain);
        assertEquals("Vertex set must be unchanged after duplicate add", before, after);
    }
    
    @Test
    public void testSetAddNewEdgeAddsVerticesIfMissingAndReturnsZero() {
        Graph<String> g = emptyInstance();

        int prev = g.set("A", "B", 3);  // new edge A->B with weight 3

        assertEquals("Previous weight should be 0 for a new edge", 0, prev);
        assertTrue("Source vertex should exist", g.vertices().contains("A"));
        assertTrue("Target vertex should exist", g.vertices().contains("B"));

        Map<String,Integer> expTargetsA = new HashMap<>();
        expTargetsA.put("B", 3);
        assertEquals("targets(A) should include B=3", expTargetsA, g.targets("A"));

        Map<String,Integer> expSourcesB = new HashMap<>();
        expSourcesB.put("A", 3);
        assertEquals("sources(B) should include A=3", expSourcesB, g.sources("B"));
    }

    @Test
    public void testSetUpdateExistingEdgeReturnsOldWeightAndOverwrites() {
        Graph<String> g = emptyInstance();
        g.set("A", "B", 3);                 // create edge
        int prev = g.set("A", "B", 7);      // update weight

        assertEquals("Previous weight should be 3", 3, prev);

        Map<String,Integer> expTargetsA = new HashMap<>();
        expTargetsA.put("B", 7);
        assertEquals("targets(A) should show updated weight 7", expTargetsA, g.targets("A"));

        Map<String,Integer> expSourcesB = new HashMap<>();
        expSourcesB.put("A", 7);
        assertEquals("sources(B) should show updated weight 7", expSourcesB, g.sources("B"));
    }

    @Test
    public void testSetZeroRemovesExistingEdgeButKeepsVertices() {
        Graph<String> g = emptyInstance();
        g.set("A", "B", 5);             // ensure edge exists
        Set<String> vertsBefore = new HashSet<>(g.vertices());

        int prev = g.set("A", "B", 0);  // remove edge

        assertEquals("Previous weight should be 5 when removing", 5, prev);
        assertEquals("Vertices should be unchanged when removing edge", vertsBefore, g.vertices());
        assertTrue("A should still be a vertex", g.vertices().contains("A"));
        assertTrue("B should still be a vertex", g.vertices().contains("B"));

        assertTrue("targets(A) should be empty after removal", g.targets("A").isEmpty());
        assertTrue("sources(B) should be empty after removal", g.sources("B").isEmpty());
    }

    @Test
    public void testSetZeroOnNonExistingEdgeBetweenExistingVerticesNoChange() {
        Graph<String> g = emptyInstance();
        g.add("A");
        g.add("B");
        Set<String> vertsBefore = new HashSet<>(g.vertices());

        int prev = g.set("A", "B", 0);  // no such edge yet

        assertEquals("Previous weight should be 0 if edge didn’t exist", 0, prev);
        assertEquals("No change to vertex set for zero-weight on non-existing edge",
                     vertsBefore, g.vertices());
        assertTrue("targets(A) should remain empty", g.targets("A").isEmpty());
        assertTrue("sources(B) should remain empty", g.sources("B").isEmpty());
    }

    @Test
    public void testSetZeroWithBothVerticesMissingDoesNotAddVertices() {
        Graph<String> g = emptyInstance();
        Set<String> emptyVerts = g.vertices(); // should be empty

        int prev = g.set("X", "Y", 0);  // zero weight: must NOT modify graph

        assertEquals("Previous weight should be 0", 0, prev);
        assertEquals("Graph should remain unmodified (no vertices added)", emptyVerts, g.vertices());
    }
   
    @Test
    public void testRemoveExistingVertexReturnsTrueAndDeletesVertex() {
        Graph<String> g = emptyInstance();
        g.add("A");

        boolean removed = g.remove("A");

        assertTrue("Removing an existing vertex should return true", removed);
        assertFalse("Vertex should be gone after removal", g.vertices().contains("A"));
    }

    @Test
    public void testRemoveNonexistentVertexReturnsFalseAndDoesNotModifyGraph() {
        Graph<String> g = emptyInstance();
        g.add("A");
        g.add("B");
        g.set("A", "B", 5); // some structure to ensure no change

        Set<String> vertsBefore = new HashSet<>(g.vertices());
        Map<String,Integer> targetsABefore = new HashMap<>(g.targets("A"));
        Map<String,Integer> sourcesBBefore = new HashMap<>(g.sources("B"));

        boolean removed = g.remove("Z"); // doesn't exist

        assertFalse("Removing a non-existent vertex should return false", removed);
        assertEquals("Vertices should be unchanged", vertsBefore, g.vertices());
        assertEquals("targets(A) should be unchanged", targetsABefore, g.targets("A"));
        assertEquals("sources(B) should be unchanged", sourcesBBefore, g.sources("B"));
    }

    @Test
    public void testRemoveVertexDeletesAllIncidentEdges() {
        Graph<String> g = emptyInstance();
        // Build: C -> A (7), A -> B (5), A -> A (self-loop 9)
        g.set("C", "A", 7);
        g.set("A", "B", 5);
        g.set("A", "A", 9);

        boolean removed = g.remove("A");
        assertTrue("Should return true when removing existing vertex", removed);

        // A is gone
        assertFalse("Removed vertex should not be present", g.vertices().contains("A"));

        // All incident edges of A should be gone
        assertTrue("sources(B) should not include A anymore", g.sources("B").isEmpty());
        assertTrue("targets(C) should not include A anymore", g.targets("C").isEmpty());
        // Self-loop is also gone implicitly since A no longer exists

        // C and B remain as vertices
        assertTrue(g.vertices().contains("B"));
        assertTrue(g.vertices().contains("C"));
    }

    @Test
    public void testRemoveVertexDoesNotAffectEdgesBetweenOtherVertices() {
        Graph<String> g = emptyInstance();
        // A -> B, and an unrelated edge B -> C
        g.set("A", "B", 4);
        g.set("B", "C", 2);

        boolean removed = g.remove("A");
        assertTrue(removed);

        // Edge B -> C should be intact
        assertEquals("targets(B) should still include C=2 after removing A",
                Collections.singletonMap("C", 2), g.targets("B"));
        assertEquals("sources(C) should still include B=2 after removing A",
                Collections.singletonMap("B", 2), g.sources("C"));
        // A is gone
        assertFalse(g.vertices().contains("A"));
    }

    @Test
    public void testRemoveLastVertexFromSingleVertexGraph() {
        Graph<String> g = emptyInstance();
        g.add("Solo");

        boolean removed = g.remove("Solo");
        assertTrue(removed);
        assertTrue("Graph should be empty after removing its only vertex",
                g.vertices().isEmpty());
    }
    @Test
    public void testVerticesAfterRemovingVertex() {
        Graph<String> g = emptyInstance();
        g.add("A");
        g.add("B");
        g.add("C");
        g.remove("B");

        Set<String> expected = new HashSet<>(Arrays.asList("A", "C"));
        assertEquals("Graph should contain only A and C after removing B", expected, g.vertices());
    }

    @Test
    public void testVerticesUnchangedByDuplicateAdd() {
        Graph<String> g = emptyInstance();
        g.add("X");
        Set<String> before = new HashSet<>(g.vertices());

        g.add("X");  // duplicate add, should not modify
        Set<String> after = g.vertices();

        assertEquals("Vertices should not change after duplicate add", before, after);
    }
    // TODO other tests for instance methods of Graph
    
        @Test
        public void testSourcesEmptyGraph() {
            Graph<String> g = emptyInstance();

            assertTrue("sources() on empty graph should return empty map",
                       g.sources("A").isEmpty());
        }

        @Test
        public void testSourcesNoIncomingEdges() {
            Graph<String> g = emptyInstance();
            g.add("A");
            g.add("B");
            g.set("A", "B", 5);  // edge A -> B

            assertTrue("sources(A) should be empty since nothing points to A",
                       g.sources("A").isEmpty());
        }

        @Test
        public void testSourcesSingleIncomingEdge() {
            Graph<String> g = emptyInstance();
            g.set("A", "B", 4);  // A -> B

            Map<String,Integer> expected = Collections.singletonMap("A", 4);
            assertEquals("sources(B) should contain A=4", expected, g.sources("B"));
        }

        @Test
        public void testSourcesMultipleIncomingEdges() {
            Graph<String> g = emptyInstance();
            g.set("A", "C", 2);  // A -> C
            g.set("B", "C", 3);  // B -> C

            Map<String,Integer> expected = new HashMap<>();
            expected.put("A", 2);
            expected.put("B", 3);

            assertEquals("sources(C) should contain both A=2 and B=3", expected, g.sources("C"));
        }

        @Test
        public void testSourcesUpdatedEdgeReflectsNewWeight() {
            Graph<String> g = emptyInstance();
            g.set("X", "Y", 7);
            g.set("X", "Y", 9);  // update weight

            Map<String,Integer> expected = Collections.singletonMap("X", 9);
            assertEquals("sources(Y) should reflect updated weight 9", expected, g.sources("Y"));
        }

        @Test
        public void testSourcesAfterRemovingEdge() {
            Graph<String> g = emptyInstance();
            g.set("M", "N", 5);
            g.set("M", "N", 0);  // remove edge

            assertTrue("sources(N) should be empty after edge removal",
                       g.sources("N").isEmpty());
        }
            @Test
            public void testTargetsEmptyGraph() {
                Graph<String> g = emptyInstance();

                assertTrue("targets() on empty graph should return empty map",
                           g.targets("A").isEmpty());
            }

            @Test
            public void testTargetsNoOutgoingEdges() {
                Graph<String> g = emptyInstance();
                g.add("A");
                g.add("B");
                g.set("B", "A", 5);  // edge B -> A

                assertTrue("targets(A) should be empty since A has no outgoing edges",
                           g.targets("A").isEmpty());
            }

            @Test
            public void testTargetsSingleOutgoingEdge() {
                Graph<String> g = emptyInstance();
                g.set("A", "B", 4);  // A -> B

                Map<String,Integer> expected = Collections.singletonMap("B", 4);
                assertEquals("targets(A) should contain B=4", expected, g.targets("A"));
            }

            @Test
            public void testTargetsMultipleOutgoingEdges() {
                Graph<String> g = emptyInstance();
                g.set("A", "B", 2);  // A -> B
                g.set("A", "C", 3);  // A -> C

                Map<String,Integer> expected = new HashMap<>();
                expected.put("B", 2);
                expected.put("C", 3);

                assertEquals("targets(A) should contain both B=2 and C=3",
                             expected, g.targets("A"));
            }

            @Test
            public void testTargetsUpdatedEdgeReflectsNewWeight() {
                Graph<String> g = emptyInstance();
                g.set("X", "Y", 7);
                g.set("X", "Y", 9);  // update weight

                Map<String,Integer> expected = Collections.singletonMap("Y", 9);
                assertEquals("targets(X) should reflect updated weight 9",
                             expected, g.targets("X"));
            }

            @Test
            public void testTargetsAfterRemovingEdge() {
                Graph<String> g = emptyInstance();
                g.set("M", "N", 5);
                g.set("M", "N", 0);  // remove edge

                assertTrue("targets(M) should be empty after edge removal",
                           g.targets("M").isEmpty());
            
            
    
            }
}
