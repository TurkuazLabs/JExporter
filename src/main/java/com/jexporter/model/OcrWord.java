/*
# Dosya Yolu: src/main/java/com/jexporter/model/OcrWord.java
# Amac: HOCR/PDF konum verisinden gelen kelime ve koordinat bilgisini tutar
# Modul - FileType
# Version: 2.4.4
# Aciklama: Layout OCR ve pozisyon bazli satir-sutun gruplama icin model sinifidir
# Bagimli Oldugu Katman: Service
*/
package com.jexporter.model;

public class OcrWord {

    private final String text;
    private final int x0;
    private final int y0;
    private final int x1;
    private final int y1;
    private final int page;

    public OcrWord(String text, int x0, int y0, int x1, int y1, int page) {
        this.text = text == null ? "" : text;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.page = page;
    }

    public String getText() {
        return text;
    }

    public int getX0() {
        return x0;
    }

    public int getY0() {
        return y0;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getPage() {
        return page;
    }

    public int centerX() {
        return (x0 + x1) / 2;
    }

    public int centerY() {
        return (y0 + y1) / 2;
    }
}
