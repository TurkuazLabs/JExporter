# Dosya Yolu: README_RUNFIX_v2_2_3.md
# Amac: NetBeans run hatasi icin uygulanan duzeltmeyi aciklar
# Modul - FileType
# Version: 2.4.4
# Aciklama: exec:exec yerine exec:java kullanan nbactions.xml eklendigini belirtir
# Bagimli Oldugu Katman: Tool

# JExporter Run Fix v2.2.3

Bu surumde `nbactions.xml` eklendi.

Cozulen hata:

```text
Usage: java [options] <mainclass> [args...]
```

Sebep:

NetBeans `exec:exec` ile `java.exe` calistiriyor ama classpath ve main class argumanlarini Java komutuna gecirmiyordu.

Cozum:

`nbactions.xml` icinde `exec:java` hedefi kullanildi.

Ana sinif:

```text
com.mycompany.jexporter.JExporter
```
