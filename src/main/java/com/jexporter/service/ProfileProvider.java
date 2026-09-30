/*
# 📄 Dosya Yolu: src/main/java/com/jexporter/service/ProfileProvider.java
# 📌 Amac: Processing pipeline sutun/profile tanimlarini public extension contractina ayirmak
# 📌 Modul - Java
# Version: 2.5.0
# Aciklama: Community ProfileManager veya harici profile provider'in field listesini sunmasini tanimlar
# Bagimli Oldugu Katman: Service | Model
*/
package com.jexporter.service;

import com.jexporter.profile.FieldDefinition;
import java.util.List;

public interface ProfileProvider {

    List<FieldDefinition> getFields();
}
