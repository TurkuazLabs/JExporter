/*
# Dosya Yolu: src/main/java/com/jexporter/config/ConfigManager.java
# Amac: Uygulama ayarlarini config.properties dosyasindan okur ve varsayilanlari olusturur
# Modul - FileType
# Version: 2.4.4
# Aciklama: DPI, dil, cikti klasoru ve tessdata yolu gibi ayarlari yonetir
# Bagimli Oldugu Katman: Tool
*/
package com.jexporter.config;

import com.jexporter.logging.AppLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class ConfigManager {

    private static final String CONFIG_FILE = "config.properties";

    private static final String KEY_DPI = "dpi";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_OUTPUT_FORMAT = "output_format";
    private static final String KEY_OUTPUT_DIR = "output_dir";
    private static final String KEY_TESSDATA_PATH = "tessdata_path";

    private static ConfigManager instance;

    private final Properties properties;

    private ConfigManager() {
        this.properties = new Properties();
        createDefaultsIfMissing();
        load();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }

        return instance;
    }

    public final void createDefaultsIfMissing() {
        File config = new File(CONFIG_FILE);

        if (!config.exists()) {
            try (FileOutputStream output = new FileOutputStream(config)) {
                Properties defaults = new Properties();
                defaults.setProperty(KEY_DPI, String.valueOf(AppConstants.DEFAULT_DPI));
                defaults.setProperty(KEY_LANGUAGE, AppConstants.DEFAULT_LANGUAGE);
                defaults.setProperty(KEY_OUTPUT_FORMAT, AppConstants.DEFAULT_OUTPUT_FORMAT);
                defaults.setProperty(KEY_OUTPUT_DIR, getDefaultDesktopPath());
                defaults.setProperty(KEY_TESSDATA_PATH, AppConstants.DEFAULT_TESSDATA_DIR);
                defaults.store(output, "JExporter default config");
                AppLogger.success("config.properties olusturuldu.");
            } catch (IOException ex) {
                AppLogger.error("config.properties olusturulamadi: " + ex.getMessage(), ex);
            }
        }

        ensureDirectory(getDefaultDesktopPath());
        ensureDirectory(AppConstants.DEFAULT_TESSDATA_DIR);
        ensureDirectory("logs");
    }

    public void load() {
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            AppLogger.success("config.properties okundu.");
        } catch (IOException ex) {
            AppLogger.error("config.properties okunamadi: " + ex.getMessage(), ex);
        }
    }

    public String getString(String key, String defaultValue) {
        String value = properties.getProperty(key);
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    public int getInt(String key, int defaultValue) {
        String value = getString(key, String.valueOf(defaultValue));

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public int getDefaultDpi() {
        return getInt(KEY_DPI, AppConstants.DEFAULT_DPI);
    }

    public String getDefaultLanguage() {
        return getString(KEY_LANGUAGE, AppConstants.DEFAULT_LANGUAGE);
    }

    public String getDefaultOutputFormat() {
        return getString(KEY_OUTPUT_FORMAT, AppConstants.DEFAULT_OUTPUT_FORMAT);
    }

    public String getDefaultTessdataPath() {
        String path = getString(KEY_TESSDATA_PATH, AppConstants.DEFAULT_TESSDATA_DIR);
        ensureDirectory(path);
        return new File(path).getAbsolutePath();
    }

    public String getOutputDirPath() {
        String path = getString(KEY_OUTPUT_DIR, getDefaultDesktopPath());
        ensureDirectory(path);
        return new File(path).getAbsolutePath();
    }

    public String[] getAvailableLanguages() {
        File tessdataDir = new File(getDefaultTessdataPath());
        String[] languages = tessdataDir.list((dir, name) -> name.toLowerCase().endsWith(".traineddata"));

        if (languages == null || languages.length == 0) {
            return new String[] { AppConstants.LANGUAGE_AUTO };
        }

        String[] resolved = Arrays.stream(languages)
                .map(name -> name.replace(".traineddata", ""))
                .sorted()
                .toArray(String[]::new);

        String[] withAuto = new String[resolved.length + 1];
        withAuto[0] = AppConstants.LANGUAGE_AUTO;
        System.arraycopy(resolved, 0, withAuto, 1, resolved.length);

        return withAuto;
    }

    public String getDefaultDesktopPath() {
        String userHome = System.getProperty("user.home", "");
        File desktop = new File(userHome, "Desktop");

        if (desktop.exists() && desktop.isDirectory()) {
            return desktop.getAbsolutePath();
        }

        return new File("output").getAbsolutePath();
    }

    private void ensureDirectory(String path) {
        File dir = new File(path);

        if (!dir.exists()) {
            boolean created = dir.mkdirs();

            if (created) {
                AppLogger.success("Klasor olusturuldu: " + dir.getAbsolutePath());
            }
        }
    }
}
