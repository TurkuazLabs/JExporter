<!--
# 📄 Dosya Yolu: README.md
# 📌 Amac: JExporter Community projesinin GitHub giris dokumanini ve edition sinirlarini saglamak
# 📌 Modul - FileType
# Version: 2.5.1
# Aciklama: Proje amaci, Community/Pro ayrimi, build komutlari, OCR verisi ve dokumantasyon baglantilarini aciklar
# Bagimli Oldugu Katman: Controller | Service | Repo | Tool | View | Language
-->

# JExporter Community

JExporter, PDF dosyalarindan metin ve OCR verisi okuyup Excel veya CSV cikti ureten Java masaustu uygulamasidir. Bu public repository **JExporter Community Edition** kaynak kodunu barindirir.

## Community ve Pro

**Community Edition**
- Bu public repoda bulunan TurkuazLabs kaynaklari Apache License 2.0 altindadir.
- Kaynak kod Apache-2.0 kosullarina uygun olarak kullanilabilir, degistirilebilir ve dagitilabilir.

**Pro Edition**
- Pro kaynak kodu bu public repoda bulunmaz.
- Pro moduller, ticari servisler, ozel entegrasyonlar ve Pro'ya ozel dagitimlar ayri/private kaynaklar veya paketler uzerinden saglanir.
- Pro urunlerine erisim ayri ticari lisans veya abonelik kosullarina tabidir.
- Community lisansi, ayri dagitilan Pro kaynak kodu veya Pro servisleri icin otomatik lisans hakki vermez.

Ayrintili edition sinirlari icin `EDITIONS.md`, Community lisansi icin `LICENSE` dosyasina bakin.

## Gereksinimler

- Java 17
- Maven 3.9+
- OCR icin Tesseract `traineddata` dosyalari

## Build

```bash
mvn clean package
```

## Calistirma

```bash
mvn org.codehaus.mojo:exec-maven-plugin:3.5.1:java -Dexec.mainClass=com.mycompany.jexporter.JExporter
```

## OCR Dil Verileri

Buyuk `*.traineddata` dosyalari Git repository'sine eklenmez. Turkce ve OSD modellerini indirmek icin:

Windows:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/download-tessdata.ps1
```

Linux/macOS:

```bash
bash scripts/download-tessdata.sh
```

Dosyalar `tessdata/` klasorune indirilir ve indirme scriptleri beklenen SHA-256 degerlerini dogrular.

## Public Extension Contractlari

v2.5.0 ile processing pipeline concrete OCR/export siniflarindan ayrildi; v2.5.1 ile klasik OCR adimi da public `OcrProvider` contractina alindi.

Public Service contractlari:

- `TextSourceProvider`
- `OcrProvider`
- `OutputExporter`
- `ProfileProvider`
- `ProcessingProgress`

Varsayilan Community davranisi `CommunityTextSourceProvider` ve `CommunityOutputExporter` ile korunur. Public contract paketleri Swing, Tess4J ve PDFBox'a bagimli degildir.

## Dokumantasyon

- `docs/README.md`
- `docs/ARCHITECTURE.md`
- `docs/RUN.md`
- `docs/CHANGELOG.md`

## Surum

Guncel kaynak surumu: `2.5.1`
