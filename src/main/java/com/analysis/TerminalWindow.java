package com.analysis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TerminalWindow extends JFrame {
    private final TerminalBuffer buffer;
    private final TerminalPanel panel;

    public TerminalWindow(TerminalBuffer buffer) {
        this.buffer = buffer;
        this.panel = new TerminalPanel();

        setTitle("Java Terminal Emulator - Press ESC to Exit");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel);
        pack();
        setLocationRelativeTo(null);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleInput(e);
                panel.repaint();
            }
        });
    }

    private void handleInput(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        } else if (keyCode == KeyEvent.VK_ENTER) {
            buffer.writeText("\n");
        } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
            buffer.backspace();
        } else {
            char c = e.getKeyChar();
            if (c != KeyEvent.CHAR_UNDEFINED && Character.getType(c) != Character.CONTROL) {
                buffer.writeText(String.valueOf(c));
            }
        }
    }

    private class TerminalPanel extends JPanel {
        private final int charWidth = 10;
        private final int charHeight = 20;

        public TerminalPanel() {
            setPreferredSize(new Dimension(80 * charWidth + 20, 24 * charHeight + 20));
            setBackground(new Color(15, 15, 15));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g.setFont(new Font("Monospaced", Font.PLAIN, 16));

            for (int r = 0; r < 24; r++) {
                for (int c = 0; c < 80; c++) {
                    Cell cell = buffer.getCell(c, r, false);
                    if (cell == null) continue;

                    if (cell.fgColor == 31) g.setColor(Color.RED);
                    else if (cell.fgColor == 32) g.setColor(Color.GREEN);
                    else g.setColor(Color.LIGHT_GRAY);

                    if (cell.bold) {
                        g.setFont(new Font("Monospaced", Font.BOLD, 16));
                    } else {
                        g.setFont(new Font("Monospaced", Font.PLAIN, 16));
                    }

                    g.drawString(String.valueOf(cell.character), 10 + c * charWidth, 25 + r * charHeight);
                }
            }

            drawCursor(g);
        }

        private void drawCursor(Graphics g) {
            g.setColor(Color.CYAN);
            int x = 10 + buffer.getCursorCol() * charWidth;
            int y = 25 + buffer.getCursorRow() * charHeight;
            g.fillRect(x, y - 2, charWidth - 2, 3); // O liniuță sub caracter
        }
    }
}