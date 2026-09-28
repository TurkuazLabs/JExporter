/*
# Dosya Yolu: src/main/java/com/jexporter/ocr/OcrEngine.java
# Amac: BufferedImage veya bolge listesi uzerinden Tess4J OCR calistirir
# Modul - FileType
# Version: 2.4.4
# Aciklama: Eski bolgesel OCR motoru Log4j olmadan modern projeye uyarlanmistir
# Bagimli Oldugu Katman: Tool
*/
package com.jexporter.ocr;

import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class OcrEngine {

    private final String tessDataPath;
    private final String language;

    public OcrEngine(String tessDataPath, String language) {
        this.tessDataPath = tessDataPath;
        this.language = language;
    }

    public String readImage(BufferedImage image) throws TesseractException {
        ITesseract tesseract = createTesseract();
        return tesseract.doOCR(image).trim();
    }

    public List<OcrResult> readRegions(BufferedImage image, List<OcrRegion> regions) {
        List<OcrResult> results = new ArrayList<>();

        if (image == null || regions == null || regions.isEmpty()) {
            return results;
        }

        for (OcrRegion region : regions) {
            try {
                BufferedImage subImage = safeSubImage(image, region);
                String text = readImage(subImage);
                results.add(new OcrResult(text, region.getX(), region.getY(), region.getWidth(), region.getHeight()));
            } catch (Exception ex) {
                results.add(new OcrResult("", region.getX(), region.getY(), region.getWidth(), region.getHeight()));
                System.err.println("[BASARISIZ] Bolgesel OCR hatasi: " + ex.getMessage());
            }
        }

        return results;
    }

    private ITesseract createTesseract() {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage(language);
        tesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_BLOCK);
        return tesseract;
    }

    private BufferedImage safeSubImage(BufferedImage image, OcrRegion region) {
        int x = Math.max(0, Math.round(region.getX()));
        int y = Math.max(0, Math.round(region.getY()));
        int width = Math.max(1, Math.round(region.getWidth()));
        int height = Math.max(1, Math.round(region.getHeight()));

        if (x + width > image.getWidth()) {
            width = image.getWidth() - x;
        }

        if (y + height > image.getHeight()) {
            height = image.getHeight() - y;
        }

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("OCR bolgesi sayfa disinda kaliyor.");
        }

        return image.getSubimage(x, y, width, height);
    }
}
