package co.edu.uptc.view.route;

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

public class CalculateRouteDialog extends JDialog {

    private JTextField originField;
    private JTextField destinationField;
    private boolean confirmed;

    public CalculateRouteDialog(JFrame parent, LanguageService languageService) {
        super(parent, languageService.getText("dialog.calculateRoute.title"), true);
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
        originField = createReadOnlyField();
        destinationField = createReadOnlyField();
    }

    private JTextField createReadOnlyField() {
        JTextField field = new JTextField(18);
        field.setEnabled(false);
        return field;
    }

    private JPanel createFormPanel(LanguageService languageService) {
        JPanel panel = new JPanel(new GridLayout(5, 1, 6, 6));
        panel.add(new JLabel(languageService.getText("dialog.calculateRoute.origin") + ":"));
        panel.add(originField);
        panel.add(new JLabel(languageService.getText("dialog.calculateRoute.destination") + ":"));
        panel.add(destinationField);
        panel.add(new JLabel(languageService.getText("dialog.calculateRoute.hint")));
        return panel;
    }

    private JPanel createButtonPanel(LanguageService languageService) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        panel.add(createButton(languageService.getText("common.calculate"), true));
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

    public void setRouteEndpoints(String origin, String destination) {
        originField.setText(origin);
        destinationField.setText(destination);
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
