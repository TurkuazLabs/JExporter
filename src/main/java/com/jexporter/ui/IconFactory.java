/*
# Dosya Yolu: src/main/java/com/jexporter/ui/IconFactory.java
# Amac: Harici ikon dosyasi kullanmadan Swing butonlari icin vektor ikonlar uretir
# Modul - FileType
# Version: 2.4.4
# Aciklama: PDF sec, baslat, durdur, klasor ve kontrol ikonlarini Java2D ile cizer
# Bagimli Oldugu Katman: View
*/
package com.jexporter.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Icon;

public final class IconFactory {

    private static final int SIZE = 16;

    public enum Type {
        FILE,
        FOLDER,
        PLAY,
        STOP,
        CHECK,
        DOWNLOAD
    }

    private IconFactory() {
    }

    public static Icon create(Type type, Color color) {
        return new VectorIcon(type, color, SIZE, SIZE);
    }

    private static class VectorIcon implements Icon {

        private final Type type;
        private final Color color;
        private final int width;
        private final int height;

        VectorIcon(Type type, Color color, int width, int height) {
            this.type = type;
            this.color = color;
            this.width = width;
            this.height = height;
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.translate(x, y);

            switch (type) {
                case FILE:
                    paintFile(g);
                    break;
                case FOLDER:
                    paintFolder(g);
                    break;
                case PLAY:
                    paintPlay(g);
                    break;
                case STOP:
                    paintStop(g);
                    break;
                case CHECK:
                    paintCheck(g);
                    break;
                case DOWNLOAD:
                    paintDownload(g);
                    break;
                default:
                    paintCheck(g);
                    break;
            }

            g.dispose();
        }

        private void paintFile(Graphics2D g) {
            g.drawRoundRect(3, 2, 10, 12, 2, 2);
            g.drawLine(9, 2, 13, 6);
            g.drawLine(9, 2, 9, 6);
            g.drawLine(9, 6, 13, 6);
            g.drawLine(5, 9, 11, 9);
            g.drawLine(5, 12, 10, 12);
        }

        private void paintFolder(Graphics2D g) {
            g.drawRoundRect(2, 5, 12, 9, 2, 2);
            g.drawLine(2, 7, 14, 7);
            g.drawLine(3, 5, 6, 2);
            g.drawLine(6, 2, 9, 5);
        }

        private void paintPlay(Graphics2D g) {
            int[] xs = {5, 5, 12};
            int[] ys = {3, 13, 8};
            g.fillPolygon(xs, ys, 3);
        }

        private void paintStop(Graphics2D g) {
            g.fillRoundRect(4, 4, 8, 8, 2, 2);
        }

        private void paintCheck(Graphics2D g) {
            g.drawLine(3, 8, 7, 12);
            g.drawLine(7, 12, 13, 4);
        }

        private void paintDownload(Graphics2D g) {
            g.drawLine(8, 2, 8, 10);
            g.drawLine(5, 7, 8, 10);
            g.drawLine(11, 7, 8, 10);
            g.drawLine(3, 13, 13, 13);
        }
    }
}
