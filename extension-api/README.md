# 📄 Dosya Yolu: extension-api/README.md
# 📌 Amac: JExporter public extension API Maven artifact kapsam ve tuketim sinirini aciklamak
# 📌 Modul - Markdown
# Version: 2.5.0
# Aciklama: Community veya private extension implementationlarinin source kopyalamadan tuketecegi compile-time contract
# Bagimli Oldugu Katman: Service | Model

# JExporter Extension API

Artifact:

```text
com.turkuazlabs.jexporter:jexporter-extension-api:2.5.0-SNAPSHOT
```

Bu modul yeni source kopyasi tutmaz. Root `src/main/java` agacindan whitelist ile yalniz public extension contractlarini compile eder.

## Dahil

- `ProcessingProgress`
- `TextSourceProvider`
- `OutputExporter`
- `ProfileProvider`
- `ProcessRequest`
- `FieldDefinition`

## Haric

- Swing UI
- PDFBox
- Tess4J
- Community adapter implementationlari
- OCR engine implementationlari
- XLSX/CSV implementationi
- Pro/private source
- entitlement implementation
- traineddata

## Local Build

```bash
mvn -B -f extension-api/pom.xml clean verify
```

## Kural

Community extension API private Pro repository'ye bagimli olamaz.

Private veya third-party implementationlar bu artifact'i dependency olarak tuketebilir.
