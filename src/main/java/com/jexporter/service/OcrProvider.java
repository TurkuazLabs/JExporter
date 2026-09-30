/*
# 📄 Dosya Yolu: src/main/java/com/jexporter/service/OcrProvider.java
# 📌 Amac: Klasik OCR implementationini Community pipeline'dan ayiran public extension contractini tanimlamak
# 📌 Modul - Java
# Version: 2.5.1
# Aciklama: Harici OCR implementationlarinin Tess4J/PDFBox bagimliligini public Extension API'ye sizdirmadan sayfa metni saglamasini tanimlar
# Bagimli Oldugu Katman: Service | Model
*/
package com.jexporter.service;

import com.jexporter.model.ProcessRequest;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface OcrProvider {

    List<String> extractPageTexts(
            ProcessRequest request,
            String tessdataPath,
            AtomicBoolean shouldStop,
            ProcessingProgress progress) throws IOException;
}
