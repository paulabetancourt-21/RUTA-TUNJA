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

    public CalculateRouteDialog(JFrame parent, LanguageService languageService, String originName, String destName) {
        super(parent, languageService.getText("menu.calculateRoute"), true);
        initDialog();
        initFields(originName, destName);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initDialog() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(14, 18, 14, 18));
    }

    private void initFields(String originName, String destName) {
        originField = new JTextField(originName, 18);
        originField.setEnabled(false);
        destinationField = new JTextField(destName, 18);
        destinationField.setEnabled(false);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 6, 6));
        panel.add(new JLabel("Origen seleccionada:"));
        panel.add(originField);
        panel.add(new JLabel("Destino seleccionado:"));
        panel.add(destinationField);
        panel.add(new JLabel("La ruta calcula el camino vehicular más corto."));
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        JButton calcBtn = new JButton("Calcular");
        JButton cancelBtn = new JButton("Cancelar");
        calcBtn.addActionListener(e -> { confirmed = true; dispose(); });
        cancelBtn.addActionListener(e -> { confirmed = false; dispose(); });
        panel.add(calcBtn);
        panel.add(cancelBtn);
        return panel;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}