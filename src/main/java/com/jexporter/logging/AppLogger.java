/*
# Dosya Yolu: src/main/java/com/jexporter/logging/AppLogger.java
# Amac: Log4j kullanmadan hem konsola hem dosyaya sade log yazmak
# Modul - FileType
# Version: 2.4.4
# Aciklama: BASLADI, BILGI, BASARILI, UYARI, BASARISIZ ve DURDURULDU loglarini merkezi yonetir
# Bagimli Oldugu Katman: Tool
*/
package com.jexporter.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class AppLogger {

    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = "jexporter.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private AppLogger() {
    }

    public static void started(String message) {
        write(System.out, "BASLADI", message, null);
    }

    public static void info(String message) {
        write(System.out, "BILGI", message, null);
    }

    public static void success(String message) {
        write(System.out, "BASARILI", message, null);
    }

    public static void warning(String message) {
        write(System.out, "UYARI", message, null);
    }

    public static void stopped(String message) {
        write(System.out, "DURDURULDU", message, null);
    }

    public static void error(String message) {
        write(System.err, "BASARISIZ", message, null);
    }

    public static void error(String message, Throwable throwable) {
        write(System.err, "BASARISIZ", message, throwable);
    }

    private static synchronized void write(PrintStream stream, String level, String message, Throwable throwable) {
        String safeMessage = message == null ? "" : message;
        String line = "[" + level + "] " + safeMessage;

        stream.println(line);

        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File logFile = new File(dir, LOG_FILE);

        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(LocalDateTime.now().format(FORMATTER));
            writer.write(" ");
            writer.write(line);
            writer.write(System.lineSeparator());

            if (throwable != null) {
                writer.write(throwable.getClass().getName());
                writer.write(": ");
                writer.write(throwable.getMessage() == null ? "" : throwable.getMessage());
                writer.write(System.lineSeparator());

                for (StackTraceElement element : throwable.getStackTrace()) {
                    writer.write("    at ");
                    writer.write(element.toString());
                    writer.write(System.lineSeparator());
                }
            }
        } catch (IOException ignored) {
            stream.println("[UYARI] Log dosyasina yazilamadi.");
        }
    }
}
