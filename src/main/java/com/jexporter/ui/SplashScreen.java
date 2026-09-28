/*
# Dosya Yolu: src/main/java/com/jexporter/ui/SplashScreen.java
# Amac: Uygulama baslangicinda logo ve hazirlik durumunu gosterir
# Modul - FileType
# Version: 2.4.4
# Aciklama: Eski JXporter splash/logo kullanimini yeni JExporter projesine uyarlar
# Bagimli Oldugu Katman: View
*/
package com.jexporter.ui;

import com.jexporter.config.AppConstants;
import com.jexporter.config.ConfigManager;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.net.URL;

public class SplashScreen extends JWindow {

    private static final int WIDTH = 460;
    private static final int HEIGHT = 310;
    private static final int MIN_DURATION_MS = 1800;

    private final JLabel statusLabel;
    private final JProgressBar progressBar;

    public SplashScreen() {
        JPanel rootPanel = new JPanel(new BorderLayout(0, 10));
        rootPanel.setBackground(Color.WHITE);
        rootPanel.setBorder(BorderFactory.createLineBorder(AppConstants.COLOR_BORDER));

        JLabel logoLabel = createLogoLabel();
        rootPanel.add(logoLabel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 8));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 28, 22, 28));

        statusLabel = new JLabel("Hazirlaniyor...", JLabel.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(AppConstants.COLOR_PRIMARY_DARK);

        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(380, 10));
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setForeground(AppConstants.COLOR_PRIMARY);
        progressBar.setBackground(new Color(232, 240, 254));

        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);

        rootPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(rootPanel);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
    }

    public void showAndThen(Runnable onComplete) {
        setVisible(true);

        Thread worker = new Thread(() -> {
            long startTime = System.currentTimeMillis();

            try {
                updateStatus("Ayarlar kontrol ediliyor...", 20);
                ConfigManager.getInstance().createDefaultsIfMissing();
                sleep(350);

                updateStatus("Tessdata klasoru kontrol ediliyor...", 55);
                File tessdataDir = new File(ConfigManager.getInstance().getDefaultTessdataPath());
                if (!tessdataDir.exists()) {
                    tessdataDir.mkdirs();
                }
                sleep(350);

                updateStatus("Arayuz yukleniyor...", 85);
                sleep(350);

                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed < MIN_DURATION_MS) {
                    sleep(MIN_DURATION_MS - elapsed);
                }

                updateStatus("Baslatiliyor...", 100);
                fadeOut(onComplete);
            } catch (Exception ex) {
                System.err.println("[BASARISIZ] Splash hazirlik hatasi: " + ex.getMessage());
                closeAndRun(onComplete);
            }
        }, "jexporter-splash-thread");

        worker.start();
    }

    private JLabel createLogoLabel() {
        URL logoUrl = getClass().getResource(AppConstants.RESOURCE_LOGO);

        if (logoUrl == null) {
            JLabel fallback = new JLabel(AppConstants.APP_NAME, JLabel.CENTER);
            fallback.setFont(new Font("Segoe UI", Font.BOLD, 34));
            fallback.setForeground(AppConstants.COLOR_PRIMARY_DARK);
            return fallback;
        }

        ImageIcon icon = new ImageIcon(logoUrl);
        Image scaled = icon.getImage().getScaledInstance(260, 164, Image.SCALE_SMOOTH);

        JLabel label = new JLabel(new ImageIcon(scaled), JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(28, 20, 10, 20));
        return label;
    }

    private void updateStatus(String text, int progress) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(text);
            progressBar.setValue(progress);
        });
    }

    private void fadeOut(Runnable onComplete) {
        try {
            for (float opacity = 1.0f; opacity > 0.05f; opacity -= 0.08f) {
                final float value = opacity;
                SwingUtilities.invokeAndWait(() -> {
                    try {
                        setOpacity(value);
                    } catch (UnsupportedOperationException ignored) {
                    }
                });
                sleep(25);
            }
        } catch (Exception ignored) {
        } finally {
            closeAndRun(onComplete);
        }
    }

    private void closeAndRun(Runnable onComplete) {
        SwingUtilities.invokeLater(() -> {
            setVisible(false);
            dispose();
            onComplete.run();
        });
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
