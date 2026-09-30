/*
# 📄 Dosya Yolu: src/test/java/com/jexporter/core/ProcessingManagerExtensionTest.java
# 📌 Amac: ProcessingManager public extension injection ve Community output format davranisini regression olarak dogrulamak
# 📌 Modul - Java
# Version: 2.5.0
# Aciklama: Fake text/profile/output providerlarla pipeline'in concrete OCR/export implementationina bagli olmadigini test eder
# Bagimli Oldugu Katman: Tool | Service | Model
*/
package com.jexporter.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jexporter.model.ProcessRequest;
import com.jexporter.profile.FieldDefinition;
import com.jexporter.service.CommunityOutputExporter;
import com.jexporter.service.OutputExporter;
import com.jexporter.service.ProcessingProgress;
import com.jexporter.service.ProfileProvider;
import com.jexporter.service.TextSourceProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class ProcessingManagerExtensionTest {

    @TempDir
    Path tempDir;

    @Test
    void injectedPipelineProducesOutputWithoutCommunityConcreteTools() throws Exception {
        Path input = tempDir.resolve("sample.pdf");
        Files.writeString(input, "test-placeholder", StandardCharsets.UTF_8);

        CapturingOutputExporter exporter = new CapturingOutputExporter();
        TextSourceProvider textSourceProvider = (
                request,
                tessdataPath,
                shouldStop,
                progress) -> {
            progress.update(40);
            return List.of("first line\nsecond line");
        };
        ProfileProvider profileProvider = List::of;

        ProcessingManager manager = new ProcessingManager(
                textSourceProvider,
                profileProvider,
                exporter);

        File result = manager.process(
                new ProcessRequest(
                        input.toString(),
                        300,
                        "tur",
                        "custom"),
                "unused-tessdata",
                tempDir.resolve("out").toString(),
                null,
                new AtomicBoolean(false));

        assertTrue(result.exists());
        assertTrue(result.length() > 0);
        assertTrue(result.getName().endsWith(".custom"));
        assertEquals("custom", exporter.requestedFormat);
        assertFalse(exporter.rows.isEmpty());
        assertEquals("first line", exporter.rows.get(0).get("raw_text"));
    }

    @Test
    void communityOutputKeepsLegacyCsvElseXlsxResolution() {
        CommunityOutputExporter exporter = new CommunityOutputExporter();

        assertEquals("csv", exporter.resolveExtension("CSV"));
        assertEquals("xlsx", exporter.resolveExtension("xlsx"));
        assertEquals("xlsx", exporter.resolveExtension("unknown"));
        assertEquals("xlsx", exporter.resolveExtension(null));
    }

    @Test
    void cancelledPipelineDoesNotWriteOutput() throws Exception {
        Path input = tempDir.resolve("cancelled.pdf");
        Files.writeString(input, "test-placeholder", StandardCharsets.UTF_8);

        AtomicBoolean stopped = new AtomicBoolean(false);
        CapturingOutputExporter exporter = new CapturingOutputExporter();

        TextSourceProvider textSourceProvider = (
                request,
                tessdataPath,
                shouldStop,
                progress) -> {
            shouldStop.set(true);
            return List.of("ignored");
        };

        ProcessingManager manager = new ProcessingManager(
                textSourceProvider,
                List::of,
                exporter);

        File result = manager.process(
                new ProcessRequest(
                        input.toString(),
                        300,
                        "tur",
                        "custom"),
                "unused-tessdata",
                tempDir.resolve("cancel-out").toString(),
                null,
                stopped);

        assertEquals(null, result);
        assertFalse(exporter.called);
    }

    private static final class CapturingOutputExporter implements OutputExporter {
        private boolean called;
        private String requestedFormat;
        private List<Map<String, String>> rows = List.of();

        @Override
        public String resolveExtension(String requestedFormat) {
            return "custom";
        }

        @Override
        public void export(
                List<Map<String, String>> rows,
                List<FieldDefinition> fields,
                File outputFile,
                String requestedFormat) throws IOException {

            this.called = true;
            this.requestedFormat = requestedFormat;
            this.rows = rows;

            Files.createDirectories(outputFile.toPath().getParent());
            Files.writeString(
                    outputFile.toPath(),
                    "extension-test",
                    StandardCharsets.UTF_8);
        }
    }
}
