<!--
# Dosya Yolu: docs/AUTO_SETTINGS.md
# Amac: Auto DPI ve Auto OCR dili davranisini aciklar
# Modul - FileType
# Version: 2.4.4
# Aciklama: Dosya boyutuna gore DPI secimi ve tessdata kontrol akislarini dokumante eder
# Bagimli Oldugu Katman: Tool
-->

# Auto Ayarlar

## Auto DPI

DPI seceneginde `Auto` secilirse dosya boyutuna gore otomatik DPI belirlenir.

```text
0 - 2 MB     -> 600 DPI
2 - 8 MB     -> 450 DPI
8 - 20 MB    -> 350 DPI
20 MB ustu   -> 300 DPI
```

## Auto OCR Dili

OCR dili `auto` secilirse sistem once `tur.traineddata` dosyasini arar.

Bulunamazsa `tessdata` klasorundeki ilk `.traineddata` dosyasini kullanir.

Hic dosya yoksa kullaniciya indirme onerilir.

## Varsayilan Cikti Klasoru

Varsayilan cikti klasoru kullanicinin Desktop klasorudur.


## DPI Manual Secenekleri

DPI dropdown icinde manual secenekler 300 DPI ile baslar:

```text
Auto
300
350
400
450
500
600
700
800
900
1000
1200
```


## Yuksek DPI Uyarisi

600 DPI uzeri manual secimlerde uygulama kullanicidan onay ister.

Yuksek DPI daha iyi goruntu verebilir; fakat OCR suresini, RAM kullanimini ve gecici goruntu boyutunu ciddi sekilde artirir.
