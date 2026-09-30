/*
# 📄 Dosya Yolu: src/main/java/com/jexporter/core/OCRProcessor.java
# 📌 Amac: PDF sayfalarini gorsele cevirip Tess4J ile OCR yapmak
# 📌 Modul - Java
# Version: 2.5.1
# Aciklama: Klasik Community OCR Tool; public OcrProvider contractini uygular ve eski Swing overloadunu geriye uyumlu tutar
# Bagimli Oldugu Katman: Tool | Service | Model
*/
package com.jexporter.core;

import com.jexporter.model.ProcessRequest;
import com.jexporter.service.OcrProvider;
import com.jexporter.service.ProcessingProgress;
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

public class OCRProcessor implements OcrProvider {

    private static final int PROGRESS_OCR_START = 30;
    private static final int PROGRESS_OCR_RANGE = 50;

    public List<String> extractPageTexts(
            ProcessRequest request,
            String tessdataPath,
            AtomicBoolean shouldStop,
            JProgressBar progressBar) throws IOException {

        return extractPageTexts(
                request,
                tessdataPath,
                shouldStop,
                swingProgress(progressBar));
    }

    @Override
    public List<String> extractPageTexts(
            ProcessRequest request,
            String tessdataPath,
            AtomicBoolean shouldStop,
            ProcessingProgress progress) throws IOException {

        File inputPdf = new File(request.getPdfPath());

        if (!inputPdf.exists()) {
            throw new IllegalArgumentException("PDF bulunamadi: " + inputPdf.getAbsolutePath());
        }

        ProcessingProgress safeProgress = progress == null
                ? ProcessingProgress.noop()
                : progress;

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
                } catch (TesseractException exception) {
                    pageTexts.add("");
                    System.err.println(
                            "[BASARISIZ] OCR sayfa hatasi: "
                                    + (page + 1)
                                    + " - "
                                    + exception.getMessage());
                }

                updateProgress(safeProgress, page + 1, totalPages);
            }
        }

        return pageTexts;
    }

    private void updateProgress(
            ProcessingProgress progress,
            int current,
            int total) {

        if (total <= 0) {
            return;
        }

        int value = PROGRESS_OCR_START
                + (int) (current * (double) PROGRESS_OCR_RANGE / total);
        progress.update(value);
    }

    private ProcessingProgress swingProgress(JProgressBar progressBar) {
        if (progressBar == null) {
            return ProcessingProgress.noop();
        }

        return value -> SwingUtilities.invokeLater(() -> {
            progressBar.setValue(value);
            progressBar.setString(value + " %");
        });
    }
}
