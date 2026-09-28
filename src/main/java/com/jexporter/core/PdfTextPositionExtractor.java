/*
# Dosya Yolu: src/main/java/com/jexporter/core/PdfTextPositionExtractor.java
# Amac: PDF metin katmanindan karakter bazli koordinat cikarmak
# Modul - FileType
# Version: 2.4.4
# Aciklama: Eski PDFTextPositionExtractor sinifi yeni paket adi ve daha guvenli listeleme ile tasindi
# Bagimli Oldugu Katman: Tool
*/
package com.jexporter.core;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfTextPositionExtractor extends PDFTextStripper {

    private final List<TextChunk> chunks;
    private int currentPage;

    public PdfTextPositionExtractor() throws IOException {
        super.setSortByPosition(true);
        this.chunks = new ArrayList<>();
        this.currentPage = 1;
    }

    public List<TextChunk> extractChunks(File pdfFile) throws IOException {
        List<TextChunk> allChunks = new ArrayList<>();

        try (PDDocument document = PDDocument.load(pdfFile)) {
            int totalPages = document.getNumberOfPages();

            for (int page = 1; page <= totalPages; page++) {
                chunks.clear();
                currentPage = page;
                setStartPage(page);
                setEndPage(page);
                getText(document);
                allChunks.addAll(new ArrayList<>(chunks));
            }
        }

        return allChunks;
    }

    @Override
    protected void writeString(String text, List<TextPosition> textPositions) {
        for (TextPosition position : textPositions) {
            chunks.add(new TextChunk(
                    position.getUnicode(),
                    position.getXDirAdj(),
                    position.getYDirAdj(),
                    position.getWidthDirAdj(),
                    position.getHeightDir(),
                    currentPage
            ));
        }
    }

    public static class TextChunk {

        private final String text;
        private final float x;
        private final float y;
        private final float width;
        private final float height;
        private final int page;

        public TextChunk(String text, float x, float y, float width, float height, int page) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.page = page;
        }

        public String getText() {
            return text;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public int getPage() {
            return page;
        }
    }
}
