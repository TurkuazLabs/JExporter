<!--
# Dosya Yolu: docs/LOGGING.md
# Amac: JExporter sade loglama sistemini aciklar
# Modul - FileType
# Version: 2.4.4
# Aciklama: AppLogger ve ConsoleOutputCapturer kullanimini dokumante eder
# Bagimli Oldugu Katman: Tool
-->

# Loglama

JExporter Log4j kullanmaz.

## Kullanilan Siniflar

```text
src/main/java/com/jexporter/logging/AppLogger.java
src/main/java/com/jexporter/util/ConsoleOutputCapturer.java
```

## Akis

- `AppLogger` konsola yazar.
- `ConsoleOutputCapturer` konsol ciktilarini UI icindeki konsola aktarir.
- `AppLogger` ayni zamanda `logs/jexporter.log` dosyasina yazar.

## Log Seviyeleri

```text
[BASLADI]
[BILGI]
[BASARILI]
[UYARI]
[BASARISIZ]
[DURDURULDU]
```

## Dosya Logu

Runtime log dosyasi:

```text
logs/jexporter.log
```
