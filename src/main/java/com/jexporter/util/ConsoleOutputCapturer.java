/*
# Dosya Yolu: src/main/java/com/jexporter/util/ConsoleOutputCapturer.java
# Amac: System.out ve System.err ciktilarini JTextArea icine yonlendirir
# Modul - FileType
# Version: 2.4.4
# Aciklama: Log4j kullanmadan GUI konsol cikti takibi saglar
# Bagimli Oldugu Katman: Tool
*/
package com.jexporter.util;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.io.OutputStream;
import java.io.PrintStream;

public class ConsoleOutputCapturer {

    private final JTextArea textArea;
    private PrintStream originalOut;
    private PrintStream originalErr;
    private boolean started;

    public ConsoleOutputCapturer(JTextArea textArea) {
        this.textArea = textArea;
    }

    public void start() {
        if (started) {
            return;
        }

        originalOut = System.out;
        originalErr = System.err;

        PrintStream stream = new PrintStream(new TextAreaOutputStream(textArea), true);
        System.setOut(stream);
        System.setErr(stream);

        started = true;
    }

    public void stop() {
        if (!started) {
            return;
        }

        if (originalOut != null) {
            System.setOut(originalOut);
        }

        if (originalErr != null) {
            System.setErr(originalErr);
        }

        started = false;
    }

    private static class TextAreaOutputStream extends OutputStream {

        private final JTextArea textArea;
        private final StringBuilder buffer;

        TextAreaOutputStream(JTextArea textArea) {
            this.textArea = textArea;
            this.buffer = new StringBuilder();
        }

        @Override
        public void write(int b) {
            char c = (char) b;
            buffer.append(c);

            if (c == '\n') {
                flushBuffer();
            }
        }

        @Override
        public void flush() {
            flushBuffer();
        }

        private void flushBuffer() {
            if (buffer.length() == 0) {
                return;
            }

            String text = buffer.toString();
            buffer.setLength(0);

            SwingUtilities.invokeLater(() -> {
                textArea.append(text);
                textArea.setCaretPosition(textArea.getDocument().getLength());
            });
        }
    }
}
