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

    private JTextField latField;
    private JTextField lonField;
    private boolean confirmed;

    public CreateNodeDialog(JFrame parent, LanguageService languageService, double lat, double lon) {
        super(parent, languageService.getText("menu.createNode"), true);
        initDialog();
        initFields(lat, lon);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initDialog() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(14, 18, 14, 18));
    }

    private void initFields(double lat, double lon) {
        latField = new JTextField(String.format("%.6f", lat), 15);
        latField.setEnabled(false);
        lonField = new JTextField(String.format("%.6f", lon), 15);
        lonField.setEnabled(false);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 6, 6));
        panel.add(new JLabel("Latitud:"));
        panel.add(latField);
        panel.add(new JLabel("Longitud:"));
        panel.add(lonField);
        panel.add(new JLabel("Las coordenadas se llenan con el clic en el mapa."));
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        JButton saveBtn = new JButton("Guardar");
        JButton cancelBtn = new JButton("Cancelar");
        saveBtn.addActionListener(e -> { confirmed = true; dispose(); });
        cancelBtn.addActionListener(e -> { confirmed = false; dispose(); });
        panel.add(saveBtn);
        panel.add(cancelBtn);
        return panel;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}