package co.edu.uptc.view.help;

import co.edu.uptc.i18n.LanguageService;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class AboutDialog extends JDialog {

    public AboutDialog(JFrame parent, LanguageService languageService) {
        super(parent, languageService.getText("dialog.about.title"), true);
        initDialog();
        add(createContentPanel(languageService), BorderLayout.CENTER);
        add(createButtonPanel(languageService), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initDialog() {
        setLayout(new BorderLayout(12, 12));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(16, 20, 16, 20));
    }

    private JPanel createContentPanel(LanguageService languageService) {
        JPanel panel = new JPanel(new GridLayout(5, 1, 4, 4));
        panel.add(createTitleLabel(languageService.getText("dialog.about.appName")));
        panel.add(createCenterLabel(languageService.getText("dialog.about.version")));
        panel.add(createCenterLabel(languageService.getText("dialog.about.author")));
        panel.add(createCenterLabel(languageService.getText("dialog.about.university")));
        panel.add(createCenterLabel(languageService.getText("dialog.about.faculty")));
        return panel;
    }

    private JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        return label;
    }

    private JLabel createCenterLabel(String text) {
        return new JLabel(text, JLabel.CENTER);
    }

    private JPanel createButtonPanel(LanguageService languageService) {
        JPanel panel = new JPanel();
        JButton acceptButton = new JButton(languageService.getText("common.accept"));
        acceptButton.addActionListener(event -> dispose());
        panel.add(acceptButton);
        return panel;
    }
}
