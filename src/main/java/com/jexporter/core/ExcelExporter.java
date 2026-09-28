/*
# Dosya Yolu: src/main/java/com/jexporter/core/ExcelExporter.java
# Amac: Islenmis satir verisini orijinal PDF duzenine uygun XLSX veya CSV formatinda kaydeder
# Modul - FileType
# Version: 2.4.4
# Aciklama: Varsayilan modda teknik basliklari yazmadan PDF satirlarini Excel'e aktarir
# Bagimli Oldugu Katman: Tool
*/
package com.jexporter.core;

import com.jexporter.logging.AppLogger;
import com.jexporter.profile.FieldDefinition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExcelExporter {

    private static final String SHEET_NAME_LAYOUT = "PDF Layout";
    private static final String SHEET_NAME_DATA = "JExporter Data";

    public void export(List<Map<String, String>> rows,
                       List<FieldDefinition> fields,
                       File outputFile,
                       String outputFormat) throws IOException {
        ensureParentDirectory(outputFile);

        boolean originalLayoutMode = fields == null || fields.isEmpty();

        if ("csv".equalsIgnoreCase(outputFormat)) {
            exportCsv(rows, fields, outputFile, originalLayoutMode);
            verifyOutput(outputFile);
            return;
        }

        exportXlsx(rows, fields, outputFile, originalLayoutMode);
        verifyOutput(outputFile);
    }

    private void exportXlsx(List<Map<String, String>> rows,
                            List<FieldDefinition> fields,
                            File outputFile,
                            boolean originalLayoutMode) throws IOException {
        File tempFile = new File(outputFile.getParentFile(), outputFile.getName() + ".tmp");

        if (tempFile.exists() && !tempFile.delete()) {
            throw new IOException("Gecici cikti dosyasi silinemedi: " + tempFile.getAbsolutePath());
        }

        AppLogger.started(originalLayoutMode
                ? "XLSX orijinal PDF duzeniyle olusturuluyor."
                : "XLSX tablo modu ile olusturuluyor.");

        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
            writeEntry(zip, "[Content_Types].xml", contentTypesXml());
            writeEntry(zip, "_rels/.rels", rootRelsXml());
            writeEntry(zip, "docProps/app.xml", appXml());
            writeEntry(zip, "docProps/core.xml", coreXml());
            writeEntry(zip, "xl/workbook.xml", workbookXml(originalLayoutMode ? SHEET_NAME_LAYOUT : SHEET_NAME_DATA));
            writeEntry(zip, "xl/_rels/workbook.xml.rels", workbookRelsXml());
            writeEntry(zip, "xl/styles.xml", stylesXml());
            writeEntry(zip, "xl/worksheets/sheet1.xml",
                    originalLayoutMode ? layoutSheetXml(rows) : tableSheetXml(rows, fields));
        } catch (IOException ex) {
            if (tempFile.exists()) {
                tempFile.delete();
            }

            throw ex;
        }

        Files.move(tempFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        AppLogger.success("XLSX hedef dosyaya tasindi: " + outputFile.getAbsolutePath());
    }

    private void exportCsv(List<Map<String, String>> rows,
                           List<FieldDefinition> fields,
                           File outputFile,
                           boolean originalLayoutMode) throws IOException {
        File tempFile = new File(outputFile.getParentFile(), outputFile.getName() + ".tmp");

        if (tempFile.exists() && !tempFile.delete()) {
            throw new IOException("Gecici CSV dosyasi silinemedi: " + tempFile.getAbsolutePath());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, StandardCharsets.UTF_8))) {
            if (originalLayoutMode) {
                for (Map<String, String> row : rows) {
                    String text = row.getOrDefault("raw_text", "");
                    writer.write(escapeCsv(text));
                    writer.newLine();
                }
            } else {
                List<String> headers = buildHeaders(fields);
                writer.write(String.join(";", headers));
                writer.newLine();

                for (Map<String, String> row : rows) {
                    List<String> cells = new ArrayList<>();

                    for (String header : headers) {
                        cells.add(escapeCsv(row.getOrDefault(header, "")));
                    }

                    writer.write(String.join(";", cells));
                    writer.newLine();
                }
            }
        } catch (IOException ex) {
            if (tempFile.exists()) {
                tempFile.delete();
            }

            throw ex;
        }

        Files.move(tempFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private String layoutSheetXml(List<Map<String, String>> rows) {
        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        xml.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" ");
        xml.append("xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">");
        xml.append("<sheetViews><sheetView workbookViewId=\"0\"/></sheetViews>");
        xml.append("<sheetFormatPr defaultRowHeight=\"18\"/>");
        xml.append("<cols><col min=\"1\" max=\"1\" width=\"120\" customWidth=\"1\"/></cols>");
        xml.append("<sheetData>");

        int rowNumber = 1;
        int lastPage = -1;
        boolean multiPage = hasMultiplePages(rows);

        for (Map<String, String> dataRow : rows) {
            int page = parseInt(dataRow.get("page"), 1);
            String text = dataRow.getOrDefault("raw_text", "");

            if (multiPage && page != lastPage) {
                if (rowNumber > 1) {
                    appendSingleCellRow(xml, rowNumber++, "", false);
                }

                appendSingleCellRow(xml, rowNumber++, "Sayfa " + page, true);
                lastPage = page;
            }

            appendSingleCellRow(xml, rowNumber++, text, false);
        }

        xml.append("</sheetData>");
        xml.append("</worksheet>");

        return xml.toString();
    }

    private boolean hasMultiplePages(List<Map<String, String>> rows) {
        int firstPage = -1;

        for (Map<String, String> row : rows) {
            int page = parseInt(row.get("page"), 1);

            if (firstPage == -1) {
                firstPage = page;
            } else if (page != firstPage) {
                return true;
            }
        }

        return false;
    }

    private String tableSheetXml(List<Map<String, String>> rows, List<FieldDefinition> fields) {
        List<String> headers = buildHeaders(fields);
        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
        xml.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" ");
        xml.append("xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">");
        xml.append("<sheetViews><sheetView workbookViewId=\"0\"/></sheetViews>");
        xml.append("<sheetFormatPr defaultRowHeight=\"15\"/>");
        xml.append("<cols>");

        for (int i = 0; i < headers.size(); i++) {
            int width = i == 2 ? 70 : 18;
            xml.append("<col min=\"").append(i + 1).append("\" max=\"").append(i + 1)
                    .append("\" width=\"").append(width).append("\" customWidth=\"1\"/>");
        }

        xml.append("</cols>");
        xml.append("<sheetData>");

        appendRow(xml, 1, headers, true);

        int rowIndex = 2;
        for (Map<String, String> dataRow : rows) {
            List<String> values = new ArrayList<>();

            for (String header : headers) {
                values.add(dataRow.getOrDefault(header, ""));
            }

            appendRow(xml, rowIndex++, values, false);
        }

        xml.append("</sheetData>");
        xml.append("</worksheet>");

        return xml.toString();
    }

    private void appendSingleCellRow(StringBuilder xml, int rowNumber, String value, boolean bold) {
        xml.append("<row r=\"").append(rowNumber).append("\">");
        xml.append("<c r=\"A").append(rowNumber).append("\" t=\"inlineStr\"");
        if (bold) {
            xml.append(" s=\"1\"");
        }
        xml.append("><is><t xml:space=\"preserve\">");
        xml.append(escapeXml(value));
        xml.append("</t></is></c>");
        xml.append("</row>");
    }

    private void appendRow(StringBuilder xml, int rowNumber, List<String> values, boolean header) {
        xml.append("<row r=\"").append(rowNumber).append("\">");

        for (int col = 0; col < values.size(); col++) {
            String cellRef = columnName(col + 1) + rowNumber;
            xml.append("<c r=\"").append(cellRef).append("\" t=\"inlineStr\"");

            if (header) {
                xml.append(" s=\"1\"");
            }

            xml.append("><is><t xml:space=\"preserve\">");
            xml.append(escapeXml(values.get(col)));
            xml.append("</t></is></c>");
        }

        xml.append("</row>");
    }

    private List<String> buildHeaders(List<FieldDefinition> fields) {
        List<String> headers = new ArrayList<>();
        headers.add("page");
        headers.add("line");
        headers.add("raw_text");

        if (fields != null) {
            for (FieldDefinition field : fields) {
                headers.add(field.getName());
            }
        }

        return headers;
    }

    private String columnName(int index) {
        StringBuilder name = new StringBuilder();

        while (index > 0) {
            int remainder = (index - 1) % 26;
            name.insert(0, (char) ('A' + remainder));
            index = (index - 1) / 26;
        }

        return name.toString();
    }

    private void writeEntry(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        zip.write(bytes);
        zip.closeEntry();
    }

    private String contentTypesXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
                + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                + "<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>"
                + "<Override PartName=\"/docProps/core.xml\" ContentType=\"application/vnd.openxmlformats-package.core-properties+xml\"/>"
                + "<Override PartName=\"/docProps/app.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.extended-properties+xml\"/>"
                + "</Types>";
    }

    private String rootRelsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
                + "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties\" Target=\"docProps/core.xml\"/>"
                + "<Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties\" Target=\"docProps/app.xml\"/>"
                + "</Relationships>";
    }

    private String workbookXml(String sheetName) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "
                + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
                + "<sheets><sheet name=\"" + escapeXml(sheetName) + "\" sheetId=\"1\" r:id=\"rId1\"/></sheets>"
                + "</workbook>";
    }

    private String workbookRelsXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
                + "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>"
                + "</Relationships>";
    }

    private String stylesXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
                + "<fonts count=\"2\"><font><sz val=\"11\"/><name val=\"Calibri\"/></font><font><b/><sz val=\"11\"/><name val=\"Calibri\"/></font></fonts>"
                + "<fills count=\"2\"><fill><patternFill patternType=\"none\"/></fill><fill><patternFill patternType=\"gray125\"/></fill></fills>"
                + "<borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>"
                + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
                + "<cellXfs count=\"2\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/><xf numFmtId=\"0\" fontId=\"1\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\"/></cellXfs>"
                + "<cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\"/></cellStyles>"
                + "</styleSheet>";
    }

    private String appXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<Properties xmlns=\"http://schemas.openxmlformats.org/officeDocument/2006/extended-properties\" "
                + "xmlns:vt=\"http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes\">"
                + "<Application>JExporter</Application>"
                + "</Properties>";
    }

    private String coreXml() {
        String now = OffsetDateTime.now().toString();

        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<cp:coreProperties xmlns:cp=\"http://schemas.openxmlformats.org/package/2006/metadata/core-properties\" "
                + "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" "
                + "xmlns:dcterms=\"http://purl.org/dc/terms/\" "
                + "xmlns:dcmitype=\"http://purl.org/dc/dcmitype/\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<dc:creator>JExporter</dc:creator>"
                + "<cp:lastModifiedBy>JExporter</cp:lastModifiedBy>"
                + "<dcterms:created xsi:type=\"dcterms:W3CDTF\">" + escapeXml(now) + "</dcterms:created>"
                + "<dcterms:modified xsi:type=\"dcterms:W3CDTF\">" + escapeXml(now) + "</dcterms:modified>"
                + "</cp:coreProperties>";
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value == null ? "" : value.trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String escapeCsv(String value) {
        String safe = value == null ? "" : value;

        if (safe.contains(";") || safe.contains("\"") || safe.contains("\n")) {
            safe = safe.replace("\"", "\"\"");
            return "\"" + safe + "\"";
        }

        return safe;
    }

    private void ensureParentDirectory(File outputFile) throws IOException {
        File parent = outputFile.getParentFile();

        if (parent == null) {
            throw new IOException("Cikti klasoru belirlenemedi: " + outputFile.getAbsolutePath());
        }

        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Cikti klasoru olusturulamadi: " + parent.getAbsolutePath());
        }
    }

    private void verifyOutput(File outputFile) throws IOException {
        if (!outputFile.exists()) {
            throw new IOException("Cikti dosyasi olusmadi: " + outputFile.getAbsolutePath());
        }

        if (outputFile.length() <= 0) {
            throw new IOException("Cikti dosyasi bos olustu: " + outputFile.getAbsolutePath());
        }

        AppLogger.success("Cikti dosyasi dogrulandi. Boyut: " + outputFile.length() + " byte");
    }
}
