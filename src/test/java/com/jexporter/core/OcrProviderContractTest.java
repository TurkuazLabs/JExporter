/*
# 📄 Dosya Yolu: src/test/java/com/jexporter/core/OcrProviderContractTest.java
# 📌 Amac: Community OCR implementationinin public OcrProvider contractini uyguladigini regression olarak dogrulamak
# 📌 Modul - Java
# Version: 2.5.1
# Aciklama: OCRProcessor assignability ve CommunityTextSourceProvider constructor dependency tipini kilitler
# Bagimli Oldugu Katman: Tool | Service | Model
*/
package com.jexporter.core;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jexporter.service.CommunityTextSourceProvider;
import com.jexporter.service.OcrProvider;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

final class OcrProviderContractTest {

    @Test
    void ocrProcessorImplementsPublicProvider() {
        assertTrue(OcrProvider.class.isAssignableFrom(OCRProcessor.class));
    }

    @Test
    void communityTextSourceAcceptsPublicOcrProvider() {
        boolean found = Arrays.stream(CommunityTextSourceProvider.class.getConstructors())
                .map(Constructor::getParameterTypes)
                .anyMatch(parameters ->
                        parameters.length == 3
                                && parameters[0] == PdfTextExtractor.class
                                && parameters[1] == LayoutOcrProcessor.class
                                && parameters[2] == OcrProvider.class);

        assertTrue(found);
    }
}
