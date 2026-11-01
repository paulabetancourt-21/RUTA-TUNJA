package co.edu.uptc.view.via;

import co.edu.uptc.i18n.LanguageService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class CreateEdgeDialog extends JDialog {

    private JTextField nodeAField;
    private JTextField nodeBField;
    private JTextField distanceField;
    private JComboBox<String> directionCombo;
    private boolean confirmed;

    public CreateEdgeDialog(JFrame parent, LanguageService languageService, String nodeA, String nodeB, double distanceKm) {
        super(parent, languageService.getText("menu.createEdge"), true);
        initDialog();
        initFields(nodeA, nodeB, distanceKm);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initDialog() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(14, 18, 14, 18));
    }

    private void initFields(String nodeA, String nodeB, double distanceKm) {
        nodeAField = createReadOnlyField(nodeA);
        nodeBField = createReadOnlyField(nodeB);
        distanceField = createReadOnlyField(String.format("%.2f m", distanceKm * 1000));
        directionCombo = new JComboBox<>(new String[]{"Doble sentido", "Solo un sentido"});
    }

    private JTextField createReadOnlyField(String text) {
        JTextField field = new JTextField(text, 15);
        field.setEnabled(false);
        return field;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 1, 4, 4));
        panel.add(new JLabel("Nodo A:"));
        panel.add(nodeAField);
        panel.add(new JLabel("Nodo B:"));
        panel.add(nodeBField);
        panel.add(new JLabel("Distancia:"));
        panel.add(distanceField);
        panel.add(new JLabel("Sentido:"));
        panel.add(directionCombo);
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

    public boolean isOneWay() {
        return "Solo un sentido".equals(directionCombo.getSelectedItem());
    }
}