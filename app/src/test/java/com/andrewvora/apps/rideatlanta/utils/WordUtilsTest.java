package com.andrewvora.apps.rideatlanta.utils;

import com.andrewvora.apps.rideatlanta.BaseUnitTest;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * {@link WordUtils}.
 * Created on 4/19/2017.
 *
 * @author Andrew Vorakrajangthiti
 */
public class WordUtilsTest extends BaseUnitTest {
    @Test
    public void capitalizeWords() {
        String input = "COOl MAN dawg bOI";
        String expected = "Cool Man Dawg Boi";

        assertEquals(expected, WordUtils.capitalizeWords(input));
    }

    // TODO: make class work for this test
    @Ignore
    @Test
    public void capitalizeWords_parentheses() {
        String input = "Illuminati (confirmed)";
        String expected = "Illuminati (Confirmed)";

        assertEquals(expected, WordUtils.capitalizeWords(input));
    }

    @Test
    public void capitalizeWords_singleWord() {
        String input = "terrific";
        String expected = "Terrific";

        assertEquals(expected, WordUtils.capitalizeWords(input
        ));
    }

    @Test
    public void capitalizeWords_hyphens() {
        String input = "E-mail up-and-at-em";
        String expected = "E-Mail Up-And-At-Em";

        assertEquals(expected, WordUtils.capitalizeWords(input));
    }

    @Test
    public void capitalizeWords_sentence() {
        String input = "Mary had a little lamb.";
        String expected = "Mary Had A Little Lamb.";

        assertEquals(expected, WordUtils.capitalizeWords(input));
    }

    @Test
    public void capitalizeWords_emptyString() {
        String result = WordUtils.capitalizeWords("");

        assertTrue(result.isEmpty());
    }

    @Test
    public void capitalizeWords_whitespace() {
        String result = WordUtils.capitalizeWords("\t\n  ");

        assertTrue(result.isEmpty());
    }
}