package co.edu.uptc.view.route;

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

public class CalculateRouteDialog extends JDialog {

    private final DialogContext context;
    private JTextField originField;
    private JTextField destinationField;
    private boolean confirmed;

    public CalculateRouteDialog(JFrame parent, DialogContext context) {
        super(parent, context.languageService().getText("dialog.calculateRoute.title"), true);
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
        originField = createReadOnlyField();
        destinationField = createReadOnlyField();
    }

    private JTextField createReadOnlyField() {
        JTextField field = new JTextField(config().getInt("ui.dialogs.routeFieldColumns"));
        field.setEnabled(false);
        return field;
    }

    private JPanel createFormPanel() {
        int gap = config().getInt("ui.dialogs.formRowGap");
        JPanel panel = new JPanel(new GridLayout(0, 1, gap, gap));
        addLabeledField(panel, "dialog.calculateRoute.origin", originField);
        addLabeledField(panel, "dialog.calculateRoute.destination", destinationField);
        panel.add(new JLabel(languageService().getText("dialog.calculateRoute.hint")));
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
        panel.add(createButton(languageService().getText("common.calculate"), true));
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

    public void setRouteEndpoints(String origin, String destination) {
        originField.setText(origin);
        destinationField.setText(destination);
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
