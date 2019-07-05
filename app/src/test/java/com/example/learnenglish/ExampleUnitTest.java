package com.example.learnenglish;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        ForWords forWords = new ForWords();
        forWords.createSeparate();
        assertEquals(4, 2 + 2);
    }
}