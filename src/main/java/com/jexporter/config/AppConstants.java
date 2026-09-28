/*
# Dosya Yolu: src/main/java/com/jexporter/config/AppConstants.java
# Amac: Uygulama genelinde kullanilan sabit degerleri merkezi olarak tutar
# Modul - FileType
# Version: 2.4.4
# Aciklama: Magic string kullanimini azaltmak icin ortak sabitleri ve kaynak yollarini saglar
# Bagimli Oldugu Katman: Tool
*/
package com.jexporter.config;

import java.awt.Color;

public final class AppConstants {

    public static final String APP_NAME = "JExporter";
    public static final String APP_VERSION = "2.4.4";
    public static final String APP_TITLE = APP_NAME + " - PDF OCR Export";

    public static final String RESOURCE_LOGO = "/logo.png";
    public static final String RESOURCE_ICON = "/jxporter_icon.ico";

    public static final String LANGUAGE_AUTO = "auto";
    public static final String DPI_AUTO = "Auto";
    public static final String DEFAULT_LANGUAGE = LANGUAGE_AUTO;
    public static final String FALLBACK_LANGUAGE = "tur";
    public static final String DEFAULT_OUTPUT_FORMAT = "xlsx";
    public static final String DEFAULT_TESSDATA_DIR = "tessdata";

    public static final String FORMAT_XLSX = "xlsx";
    public static final String FORMAT_CSV = "csv";

    public static final int DEFAULT_DPI = 300;
    public static final int WINDOW_WIDTH = 1120;
    public static final int WINDOW_HEIGHT = 760;

    public static final String TESSDATA_DOWNLOAD_URL = "https://github.com/tesseract-ocr/tessdata_best";

    public static final Color COLOR_BACKGROUND = new Color(232, 239, 248);
    public static final Color COLOR_CARD = new Color(255, 255, 255);
    public static final Color COLOR_PRIMARY = new Color(14, 76, 150);
    public static final Color COLOR_PRIMARY_DARK = new Color(8, 28, 58);
    public static final Color COLOR_BORDER = new Color(145, 164, 190);
    public static final Color COLOR_CONSOLE_BG = new Color(18, 24, 33);
    public static final Color COLOR_TEXT = new Color(12, 22, 40);
    public static final Color COLOR_TEXT_MUTED = new Color(38, 55, 78);
    public static final Color COLOR_DANGER = new Color(178, 34, 34);
    public static final Color COLOR_SUCCESS = new Color(24, 116, 69);
    public static final Color COLOR_CONSOLE_FG = new Color(177, 245, 190);

    private AppConstants() {
    }
}
