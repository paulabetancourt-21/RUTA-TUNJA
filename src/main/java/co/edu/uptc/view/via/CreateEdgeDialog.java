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
    private final String oneWayText;

    public CreateEdgeDialog(JFrame parent, LanguageService languageService) {
        super(parent, languageService.getText("dialog.createEdge.title"), true);
        this.oneWayText = languageService.getText("dialog.createEdge.oneWay");
        initDialog();
        initFields(languageService);
        add(createFormPanel(languageService), BorderLayout.CENTER);
        add(createButtonPanel(languageService), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initDialog() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(14, 18, 14, 18));
    }

    private void initFields(LanguageService languageService) {
        nodeAField = createReadOnlyField();
        nodeBField = createReadOnlyField();
        distanceField = createReadOnlyField();
        directionCombo = new JComboBox<>(new String[]{
                languageService.getText("dialog.createEdge.twoWay"),
                oneWayText
        });
    }

    private JTextField createReadOnlyField() {
        JTextField field = new JTextField(15);
        field.setEnabled(false);
        return field;
    }

    private JPanel createFormPanel(LanguageService languageService) {
        JPanel panel = new JPanel(new GridLayout(8, 1, 4, 4));
        panel.add(new JLabel(languageService.getText("dialog.createEdge.nodeA") + ":"));
        panel.add(nodeAField);
        panel.add(new JLabel(languageService.getText("dialog.createEdge.nodeB") + ":"));
        panel.add(nodeBField);
        panel.add(new JLabel(languageService.getText("dialog.createEdge.distance") + ":"));
        panel.add(distanceField);
        panel.add(new JLabel(languageService.getText("dialog.createEdge.direction") + ":"));
        panel.add(directionCombo);
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

    public void setEdgeData(String nodeA, String nodeB, double distanceKm) {
        nodeAField.setText(nodeA);
        nodeBField.setText(nodeB);
        distanceField.setText(String.format("%.2f m", distanceKm * 1000));
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isOneWay() {
        return oneWayText.equals(directionCombo.getSelectedItem());
    }
}
