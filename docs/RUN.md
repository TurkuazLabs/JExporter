<!--
# Dosya Yolu: docs/RUN.md
# Amac: JExporter projesini NetBeans veya komut satiri ile calistirma adimlarini aciklar
# Modul - FileType
# Version: 2.4.4
# Aciklama: exec:java kullanimi ve NetBeans run fix bilgisini tutar
# Bagimli Oldugu Katman: Tool
-->

# Calistirma

## NetBeans

1. `JExporter` projesini ac.
2. Project Properties > Run ekranina gir.
3. Main Class alanini su yap:

```text
com.mycompany.jexporter.JExporter
```

4. Run Project kullan.

## Dogru Maven Komutu

```bat
cd /d C:\Users\Burak01\Documents\NetBeansProjects\JExporter
mvn clean compile org.codehaus.mojo:exec-maven-plugin:3.5.1:java -Dexec.mainClass=com.mycompany.jexporter.JExporter
```

## Kullanilmamasi Gereken Komut

`exec:exec` hedefi ve `-Dexec.executable=java.exe` beraber kullanildiginda NetBeans bazen classpath/main args vermeden Java calistirir. Bu durumda Java usage ekrani gelir.

Yanlis ornek:

```text
org.codehaus.mojo:exec-maven-plugin:3.5.1:exec
-Dexec.executable=...java.exe
```

Dogru hedef:

```text
org.codehaus.mojo:exec-maven-plugin:3.5.1:java
```

## Tessdata

Su dosya bulunmali:

```text
JExporter/tessdata/tur.traineddata
```
