# Eski JXporter Parca Arastirmasi - v2.2.0

Eski proje tarandi ve yeni JExporter projesine alinacak/alınmayacak kisimlar belirlendi.

## Yeni projeye aktif alinan kisimlar

1. Splash/logo kullanimi
   - Eski: JXporter/ui/SplashScreen.java
   - Yeni: JExporter/ui/SplashScreen.java

2. Konumlu OCR kelime modeli
   - Eski: model/OcrWord.java
   - Yeni: model/OcrWord.java

3. Layout OCR satir gruplama
   - Eski: core/LayoutTableBuilder.java
   - Yeni: core/LayoutTableBuilder.java

4. HOCR tabanli layout OCR
   - Eski: core/DocumentProcessorWithLayout.java
   - Yeni: core/LayoutOcrProcessor.java

5. PDF metin pozisyon cikarici
   - Eski: core/PDFTextPositionExtractor.java
   - Yeni: core/PdfTextPositionExtractor.java

6. Bolgesel OCR altyapisi
   - Eski: ocr/OcrEngine.java, OcrRegion.java, OcrResult.java
   - Yeni: ocr/OcrEngine.java, OcrRegion.java, OcrResult.java

7. PDF sayfa render yardimcisi
   - Eski: pdf/PdfReader.java ve core/PdfUtil.java
   - Yeni: pdf/PdfPageRenderer.java

## Yeni akisa uygulanan ana degisiklik

ProcessingManager artik su sirayla calisir:

1. PDF metin katmani
2. Layout OCR / HOCR
3. Klasik OCR

## Alinmayan kisimlar

- Log4j dosyalari ve logger kullanimlari alinmadi.
- Profile JSON sistemi alinmadi.
- Eski ExcelWriter dogrudan alinmadi; mevcut ExcelExporter zaten daha gelismis oldugu icin korunup genisletildi.
- Eski MainUI dogrudan alinmadi; modern MainUI korunup logo/splash entegre edildi.
