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

public class SaveRouteDialog extends JDialog {

    private JTextField routeNameField;
    private JTextField routeInfoField;
    private boolean confirmed;

    public SaveRouteDialog(JFrame parent, LanguageService languageService, String routeSummary) {
        super(parent, languageService.getText("dialog.saveRoute.title"), true);
        initDialog();
        initFields(routeSummary);
        add(createFormPanel(languageService), BorderLayout.CENTER);
        add(createButtonPanel(languageService), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initDialog() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(16, 20, 16, 20));
    }

    private void initFields(String routeSummary) {
        routeNameField = new JTextField(20);
        routeInfoField = new JTextField(routeSummary, 20);
        routeInfoField.setEnabled(false);
    }

    private JPanel createFormPanel(LanguageService languageService) {
        JPanel panel = new JPanel(new GridLayout(4, 1, 6, 6));
        panel.add(new JLabel(languageService.getText("dialog.saveRoute.name") + ":"));
        panel.add(routeNameField);
        panel.add(new JLabel(languageService.getText("dialog.saveRoute.summary") + ":"));
        panel.add(routeInfoField);
        return panel;
    }

    private JPanel createButtonPanel(LanguageService languageService) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(createButton(languageService.getText("common.save"), true));
        panel.add(createButton(languageService.getText("common.cancel"), false));
        return panel;
    }

    private JButton createButton(String text, boolean value) {
        JButton button = new JButton(text);
        button.addActionListener(event -> confirm(value));
        return button;
    }

    private void confirm(boolean value) {
        if (value && routeNameField.getText().trim().isEmpty()) {
            return;
        }
        confirmed = value;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getRouteName() {
        return routeNameField.getText().trim();
    }
}
