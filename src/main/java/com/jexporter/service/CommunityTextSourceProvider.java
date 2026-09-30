/*
# 📄 Dosya Yolu: src/main/java/com/jexporter/service/CommunityTextSourceProvider.java
# 📌 Amac: JExporter Community PDF text -> layout OCR -> klasik OCR fallback zincirini TextSourceProvider olarak uygulamak
# 📌 Modul - Java
# Version: 2.5.0
# Aciklama: Mevcut 2.4.4 metin kaynagi secim davranisini public provider contracti arkasinda korur
# Bagimli Oldugu Katman: Service | Tool | Model
*/
package com.jexporter.service;

import com.jexporter.core.LayoutOcrProcessor;
import com.jexporter.core.OCRProcessor;
import com.jexporter.core.PdfTextExtractor;
import com.jexporter.logging.AppLogger;
import com.jexporter.model.ProcessRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CommunityTextSourceProvider implements TextSourceProvider {

    private static final int PROGRESS_PDF_TEXT_CHECKED = 25;

    private final PdfTextExtractor pdfTextExtractor;
    private final LayoutOcrProcessor layoutOcrProcessor;
    private final OCRProcessor ocrProcessor;

    public CommunityTextSourceProvider() {
        this(new PdfTextExtractor(), new LayoutOcrProcessor(), new OCRProcessor());
    }

    public CommunityTextSourceProvider(
            PdfTextExtractor pdfTextExtractor,
            LayoutOcrProcessor layoutOcrProcessor,
            OCRProcessor ocrProcessor) {
        this.pdfTextExtractor = Objects.requireNonNull(pdfTextExtractor, "pdfTextExtractor");
        this.layoutOcrProcessor = Objects.requireNonNull(layoutOcrProcessor, "layoutOcrProcessor");
        this.ocrProcessor = Objects.requireNonNull(ocrProcessor, "ocrProcessor");
    }

    @Override
    public List<String> load(
            ProcessRequest request,
            String tessdataPath,
            AtomicBoolean shouldStop,
            ProcessingProgress progress) throws IOException {

        Objects.requireNonNull(request, "request");
        ProcessingProgress safeProgress = progress == null ? ProcessingProgress.noop() : progress;
        File inputPdf = new File(request.getPdfPath());

        AppLogger.started("PDF metin katmani kontrol ediliyor.");
        List<String> pdfTexts = pdfTextExtractor.extractPageTexts(inputPdf);
        safeProgress.update(PROGRESS_PDF_TEXT_CHECKED);

        if (isTextBlocksUsable(pdfTexts)) {
            AppLogger.success("PDF metin katmani kullanildi.");
            return pdfTexts;
        }

        AppLogger.warning("PDF metin katmani yetersiz. Layout OCR deneniyor.");

        try {
            List<String> layoutPageTexts = layoutOcrProcessor.extractTextLines(
                    request,
                    tessdataPath,
                    shouldStop,
                    safeProgress);

            if (isTextBlocksUsable(layoutPageTexts)) {
                AppLogger.success("Layout OCR metin kaynagi kullanildi.");
                return layoutPageTexts;
            }

            AppLogger.warning("Layout OCR yetersiz. Klasik OCR deneniyor.");
        } catch (Exception exception) {
            AppLogger.error("Layout OCR hatasi: " + exception.getMessage(), exception);
            AppLogger.warning("Klasik OCR deneniyor.");
        }

        List<String> ocrTexts = ocrProcessor.extractPageTexts(
                request,
                tessdataPath,
                shouldStop,
                safeProgress);
        AppLogger.success("Klasik OCR metin cikarma tamamlandi.");
        return ocrTexts;
    }

    private boolean isTextBlocksUsable(List<String> textBlocks) {
        if (textBlocks == null || textBlocks.isEmpty()) {
            return false;
        }

        int totalLength = 0;
        int lineCount = 0;

        for (String block : textBlocks) {
            if (block == null) {
                continue;
            }

            String trimmedBlock = block.trim();
            if (trimmedBlock.isEmpty()) {
                continue;
            }

            totalLength += trimmedBlock.length();
            String[] lines = trimmedBlock.split("\\r?\\n");

            for (String line : lines) {
                if (line != null && line.trim().length() >= 4) {
                    lineCount++;
                }
            }
        }

        return totalLength > 120 && lineCount > 8;
    }
}
