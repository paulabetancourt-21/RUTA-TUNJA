package co.edu.uptc.view.help;

import co.edu.uptc.i18n.LanguageService;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;

public class LoadLanguageDialog extends JDialog {

    private final LanguageService languageService;
    private JTextField languageNameField;
    private JTextArea yamlArea;

    public LoadLanguageDialog(JFrame parent, LanguageService languageService) {
        super(parent, languageService.getText("dialog.loadLanguage.title"), true);
        this.languageService = languageService;
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(14, 18, 14, 18));
        add(createNamePanel(), BorderLayout.NORTH);
        add(createYamlPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private JPanel createNamePanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.add(new JLabel(languageService.getText("dialog.loadLanguage.name") + ":"), BorderLayout.NORTH);
        languageNameField = new JTextField(15);
        panel.add(languageNameField, BorderLayout.SOUTH);
        return panel;
    }

    private JScrollPane createYamlPanel() {
        yamlArea = new JTextArea(8, 25);
        return new JScrollPane(yamlArea);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.add(createButton(languageService.getText("dialog.loadLanguage.fromFile"), e -> selectFile()));
        panel.add(createButton(languageService.getText("dialog.loadLanguage.add"), e -> dispose()));
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
        chooser.setFileFilter(new FileNameExtensionFilter(
                languageService.getText("dialog.loadLanguage.filter"), "yml"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            languageNameField.setText(chooser.getSelectedFile().getName());
        }
    }
}
