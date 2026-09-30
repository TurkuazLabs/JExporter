<!--
# 📄 Dosya Yolu: docs/ARCHITECTURE.md
# 📌 Amac: JExporter mimari katmanlarini, public extension contractlarini ve modul sorumluluklarini aciklamak
# 📌 Modul - Markdown
# Version: 2.5.0
# Aciklama: Controller, Service, Tool, Model ve Community adapter sinirlarini dokumante eder
# Bagimli Oldugu Katman: Controller | Service | Tool | Model
-->

# Mimari

JExporter katmanli mimari ile ilerler.

## Controller / View

Kullanici arayuzu ve islem tetikleme katmani:

```text
src/main/java/com/jexporter/ui/MainUI.java
src/main/java/com/jexporter/ui/SplashScreen.java
src/main/java/com/mycompany/jexporter/JExporter.java
```

## Service

Is akisi ve public extension contractlari:

```text
src/main/java/com/jexporter/core/ProcessingManager.java
src/main/java/com/jexporter/core/TextGrouper.java

src/main/java/com/jexporter/service/TextSourceProvider.java
src/main/java/com/jexporter/service/OutputExporter.java
src/main/java/com/jexporter/service/ProfileProvider.java
src/main/java/com/jexporter/service/ProcessingProgress.java
```

Public extension contractlari:

- Swing bilmez.
- Tess4J bilmez.
- PDFBox bilmez.
- Pro/private repository bilmez.

## Community Adapter

Varsayilan Community davranisi public contractlar arkasinda su adapterlarla uygulanir:

```text
CommunityTextSourceProvider
  -> PdfTextExtractor
  -> LayoutOcrProcessor
  -> OCRProcessor

CommunityOutputExporter
  -> ExcelExporter

ProfileManager
  -> ProfileProvider
```

## Tool

Dis kutuphane ve yardimci implementation katmani:

```text
src/main/java/com/jexporter/core/OCRProcessor.java
src/main/java/com/jexporter/core/PdfTextExtractor.java
src/main/java/com/jexporter/core/PdfTextPositionExtractor.java
src/main/java/com/jexporter/core/ExcelExporter.java
src/main/java/com/jexporter/core/LayoutOcrProcessor.java
src/main/java/com/jexporter/pdf/PdfPageRenderer.java
src/main/java/com/jexporter/ocr/OcrEngine.java
src/main/java/com/jexporter/util/ConsoleOutputCapturer.java
```

## Model

Veri tasima siniflari:

```text
src/main/java/com/jexporter/model/ProcessRequest.java
src/main/java/com/jexporter/model/OcrWord.java
src/main/java/com/jexporter/profile/FieldDefinition.java
```

## Ana Is Akisi

```text
MainUI
  -> ProcessingManager
      -> TextSourceProvider
          -> CommunityTextSourceProvider
      -> TextGrouper
      -> OutputExporter
          -> CommunityOutputExporter
```

## Dependency Injection

Eski davranis:

```text
ProcessingManager
  -> new PdfTextExtractor()
  -> new LayoutOcrProcessor()
  -> new OCRProcessor()
  -> new ExcelExporter()
```

v2.5.0 ile:

```text
ProcessingManager
  -> TextSourceProvider
  -> ProfileProvider
  -> OutputExporter
```

Default constructor Community adapterlarini compose eder.

Ikinci constructor public extension provider injection destekler.

Bu sayede private/ticari implementationlar Community source'u fork etmeden public contractlari tuketebilir.

## Community / Pro Bagimlilik Yonu

```text
TurkuazLabs/JExporter
      ^
      |
public Service contracts
      |
TurkuazSoft/JExporter-Pro
```

Community kodu private Pro repository'ye bagimli olamaz.

Paid feature isimleri public extension contractinin varligindan otomatik olarak turetilmez; somut urun karari ayrica verilmelidir.
