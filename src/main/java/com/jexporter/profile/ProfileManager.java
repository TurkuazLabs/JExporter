/*
# Dosya Yolu: src/main/java/com/jexporter/profile/ProfileManager.java
# Amac: JSON kullanmadan kod icinden opsiyonel sutun tanimi saglar
# Modul - FileType
# Version: 2.4.4
# Aciklama: Varsayilan cikti orijinal PDF metin duzenini korur; fatura sutunlari ayri mod icin saklanir
# Bagimli Oldugu Katman: Service
*/
package com.jexporter.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileManager {

    private final List<FieldDefinition> fields;

    public ProfileManager() {
        this.fields = new ArrayList<>();
        loadOriginalLayoutProfile();
    }

    private void loadOriginalLayoutProfile() {
        fields.clear();
    }

    public void loadInvoiceDefaultFields() {
        fields.clear();

        fields.add(new FieldDefinition("sira", 0, 5));
        fields.add(new FieldDefinition("malzeme_kodu", 5, 25));
        fields.add(new FieldDefinition("aciklama", 25, 95));
        fields.add(new FieldDefinition("miktar", 95, 108));
        fields.add(new FieldDefinition("birim_fiyat", 108, 126));
        fields.add(new FieldDefinition("iskonto", 126, 142));
        fields.add(new FieldDefinition("kdv", 142, 154));
        fields.add(new FieldDefinition("tutar", 154, 190));
    }

    public List<FieldDefinition> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public boolean isOriginalLayoutMode() {
        return fields.isEmpty();
    }

    public void addField(FieldDefinition field) {
        fields.add(field);
    }

    public void clearFields() {
        fields.clear();
    }
}
