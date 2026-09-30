<!--
# Dosya Yolu: docs/CHANGELOG.md
# Amac: JExporter surum gecmisini ve degisiklikleri takip eder
# Modul - FileType
# Version: 2.5.0
# Aciklama: Patch, minor ve major surum notlarini tutar
# Bagimli Oldugu Katman: Tool
-->

# Changelog

## 2.5.0

- `TextSourceProvider`, `OutputExporter`, `ProfileProvider` ve `ProcessingProgress` public extension contractlari eklendi.
- `ProcessingManager` concrete OCR/export siniflarini dogrudan olusturmak yerine dependency injection kullanacak sekilde refactor edildi.
- Varsayilan PDF text -> Layout OCR -> klasik OCR davranisi `CommunityTextSourceProvider` arkasina alindi.
- XLSX/CSV davranisi `CommunityOutputExporter` arkasina alindi.
- OCR progress akisi Swing bagimliligindan ayrildi; eski JProgressBar overloadlari geriye uyumlu tutuldu.
- Extension boundary validator ve JUnit regression testleri CI'a eklendi.
- Community/Pro bagimlilik yonu public Service contractlari uzerinden netlestirildi.


## 2.4.4

- Progress akisi metin katmani, gruplama ve cikti yazma adimlari icin tamamlandi.
- Layout OCR sonucunda her satirin ayri sayfa gibi gorunmesine neden olan sayfa ayrimi hatasi duzeltildi.
- Orijinal PDF duzeni modunda satir ici bosluklar korunacak sekilde `TextGrouper` guncellendi.
- OCR ve Layout OCR progress degerleri genel islem akisina uygun araliga alindi.
- `ThreadDeath` derleme uyarisi referans kullanmadan temizlendi.


## 2.4.3

- Baslat ve durdur tek butonda birlestirildi.
- Buton calisma durumuna gore renk, ikon ve metin degistirir hale getirildi.
- UI okunabilirligi artirildi; fontlar buyutuldu ve koyulastirildi.
- Konsol fontu buyutuldu.


## 2.4.2

- UI kontrasti artirildi.
- Form alanlari yukariya sabitlendi ve bosluk hissi azaltildi.
- Butonlar daha buyuk ikonlu aksiyon grid yapisina alindi.
- Konsol ve form yazilari kalinlastirildi.


## 2.4.1

- `IconFactory` eklendi.
- Butonlar ikonlu hale getirildi.
- Yazilar daha koyu ve okunabilir hale getirildi.
- Stop butonu kirmizi aksiyon butonu olarak guncellendi.
- Form alanlari daha belirgin hale getirildi.


## 2.4.0

- Varsayilan Excel cikti modu orijinal PDF satir duzenine alindi.
- Eski sabit fatura kolonlari varsayilan cikti basligindan kaldirildi.
- `ProfileManager` default olarak bos alan listesi kullanacak sekilde guncellendi.
- `ExcelExporter` orijinal layout ve tablo modu ayrimini destekleyecek sekilde guncellendi.
- `docs/OUTPUT_LAYOUT.md` eklendi.


## 2.3.2

- DPI secenekleri 1200 DPI seviyesine kadar genisletildi.
- Auto DPI kucuk dosyalarda 600 DPI kullanacak sekilde guncellendi.
- 600 DPI uzeri manual secimlerde kullanici onayi eklendi.


## 2.3.1

- DPI manual secenekleri 300 DPI ile baslayacak sekilde sadeleştirildi.
- Auto DPI artik minimum 300 DPI secer.
- Auto DPI kalite degerleri 450/400/350/300 olarak guncellendi.


## 2.3.0

- Global hata yakalayici `AppExceptionHandler` eklendi.
- UI DPI secenegi genisletildi.
- Auto DPI modu eklendi.
- OCR dili `auto` modu eklendi.
- Eksik tessdata dosyasi icin indirme onerisi eklendi.
- Varsayilan cikti klasoru Desktop yapildi.
- `docs/AUTO_SETTINGS.md` ve `docs/ERROR_HANDLING.md` eklendi.


## 2.2.7

- `AppLogger` eklendi.
- Loglar hem UI konsoluna hem `logs/jexporter.log` dosyasina yazilacak sekilde hazirlandi.
- Log4j kullanmadan sade dosya loglama altyapisi kuruldu.
- `docs/LOGGING.md` eklendi.


## 2.2.6

- Apache POI bagimliligi kaldirildi.
- XLSX cikti saf Java OpenXML yazici ile uretilmeye baslandi.
- Log4j2 StatusLogger uyarisinin ana kaynagi ortadan kaldirildi.
- Cikti yazma asamasindaki takilma riskleri azaltildi.


## 2.2.5

- XLSX/CSV cikti yazma islemi gecici dosya + tasima modeliyle guvenli hale getirildi.
- Cikti dosyasi varlik ve boyut dogrulamasi eklendi.
- Apache POI Log4j2 uyari mesaji icin dokumantasyon eklendi.
- `docs/OUTPUT_TROUBLESHOOT.md` eklendi.


## 2.2.4

- Kok dizindeki README dosyalari `docs/` klasorune tasindi.
- `docs/INDEX.md` eklendi.
- Dokumantasyon yapisi tek klasorde toplandi.


## 2.2.2

- `docs/` klasoru eklendi.
- Proje dokumantasyon dosyalari olusturuldu.
- Surum notlari temizlendi.

## 2.2.1

- Gereksiz dosyalar temizlendi.
- Eski `JXporter` klasoru temiz paketten cikarildi.
- Guncel `JExporter` tek proje olarak paketlendi.

## 2.2.0

- Eski projeden layout OCR altyapisi modern projeye tasindi.
- `LayoutOcrProcessor` eklendi.
- `PdfTextPositionExtractor` eklendi.
- Bolgesel OCR altyapisi eklendi.
- ProcessingManager akis sirasi guncellendi.

## 2.1.1

- Logo ve splash kaynaklari yeni projeye uygulandi.
- `SplashScreen` eklendi.
- `MainUI` logo kullanacak sekilde guncellendi.

## 2.1.0

- Modern kart tabanli Swing arayuz eklendi.
- `AppConstants` eklendi.
- `ConfigManager` sade hale getirildi.

## 2.0.0

- Temiz `JExporter` restore cekirdegi olusturuldu.
- Log4j kaldirildi.
- Profile JSON kaldirildi.
