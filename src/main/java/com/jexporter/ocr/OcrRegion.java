/*
# Dosya Yolu: src/main/java/com/jexporter/ocr/OcrRegion.java
# Amac: OCR icin kesilecek bolge koordinatlarini temsil eder
# Modul - FileType
# Version: 2.4.4
# Aciklama: Eski JXporter bolgesel OCR yapisindan modern projeye tasinmistir
# Bagimli Oldugu Katman: Tool
*/
package com.jexporter.ocr;

public class OcrRegion {

    private final float x;
    private final float y;
    private final float width;
    private final float height;

    public OcrRegion(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
