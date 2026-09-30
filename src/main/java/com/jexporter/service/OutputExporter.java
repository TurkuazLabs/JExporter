/*
# 📄 Dosya Yolu: src/main/java/com/jexporter/service/OutputExporter.java
# 📌 Amac: Islenmis satir verisinin cikti formatina yazilmasini public extension contractina ayirmak
# 📌 Modul - Java
# Version: 2.5.0
# Aciklama: Community XLSX/CSV adapteri veya harici exporter implementationlari icin ortak Service contracti
# Bagimli Oldugu Katman: Service | Model
*/
package com.jexporter.service;

import com.jexporter.profile.FieldDefinition;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface OutputExporter {

    String resolveExtension(String requestedFormat);

    void export(
            List<Map<String, String>> rows,
            List<FieldDefinition> fields,
            File outputFile,
            String requestedFormat) throws IOException;
}
