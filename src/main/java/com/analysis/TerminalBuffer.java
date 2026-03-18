package com.analysis;
import java.util.Arrays;

public class TerminalBuffer {
    // the buffer
    private final Cell[][] buffer;

    // shell configuration
    private int width;
    private int height;
    private int totalCapacity;
    private int maxScrollback;

    private int startPtr = 0;
    private int totalLines = 0;

    private int cursorCol = 0;
    private int cursorRow = 0;

    private int currentFg = 0; // 0 = Default
    private int currentBg = 0;
    private boolean bold, italic, underline;

    public TerminalBuffer(int width, int height, int maxScrollback) {
        this.width = width;
        this.height = height;
        this.maxScrollback = maxScrollback;
        this.totalCapacity = height + maxScrollback;
        this.buffer = new Cell[totalCapacity][width];

        for (int r = 0; r < totalCapacity; r++) {
            for (int c = 0; c < width; c++) {
                buffer[r][c] = new Cell();
            }
        }

        this.totalLines = height;
    }


    public Cell getCell(int col, int row, boolean fromScrollback) {
        int logicalRow = fromScrollback ? row : (Math.max(0, totalLines - height) + row);
        if (logicalRow < 0 || logicalRow >= totalLines || col < 0 || col >= width) {
            return null;
        }
        return buffer[getPhysicalIndex(logicalRow)][col];
    }

