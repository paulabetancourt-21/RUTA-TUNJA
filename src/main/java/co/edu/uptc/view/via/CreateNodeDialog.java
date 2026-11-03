package co.edu.uptc.view.via;

import co.edu.uptc.i18n.LanguageService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class CreateNodeDialog extends JDialog {

    private JTextField nameField;
    private JTextField latitudeField;
    private JTextField longitudeField;
    private boolean confirmed;

    public CreateNodeDialog(JFrame parent, LanguageService languageService) {
        super(parent, languageService.getText("dialog.createNode.title"), true);
        initDialog();
        initFields();
        add(createFormPanel(languageService), BorderLayout.CENTER);
        add(createButtonPanel(languageService), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initDialog() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(14, 18, 14, 18));
    }

    private void initFields() {
        nameField = new JTextField(15);
        latitudeField = createReadOnlyField();
        longitudeField = createReadOnlyField();
    }

    private JTextField createReadOnlyField() {
        JTextField field = new JTextField(15);
        field.setEnabled(false);
        return field;
    }

    private JPanel createFormPanel(LanguageService languageService) {
        JPanel panel = new JPanel(new GridLayout(7, 1, 6, 6));
        panel.add(new JLabel(languageService.getText("dialog.createNode.name") + ":"));
        panel.add(nameField);
        panel.add(new JLabel(languageService.getText("dialog.createNode.latitude") + ":"));
        panel.add(latitudeField);
        panel.add(new JLabel(languageService.getText("dialog.createNode.longitude") + ":"));
        panel.add(longitudeField);
        panel.add(new JLabel(languageService.getText("dialog.createNode.hint")));
        return panel;
    }

    private JPanel createButtonPanel(LanguageService languageService) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        panel.add(createButton(languageService.getText("common.save"), true));
        panel.add(createButton(languageService.getText("common.cancel"), false));
        return panel;
    }

    private JButton createButton(String text, boolean value) {
        JButton button = new JButton(text);
        button.addActionListener(event -> {
            confirmed = value;
            dispose();
        });
        return button;
    }

    public void setCoordinates(double latitude, double longitude) {
        latitudeField.setText(String.format("%.6f", latitude));
        longitudeField.setText(String.format("%.6f", longitude));
    }

    public String getNodeName() {
        return nameField.getText().trim();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
