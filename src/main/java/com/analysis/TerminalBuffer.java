package com.analysis;
import java.util.Arrays;
//import java.stream.

public class TerminalBuffer {
    // the buffer
    private final Cell[][] buffer;

    // shell configuration
    private int width;
    private int height;
    private int total_capacity;
    private int max_scrollback;

    private int start_ptr = 0;
    private int total_lines = 0;

    private int cursorCol = 0;
    private int cursorRow = 0;

    private int currentFg = 0; // 0 = Default
    private int currentBg = 0;
    private boolean bold, italic, underline;
}
