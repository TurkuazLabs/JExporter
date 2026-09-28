/*
# Dosya Yolu: src/main/java/com/jexporter/service/LanguageResolver.java
# Amac: Auto OCR dili secimini tessdata klasorune gore belirler
# Modul - FileType
# Version: 2.4.4
# Aciklama: Auto secimde tur oncelikli dil secer, eksik traineddata durumunu raporlar
# Bagimli Oldugu Katman: Service
*/
package com.jexporter.service;

import com.jexporter.config.AppConstants;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class LanguageResolver {

    public String resolve(String selectedLanguage, File tessdataDir) {
        if (selectedLanguage == null || selectedLanguage.trim().isEmpty()
                || AppConstants.LANGUAGE_AUTO.equalsIgnoreCase(selectedLanguage)) {
            return resolveAuto(tessdataDir);
        }

        return selectedLanguage.trim();
    }

    public boolean isLanguageAvailable(String language, File tessdataDir) {
        if (language == null || language.trim().isEmpty() || tessdataDir == null) {
            return false;
        }

        File trainedData = new File(tessdataDir, language + ".traineddata");
        return trainedData.exists() && trainedData.isFile();
    }

    public boolean hasAnyLanguage(File tessdataDir) {
        String[] languages = listLanguages(tessdataDir);
        return languages.length > 0;
    }

    public String[] listLanguages(File tessdataDir) {
        if (tessdataDir == null || !tessdataDir.exists() || !tessdataDir.isDirectory()) {
            return new String[0];
        }

        String[] files = tessdataDir.list((dir, name) -> name.toLowerCase().endsWith(".traineddata"));

        if (files == null) {
            return new String[0];
        }

        return Arrays.stream(files)
                .map(name -> name.replace(".traineddata", ""))
                .sorted()
                .toArray(String[]::new);
    }

    private String resolveAuto(File tessdataDir) {
        if (isLanguageAvailable(AppConstants.FALLBACK_LANGUAGE, tessdataDir)) {
            return AppConstants.FALLBACK_LANGUAGE;
        }

        Optional<String> first = Arrays.stream(listLanguages(tessdataDir)).findFirst();
        return first.orElse(AppConstants.FALLBACK_LANGUAGE);
    }
}
