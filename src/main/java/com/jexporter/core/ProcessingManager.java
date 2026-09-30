/*
# 📄 Dosya Yolu: src/main/java/com/jexporter/core/ProcessingManager.java
# 📌 Amac: PDF metin kaynagi, gruplama ve cikti akislarini extension contractlari uzerinden yonetmek
# 📌 Modul - Java
# Version: 2.5.0
# Aciklama: Community default adapterlarini compose eder; TextSourceProvider, ProfileProvider ve OutputExporter injection destekler
# Bagimli Oldugu Katman: Service | Model | Tool
*/
package com.jexporter.core;

import com.jexporter.logging.AppLogger;
import com.jexporter.model.ProcessRequest;
import com.jexporter.profile.FieldDefinition;
import com.jexporter.profile.ProfileManager;
import com.jexporter.service.CommunityOutputExporter;
import com.jexporter.service.CommunityTextSourceProvider;
import com.jexporter.service.OutputExporter;
import com.jexporter.service.ProcessingProgress;
import com.jexporter.service.ProfileProvider;
import com.jexporter.service.TextSourceProvider;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProcessingManager {

    private static final int PROGRESS_STARTED = 5;
    private static final int PROGRESS_TEXT_SOURCE_READY = 60;
    private static final int PROGRESS_GROUPED = 75;
    private static final int PROGRESS_WRITING = 90;
    private static final int PROGRESS_DONE = 100;

    private final TextSourceProvider textSourceProvider;
    private final ProfileProvider profileProvider;
    private final OutputExporter outputExporter;

    public ProcessingManager() {
        this(
                new CommunityTextSourceProvider(),
                new ProfileManager(),
                new CommunityOutputExporter());
    }

    public ProcessingManager(
            TextSourceProvider textSourceProvider,
            ProfileProvider profileProvider,
            OutputExporter outputExporter) {
        this.textSourceProvider = Objects.requireNonNull(
                textSourceProvider,
                "textSourceProvider");
        this.profileProvider = Objects.requireNonNull(
                profileProvider,
                "profileProvider");
        this.outputExporter = Objects.requireNonNull(
                outputExporter,
                "outputExporter");
    }

    public File process(
            ProcessRequest request,
            String tessdataPath,
            String outputDir,
            JProgressBar progressBar,
            AtomicBoolean shouldStop) throws IOException {

        Objects.requireNonNull(request, "request");

        AppLogger.started("Islem yoneticisi basladi.");
        updateProgress(progressBar, PROGRESS_STARTED);

        File inputPdf = new File(request.getPdfPath());
        if (!inputPdf.exists()) {
            throw new IOException("PDF bulunamadi: " + inputPdf.getAbsolutePath());
        }

        ProcessingProgress providerProgress = value -> updateProgress(progressBar, value);
        List<String> sourceTexts = textSourceProvider.load(
                request,
                tessdataPath,
                shouldStop,
                providerProgress);

        if (shouldStop != null && shouldStop.get()) {
            AppLogger.stopped("Islem cikti uretmeden durduruldu.");
            return null;
        }

        updateProgress(progressBar, PROGRESS_TEXT_SOURCE_READY);

        List<FieldDefinition> fields = List.copyOf(profileProvider.getFields());
        TextGrouper textGrouper = new TextGrouper(fields);

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

        String extension = normalizeExtension(
                outputExporter.resolveExtension(request.getOutputFormat()));
        String outputFileName = inputPdf.getName()
                .replaceAll("(?i)\\.pdf$", "_jexporter." + extension);
        File outputFile = new File(outDir, outputFileName);

        AppLogger.started("Cikti dosyasi yaziliyor: " + outputFile.getAbsolutePath());
        updateProgress(progressBar, PROGRESS_WRITING);

        if (fields.isEmpty()) {
            AppLogger.info("Cikti modu: Orijinal PDF duzeni");
        }

        outputExporter.export(
                rows,
                fields,
                outputFile,
                request.getOutputFormat());

        if (!outputFile.exists() || outputFile.length() <= 0) {
            throw new IOException(
                    "Cikti dosyasi yazilamadi veya bos olustu: "
                            + outputFile.getAbsolutePath());
        }

        AppLogger.success("Cikti dosyasi tamamlandi: " + outputFile.getAbsolutePath());
        updateProgress(progressBar, PROGRESS_DONE);

        return outputFile;
    }

    private String normalizeExtension(String extension) throws IOException {
        String normalized = extension == null
                ? ""
                : extension.trim().toLowerCase();

        while (normalized.startsWith(".")) {
            normalized = normalized.substring(1);
        }

        if (!normalized.matches("[a-z0-9]{1,12}")) {
            throw new IOException("Gecersiz cikti uzantisi: " + extension);
        }

        return normalized;
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
