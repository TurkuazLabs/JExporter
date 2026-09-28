# JExporter Clean v2.2.1

Bu paket temizlenmis guncel JExporter paketidir.

## Silinen / dahil edilmeyen kisimlar

- Eski `JXporter` klasoru
- `target` build ciktisi
- NetBeans private cache klasorleri
- Kopya logo dosyalari
- Eski Log4j dosyalari
- Profile JSON altyapisi

## Korunan kisimlar

- Guncel `JExporter` kaynak kodu
- `tessdata/tur.traineddata`
- `tessdata/osd.traineddata`
- `src/main/resources/logo.png`
- `src/main/resources/jxporter_icon.ico`
- `src/main/resources/icon/app.ico`
- SplashScreen ve modern MainUI
- Layout OCR / HOCR destek siniflari
- Bolgesel OCR destek siniflari
- PDF metin katmani + OCR hibrit ProcessingManager

## Calistirma

NetBeans projesi:

`JExporter`

Ana sinif:

`com.mycompany.jexporter.JExporter`

Komut satiri:

```bat
cd /d C:\Users\Burak01\Documents\NetBeansProjects\JExporter
mvn clean compile exec:java -Dexec.mainClass=com.mycompany.jexporter.JExporter
```

## Not

Eski `JXporter` artik bu pakete dahil degildir. Yine de kendi bilgisayarinda tamamen silmeden once bu temiz paketi derleyip calistir.
