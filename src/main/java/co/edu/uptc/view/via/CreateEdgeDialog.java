package co.edu.uptc.view.via;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.i18n.LanguageService;
import co.edu.uptc.view.help.DialogContext;
import co.edu.uptc.view.help.DialogTheme;

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

    private final DialogContext context;
    private JTextField nodeAField;
    private JTextField nodeBField;
    private JTextField distanceField;
    private JComboBox<String> directionCombo;
    private boolean confirmed;
    private final String oneWayText;

    public CreateEdgeDialog(JFrame parent, DialogContext context) {
        super(parent, context.languageService().getText("dialog.createEdge.title"), true);
        this.context = context;
        this.oneWayText = context.languageService().getText("dialog.createEdge.oneWay");
        initDialog();
        initFields();
        assemble(parent);
    }

    private void assemble(JFrame parent) {
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initDialog() {
        int gap = config().getInt("ui.dialogs.contentGap");
        setLayout(new BorderLayout(gap, gap));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(theme().insets("ui.dialogs.contentBorder")));
    }

    private void initFields() {
        nodeAField = createReadOnlyField();
        nodeBField = createReadOnlyField();
        distanceField = createReadOnlyField();
        directionCombo = new JComboBox<>(directionOptions());
    }

    private String[] directionOptions() {
        return new String[]{
                languageService().getText("dialog.createEdge.twoWay"),
                oneWayText
        };
    }

    private JTextField createReadOnlyField() {
        JTextField field = new JTextField(config().getInt("ui.dialogs.fieldColumns"));
        field.setEnabled(false);
        return field;
    }

    private JPanel createFormPanel() {
        int gap = config().getInt("ui.dialogs.edgeFormRowGap");
        JPanel panel = new JPanel(new GridLayout(0, 1, gap, gap));
        addLabeledField(panel, "dialog.createEdge.nodeA", nodeAField);
        addLabeledField(panel, "dialog.createEdge.nodeB", nodeBField);
        addLabeledField(panel, "dialog.createEdge.distance", distanceField);
        panel.add(new JLabel(languageService().getText("dialog.createEdge.direction") + ":"));
        panel.add(directionCombo);
        return panel;
    }

    private void addLabeledField(JPanel panel, String key, JTextField field) {
        panel.add(new JLabel(languageService().getText(key) + ":"));
        panel.add(field);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        int padding = config().getInt("ui.dialogs.buttonTopPadding");
        panel.setBorder(BorderFactory.createEmptyBorder(padding, 0, 0, 0));
        panel.add(createButton(languageService().getText("common.save"), true));
        panel.add(createButton(languageService().getText("common.cancel"), false));
        return panel;
    }

    private JButton createButton(String text, boolean value) {
        JButton button = new JButton(text);
        button.addActionListener(event -> confirm(value));
        return button;
    }

    private void confirm(boolean value) {
        confirmed = value;
        dispose();
    }

    public void setEdgeData(String nodeA, String nodeB, double distanceKm) {
        nodeAField.setText(nodeA);
        nodeBField.setText(nodeB);
        distanceField.setText(formatDistance(distanceKm));
    }

    private String formatDistance(double distanceKm) {
        double meters = distanceKm * config().getInt("ui.dialogs.metersPerKm");
        return String.format(config().getString("ui.dialogs.edgeDistanceFormat"), meters);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isOneWay() {
        return oneWayText.equals(directionCombo.getSelectedItem());
    }

    private LanguageService languageService() {
        return context.languageService();
    }

    private ConfigService config() {
        return context.configService();
    }

    private DialogTheme theme() {
        return context.theme();
    }
}
