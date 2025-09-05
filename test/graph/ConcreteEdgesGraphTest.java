package graph;

import static org.junit.Assert.*;

import org.junit.Test;
import java.util.*;

/**
 * Tests for ConcreteEdgesGraph.
 */
public class ConcreteEdgesGraphTest extends GraphInstanceTest {

    /*
     * Provide a ConcreteEdgesGraph for tests in GraphInstanceTest.
     */
    @Override public Graph<String> emptyInstance() {
        return new ConcreteEdgesGraph();
    }

    /* ================================
     * Tests for ConcreteEdgesGraph.toString()
     * ================================ */

    @Test
    public void testToStringEmpty() {
        Graph<String> g = emptyInstance();
        String s = g.toString();
        // Should not crash; basic structure present
        assertNotNull(s);
        // No user labels yet
        assertFalse(s.contains("A"));
        assertFalse(s.contains("B"));
    }

    @Test
    public void testToStringVerticesOnly() {
        Graph<String> g = emptyInstance();
        g.add("A");
        g.add("B");

        String s = g.toString();
        assertTrue("should mention A", s.contains("A"));
        assertTrue("should mention B", s.contains("B"));
        // No explicit edge substring required; just make sure no unrelated label
        assertFalse(s.contains("Z"));
    }

    @Test
    public void testToStringSingleEdge() {
        Graph<String> g = emptyInstance();
        g.set("A", "B", 5);

        String s = g.toString();
        assertTrue("mentions source A", s.contains("A"));
        assertTrue("mentions target B", s.contains("B"));
        assertTrue("mentions weight 5", s.contains("5"));
    }

    @Test
    public void testToStringMultipleEdgesAndUpdates() {
        Graph<String> g = emptyInstance();
        g.set("A", "B", 2);
        g.set("A", "C", 3);
        g.set("C", "C", 7); // self-loop allowed
        String s1 = g.toString();

        assertTrue(s1.contains("A"));
        assertTrue(s1.contains("B"));
        assertTrue(s1.contains("C"));
        assertTrue(s1.contains("2")); // A->B(2)
        assertTrue(s1.contains("3")); // A->C(3)
        assertTrue(s1.contains("7")); // C->C(7)

        // Update A->B to 9; string should reflect new weight
        g.set("A", "B", 9);
        String s2 = g.toString();
        assertTrue("updated weight visible", s2.contains("9"));
        // It's possible "2" appears elsewhere, so we don't strictly assert !contains("2")
    }

    /* ================================
     * Tests for Edge (inner class)
     * ================================ */

    // These tests assume Edge is package-private and accessible from the same package.

    @Test
    public void testEdgeGettersAndToString() {
        ConcreteEdgesGraph.Edge e = new ConcreteEdgesGraph.Edge("S", "T", 4);
        assertEquals("S", e.getSource());
        assertEquals("T", e.getTarget());
        assertEquals(4, e.getWeight());

        String s = e.toString();
        assertTrue("toString should include source", s.contains("S"));
        assertTrue("toString should include target", s.contains("T"));
        assertTrue("toString should include weight", s.contains("4"));
    }

    @Test(expected = AssertionError.class)
    public void testEdgeRejectsNullSourceWithAsserts() {
        new ConcreteEdgesGraph.Edge(null, "T", 1);
    }

    @Test(expected = AssertionError.class)
    public void testEdgeRejectsNullTargetWithAsserts() {
        new ConcreteEdgesGraph.Edge("S", null, 1);
    }

    @Test(expected = AssertionError.class)
    public void testEdgeRejectsNonPositiveWeightWithAsserts() {
        new ConcreteEdgesGraph.Edge("S", "T", 0);
    }
}