# 📄 Dosya Yolu: scripts/download-tessdata.ps1
# 📌 Amac: JExporter icin gerekli Tesseract Turkce ve OSD dil modellerini indirmek
# 📌 Modul - FileType
# Version: 2.4.4
# Aciklama: Orijinal JExporter paketindeki modelleri resmi tessdata repository'sinden indirir ve SHA-256 ile dogrular
# Bagimli Oldugu Katman: Tool

$ErrorActionPreference = 'Stop'

$RepoRoot = Split-Path -Parent $PSScriptRoot
$TessdataDir = Join-Path $RepoRoot 'tessdata'
$BaseUrl = 'https://raw.githubusercontent.com/tesseract-ocr/tessdata/main'

$Models = @(
    @{
        Name = 'tur.traineddata'
        Sha256 = '489b9504e80d7184ed1ac9a1976647884ee71149da231ff3c2c1dc15370f2f3d'
    },
    @{
        Name = 'osd.traineddata'
        Sha256 = 'e19f2ae860792fdf372cf48d8ce70ae5da3c4052962fe22e9de1f680c374bb0e'
    }
)

New-Item -ItemType Directory -Force -Path $TessdataDir | Out-Null

foreach ($Model in $Models) {
    $File = $Model.Name
    $ExpectedHash = $Model.Sha256
    $Target = Join-Path $TessdataDir $File

    Write-Host "Indiriliyor: $File"
    Invoke-WebRequest -Uri "$BaseUrl/$File" -OutFile $Target

    $ActualHash = (Get-FileHash -Path $Target -Algorithm SHA256).Hash.ToLowerInvariant()

    if ($ActualHash -ne $ExpectedHash) {
        Remove-Item -Path $Target -Force -ErrorAction SilentlyContinue
        throw "SHA-256 dogrulamasi basarisiz: $File"
    }

    Write-Host "Dogrulandi: $File"
}

Write-Host 'Tessdata modelleri hazir.'
