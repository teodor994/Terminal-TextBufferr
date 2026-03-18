package com.analysis;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        TerminalBuffer buffer = new TerminalBuffer(80, 24, 50);

        SwingUtilities.invokeLater(() -> {
            TerminalWindow window = new TerminalWindow(buffer);
            window.setVisible(true);

            buffer.setAttributes(32, 0, true, false, false); // Verde Bold
            buffer.writeText("Terminal Buffer started.\n");
            buffer.setAttributes(0, 0, false, false, false); // Reset
            buffer.writeText("Press keys to test\n");
            buffer.writeText("--------------------------------------------------\n");

            window.repaint();
        });
    }
}