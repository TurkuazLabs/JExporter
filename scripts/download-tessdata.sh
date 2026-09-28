#!/usr/bin/env bash
# 📄 Dosya Yolu: scripts/download-tessdata.sh
# 📌 Amac: JExporter icin gerekli Tesseract Turkce ve OSD dil modellerini indirmek
# 📌 Modul - FileType
# Version: 2.4.4
# Aciklama: Resmi tessdata_best repository'sinden runtime OCR modellerini tessdata klasorune indirir
# Bagimli Oldugu Katman: Tool

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TESSDATA_DIR="$REPO_ROOT/tessdata"
BASE_URL="https://raw.githubusercontent.com/tesseract-ocr/tessdata_best/main"

mkdir -p "$TESSDATA_DIR"

for file in tur.traineddata osd.traineddata; do
  echo "Indiriliyor: $file"
  curl -fL "$BASE_URL/$file" -o "$TESSDATA_DIR/$file"
done

echo "Tessdata modelleri hazir."
