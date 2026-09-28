/*
# Dosya Yolu: src/main/java/com/jexporter/profile/FieldDefinition.java
# Amac: Cikti tablosundaki dinamik sutun tanimini tutar
# Modul - FileType
# Version: 2.4.4
# Aciklama: Alan adi ve karakter bazli baslangic-bitis araliklarini tanimlar
# Bagimli Oldugu Katman: Service
*/
package com.jexporter.profile;

public class FieldDefinition {

    private final String name;
    private final int startCol;
    private final int endCol;

    public FieldDefinition(String name, int startCol, int endCol) {
        this.name = name;
        this.startCol = startCol;
        this.endCol = endCol;
    }

    public String getName() {
        return name;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getEndCol() {
        return endCol;
    }
}
