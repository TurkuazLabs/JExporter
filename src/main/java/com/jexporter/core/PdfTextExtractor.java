/*
# Dosya Yolu: src/main/java/com/jexporter/core/PdfTextExtractor.java
# Amac: PDF icindeki gomulu metni PDFBox ile okur
# Modul - FileType
# Version: 2.4.4
# Aciklama: OCR oncesi hizli ve temiz metin cikarma denemesi yapar
# Bagimli Oldugu Katman: Tool
*/
package com.jexporter.core;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfTextExtractor {

    public List<String> extractPageTexts(File pdfFile) throws IOException {
        if (!pdfFile.exists()) {
            throw new IllegalArgumentException("PDF bulunamadi: " + pdfFile.getAbsolutePath());
        }

        List<String> pageTexts = new ArrayList<>();

        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            int totalPages = document.getNumberOfPages();

            for (int page = 1; page <= totalPages; page++) {
                stripper.setStartPage(page);
                stripper.setEndPage(page);
                pageTexts.add(stripper.getText(document));
            }
        }

        return pageTexts;
    }
}
