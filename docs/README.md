<!--
# Dosya Yolu: docs/README.md
# Amac: JExporter projesinin genel dokumantasyon giris dosyasidir
# Modul - FileType
# Version: 2.4.4
# Aciklama: Proje amaci, klasor yapisi ve temel calisma notlarini aciklar
# Bagimli Oldugu Katman: Tool
-->

# JExporter

JExporter, PDF dosyalarindan veri okuyup Excel veya CSV cikti uretmek icin gelistirilen masaustu Java uygulamasidir.

## Guncel Proje

Calistirilacak ana proje:

```text
JExporter
```

Ana sinif:

```text
com.mycompany.jexporter.JExporter
```

## Temel Akis

1. PDF dosyasi secilir.
2. PDF metin katmani okunmaya calisilir.
3. Metin katmani yetersizse layout OCR denenir.
4. Gerekirse klasik OCR kullanilir.
5. Metin satir ve sutunlara ayrilir.
6. Cikti Excel veya CSV olarak kaydedilir.

## Onemli Kararlar

- Log4j kullanilmaz.
- Profile JSON kullanilmaz.
- `ConsoleOutputCapturer` korunur.
- Logo ve splash kaynaklari `src/main/resources` altindadir.

## Dokumantasyon Duzeni

Tum README dosyalari ve proje notlari `docs/` klasoru altinda tutulur.
