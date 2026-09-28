/*
# Dosya Yolu: src/main/java/com/jexporter/model/ProcessRequest.java
# Amac: Kullanici tarafindan baslatilan islem ayarlarini tasir
# Modul - FileType
# Version: 2.4.4
# Aciklama: PDF yolu, DPI, dil ve cikti formati bilgisini tutar
# Bagimli Oldugu Katman: Service
*/
package com.jexporter.model;

public class ProcessRequest {

    private String pdfPath;
    private int dpi;
    private String language;
    private String outputFormat;

    public ProcessRequest(String pdfPath, int dpi, String language, String outputFormat) {
        this.pdfPath = pdfPath;
        this.dpi = dpi;
        this.language = language;
        this.outputFormat = outputFormat;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public int getDpi() {
        return dpi;
    }

    public String getLanguage() {
        return language;
    }

    public String getOutputFormat() {
        return outputFormat;
    }
}
