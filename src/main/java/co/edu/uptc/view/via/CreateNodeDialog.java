package co.edu.uptc.view.via;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.i18n.LanguageService;
import co.edu.uptc.view.help.DialogContext;
import co.edu.uptc.view.help.DialogTheme;

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

    private final DialogContext context;
    private JTextField nameField;
    private JTextField latitudeField;
    private JTextField longitudeField;
    private boolean confirmed;

    public CreateNodeDialog(JFrame parent, DialogContext context) {
        super(parent, context.languageService().getText("dialog.createNode.title"), true);
        this.context = context;
        initDialog();
        initFields();
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
        nameField = new JTextField(config().getInt("ui.dialogs.fieldColumns"));
        latitudeField = createReadOnlyField();
        longitudeField = createReadOnlyField();
    }

    private JTextField createReadOnlyField() {
        JTextField field = new JTextField(config().getInt("ui.dialogs.fieldColumns"));
        field.setEnabled(false);
        return field;
    }

    private JPanel createFormPanel() {
        int gap = config().getInt("ui.dialogs.formRowGap");
        JPanel panel = new JPanel(new GridLayout(0, 1, gap, gap));
        addLabeledField(panel, "dialog.createNode.name", nameField);
        addLabeledField(panel, "dialog.createNode.latitude", latitudeField);
        addLabeledField(panel, "dialog.createNode.longitude", longitudeField);
        panel.add(new JLabel(languageService().getText("dialog.createNode.hint")));
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

    public void setCoordinates(double latitude, double longitude) {
        String format = config().getString("ui.dialogs.coordinateFormat");
        latitudeField.setText(String.format(format, latitude));
        longitudeField.setText(String.format(format, longitude));
    }

    public String getNodeName() {
        return nameField.getText().trim();
    }

    public boolean isConfirmed() {
        return confirmed;
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
