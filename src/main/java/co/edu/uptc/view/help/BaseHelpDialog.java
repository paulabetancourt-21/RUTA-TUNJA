package co.edu.uptc.view.help;

import co.edu.uptc.config.ConfigService;
import co.edu.uptc.i18n.LanguageService;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

public abstract class BaseHelpDialog extends JDialog {

    protected final LanguageService languageService;
    protected final ConfigService config;
    protected final DialogTheme theme;

    protected BaseHelpDialog(JFrame parent, DialogContext context, String titleKey) {
        super(parent, context.languageService().getText(titleKey), true);
        this.languageService = context.languageService();
        this.config = context.configService();
        this.theme = context.theme();
    }

    protected void assemble(JFrame parent, JPanel content, Insets insets) {
        int gap = config.getInt("ui.dialog.dialogGap");
        setLayout(new BorderLayout(0, gap));
        setResizable(false);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(insets));
        add(content, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    protected JPanel createVerticalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    protected void addSection(JPanel parent, Component section) {
        int spacing = theme.intValue("sectionSpacing");
        parent.add(Box.createVerticalStrut(spacing));
        parent.add(section);
    }

    protected JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    protected JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        JButton acceptButton = createAcceptButton();
        panel.add(acceptButton);
        getRootPane().setDefaultButton(acceptButton);
        return panel;
    }

    private JButton createAcceptButton() {
        JButton button = new JButton(languageService.getText("common.accept"));
        button.addActionListener(event -> dispose());
        return button;
    }

    protected void addSeparator(JPanel panel) {
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(theme.intValue("separatorSpacing")));
    }
}
