/*
# Dosya Yolu: src/main/java/com/jexporter/pdf/PdfPageRenderer.java
# Amac: PDF sayfalarini BufferedImage olarak render eder
# Modul - FileType
# Version: 2.4.4
# Aciklama: Eski PdfReader/PdfUtil siniflari tek modern adaptor altinda birlestirildi
# Bagimli Oldugu Katman: Tool
*/
package com.jexporter.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfPageRenderer {

    public BufferedImage renderPage(File pdfFile, int pageIndex, int dpi) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            validatePageIndex(document, pageIndex);
            PDFRenderer renderer = new PDFRenderer(document);
            return renderer.renderImageWithDPI(pageIndex, dpi);
        }
    }

    public BufferedImage renderFirstPage(File pdfFile, int dpi) throws IOException {
        return renderPage(pdfFile, 0, dpi);
    }

    private void validatePageIndex(PDDocument document, int pageIndex) {
        if (pageIndex < 0 || pageIndex >= document.getNumberOfPages()) {
            throw new IllegalArgumentException("Sayfa bulunamadi: " + pageIndex);
        }
    }
}
