# 📄 Dosya Yolu: tools/validate-extension-api.py
# 📌 Amac: JExporter Extension API Maven whitelist ve dependency-free public contract sinirini dogrulamak
# 📌 Modul - Python
# Version: 2.5.0
# Aciklama: API artifact'inde Community adapter, UI, PDFBox, Tess4J veya private dependency sizmasini engeller
# Bagimli Oldugu Katman: Tool | Service | Model | Config

from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
POM = ROOT / "extension-api" / "pom.xml"

REQUIRED_INCLUDES = [
    "ProcessingProgress.java",
    "TextSourceProvider.java",
    "OutputExporter.java",
    "ProfileProvider.java",
    "ProcessRequest.java",
    "FieldDefinition.java",
]

FORBIDDEN_POM_TOKENS = [
    "CommunityTextSourceProvider",
    "CommunityOutputExporter",
    "ProcessingManager",
    "OCRProcessor",
    "LayoutOcrProcessor",
    "ExcelExporter",
    "javax.swing",
    "pdfbox",
    "tess4j",
    "TurkuazSoft",
    "entitlement",
]


def fail(message: str) -> None:
    raise SystemExit(f"EXTENSION_API_FAIL: {message}")


if not POM.is_file():
    fail("extension-api/pom.xml missing")

content = POM.read_text(encoding="utf-8")

for required in REQUIRED_INCLUDES:
    if required not in content:
        fail(f"required public API include missing: {required}")

for forbidden in FORBIDDEN_POM_TOKENS:
    if forbidden.lower() in content.lower():
        fail(f"forbidden token in Extension API POM: {forbidden}")

if "<dependencies>" in content:
    fail("Extension API must remain dependency-free")

print(f"EXTENSION_API_OK: {len(REQUIRED_INCLUDES)}/{len(REQUIRED_INCLUDES)}")
