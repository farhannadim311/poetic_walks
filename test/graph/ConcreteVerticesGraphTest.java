/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package graph;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Collections;

import java.util.Map;
/**
 * Tests for ConcreteVerticesGraph.
 * 
 * This class runs the GraphInstanceTest tests against ConcreteVerticesGraph, as
 * well as tests for that particular implementation.
 * 
 * Tests against the Graph spec should be in GraphInstanceTest.
 */
public class ConcreteVerticesGraphTest extends GraphInstanceTest {
    
    /*
     * Provide a ConcreteVerticesGraph for tests in GraphInstanceTest.
     */
    @Override public Graph<String> emptyInstance() {
        return new ConcreteVerticesGraph();
    }
    
    /*
     * Testing ConcreteVerticesGraph...
     */
    
    // Testing strategy for ConcreteVerticesGraph.toString()
    //   TODO
    @Test
    public void testToStringEmptyGraph() {
    	Graph<String> g = emptyInstance();
    	String s = g.toString();
    	assertNotNull(s);
    	assertFalse(s.contains("A"));
    }
    
   
    @Test
    public void testToStringWithEdges() {
    	Graph<String> g = emptyInstance();
    	g.set("A", "B", 3);
    	g.set("A", "C", 5);
    	String s = g.toString();
    	assertTrue(s.contains("A"));
    	assertTrue(s.contains("B"));
    	assertTrue(s.contains("C"));
    	assertTrue(s.contains("3"));
    	assertTrue(s.contains("5"));
    }
    
    @Test
    public void testToStringReflectsUpdates() {
        Graph<String> g = emptyInstance();
        g.set("X", "Y", 2);
        String before = g.toString();
        assertTrue(before.contains("2"));
        g.set("X", "Y", 9);
        String after = g.toString();
        assertTrue(after.contains("9"));
    }
    
    @Test
    public void testVertexLabel() {
        Vertex v = new Vertex("A");
        // expect a label() observer
        assertEquals("A", v.getLabel());
    }
    
    @Test
    public void testVertexTargetsAddAndGet() {
        Vertex v = new Vertex("A");
        int prev = v.setTarget("B", 4);     // add new edge A->B(4)
        assertEquals(0, prev);
        Map<String,Integer> t = v.getTargets();
        assertEquals(Collections.singletonMap("B", 4), t);
    }

    @Test
    public void testVertexUpdateReturnsOldAndReflectsNew() {
        Vertex v = new Vertex("A");
        v.setTarget("B", 3);
        int prev = v.setTarget("B", 7);     // update A->B from 3 to 7
        assertEquals(3, prev);
        assertEquals(Collections.singletonMap("B", 7), v.getTargets());
    }
    
    @Test
    public void testVertexRemoveTargetWithZeroWeight() {
        Vertex v = new Vertex("A");
        v.setTarget("B", 5);
        int prev = v.setTarget("B", 0);     // remove edge
        assertEquals(5, prev);
        assertTrue(v.getTargets().isEmpty());
    }
    
    @Test
    public void testVertexTargetsIsDefensiveCopy() {
        Vertex v = new Vertex("A");
        v.setTarget("B", 1);
        Map<String,Integer> copy = v.getTargets();
        copy.put("C", 2); // mutate the returned map
        // original should be unchanged
        assertEquals(Collections.singletonMap("B", 1), v.getTargets());
    }
    
    @Test(expected = AssertionError.class)
    public void testVertexRejectsNegativeWeightWithAsserts() {
        Vertex v = new Vertex("A");
        v.setTarget("B", -1); // should assert-fail if -ea is enabled
    }

    @Test
    public void testVertexToStringContainsLabelAndEdges() {
        Vertex v = new Vertex("A");
        v.setTarget("B", 2);
        v.setTarget("C", 3);
        String s = v.toString();
        assertTrue(s.contains("A"));
        assertTrue(s.contains("B"));
        assertTrue(s.contains("C"));
        assertTrue(s.contains("2"));
        assertTrue(s.contains("3"));
    }
    
    // TODO tests for ConcreteVerticesGraph.toString()
    
    /*
     * Testing Vertex...
     */
    
    // Testing strategy for Vertex
    //   TODO
    
    // TODO tests for operations of Vertex
    
}
