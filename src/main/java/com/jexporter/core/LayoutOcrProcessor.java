/*
# Dosya Yolu: src/main/java/com/jexporter/core/LayoutOcrProcessor.java
# Amac: Tess4J HOCR cikti ile kelime koordinatlarini okuyup sayfa bazli metin uretir
# Modul - FileType
# Version: 2.4.4
# Aciklama: Layout OCR sonucunu sayfa ayrimini bozmadan TextGrouper akisina hazirlar
# Bagimli Oldugu Katman: Service
*/
package com.jexporter.core;

import com.jexporter.model.OcrWord;
import com.jexporter.model.ProcessRequest;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LayoutOcrProcessor {

    private static final int PROGRESS_OCR_START = 30;
    private static final int PROGRESS_OCR_RANGE = 50;
    private static final int BBOX_COORD_COUNT = 4;

    private final LayoutTableBuilder tableBuilder;

    public LayoutOcrProcessor() {
        this.tableBuilder = new LayoutTableBuilder();
    }

    public List<String> extractTextLines(ProcessRequest request,
                                         String tessdataPath,
                                         AtomicBoolean shouldStop,
                                         JProgressBar progressBar) throws Exception {
        File inputPdf = new File(request.getPdfPath());
        List<String> pageTexts = new ArrayList<>();

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessdataPath);
        tesseract.setLanguage(request.getLanguage());
        tesseract.setHocr(true);

        try (PDDocument document = PDDocument.load(inputPdf)) {
            PDFRenderer renderer = new PDFRenderer(document);
            int totalPages = document.getNumberOfPages();

            for (int page = 0; page < totalPages; page++) {
                if (shouldStop != null && shouldStop.get()) {
                    System.out.println("[DURDURULDU] Layout OCR kullanici tarafindan durduruldu.");
                    break;
                }

                System.out.println("[BASLADI] Layout OCR sayfa isleniyor: " + (page + 1) + "/" + totalPages);

                BufferedImage image = renderer.renderImageWithDPI(page, request.getDpi());
                String hocr = tesseract.doOCR(image);

                List<OcrWord> words = parseHocr(hocr, page + 1);
                List<List<OcrWord>> rows = tableBuilder.groupWordsByRows(words);
                List<String> lines = tableBuilder.toTextLines(rows);
                pageTexts.add(String.join(System.lineSeparator(), lines));

                updateProgress(progressBar, page + 1, totalPages);
                System.out.println("[BASARILI] Layout OCR sayfa tamamlandi: " + (page + 1));
            }
        }

        return pageTexts;
    }

    private List<OcrWord> parseHocr(String hocr, int page) throws Exception {
        List<OcrWord> words = new ArrayList<>();

        if (hocr == null || hocr.trim().isEmpty()) {
            return words;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = factory
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(hocr)));

        NodeList spans = document.getElementsByTagName("span");

        for (int i = 0; i < spans.getLength(); i++) {
            Element span = (Element) spans.item(i);

            if (!span.getAttribute("class").contains("ocrx_word") || !span.hasAttribute("title")) {
                continue;
            }

            String title = span.getAttribute("title");
            String text = span.getTextContent() == null ? "" : span.getTextContent().trim();

            if (text.isEmpty()) {
                continue;
            }

            int[] bbox = parseBbox(title);
            if (bbox == null) {
                continue;
            }

            words.add(new OcrWord(text, bbox[0], bbox[1], bbox[2], bbox[3], page));
        }

        return words;
    }

    private int[] parseBbox(String title) {
        String[] parts = title.split(";");

        for (String part : parts) {
            String trimmed = part.trim();

            if (!trimmed.startsWith("bbox")) {
                continue;
            }

            String[] coords = trimmed.substring(5).trim().split(" ");
            if (coords.length < BBOX_COORD_COUNT) {
                return null;
            }

            try {
                return new int[] {
                        Integer.parseInt(coords[0]),
                        Integer.parseInt(coords[1]),
                        Integer.parseInt(coords[2]),
                        Integer.parseInt(coords[3])
                };
            } catch (NumberFormatException ex) {
                return null;
            }
        }

        return null;
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
