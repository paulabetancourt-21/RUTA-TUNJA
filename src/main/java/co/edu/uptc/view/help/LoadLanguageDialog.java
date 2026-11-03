package co.edu.uptc.view.help;

import co.edu.uptc.config.ConfigService;
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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadLanguageDialog extends JDialog {

    private static final Logger LOGGER = Logger.getLogger(LoadLanguageDialog.class.getName());
    private final DialogContext context;
    private JTextField languageCodeField;
    private JTextArea yamlArea;
    private File selectedFile;
    private String loadedLanguageCode;

    public LoadLanguageDialog(JFrame parent, DialogContext context) {
        super(parent, context.languageService().getText("dialog.loadLanguage.title"), true);
        this.context = context;
        initDialog();
        add(createCodePanel(), BorderLayout.NORTH);
        add(createYamlPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initDialog() {
        int gap = config().getInt("ui.dialogs.contentGap");
        setLayout(new BorderLayout(gap, gap));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(theme().insets("ui.dialogs.loadLanguageInsets")));
    }

    private JPanel createCodePanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        String label = languageService().getText("dialog.loadLanguage.code") + ":";
        panel.add(new JLabel(label), BorderLayout.NORTH);
        languageCodeField = new JTextField(config().getInt("ui.dialogs.languageCodeColumns"));
        panel.add(languageCodeField, BorderLayout.SOUTH);
        return panel;
    }

    private JScrollPane createYamlPanel() {
        int rows = config().getInt("ui.dialogs.textAreaRows");
        int columns = config().getInt("ui.dialogs.textAreaColumns");
        yamlArea = new JTextArea(rows, columns);
        yamlArea.setEditable(false);
        yamlArea.setLineWrap(true);
        return new JScrollPane(yamlArea);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.add(createButton(languageService().getText("dialog.loadLanguage.fromFile"), e -> selectFile()));
        panel.add(createButton(languageService().getText("dialog.loadLanguage.add"), e -> addLanguage()));
        panel.add(createButton(languageService().getText("common.cancel"), e -> dispose()));
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
        String text = languageService().getText("dialog.loadLanguage.filter");
        String extension = config().getString("files.i18nFilterExtension");
        return new FileNameExtensionFilter(text, extension);
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
            Charset charset = Charset.forName(config().getString("files.i18nEncoding"));
            return Files.readString(file.toPath(), charset);
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Error al leer el archivo de idioma: " + file.getAbsolutePath(), exception);
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
        if (!hasSelectedFile()) {
            return false;
        }
        return applyLanguageCode();
    }

    private boolean hasSelectedFile() {
        if (selectedFile == null) {
            LOGGER.warning("No se seleccionó ningún archivo de idioma");
            return false;
        }
        return true;
    }

    private boolean applyLanguageCode() {
        String code = languageCodeField.getText().trim().toLowerCase();
        if (!languageService().loadExternalLanguage(code, selectedFile)) {
            return false;
        }
        loadedLanguageCode = code;
        return true;
    }

    private void showError() {
        String message = languageService().getText("dialog.loadLanguage.error");
        String title = languageService().getText("dialog.loadLanguage.errorTitle");
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public String getLoadedLanguageCode() {
        return loadedLanguageCode;
    }

    private LanguageService languageService() {
        return context.languageService();
    }

    private ConfigService config() {
        return context.configService();
    }

    private DialogTheme theme() {
        return context.theme();
    }
}
