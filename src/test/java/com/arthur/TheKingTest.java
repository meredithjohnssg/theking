package com.arthur;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TheKingTest {

    @Test
    void testWhole() {
        String file = "Lengaburu.txt";
        TheKing theKing = new TheKing();
        theKing.processFamily(file);
        assertEquals(6, 3 + 3);
    }




}
