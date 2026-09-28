/*
# Dosya Yolu: src/main/java/com/jexporter/core/TextGrouper.java
# Amac: PDF/OCR metnini satir ve sutun alanlarina ayirir
# Modul - FileType
# Version: 2.4.4
# Aciklama: Orijinal layout modunda satir bosluklarini korur, tablo modunda alanlari ayirir
# Bagimli Oldugu Katman: Service
*/
package com.jexporter.core;

import com.jexporter.profile.FieldDefinition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TextGrouper {

    private final List<FieldDefinition> fields;
    private int maxEmptyRows;

    public TextGrouper(List<FieldDefinition> fields) {
        this.fields = fields;
        this.maxEmptyRows = 3;
    }

    public List<Map<String, String>> group(List<String> pageTexts) {
        List<Map<String, String>> rows = new ArrayList<>();
        int pageNo = 1;

        for (String pageText : pageTexts) {
            int emptyCount = 0;
            String[] lines = pageText == null ? new String[0] : pageText.split("\\r?\\n");

            int lineNo = 1;
            for (String rawLine : lines) {
                String line = normalizeLine(rawLine);

                if (line.trim().isEmpty()) {
                    emptyCount++;

                    if (emptyCount <= maxEmptyRows) {
                        rows.add(createInfoRow(pageNo, lineNo, ""));
                    }

                    lineNo++;
                    continue;
                }

                emptyCount = 0;

                Map<String, String> row = new LinkedHashMap<>();
                row.put("page", String.valueOf(pageNo));
                row.put("line", String.valueOf(lineNo));
                row.put("raw_text", line);

                for (FieldDefinition field : fields) {
                    row.put(field.getName(), slice(line, field.getStartCol(), field.getEndCol()));
                }

                rows.add(row);
                lineNo++;
            }

            pageNo++;
        }

        return rows;
    }

    public void setMaxEmptyRows(int maxEmptyRows) {
        this.maxEmptyRows = Math.max(0, maxEmptyRows);
    }

    private Map<String, String> createInfoRow(int pageNo, int lineNo, String rawText) {
        Map<String, String> row = new LinkedHashMap<>();
        row.put("page", String.valueOf(pageNo));
        row.put("line", String.valueOf(lineNo));
        row.put("raw_text", rawText);

        for (FieldDefinition field : fields) {
            row.put(field.getName(), "");
        }

        return row;
    }

    private String normalizeLine(String line) {
        if (line == null) {
            return "";
        }

        String normalized = line
                .replace('\t', ' ')
                .replace('\u00A0', ' ');

        if (isOriginalLayoutMode()) {
            return trimRight(normalized);
        }

        return normalized
                .replaceAll("[ ]{2,}", " ")
                .trim();
    }

    private boolean isOriginalLayoutMode() {
        return fields == null || fields.isEmpty();
    }

    private String trimRight(String value) {
        int end = value.length();

        while (end > 0 && Character.isWhitespace(value.charAt(end - 1))) {
            end--;
        }

        return value.substring(0, end);
    }

    private String slice(String value, int start, int end) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        int safeStart = Math.max(0, Math.min(start, value.length()));
        int safeEnd = Math.max(safeStart, Math.min(end, value.length()));

        return value.substring(safeStart, safeEnd).trim();
    }
}
