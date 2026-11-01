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
        super(parent, languageService.getText("menu.saveRoute"), true);
        initDialog();
        initFields(routeSummary);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
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

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 6, 6));
        panel.add(new JLabel("Nombre de la ruta:"));
        panel.add(routeNameField);
        panel.add(new JLabel("Resumen de la ruta:"));
        panel.add(routeInfoField);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JButton saveBtn = new JButton("Guardar");
        JButton cancelBtn = new JButton("Cancelar");
        
        saveBtn.addActionListener(e -> {
            if (!routeNameField.getText().trim().isEmpty()) {
                confirmed = true;
                dispose();
            }
        });
        cancelBtn.addActionListener(e -> { confirmed = false; dispose(); });
        
        panel.add(saveBtn);
        panel.add(cancelBtn);
        return panel;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getRouteName() {
        return routeNameField.getText().trim();
    }
}