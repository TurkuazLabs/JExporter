<!--
# 📄 Dosya Yolu: README.md
# 📌 Amac: JExporter projesinin GitHub giris dokumanini saglamak
# 📌 Modul - FileType
# Version: 2.4.4
# Aciklama: Proje amaci, build komutlari, OCR verisi ve dokumantasyon baglantilarini aciklar
# Bagimli Oldugu Katman: Controller | Service | Repo | Tool | View | Language
-->

# JExporter

JExporter, PDF dosyalarindan metin ve OCR verisi okuyup Excel veya CSV cikti ureten Java masaustu uygulamasidir.

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

Dosyalar `tessdata/` klasorune indirilir.

## Dokumantasyon

- `docs/README.md`
- `docs/ARCHITECTURE.md`
- `docs/RUN.md`
- `docs/CHANGELOG.md`

## Surum

Guncel kaynak surumu: `2.4.4`
