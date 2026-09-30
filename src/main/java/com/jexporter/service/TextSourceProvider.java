/*
# 📄 Dosya Yolu: src/main/java/com/jexporter/service/TextSourceProvider.java
# 📌 Amac: PDF/OCR metin kaynagi secimini ProcessingManager'dan ayiran public extension contractini tanimlamak
# 📌 Modul - Java
# Version: 2.5.0
# Aciklama: Community veya harici provider'in ProcessRequest icin sayfa bazli metin bloklari saglamasini tanimlar
# Bagimli Oldugu Katman: Service | Model
*/
package com.jexporter.service;

import com.jexporter.model.ProcessRequest;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface TextSourceProvider {

    List<String> load(
            ProcessRequest request,
            String tessdataPath,
            AtomicBoolean shouldStop,
            ProcessingProgress progress) throws IOException;
}
