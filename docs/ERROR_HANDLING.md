<!--
# Dosya Yolu: docs/ERROR_HANDLING.md
# Amac: JExporter hata yakalama sistemini aciklar
# Modul - FileType
# Version: 2.4.4
# Aciklama: AppExceptionHandler ve AppLogger ile global hata yakalama akislarini dokumante eder
# Bagimli Oldugu Katman: Tool
-->

# Hata Yakalama

JExporter global hata yakalayici kullanir.

## Dosyalar

```text
src/main/java/com/jexporter/logging/AppExceptionHandler.java
src/main/java/com/jexporter/logging/AppLogger.java
```

## Yakaladigi Hatalar

- Swing UI hatalari
- Thread hatalari
- RuntimeException
- Error turleri
- NoSuchMethodError benzeri bagimlilik hatalari

## Log Dosyasi

```text
logs/jexporter.log
```