    public void drawToSystemConsole() {
        System.out.print("\033[H");

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                Cell cell = getCellAtScreenPos(c, r);

                if (cell.bold) System.out.print("\033[1m");

                System.out.print(cell.character);

                System.out.print("\033[0m");
            }
            System.out.println();
        }
    }

    // Cursor

    public void setAttributes(int fg, int bg, boolean b, boolean i, boolean u) {
        this.currentFg = fg;
        this.currentBg = bg;
        this.bold = b;
        this.italic = i;
        this.underline = u;
    }

    public void setCursor(int col, int row) {
        this.cursorCol = Math.max(0, Math.min(col, width - 1));
        this.cursorRow = Math.max(0, Math.min(row, height - 1));
    }

    public void moveCursor(int deltaCol, int deltaRow) {
        setCursor(cursorCol + deltaCol, cursorRow + deltaRow);
    }

    // Clear screen AND scrollback (Hard reset)
    public void clearAll() {
        startPtr = 0;
        totalLines = height;
        for (int r = 0; r < totalCapacity; r++) {
            for (int c = 0; c < width; c++) {
                buffer[r][c].reset(' ', 0, 0, false, false, false);
            }
        }
        cursorCol = 0;
        cursorRow = 0;
    }

    private Cell getCellAtScreenPos(int col, int row) {
        int scrollbackCount = Math.max(0, totalLines - height);
        int physicalRow = getPhysicalIndex(scrollbackCount + row);
        return buffer[physicalRow][col];
    }

    private int getPhysicalIndex(int row) {
        return (startPtr + row) % totalCapacity;
    }

    public void writeText(String text) {
        for (char c : text.toCharArray()) {
            if (c == '\n') {
                lineFeed();
                continue;
            }

            if (c == '\b') { // added backspace support
                backspace();
                continue;
            }
            // newline?
            if (cursorCol >= width) {
                lineFeed();
            }

            int physicalRow = getScreenRowPhysical(cursorRow);

            // write in cell
            buffer[physicalRow][cursorCol].reset(c, currentFg, currentBg, bold, italic, underline);
            cursorCol++;
        }
    }

    private void lineFeed() {
        cursorCol = 0;
        if (cursorRow < height - 1) {
            cursorRow++;
        } else {
            // last line => Scroll up
            // advance the start pointer to "remove" the top line
            startPtr = (startPtr + 1) % totalCapacity;

            // buffer not full => we can add more lines without losing old ones
            if (totalLines < totalCapacity) {
                totalLines++;
            }

            // cleaned the new bottom line
            int newBottomRow = getScreenRowPhysical(height - 1);
            for (int i = 0; i < width; i++) {
                buffer[newBottomRow][i].reset(' ', 0, 0, false, false, false);
            }
        }
    }

    private int getScreenRowPhysical(int screenRow) {
        // number of lines in scrollback (lines that are not currently visible on screen)
        int scrollbackCount = Math.max(0, totalLines - height);
        // logical Index of the line on the screen (0-based) + scrollbackCount = logical index in the buffer
        return getPhysicalIndex(scrollbackCount + screenRow);
    }

    public void fillCurrentLine(char c) {
        int physicalRow = getScreenRowPhysical(cursorRow);
        for (int col = 0; col < width; col++) {
            buffer[physicalRow][col].reset(c, currentFg, currentBg, bold, italic, underline);
        }
    }

    // line as String
    public String getLineContent(int logicalRow) {
        if (logicalRow < 0 || logicalRow >= totalLines) return "";

        int physicalIndex = getPhysicalIndex(logicalRow);
        StringBuilder sb = new StringBuilder();
        for (Cell cell : buffer[physicalIndex]) {
            sb.append(cell.character);
        }
        return sb.toString();
    }

    // screen as String
    public String getScreenAsString() {
        StringBuilder sb = new StringBuilder();
        int scrollbackCount = Math.max(0, totalLines - height);
        for (int i = 0; i < height; i++) {
            sb.append(getLineContent(scrollbackCount + i)).append("\n");
        }
        return sb.toString();
    }

    // Get everything (Scrollback + Screen)
    public String getAllVisibleHistoryAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < totalLines; i++) {
            sb.append(getLineContent(i)).append("\n");
        }
        return sb.toString();
    }

    public void insertText(String text) {
        for (char c : text.toCharArray()) {
            if (c == '\n') {
                lineFeed();
                continue;
            }
            if (cursorCol >= width) {
                lineFeed();
            }

            int physicalRow = getScreenRowPhysical(cursorRow);

            // Shift cells to the right to make room
            for (int i = width - 1; i > cursorCol; i--) {
                Cell source = buffer[physicalRow][i - 1];
                buffer[physicalRow][i].reset(source.character, source.fgColor, source.bgColor,
                        source.bold, source.italic, source.underline);
            }

            // Insert the new character
            buffer[physicalRow][cursorCol].reset(c, currentFg, currentBg, bold, italic, underline);
            cursorCol++;
        }
    }


    public void backspace() {
        if (cursorCol > 0) {
            cursorCol--;
            clearCell(cursorCol, cursorRow);
        } else {
            if (cursorRow > 0) {
                cursorRow--;
                moveToEndOfTextOnRow(cursorRow);
            } else {
                int scrollbackCount = Math.max(0, totalLines - height);

                if (scrollbackCount > 0) {
                    startPtr = (startPtr - 1 + totalCapacity) % totalCapacity;

                    moveToEndOfTextOnRow(0);
                }
            }
        }
    }

    private void moveToEndOfTextOnRow(int screenRow) {
        int physRow = getScreenRowPhysical(screenRow);
        int lastContentCol = 0;

        for (int c = width - 1; c >= 0; c--) {
            if (buffer[physRow][c].character != ' ') {
                lastContentCol = Math.min(c + 1, width - 1);
                break;
            }
        }
        cursorCol = lastContentCol;

        if (cursorCol > 0) {
            cursorCol--;
            clearCell(cursorCol, screenRow);
        }
    }

    // reset a cell to empty
    private void clearCell(int col, int row) {
        int physicalRow = getScreenRowPhysical(row);
        buffer[physicalRow][col].reset(' ', 0, 0, false, false, false);
    }

    public int getCursorCol() {
        return this.cursorCol;
    }

    public int getCursorRow() {
        return this.cursorRow;
    }
}
