<!--
# Dosya Yolu: docs/OUTPUT_TROUBLESHOOT.md
# Amac: Cikti dosyasi olusmama veya gorunmeme sorunlarini aciklar
# Modul - FileType
# Version: 2.4.4
# Aciklama: XLSX/CSV yazma, POI kaldirma ve dosya kilidi sorunlarini dokumante eder
# Bagimli Oldugu Katman: Tool
-->

# Cikti Sorun Giderme

## v2.2.6 Degisikligi

Apache POI kaldirildi. XLSX dosyasi artik saf Java ile OpenXML zip paketi olarak uretilir.

Bu nedenle su uyari artik gelmemelidir:

```text
ERROR StatusLogger Log4j2 could not find a logging implementation.
```

## Basarili Cikti Loglari

```text
[BASLADI] XLSX OpenXML paketi olusturuluyor.
[BASARILI] XLSX hedef dosyaya tasindi
[BASARILI] Cikti dosyasi dogrulandi
[BASARILI] Cikti dosyasi tamamlandi
```

## Halen Cikti Yoksa

- Dosya Excel tarafindan acik olabilir.
- Cikti klasorunde yazma izni olmayabilir.
- Desktop klasoru OneDrive veya Windows korumasi altinda olabilir.
- Once `output` klasoru ile deneyin.
