package co.edu.uptc.view.help;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.i18n.LanguageService;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

public class ConfigDialog extends JDialog {

    private JButton colorChooserBtn;
    private Color selectedRouteColor = Color.BLUE;
    private JComboBox<Integer> nodeSizeCombo;
    private JComboBox<Integer> strokeWidthCombo;

    public ConfigDialog(JFrame parent, LanguageService languageService, ConfigService configService) {
        super(parent, "Preferencias / Configuración Visual", true);
        initDialog();
        initFields();
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initDialog() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(16, 20, 16, 20));
    }

    private void initFields() {
        colorChooserBtn = new JButton("Seleccionar color...");
        colorChooserBtn.setBackground(selectedRouteColor);
        colorChooserBtn.setForeground(Color.WHITE);
        colorChooserBtn.addActionListener(e -> chooseColor());
        Integer[] sizes = {4, 6, 8, 10, 12, 14, 16};
        Integer[] strokes = {1, 2, 3, 4, 5, 6, 8};
        nodeSizeCombo = new JComboBox<>(sizes);
        nodeSizeCombo.setSelectedItem(8);
        strokeWidthCombo = new JComboBox<>(strokes);
        strokeWidthCombo.setSelectedItem(3);
    }

    private void chooseColor() {
        Color chosen = JColorChooser.showDialog(this, "Elija el color de la ruta", selectedRouteColor);
        if (chosen != null) {
            selectedRouteColor = chosen;
            colorChooserBtn.setBackground(selectedRouteColor);
        }
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 6, 6));
        panel.add(new JLabel("Color de la ruta calculada:"));
        panel.add(colorChooserBtn);
        panel.add(new JLabel("Tamaño del punto del nodo (px):"));
        panel.add(nodeSizeCombo);
        panel.add(new JLabel("Grosor de línea / arista (px):"));
        panel.add(strokeWidthCombo);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JButton saveBtn = new JButton("Guardar");
        JButton cancelBtn = new JButton("Cancelar");
        saveBtn.addActionListener(e -> dispose());
        cancelBtn.addActionListener(e -> dispose());
        panel.add(saveBtn);
        panel.add(cancelBtn);
        return panel;
    }

    public Color getSelectedRouteColor() {
        return selectedRouteColor;
    }

    public int getNodeSize() {
        return (Integer) nodeSizeCombo.getSelectedItem();
    }

    public int getStrokeWidth() {
        return (Integer) strokeWidthCombo.getSelectedItem();
    }
}