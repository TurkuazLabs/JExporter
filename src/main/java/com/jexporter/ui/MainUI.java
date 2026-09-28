/*
# Dosya Yolu: src/main/java/com/jexporter/ui/MainUI.java
# Amac: Kullanici arayuzu uzerinden PDF secimi ve islem baslatma/durdurma akisini yonetir
# Modul - FileType
# Version: 2.4.4
# Aciklama: Tek baslat/durdur butonlu, ikonlu ve okunabilirligi artirilmis modern Swing arayuzudur
# Bagimli Oldugu Katman: Controller
*/
package com.jexporter.ui;

import com.jexporter.config.AppConstants;
import com.jexporter.config.ConfigManager;
import com.jexporter.core.ProcessingManager;
import com.jexporter.logging.AppLogger;
import com.jexporter.model.ProcessRequest;
import com.jexporter.service.DpiResolver;
import com.jexporter.service.LanguageResolver;
import com.jexporter.util.ConsoleOutputCapturer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainUI extends JFrame {

    private static final String FILE_EXTENSION_PDF = "pdf";
    private static final String[] DPI_OPTIONS = new String[] {
            AppConstants.DPI_AUTO,
            "300",
            "350",
            "400",
            "450",
            "500",
            "600",
            "700",
            "800",
            "900",
            "1000",
            "1200"
    };

    private final ProcessingManager processingManager;
    private final ConfigManager configManager;
    private final DpiResolver dpiResolver;
    private final LanguageResolver languageResolver;

    private JTextField selectedFileField;
    private JComboBox<String> outputFormatComboBox;
    private JComboBox<String> languageComboBox;
    private JComboBox<String> dpiComboBox;
    private JTextField outputDirField;
    private JTextArea consoleTextArea;
    private JProgressBar progressBar;
    private JButton actionButton;
    private JLabel statusLabel;
    private JLabel autoInfoLabel;
    private ConsoleOutputCapturer consoleCapturer;
    private AtomicBoolean shouldStop;
    private volatile boolean running;

    public MainUI() {
        this.processingManager = new ProcessingManager();
        this.configManager = ConfigManager.getInstance();
        this.dpiResolver = new DpiResolver();
        this.languageResolver = new LanguageResolver();
        this.running = false;

        setTitle(AppConstants.APP_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(AppConstants.WINDOW_WIDTH, AppConstants.WINDOW_HEIGHT);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        applyWindowIcon();

        initComponents();
    }

    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout(18, 18));
        rootPanel.setBackground(AppConstants.COLOR_BACKGROUND);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        rootPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        rootPanel.add(createContentPanel(), BorderLayout.CENTER);
        rootPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        getContentPane().add(rootPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = createCardPanel(new BorderLayout(16, 4));

        JLabel logo = createLogoLabel(96, 60);

        JLabel title = new JLabel(AppConstants.APP_NAME);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(AppConstants.COLOR_PRIMARY_DARK);

        JLabel subtitle = new JLabel("PDF metin katmani + Layout OCR + klasik OCR ile duzenli Excel/CSV cikti");
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtitle.setForeground(AppConstants.COLOR_TEXT_MUTED);

        JLabel version = new JLabel("v" + AppConstants.APP_VERSION);
        version.setFont(new Font("Segoe UI", Font.BOLD, 14));
        version.setForeground(AppConstants.COLOR_PRIMARY);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(title, BorderLayout.NORTH);
        textPanel.add(subtitle, BorderLayout.SOUTH);

        panel.add(logo, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(version, BorderLayout.EAST);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setOpaque(false);

        panel.add(createFormCard(), BorderLayout.WEST);
        panel.add(createConsoleCard(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormCard() {
        JPanel card = createCardPanel(new BorderLayout(0, 18));
        card.setPreferredSize(new Dimension(460, 510));

        JLabel sectionTitle = createSectionTitle("Islem Ayarlari");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints gbc = baseGbc();

        addRow(form, gbc, 0, "PDF Dosyasi", createFileInputPanel());
        addRow(form, gbc, 1, "DPI", createDpiInput());
        addRow(form, gbc, 2, "OCR Dili", createLanguageInput());
        addRow(form, gbc, 3, "Cikti Formati", createFormatInput());
        addRow(form, gbc, 4, "Cikti Klasoru", createOutputInputPanel());

        JPanel formHolder = new JPanel(new BorderLayout());
        formHolder.setOpaque(false);
        formHolder.add(form, BorderLayout.NORTH);

        autoInfoLabel = new JLabel("Auto DPI dengeli secim yapar. Manuel DPI 1200 kadar aciktir.");
        autoInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        autoInfoLabel.setForeground(AppConstants.COLOR_TEXT_MUTED);
        autoInfoLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JButton tessdataButton = createSecondaryButton("Tessdata Kontrol", IconFactory.Type.CHECK);
        tessdataButton.addActionListener(event -> checkTessdataWithDialog());

        actionButton = createRunButton();
        actionButton.addActionListener(this::onActionButton);

        JPanel actionGrid = new JPanel(new GridLayout(2, 1, 0, 10));
        actionGrid.setOpaque(false);
        actionGrid.add(tessdataButton);
        actionGrid.add(actionButton);

        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.setOpaque(false);
        bottom.add(autoInfoLabel, BorderLayout.NORTH);
        bottom.add(actionGrid, BorderLayout.SOUTH);

        card.add(sectionTitle, BorderLayout.NORTH);
        card.add(formHolder, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        new DropTarget(card, new FileDropTargetListener());

        return card;
    }

    private JPanel createConsoleCard() {
        JPanel card = createCardPanel(new BorderLayout(0, 12));

        JLabel sectionTitle = createSectionTitle("Canli Islem Konsolu");

        consoleTextArea = new JTextArea();
        consoleTextArea.setEditable(false);
        consoleTextArea.setFont(new Font("Consolas", Font.BOLD, 14));
        consoleTextArea.setBackground(AppConstants.COLOR_CONSOLE_BG);
        consoleTextArea.setForeground(AppConstants.COLOR_CONSOLE_FG);
        consoleTextArea.setMargin(new Insets(12, 12, 12, 12));
        consoleTextArea.setLineWrap(true);
        consoleTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(consoleTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppConstants.COLOR_BORDER));
        scrollPane.setPreferredSize(new Dimension(600, 510));

        card.add(sectionTitle, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        consoleCapturer = new ConsoleOutputCapturer(consoleTextArea);
        consoleCapturer.start();

        return card;
    }

    private JPanel createFooterPanel() {
        JPanel panel = createCardPanel(new BorderLayout(12, 0));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        progressBar.setString("0 %");
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));

        statusLabel = new JLabel("Hazir");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setForeground(AppConstants.COLOR_TEXT_MUTED);

        panel.add(progressBar, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createFileInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);

        selectedFileField = new JTextField();
        selectedFileField.setEditable(false);
        styleTextField(selectedFileField);
        selectedFileField.setToolTipText("PDF dosyasini secin veya buraya surukleyin");
        new DropTarget(selectedFileField, new FileDropTargetListener());

        JButton browseButton = createSecondaryButton("PDF Sec", IconFactory.Type.FILE);
        browseButton.addActionListener(this::onSelectFile);

        panel.add(selectedFileField, BorderLayout.CENTER);
        panel.add(browseButton, BorderLayout.EAST);

        return panel;
    }

    private JComboBox<String> createDpiInput() {
        dpiComboBox = new JComboBox<>(DPI_OPTIONS);
        styleComboBox(dpiComboBox);
        dpiComboBox.setSelectedItem(AppConstants.DPI_AUTO);
        return dpiComboBox;
    }

    private JComboBox<String> createLanguageInput() {
        languageComboBox = new JComboBox<>(configManager.getAvailableLanguages());
        styleComboBox(languageComboBox);
        languageComboBox.setSelectedItem(configManager.getDefaultLanguage());
        return languageComboBox;
    }

    private JComboBox<String> createFormatInput() {
        outputFormatComboBox = new JComboBox<>(new String[] {
                AppConstants.FORMAT_XLSX,
                AppConstants.FORMAT_CSV
        });
        styleComboBox(outputFormatComboBox);
        outputFormatComboBox.setSelectedItem(configManager.getDefaultOutputFormat());
        return outputFormatComboBox;
    }

    private JPanel createOutputInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);

        outputDirField = new JTextField(configManager.getOutputDirPath());
        styleTextField(outputDirField);

        JButton browseButton = createSecondaryButton("Klasor Sec", IconFactory.Type.FOLDER);
        browseButton.addActionListener(event -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setDialogTitle("Cikti klasoru sec");

            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                outputDirField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        panel.add(outputDirField, BorderLayout.CENTER);
        panel.add(browseButton, BorderLayout.EAST);

        return panel;
    }

    private void onActionButton(ActionEvent event) {
        if (running) {
            if (shouldStop != null) {
                shouldStop.set(true);
                AppLogger.stopped("Kullanici islemi durdurdu.");
                setStatus("Durdurma istegi gonderildi.");
                updateActionButtonStopping();
            }
            return;
        }

        onStartProcess();
    }

    private void onSelectFile(ActionEvent event) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("PDF dosyasi sec");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Dosyalari", FILE_EXTENSION_PDF));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            setSelectedPdf(chooser.getSelectedFile());
        }
    }

    private void onStartProcess() {
        String filePath = selectedFileField.getText();

        if (filePath == null || filePath.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lutfen once PDF dosyasi secin.", "Uyari", JOptionPane.WARNING_MESSAGE);
            return;
        }

        File pdfFile = new File(filePath);
        String outputDir = outputDirField.getText();
        int resolvedDpi = dpiResolver.resolve((String) dpiComboBox.getSelectedItem(), pdfFile);

        if (!confirmHighDpi(resolvedDpi)) {
            return;
        }

        File tessdataDir = new File(configManager.getDefaultTessdataPath());
        String resolvedLanguage = languageResolver.resolve((String) languageComboBox.getSelectedItem(), tessdataDir);

        if (!ensureLanguageAvailable(resolvedLanguage, tessdataDir)) {
            return;
        }

        String outputFormat = (String) outputFormatComboBox.getSelectedItem();

        shouldStop = new AtomicBoolean(false);
        setRunningState(true);
        setStatus("Islem basladi.");

        new Thread(() -> runProcessing(filePath, outputDir, resolvedDpi, resolvedLanguage, outputFormat), "jexporter-processing-thread").start();
    }

    private void runProcessing(String filePath, String outputDir, int dpi, String language, String outputFormat) {
        try {
            AppLogger.started("Kullanici islemi baslatti.");
            AppLogger.info("PDF: " + filePath);
            AppLogger.info("DPI: " + dpi);
            AppLogger.info("Dil: " + language);
            AppLogger.info("Format: " + outputFormat);
            AppLogger.info("Cikti klasoru: " + outputDir);

            ProcessRequest request = new ProcessRequest(filePath, dpi, language, outputFormat);
            File outputFile = processingManager.process(
                    request,
                    configManager.getDefaultTessdataPath(),
                    outputDir,
                    progressBar,
                    shouldStop
            );

            if (shouldStop.get()) {
                AppLogger.stopped("Islem kullanici tarafindan iptal edildi.");
                setStatus("Islem durduruldu.");
                return;
            }

            if (outputFile != null) {
                AppLogger.success("Islem tamamlandi: " + outputFile.getAbsolutePath());
                setStatus("Islem tamamlandi.");

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Islem tamamlandi.\n" + outputFile.getAbsolutePath());
                    openFileParent(outputFile);
                });
            }
        } catch (Throwable ex) {
            AppLogger.error("Islem hatasi: " + ex.getMessage(), ex);
            setStatus("Hata olustu.");

            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "Islem sirasinda hata olustu:\n" + ex.getMessage()
                                    + "\n\nDetay: logs/jexporter.log",
                            "Hata", JOptionPane.ERROR_MESSAGE)
            );
        } finally {
            setRunningState(false);
        }
    }

    private boolean confirmHighDpi(int dpi) {
        if (dpi <= 600) {
            return true;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Yuksek DPI secildi: " + dpi + "\n\n"
                        + "Bu islem daha fazla RAM kullanir ve daha uzun surebilir.\n"
                        + "Devam etmek istiyor musun?",
                "Yuksek DPI Uyarisi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        return result == JOptionPane.YES_OPTION;
    }

    private boolean ensureLanguageAvailable(String language, File tessdataDir) {
        if (languageResolver.isLanguageAvailable(language, tessdataDir)) {
            return true;
        }

        AppLogger.warning("OCR dil dosyasi bulunamadi: " + language + ".traineddata");

        int result = JOptionPane.showConfirmDialog(
                this,
                "OCR dil dosyasi bulunamadi:\n"
                        + new File(tessdataDir, language + ".traineddata").getAbsolutePath()
                        + "\n\nTessdata indirme sayfasini acmak ister misin?",
                "Tessdata Eksik",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            openTessdataDownloadPage();
        }

        setStatus("OCR dil dosyasi eksik.");
        return false;
    }

    private void checkTessdataWithDialog() {
        File tessdataDir = new File(configManager.getDefaultTessdataPath());

        if (!languageResolver.hasAnyLanguage(tessdataDir)) {
            JOptionPane.showMessageDialog(this,
                    "tessdata klasorunde hic .traineddata dosyasi yok.\n"
                            + tessdataDir.getAbsolutePath(),
                    "Tessdata Eksik",
                    JOptionPane.WARNING_MESSAGE);
            openTessdataDownloadPage();
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Bulunan OCR dilleri:\n");

        for (String language : languageResolver.listLanguages(tessdataDir)) {
            builder.append("- ").append(language).append("\n");
        }

        JOptionPane.showMessageDialog(this, builder.toString(), "Tessdata Kontrol", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openTessdataDownloadPage() {
        try {
            Desktop.getDesktop().browse(new URI(AppConstants.TESSDATA_DOWNLOAD_URL));
        } catch (Exception ex) {
            AppLogger.error("Tessdata indirme sayfasi acilamadi: " + ex.getMessage(), ex);
        }
    }

    private void setSelectedPdf(File file) {
        if (file == null) {
            return;
        }

        if (!file.getName().toLowerCase().endsWith("." + FILE_EXTENSION_PDF)) {
            JOptionPane.showMessageDialog(this, "Lutfen PDF dosyasi secin.", "Uyari", JOptionPane.WARNING_MESSAGE);
            return;
        }

        selectedFileField.setText(file.getAbsolutePath());
        updateAutoInfo(file);
        AppLogger.success("PDF secildi: " + file.getAbsolutePath());
        setStatus("PDF secildi.");
    }

    private void updateAutoInfo(File file) {
        int autoDpi = dpiResolver.resolve(AppConstants.DPI_AUTO, file);
        File tessdataDir = new File(configManager.getDefaultTessdataPath());
        String autoLanguage = languageResolver.resolve(AppConstants.LANGUAGE_AUTO, tessdataDir);

        autoInfoLabel.setText("Auto secim: DPI " + autoDpi + " / OCR dili " + autoLanguage + " / Cikti: Desktop");
    }

    private void setRunningState(boolean isRunning) {
        running = isRunning;

        SwingUtilities.invokeLater(() -> {
            updateActionButtonState(isRunning);
            progressBar.setIndeterminate(false);

            if (isRunning) {
                progressBar.setValue(0);
                progressBar.setString("0 %");
            }
        });
    }

    private void updateActionButtonState(boolean isRunning) {
        if (isRunning) {
            actionButton.setText("Islemi Durdur");
            actionButton.setIcon(IconFactory.create(IconFactory.Type.STOP, Color.WHITE));
            actionButton.setBackground(AppConstants.COLOR_DANGER);
            actionButton.setForeground(Color.WHITE);
        } else {
            actionButton.setText("Islemi Baslat");
            actionButton.setIcon(IconFactory.create(IconFactory.Type.PLAY, Color.WHITE));
            actionButton.setBackground(AppConstants.COLOR_PRIMARY);
            actionButton.setForeground(Color.WHITE);
            actionButton.setEnabled(true);
        }
    }

    private void updateActionButtonStopping() {
        SwingUtilities.invokeLater(() -> {
            actionButton.setText("Durduruluyor...");
            actionButton.setEnabled(false);
        });
    }

    private void setStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }

    private void openFileParent(File outputFile) {
        try {
            File parent = outputFile.getParentFile();

            if (parent != null && parent.exists()) {
                Desktop.getDesktop().open(parent);
            }
        } catch (Exception ex) {
            AppLogger.warning("Cikti klasoru acilamadi: " + ex.getMessage());
        }
    }

    private void applyWindowIcon() {
        URL logoUrl = getClass().getResource(AppConstants.RESOURCE_LOGO);
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            setIconImage(icon.getImage());
        }
    }

    private JLabel createLogoLabel(int width, int height) {
        URL logoUrl = getClass().getResource(AppConstants.RESOURCE_LOGO);

        if (logoUrl == null) {
            JLabel fallback = new JLabel(AppConstants.APP_NAME);
            fallback.setFont(new Font("Segoe UI", Font.BOLD, 20));
            fallback.setForeground(AppConstants.COLOR_PRIMARY_DARK);
            return fallback;
        }

        ImageIcon icon = new ImageIcon(logoUrl);
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(image));
    }

    private JPanel createCardPanel(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(AppConstants.COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(AppConstants.COLOR_PRIMARY_DARK);
        return label;
    }

    private JButton createRunButton() {
        JButton button = new JButton("Islemi Baslat", IconFactory.create(IconFactory.Type.PLAY, Color.WHITE));
        button.setBackground(AppConstants.COLOR_PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setIconTextGap(10);
        button.setBorder(BorderFactory.createEmptyBorder(13, 16, 13, 16));
        return button;
    }

    private JButton createSecondaryButton(String text, IconFactory.Type iconType) {
        JButton button = new JButton(text, IconFactory.create(iconType, AppConstants.COLOR_PRIMARY_DARK));
        button.setBackground(new Color(224, 235, 249));
        button.setForeground(AppConstants.COLOR_PRIMARY_DARK);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setIconTextGap(8);
        button.setBorder(BorderFactory.createEmptyBorder(11, 13, 11, 13));
        return button;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.BOLD, 13));
        field.setForeground(AppConstants.COLOR_TEXT);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppConstants.COLOR_BORDER),
                BorderFactory.createEmptyBorder(8, 9, 8, 9)
        ));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.BOLD, 13));
        comboBox.setForeground(AppConstants.COLOR_TEXT);
        comboBox.setBackground(Color.WHITE);
    }

    private GridBagConstraints baseGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        return gbc;
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, java.awt.Component component) {
        GridBagConstraints labelGbc = (GridBagConstraints) gbc.clone();
        labelGbc.gridx = 0;
        labelGbc.gridy = row;
        labelGbc.weightx = 0.0;
        labelGbc.insets = new Insets(8, 0, 8, 12);

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        fieldLabel.setForeground(AppConstants.COLOR_TEXT);
        form.add(fieldLabel, labelGbc);

        GridBagConstraints inputGbc = (GridBagConstraints) gbc.clone();
        inputGbc.gridx = 1;
        inputGbc.gridy = row;
        inputGbc.weightx = 1.0;
        form.add(component, inputGbc);
    }

    private class FileDropTargetListener extends DropTargetAdapter {

        @Override
        public void drop(DropTargetDropEvent event) {
            try {
                event.acceptDrop(DnDConstants.ACTION_COPY);

                Object transferData = event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                if (!(transferData instanceof List<?>)) {
                    return;
                }

                List<?> droppedFiles = (List<?>) transferData;
                for (Object item : droppedFiles) {
                    if (item instanceof File) {
                        setSelectedPdf((File) item);
                        break;
                    }
                }
            } catch (Exception ex) {
                AppLogger.error("Surukle birak hatasi: " + ex.getMessage(), ex);
            }
        }
    }
}
