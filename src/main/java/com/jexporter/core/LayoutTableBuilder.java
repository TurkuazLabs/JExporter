/*
# Dosya Yolu: src/main/java/com/jexporter/core/LayoutTableBuilder.java
# Amac: Konum bilgili OCR kelimelerini satir ve sutun benzeri listeye gruplar
# Modul - FileType
# Version: 2.4.4
# Aciklama: Eski LayoutTableBuilder sinifi getter tabanli modern OcrWord ile uyarlandi
# Bagimli Oldugu Katman: Service
*/
package com.jexporter.core;

import com.jexporter.model.OcrWord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class LayoutTableBuilder {

    private static final int DEFAULT_Y_THRESHOLD = 10;

    public List<List<OcrWord>> groupWordsByRows(List<OcrWord> words) {
        TreeMap<Integer, List<OcrWord>> lines = new TreeMap<>();

        for (OcrWord word : words) {
            int y = word.centerY();
            int lineKey = findCloseKey(lines.keySet(), y, DEFAULT_Y_THRESHOLD);
            lines.computeIfAbsent(lineKey, key -> new ArrayList<>()).add(word);
        }

        List<List<OcrWord>> rows = new ArrayList<>();

        for (List<OcrWord> line : lines.values()) {
            line.sort(Comparator.comparingInt(OcrWord::centerX));
            rows.add(line);
        }

        return rows;
    }

    public List<String> toTextLines(List<List<OcrWord>> rows) {
        List<String> lines = new ArrayList<>();

        for (List<OcrWord> row : rows) {
            StringBuilder builder = new StringBuilder();

            for (OcrWord word : row) {
                if (builder.length() > 0) {
                    builder.append(' ');
                }

                builder.append(word.getText());
            }

            lines.add(builder.toString().trim());
        }

        return lines;
    }

    private int findCloseKey(Set<Integer> keys, int target, int threshold) {
        return keys.stream()
                .filter(key -> Math.abs(key - target) <= threshold)
                .findFirst()
                .orElse(target);
    }
}
