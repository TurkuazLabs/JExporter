# JExporter Resources Applied v2.1.1

Eski JXporter kaynak kullanimlari incelendi.

## Eski projede kullanim

- `SplashScreen.java` icinde `/logo.png` kullaniliyordu.
- `App.java` splash ekranini acip, bitince MainUI baslatiyordu.
- `jxporter_icon.ico` ve `icon/app.ico` kaynaklarda vardi fakat eski kaynak kodda aktif kullanim referansi bulunmadi.
- `log4j2.xml` eski log sistemi icindi ve yeni projeye uygulanmadi.

## Yeni projeye uygulananlar

- `SplashScreen.java` yeni projeye eklendi.
- `JExporter.java` artik once SplashScreen aciyor, sonra MainUI baslatiyor.
- `MainUI.java` header bolumunde `/logo.png` gosteriyor.
- `MainUI.java` pencere ikonunu logo uzerinden ayarliyor.
- `AppConstants.java` kaynak yollarini merkezi sabit olarak tutuyor.

## Korunan dosyalar

- `JExporter/src/main/resources/logo.png`
- `JExporter/src/main/resources/jxporter_icon.ico`
- `JExporter/src/main/resources/icon/app.ico`
- Kopya logo dosyalari kaynak olarak duruyor.
