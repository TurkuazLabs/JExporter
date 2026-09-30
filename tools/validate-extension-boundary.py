# 📄 Dosya Yolu: tools/validate-extension-boundary.py
# 📌 Amac: JExporter public extension contractlari ile Community adapter dependency sinirini statik dogrulamak
# 📌 Modul - Python
# Version: 2.5.0
# Aciklama: Public contractlarda Swing/Tess4J/PDFBox sizintisini ve ProcessingManager concrete tool composition regressionsini engeller
# Bagimli Oldugu Katman: Tool | Service | Config

from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]

PUBLIC_CONTRACTS = [
    "src/main/java/com/jexporter/service/ProcessingProgress.java",
    "src/main/java/com/jexporter/service/TextSourceProvider.java",
    "src/main/java/com/jexporter/service/OutputExporter.java",
    "src/main/java/com/jexporter/service/ProfileProvider.java",
]

REQUIRED_ADAPTERS = [
    "src/main/java/com/jexporter/service/CommunityTextSourceProvider.java",
    "src/main/java/com/jexporter/service/CommunityOutputExporter.java",
]

FORBIDDEN_PUBLIC_IMPORT_TOKENS = [
    "javax.swing",
    "net.sourceforge.tess4j",
    "org.apache.pdfbox",
]

PROCESSING_MANAGER = (
    ROOT
    / "src/main/java/com/jexporter/core/ProcessingManager.java"
)


def fail(message: str) -> None:
    raise SystemExit(f"EXTENSION_BOUNDARY_FAIL: {message}")


for relative_path in PUBLIC_CONTRACTS + REQUIRED_ADAPTERS:
    if not (ROOT / relative_path).is_file():
        fail(f"required extension file missing: {relative_path}")

for relative_path in PUBLIC_CONTRACTS:
    content = (ROOT / relative_path).read_text(encoding="utf-8")
    for forbidden in FORBIDDEN_PUBLIC_IMPORT_TOKENS:
        if forbidden in content:
            fail(f"public contract leaks external/UI dependency: {relative_path} -> {forbidden}")

manager = PROCESSING_MANAGER.read_text(encoding="utf-8")

for required in (
    "TextSourceProvider textSourceProvider",
    "ProfileProvider profileProvider",
    "OutputExporter outputExporter",
    "new CommunityTextSourceProvider()",
    "new CommunityOutputExporter()",
):
    if required not in manager:
        fail(f"ProcessingManager composition contract missing: {required}")

for forbidden in (
    "new PdfTextExtractor()",
    "new LayoutOcrProcessor()",
    "new OCRProcessor()",
    "new ExcelExporter()",
):
    if forbidden in manager:
        fail(f"ProcessingManager concrete Tool dependency returned: {forbidden}")

profile_manager = (
    ROOT
    / "src/main/java/com/jexporter/profile/ProfileManager.java"
).read_text(encoding="utf-8")

if "implements ProfileProvider" not in profile_manager:
    fail("ProfileManager must implement public ProfileProvider")

print(
    "EXTENSION_BOUNDARY_OK: "
    f"{len(PUBLIC_CONTRACTS)} contracts, "
    f"{len(REQUIRED_ADAPTERS)} Community adapters"
)
