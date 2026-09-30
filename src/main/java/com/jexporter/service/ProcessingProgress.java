/*
# 📄 Dosya Yolu: src/main/java/com/jexporter/service/ProcessingProgress.java
# 📌 Amac: Processing pipeline ilerleme bildirimini UI toolkit bagimliligindan ayirmak
# 📌 Modul - Java
# Version: 2.5.0
# Aciklama: Service ve Tool katmanlarinin Swing JProgressBar yerine basit progress contract kullanmasini saglar
# Bagimli Oldugu Katman: Service
*/
package com.jexporter.service;

@FunctionalInterface
public interface ProcessingProgress {

    void update(int value);

    static ProcessingProgress noop() {
        return value -> {
        };
    }
}
