/*
# Dosya Yolu: src/main/java/com/jexporter/core/OCRProcessor.java
# Amac: PDF sayfalarini gorsele cevirip Tess4J ile OCR yapar
# Modul - FileType
# Version: 2.4.4
# Aciklama: PDF metni yetersizse klasik OCR yedek veri kaynagi olarak kullanilir
# Bagimli Oldugu Katman: Tool
*/
package com.jexporter.core;

import com.jexporter.model.ProcessRequest;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class OCRProcessor {

    private static final int PROGRESS_OCR_START = 30;
    private static final int PROGRESS_OCR_RANGE = 50;

    public List<String> extractPageTexts(ProcessRequest request,
                                         String tessdataPath,
                                         AtomicBoolean shouldStop,
                                         JProgressBar progressBar) throws IOException {
        File inputPdf = new File(request.getPdfPath());

        if (!inputPdf.exists()) {
            throw new IllegalArgumentException("PDF bulunamadi: " + inputPdf.getAbsolutePath());
        }

        List<String> pageTexts = new ArrayList<>();

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessdataPath);
        tesseract.setLanguage(request.getLanguage());

        try (PDDocument document = PDDocument.load(inputPdf)) {
            PDFRenderer renderer = new PDFRenderer(document);
            int totalPages = document.getNumberOfPages();

            for (int page = 0; page < totalPages; page++) {
                if (shouldStop != null && shouldStop.get()) {
                    System.out.println("[DURDURULDU] OCR kullanici tarafindan durduruldu.");
                    break;
                }

                System.out.println("[BASLADI] OCR sayfa isleniyor: " + (page + 1) + "/" + totalPages);

                BufferedImage image = renderer.renderImageWithDPI(page, request.getDpi());

                try {
                    String text = tesseract.doOCR(image);
                    pageTexts.add(text == null ? "" : text);
                    System.out.println("[BASARILI] OCR sayfa tamamlandi: " + (page + 1));
                } catch (TesseractException ex) {
                    pageTexts.add("");
                    System.err.println("[BASARISIZ] OCR sayfa hatasi: " + (page + 1) + " - " + ex.getMessage());
                }

                updateProgress(progressBar, page + 1, totalPages);
            }
        }

        return pageTexts;
    }

    private void updateProgress(JProgressBar progressBar, int current, int total) {
        if (progressBar == null || total <= 0) {
            return;
        }

        int progress = PROGRESS_OCR_START + (int) (current * (double) PROGRESS_OCR_RANGE / total);

        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(progress);
            progressBar.setString(progress + " %");
        });
    }
}
