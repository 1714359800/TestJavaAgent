package org.apache.commons.cli;
import java.util.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class UtilTest {

    @Test
    public void testStripLeadingHyphens_emptyString() {
        String result = Util.stripLeadingHyphens("");
        assertEquals("", result);
    }

    @Test
    public void testStripLeadingHyphens_noHyphens() {
        String input = "example";
        String result = Util.stripLeadingHyphens(input);
        assertEquals(input, result);
    }

    @Test
    public void testStripLeadingHyphens_singleHyphen() {
        String input = "-example";
        String expected = "example";
        String result = Util.stripLeadingHyphens(input);
        assertEquals(expected, result);
    }

    @Test
    public void testStripLeadingHyphens_doubleHyphens() {
        String input = "--example";
        String expected = "example";
        String result = Util.stripLeadingHyphens(input);
        assertEquals(expected, result);
    }

    @Test
    public void testStripLeadingHyphens_multipleHyphens() {
        String input = "----example";
        String expected = "---example";
        String result = Util.stripLeadingHyphens(input);
        assertEquals("--example", result);
    }
}