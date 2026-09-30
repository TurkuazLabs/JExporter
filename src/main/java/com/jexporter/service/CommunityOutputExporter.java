/*
# 📄 Dosya Yolu: src/main/java/com/jexporter/service/CommunityOutputExporter.java
# 📌 Amac: Mevcut XLSX/CSV ExcelExporter implementationini public OutputExporter contractina baglamak
# 📌 Modul - Java
# Version: 2.5.0
# Aciklama: 2.4.4 davranisini korur; csv disindaki mevcut format girdilerini xlsx olarak resolve eder
# Bagimli Oldugu Katman: Service | Tool | Model
*/
package com.jexporter.service;

import com.jexporter.config.AppConstants;
import com.jexporter.core.ExcelExporter;
import com.jexporter.profile.FieldDefinition;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CommunityOutputExporter implements OutputExporter {

    private final ExcelExporter excelExporter;

    public CommunityOutputExporter() {
        this(new ExcelExporter());
    }

    public CommunityOutputExporter(ExcelExporter excelExporter) {
        this.excelExporter = Objects.requireNonNull(excelExporter, "excelExporter");
    }

    @Override
    public String resolveExtension(String requestedFormat) {
        return AppConstants.FORMAT_CSV.equalsIgnoreCase(requestedFormat)
                ? AppConstants.FORMAT_CSV
                : AppConstants.FORMAT_XLSX;
    }

    @Override
    public void export(
            List<Map<String, String>> rows,
            List<FieldDefinition> fields,
            File outputFile,
            String requestedFormat) throws IOException {
        excelExporter.export(rows, fields, outputFile, resolveExtension(requestedFormat));
    }
}
