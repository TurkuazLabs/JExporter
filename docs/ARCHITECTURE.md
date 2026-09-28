<!--
# Dosya Yolu: docs/ARCHITECTURE.md
# Amac: JExporter mimari katmanlarini ve modul sorumluluklarini aciklar
# Modul - FileType
# Version: 2.4.4
# Aciklama: Controller, Service, Tool ve model ayrimini dokumante eder
# Bagimli Oldugu Katman: Tool
-->

# Mimari

JExporter katmanli mimari ile ilerler.

## Controller

Kullanici arayuzu ve islem tetikleme katmani.

```text
src/main/java/com/jexporter/ui/MainUI.java
src/main/java/com/jexporter/ui/SplashScreen.java
src/main/java/com/mycompany/jexporter/JExporter.java
```

## Service

Is akisi ve metin isleme katmani.

```text
src/main/java/com/jexporter/core/ProcessingManager.java
src/main/java/com/jexporter/core/TextGrouper.java
src/main/java/com/jexporter/core/LayoutOcrProcessor.java
src/main/java/com/jexporter/core/LayoutTableBuilder.java
```

## Tool

Dis kutuphane ve yardimci adaptor katmani.

```text
src/main/java/com/jexporter/core/OCRProcessor.java
src/main/java/com/jexporter/core/PdfTextExtractor.java
src/main/java/com/jexporter/core/PdfTextPositionExtractor.java
src/main/java/com/jexporter/core/ExcelExporter.java
src/main/java/com/jexporter/pdf/PdfPageRenderer.java
src/main/java/com/jexporter/ocr/OcrEngine.java
src/main/java/com/jexporter/util/ConsoleOutputCapturer.java
```

## Model

Veri tasima siniflari.

```text
src/main/java/com/jexporter/model/ProcessRequest.java
src/main/java/com/jexporter/model/OcrWord.java
src/main/java/com/jexporter/profile/FieldDefinition.java
```

## Ana Is Akisi

```text
MainUI
  -> ProcessingManager
      -> PdfTextExtractor
      -> LayoutOcrProcessor
      -> OCRProcessor
      -> TextGrouper
      -> ExcelExporter
```
