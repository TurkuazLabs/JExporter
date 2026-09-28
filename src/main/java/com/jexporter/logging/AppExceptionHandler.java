/*
# Dosya Yolu: src/main/java/com/jexporter/logging/AppExceptionHandler.java
# Amac: Uygulama genelinde yakalanmamis hata ve thread hatalarini AppLogger ile kaydeder
# Modul - FileType
# Version: 2.4.4
# Aciklama: Swing EventQueue ve default thread exception handler kurar
# Bagimli Oldugu Katman: Tool
*/
package com.jexporter.logging;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;

public final class AppExceptionHandler {

    private static boolean installed;

    private AppExceptionHandler() {
    }

    public static synchronized void install() {
        if (installed) {
            return;
        }

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            AppLogger.error("Yakalanmamis thread hatasi: " + thread.getName(), throwable);
            showErrorDialog(throwable);
        });

        Toolkit.getDefaultToolkit()
                .getSystemEventQueue()
                .push(new SafeEventQueue());

        installed = true;
        AppLogger.success("Global hata yakalayici aktif edildi.");
    }

    private static void handle(Throwable throwable) {
        if (isThreadDeath(throwable) && throwable instanceof Error) {
            throw (Error) throwable;
        }

        AppLogger.error("Yakalanmamis arayuz hatasi: " + throwable.getMessage(), throwable);
        showErrorDialog(throwable);
    }

    private static boolean isThreadDeath(Throwable throwable) {
        return throwable != null && "java.lang.ThreadDeath".equals(throwable.getClass().getName());
    }

    private static void showErrorDialog(Throwable throwable) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                "Beklenmeyen hata olustu. Detaylar logs/jexporter.log dosyasina yazildi.\n\n"
                        + throwable.getClass().getSimpleName()
                        + ": "
                        + throwable.getMessage(),
                "JExporter Hata",
                JOptionPane.ERROR_MESSAGE
        ));
    }

    private static class SafeEventQueue extends EventQueue {

        @Override
        protected void dispatchEvent(AWTEvent event) {
            try {
                super.dispatchEvent(event);
            } catch (Throwable throwable) {
                handle(throwable);
            }
        }
    }
}
