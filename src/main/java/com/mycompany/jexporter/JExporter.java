/*
# Dosya Yolu: src/main/java/com/mycompany/jexporter/JExporter.java
# Amac: Uygulamanin ana giris noktasidir
# Modul - FileType
# Version: 2.4.4
# Aciklama: Global hata yakalayiciyi kurar, splash ekranini gosterir ve ana arayuzu baslatir
# Bagimli Oldugu Katman: Controller
*/
package com.mycompany.jexporter;

import com.jexporter.config.ConfigManager;
import com.jexporter.logging.AppExceptionHandler;
import com.jexporter.logging.AppLogger;
import com.jexporter.ui.MainUI;
import com.jexporter.ui.SplashScreen;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class JExporter {

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");

        AppExceptionHandler.install();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            AppLogger.warning("Sistem temasi yuklenemedi: " + ex.getMessage());
        }

        ConfigManager.getInstance().createDefaultsIfMissing();

        SwingUtilities.invokeLater(() -> {
            SplashScreen splashScreen = new SplashScreen();
            splashScreen.showAndThen(() -> {
                MainUI ui = new MainUI();
                ui.setVisible(true);
            });
        });
    }
}
