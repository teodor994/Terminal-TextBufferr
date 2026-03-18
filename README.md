# Terminal-TextBuffer

An efficient Circular Buffer implementation for terminal emulation, written in pure Java without any external dependencies. This project features a robust scrollback system, text attributes (Bold/Color), smart editing (Insert/Backspace), and a minimalist Swing-based GUI for real-time testing.

## Features

* **Circular Memory Management**: Optimizes RAM usage by recycling old rows in the history once capacity is reached.
* **Infinite-like Scrollback**: Configurable history size (`maxScrollback`) that persists beyond the visible screen area.
* **Smart Backspace**: Intelligent line-to-line navigation that skips trailing whitespaces, placing the cursor exactly after the last character of the previous line.
* **ANSI Attribute Support**: Per-cell storage for foreground colors and styles (Bold, Italic, Underline).
* **Integrated Swing Renderer**: A built-in interactive testing window to simulate real-time keyboard input and terminal behavior.

## Technologies

* **Language**: Java 17+
* **Rendering**: Java Swing / AWT (Standard Library)
* **Build Tool**: Gradle 8.x
* **Testing**: JUnit 5 (Jupiter)

## Project Structure

```text
src/
├── main/java/com.analysis/
│   ├── Cell.java            # Basic unit (Character + Styles)
│   ├── TerminalBuffer.java  # Core logic & Memory management
│   ├── TerminalWindow.java  # Graphic Renderer (Swing)
│   └── Main.java            # Application Entry Point
└── test/java/com.analysis/
    └── TerminalBufferTest.java # Comprehensive Unit Test Suite
```
