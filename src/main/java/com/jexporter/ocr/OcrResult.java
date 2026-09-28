/*
# Dosya Yolu: src/main/java/com/jexporter/ocr/OcrResult.java
# Amac: OCR sonucunu ve koordinat bilgisini birlikte tutar
# Modul - FileType
# Version: 2.4.4
# Aciklama: Bolgesel OCR ve debug ciktilari icin kullanilir
# Bagimli Oldugu Katman: Service
*/
package com.jexporter.ocr;

public class OcrResult {

    private final String text;
    private final float x;
    private final float y;
    private final float width;
    private final float height;

    public OcrResult(String text, float x, float y, float width, float height) {
        this.text = text == null ? "" : text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
}
