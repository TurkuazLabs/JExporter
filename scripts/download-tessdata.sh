#!/usr/bin/env bash
# 📄 Dosya Yolu: scripts/download-tessdata.sh
# 📌 Amac: JExporter icin gerekli Tesseract Turkce ve OSD dil modellerini indirmek
# 📌 Modul - FileType
# Version: 2.4.4
# Aciklama: Orijinal JExporter paketindeki modelleri resmi tessdata repository'sinden indirir ve SHA-256 ile dogrular
# Bagimli Oldugu Katman: Tool

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TESSDATA_DIR="$REPO_ROOT/tessdata"
BASE_URL="https://raw.githubusercontent.com/tesseract-ocr/tessdata/main"

mkdir -p "$TESSDATA_DIR"

sha256_of() {
  if command -v sha256sum >/dev/null 2>&1; then
    sha256sum "$1" | awk '{print $1}'
    return
  fi

  if command -v shasum >/dev/null 2>&1; then
    shasum -a 256 "$1" | awk '{print $1}'
    return
  fi

  echo "SHA-256 araci bulunamadi." >&2
  exit 1
}

download_model() {
  local file="$1"
  local expected_hash="$2"
  local target="$TESSDATA_DIR/$file"

  echo "Indiriliyor: $file"
  curl -fL "$BASE_URL/$file" -o "$target"

  local actual_hash
  actual_hash="$(sha256_of "$target")"

  if [[ "$actual_hash" != "$expected_hash" ]]; then
    rm -f "$target"
    echo "SHA-256 dogrulamasi basarisiz: $file" >&2
    exit 1
  fi

  echo "Dogrulandi: $file"
}

download_model "tur.traineddata" "489b9504e80d7184ed1ac9a1976647884ee71149da231ff3c2c1dc15370f2f3d"
download_model "osd.traineddata" "e19f2ae860792fdf372cf48d8ce70ae5da3c4052962fe22e9de1f680c374bb0e"

echo "Tessdata modelleri hazir."
