package poet;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Test;

public class GraphPoetTest {

    // ---------- helper ----------
    private static File corpusFile(String contents) throws IOException {
        File f = File.createTempFile("graphpoet-", ".txt");
        f.deleteOnExit();
        Files.write(f.toPath(), contents.getBytes(StandardCharsets.UTF_8));
        return f;
    }

    // ---------- assertions enabled ----------
    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // run with -ea
    }

    // ---------- tests ----------

    /**
     * Spec example from handout:
     * Corpus has "test of the".
     * Input "Test the system." inserts "of" between "Test" and "the".
     * Checks the basic bridging logic.
     */
    @Test
    public void testSpecExampleBridgeInserted() throws IOException {
        File corpus = corpusFile("This is a test of the Mugar Omni Theater sound system.");
        GraphPoet poet = new GraphPoet(corpus);

        String poem = poet.poem("Test the system.");
        assertEquals("Test of the system.", poem);
    }

    /**
     * Checks case-insensitivity and punctuation behavior:
     * Corpus "Hello, HELLO, hello, goodbye!" has edges hello,->hello, and hello,->goodbye!.
     * Input "Hello, goodbye!" should insert "hello," as the bridge word (lowercase).
     */
    @Test
    public void testCaseInsensitivityAndPunctuation() throws IOException {
        File corpus = corpusFile("Hello, HELLO, hello, goodbye!");
        GraphPoet poet = new GraphPoet(corpus);

        String poem = poet.poem("Hello, goodbye!");
        assertEquals("Hello, hello, goodbye!", poem);
    }

    /**
     * No two-edge path case:
     * Corpus has "to seek" but NOT "seek to".
     * Input "seek to" should return exactly "seek to" with no bridge.
     */
    @Test
    public void testNoBridgeWhenNoTwoEdgePath() throws IOException {
        File corpus = corpusFile(
            "to explore strange new worlds\n" +
            "to seek out new life and new civilizations\n"
        );
        GraphPoet poet = new GraphPoet(corpus);

        assertEquals("seek to", poet.poem("seek to"));
    }

 

    /**
     * Heaviest-bridge choice:
     * If both "x" and "y" could bridge between "a" and "c",
     * GraphPoet must pick the one with the higher total edge weight.
     * This corpus biases toward "y".
     */
    @Test
    public void testHeaviestBridgeChosen() throws IOException {
        File corpus = corpusFile("a x c a x c a y c y c y c a y c");
        GraphPoet poet = new GraphPoet(corpus);

        String poem = poet.poem("A c");
        assertEquals("A y c", poem);
    }

    /**
     * Single-word input:
     * Nothing to bridge, so the poem is exactly the same as the input.
     */
    @Test
    public void testSingleWordInput() throws IOException {
        File corpus = corpusFile("alpha beta gamma");
        GraphPoet poet = new GraphPoet(corpus);

        assertEquals("Hello", poet.poem("Hello"));
    }

    /**
     * Empty input:
     * No words means output should also be empty.
     */
    @Test
    public void testEmptyInput() throws IOException {
        File corpus = corpusFile("one two three");
        GraphPoet poet = new GraphPoet(corpus);

        assertEquals("", poet.poem(""));
    }

    /**
     * Unknown words in input:
     * If input words never appear in the corpus,
     * GraphPoet just passes them through unchanged.
     */
    @Test
    public void testUnknownWordsPassThrough() throws IOException {
        File corpus = corpusFile("red green blue");
        GraphPoet poet = new GraphPoet(corpus);

        assertEquals("Purple to Yellow", poet.poem("Purple to Yellow"));
    }

    /**
     * Case preservation and bridge lowercasing:
     * Corpus has "big BAD Wolf" → edges: big->bad, bad->wolf.
     * Input "BIG Wolf" should produce "BIG bad Wolf".
     * Notice "BIG" kept uppercase, "bad" is inserted lowercase.
     */
    @Test
    public void testCasingAndBridgeLowercase() throws IOException {
        File corpus = corpusFile("big BAD Wolf");
        GraphPoet poet = new GraphPoet(corpus);

        assertEquals("BIG bad Wolf", poet.poem("BIG Wolf"));
    }
}
