package com.analysis;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TerminalBufferTest {

    @Test
    void testScrollbackLogic() {
        TerminalBuffer buffer = new TerminalBuffer(10, 2, 2);

        buffer.writeText("L1\n");
        buffer.writeText("L2\n");
        buffer.writeText("L3\n");

        String screen = buffer.getScreenAsString();
        assertTrue(screen.contains("L2"), "Ecranul ar trebui sa contina L2");
        assertTrue(screen.contains("L3"), "Ecranul ar trebui sa contina L3");

        String history = buffer.getAllVisibleHistoryAsString();
        assertTrue(history.contains("L1"), "Istoricul ar trebui sa pastreze L1");
    }

    @Test
    void testBackspaceAtStartOfLine() {
        TerminalBuffer buffer = new TerminalBuffer(10, 5, 5);
        buffer.writeText("AB\nC");
        buffer.backspace();
        buffer.backspace();

         assertEquals(9, buffer.getCursorCol());
         assertEquals(0, buffer.getCursorRow());
    }
}