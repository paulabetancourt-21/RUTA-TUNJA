package co.edu.uptc.view.help;

import co.edu.uptc.i18n.LanguageService;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class LoadLanguageDialog extends JDialog {

    private final LanguageService languageService;
    private JTextField languageCodeField;
    private JTextArea yamlArea;
    private File selectedFile;
    private String loadedLanguageCode;

    public LoadLanguageDialog(JFrame parent, LanguageService languageService) {
        super(parent, languageService.getText("dialog.loadLanguage.title"), true);
        this.languageService = languageService;
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(14, 18, 14, 18));
        add(createCodePanel(), BorderLayout.NORTH);
        add(createYamlPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private JPanel createCodePanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.add(new JLabel(languageService.getText("dialog.loadLanguage.code") + ":"), BorderLayout.NORTH);
        languageCodeField = new JTextField(15);
        panel.add(languageCodeField, BorderLayout.SOUTH);
        return panel;
    }

    private JScrollPane createYamlPanel() {
        yamlArea = new JTextArea(8, 25);
        yamlArea.setEditable(false);
        yamlArea.setLineWrap(true);
        return new JScrollPane(yamlArea);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.add(createButton(languageService.getText("dialog.loadLanguage.fromFile"), e -> selectFile()));
        panel.add(createButton(languageService.getText("dialog.loadLanguage.add"), e -> addLanguage()));
        panel.add(createButton(languageService.getText("common.cancel"), e -> dispose()));
        return panel;
    }

    private JButton createButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    private void selectFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(createFilter());
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        applyFile(chooser.getSelectedFile());
    }

    private FileNameExtensionFilter createFilter() {
        String text = languageService.getText("dialog.loadLanguage.filter");
        return new FileNameExtensionFilter(text, "yml");
    }

    private void applyFile(File file) {
        selectedFile = file;
        languageCodeField.setText(baseCode(file));
        yamlArea.setText(readFile(file));
        yamlArea.setCaretPosition(0);
    }

    private String baseCode(File file) {
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        return (dot > 0 ? name.substring(0, dot) : name).toLowerCase();
    }

    private String readFile(File file) {
        try {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            return "";
        }
    }

    private void addLanguage() {
        if (!tryAddLanguage()) {
            showError();
            return;
        }
        dispose();
    }

    private boolean tryAddLanguage() {
        if (selectedFile == null) {
            return false;
        }
        String code = languageCodeField.getText().trim().toLowerCase();
        if (!languageService.loadExternalLanguage(code, selectedFile)) {
            return false;
        }
        loadedLanguageCode = code;
        return true;
    }

    private void showError() {
        String message = languageService.getText("dialog.loadLanguage.error");
        String title = languageService.getText("dialog.loadLanguage.errorTitle");
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public String getLoadedLanguageCode() {
        return loadedLanguageCode;
    }
}
