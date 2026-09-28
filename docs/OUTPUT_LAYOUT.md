<!--
# Dosya Yolu: docs/OUTPUT_LAYOUT.md
# Amac: JExporter varsayilan Excel cikti duzenini aciklar
# Modul - FileType
# Version: 2.4.4
# Aciklama: Eski sabit fatura kolonlari yerine orijinal PDF satir duzeninin neden kullanildigini dokumante eder
# Bagimli Oldugu Katman: Tool
-->

# Cikti Duzeni

## Varsayilan Mod

JExporter artik varsayilan olarak PDF icindeki metin satirlarini Excel'e teknik baslik yazmadan aktarir.

Bu nedenle su eski kolonlar varsayilan ciktiya yazilmaz:

```text
page
line
raw_text
sira
malzeme_kodu
aciklama
miktar
birim_fiyat
iskonto
kdv
tutar
```

## Orijinal PDF Duzeni

Tek sayfali belgelerde ilk Excel satiri PDF'in ilk metin satiridir.

Cok sayfali belgelerde sayfa ayrimlari icin `Sayfa X` satiri eklenir.

## v2.4.4 Duzen Notu

Orijinal PDF duzeni modunda satir ici bosluklar artik korunur. Layout OCR sonucunda uretilen satirlar da ayni sayfaya aitse tek sayfa metni olarak gruplenir. Boylece her OCR satirinin Excel tarafinda ayri sayfa gibi gorunmesi engellenir.

## Fatura Kolonlari

Fatura kolonlari tamamen silinmedi. `ProfileManager.loadInvoiceDefaultFields()` icinde saklandi. Daha sonra ayri bir `Fatura Modu` olarak UI'ya baglanabilir.
