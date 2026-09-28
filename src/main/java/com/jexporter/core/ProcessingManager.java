/*
# Dosya Yolu: src/main/java/com/jexporter/core/ProcessingManager.java
# Amac: PDF metin okuma, layout OCR, klasik OCR, gruplama ve cikti alma akislarini yonetir
# Modul - FileType
# Version: 2.4.4
# Aciklama: Metin kaynagi secimi, sayfa duzeni ve progress akislarini merkezi olarak yonetir
# Bagimli Oldugu Katman: Service
*/
package com.jexporter.core;

import com.jexporter.logging.AppLogger;
import com.jexporter.model.ProcessRequest;
import com.jexporter.profile.FieldDefinition;
import com.jexporter.profile.ProfileManager;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProcessingManager {

    private static final int PROGRESS_STARTED = 5;
    private static final int PROGRESS_PDF_TEXT_CHECKED = 25;
    private static final int PROGRESS_TEXT_SOURCE_READY = 60;
    private static final int PROGRESS_GROUPED = 75;
    private static final int PROGRESS_WRITING = 90;
    private static final int PROGRESS_DONE = 100;

    private final PdfTextExtractor pdfTextExtractor;
    private final LayoutOcrProcessor layoutOcrProcessor;
    private final OCRProcessor ocrProcessor;
    private final ProfileManager profileManager;
    private final TextGrouper textGrouper;
    private final ExcelExporter excelExporter;

    public ProcessingManager() {
        this.pdfTextExtractor = new PdfTextExtractor();
        this.layoutOcrProcessor = new LayoutOcrProcessor();
        this.ocrProcessor = new OCRProcessor();
        this.profileManager = new ProfileManager();
        this.textGrouper = new TextGrouper(profileManager.getFields());
        this.excelExporter = new ExcelExporter();
    }

    public File process(ProcessRequest request,
                        String tessdataPath,
                        String outputDir,
                        JProgressBar progressBar,
                        AtomicBoolean shouldStop) throws IOException {
        AppLogger.started("Islem yoneticisi basladi.");
        updateProgress(progressBar, PROGRESS_STARTED);

        File inputPdf = new File(request.getPdfPath());
        if (!inputPdf.exists()) {
            throw new IOException("PDF bulunamadi: " + inputPdf.getAbsolutePath());
        }

        List<String> sourceTexts = loadBestTextSource(request, tessdataPath, shouldStop, progressBar);

        if (shouldStop != null && shouldStop.get()) {
            AppLogger.stopped("Islem cikti uretmeden durduruldu.");
            return null;
        }

        updateProgress(progressBar, PROGRESS_TEXT_SOURCE_READY);
        AppLogger.started("Metin satir ve sutunlara ayriliyor.");
        List<Map<String, String>> rows = textGrouper.group(sourceTexts);
        AppLogger.success("Gruplanan satir sayisi: " + rows.size());
        updateProgress(progressBar, PROGRESS_GROUPED);

        File outDir = new File(outputDir);
        if (!outDir.exists()) {
            boolean created = outDir.mkdirs();

            if (created) {
                AppLogger.success("Cikti klasoru olusturuldu: " + outDir.getAbsolutePath());
            }
        }

        String extension = "csv".equalsIgnoreCase(request.getOutputFormat()) ? "csv" : "xlsx";
        String outputFileName = inputPdf.getName().replaceAll("(?i)\\.pdf$", "_jexporter." + extension);
        File outputFile = new File(outDir, outputFileName);

        AppLogger.started("Cikti dosyasi yaziliyor: " + outputFile.getAbsolutePath());
        updateProgress(progressBar, PROGRESS_WRITING);

        List<FieldDefinition> fields = profileManager.getFields();
        if (fields.isEmpty()) {
            AppLogger.info("Cikti modu: Orijinal PDF duzeni");
        }

        excelExporter.export(rows, fields, outputFile, extension);

        if (!outputFile.exists() || outputFile.length() <= 0) {
            throw new IOException("Cikti dosyasi yazilamadi veya bos olustu: " + outputFile.getAbsolutePath());
        }

        AppLogger.success("Cikti dosyasi tamamlandi: " + outputFile.getAbsolutePath());
        updateProgress(progressBar, PROGRESS_DONE);

        return outputFile;
    }

    private List<String> loadBestTextSource(ProcessRequest request,
                                            String tessdataPath,
                                            AtomicBoolean shouldStop,
                                            JProgressBar progressBar) throws IOException {
        File inputPdf = new File(request.getPdfPath());

        AppLogger.started("PDF metin katmani kontrol ediliyor.");
        List<String> pdfTexts = pdfTextExtractor.extractPageTexts(inputPdf);
        updateProgress(progressBar, PROGRESS_PDF_TEXT_CHECKED);

        if (isTextBlocksUsable(pdfTexts)) {
            AppLogger.success("PDF metin katmani kullanildi.");
            return pdfTexts;
        }

        AppLogger.warning("PDF metin katmani yetersiz. Layout OCR deneniyor.");

        try {
            List<String> layoutPageTexts = layoutOcrProcessor.extractTextLines(request, tessdataPath, shouldStop, progressBar);

            if (isTextBlocksUsable(layoutPageTexts)) {
                AppLogger.success("Layout OCR metin kaynagi kullanildi.");
                return layoutPageTexts;
            }

            AppLogger.warning("Layout OCR yetersiz. Klasik OCR deneniyor.");
        } catch (Exception ex) {
            AppLogger.error("Layout OCR hatasi: " + ex.getMessage(), ex);
            AppLogger.warning("Klasik OCR deneniyor.");
        }

        List<String> ocrTexts = ocrProcessor.extractPageTexts(request, tessdataPath, shouldStop, progressBar);
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

    private void updateProgress(JProgressBar progressBar, int value) {
        if (progressBar == null) {
            return;
        }

        int boundedValue = Math.max(0, Math.min(PROGRESS_DONE, value));

        SwingUtilities.invokeLater(() -> {
            if (boundedValue >= progressBar.getValue()) {
                progressBar.setValue(boundedValue);
                progressBar.setString(boundedValue + " %");
            }
        });
    }
}
