# 📄 Dosya Yolu: scripts/download-tessdata.ps1
# 📌 Amac: JExporter icin gerekli Tesseract Turkce ve OSD dil modellerini indirmek
# 📌 Modul - FileType
# Version: 2.4.4
# Aciklama: Resmi tessdata_best repository'sinden runtime OCR modellerini tessdata klasorune indirir
# Bagimli Oldugu Katman: Tool

$ErrorActionPreference = 'Stop'

$RepoRoot = Split-Path -Parent $PSScriptRoot
$TessdataDir = Join-Path $RepoRoot 'tessdata'
$BaseUrl = 'https://raw.githubusercontent.com/tesseract-ocr/tessdata_best/main'
$Files = @('tur.traineddata', 'osd.traineddata')

New-Item -ItemType Directory -Force -Path $TessdataDir | Out-Null

foreach ($File in $Files) {
    $Target = Join-Path $TessdataDir $File
    Write-Host "Indiriliyor: $File"
    Invoke-WebRequest -Uri "$BaseUrl/$File" -OutFile $Target
}

Write-Host 'Tessdata modelleri hazir.'
