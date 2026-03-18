package com.analysis;

public class Cell {
    public char character = ' ';
    public int fgColor = 0;
    public int bgColor = 0;
    public boolean bold, italic, underline;

    public Cell() {
        this.character = ' ';
        this.fgColor = 0;
        this.bgColor = 0;
        this.bold = false;
        this.italic = false;
        this.underline = false;
    }

    public void reset(char c, int fg, int bg, boolean b, boolean i, boolean u) {
        this.character = c;
        this.fgColor = fg;
        this.bgColor = bg;
        this.bold = b;
        this.italic = i;
        this.underline = u;
    }
}
