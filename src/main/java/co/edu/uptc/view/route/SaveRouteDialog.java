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

public class SaveRouteDialog extends JDialog {

    private final DialogContext context;
    private JTextField routeNameField;
    private JTextField routeInfoField;
    private boolean confirmed;

    public SaveRouteDialog(JFrame parent, DialogContext context, String routeSummary) {
        super(parent, context.languageService().getText("dialog.saveRoute.title"), true);
        this.context = context;
        initDialog();
        initFields(routeSummary);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initDialog() {
        int gap = config().getInt("ui.dialogs.contentGap");
        setLayout(new BorderLayout(gap, gap));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(theme().insets("ui.dialogs.routeContentBorder")));
    }

    private void initFields(String routeSummary) {
        routeNameField = new JTextField(config().getInt("ui.dialogs.saveRouteFieldColumns"));
        routeInfoField = new JTextField(routeSummary, config().getInt("ui.dialogs.saveRouteFieldColumns"));
        routeInfoField.setEnabled(false);
    }

    private JPanel createFormPanel() {
        int gap = config().getInt("ui.dialogs.formRowGap");
        JPanel panel = new JPanel(new GridLayout(0, 1, gap, gap));
        addLabeledField(panel, "dialog.saveRoute.name", routeNameField);
        addLabeledField(panel, "dialog.saveRoute.summary", routeInfoField);
        return panel;
    }

    private void addLabeledField(JPanel panel, String key, JTextField field) {
        panel.add(new JLabel(languageService().getText(key) + ":"));
        panel.add(field);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        int padding = config().getInt("ui.dialogs.configButtonTopPadding");
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
