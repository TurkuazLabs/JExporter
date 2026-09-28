/*
# Dosya Yolu: src/main/java/com/jexporter/service/DpiResolver.java
# Amac: Auto DPI secimini PDF dosya boyutuna gore belirler
# Modul - FileType
# Version: 2.4.4
# Aciklama: Kucuk dosyada kaliteli, buyuk dosyada dengeli DPI onerir
# Bagimli Oldugu Katman: Service
*/
package com.jexporter.service;

import java.io.File;

public class DpiResolver {

    private static final long MB = 1024L * 1024L;

    public int resolve(String selectedValue, File pdfFile) {
        if (selectedValue == null || selectedValue.equalsIgnoreCase("Auto")) {
            return resolveAuto(pdfFile);
        }

        try {
            return Integer.parseInt(selectedValue.trim());
        } catch (NumberFormatException ex) {
            return resolveAuto(pdfFile);
        }
    }

    private int resolveAuto(File pdfFile) {
        if (pdfFile == null || !pdfFile.exists()) {
            return 300;
        }

        long size = pdfFile.length();

        if (size <= 2 * MB) {
            return 600;
        }

        if (size <= 8 * MB) {
            return 450;
        }

        if (size <= 20 * MB) {
            return 350;
        }

        return 300;
    }
}
