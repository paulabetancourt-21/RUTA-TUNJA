package co.edu.uptc.view.help;

import co.edu.uptc.i18n.LanguageService;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class HowItWorksDialog extends JDialog {

    public HowItWorksDialog(JFrame parent, LanguageService languageService) {
        super(parent, languageService.getText("dialog.howItWorks.title"), true);
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
        JPanel panel = new JPanel(new GridLayout(5, 1, 8, 8));
        String heading = languageService.getText("dialog.howItWorks.heading");
        panel.add(new JLabel("<html><b>" + heading + "</b></html>"));
        panel.add(new JLabel(languageService.getText("dialog.howItWorks.step1")));
        panel.add(new JLabel(languageService.getText("dialog.howItWorks.step2")));
        panel.add(new JLabel(languageService.getText("dialog.howItWorks.step3")));
        panel.add(new JLabel(languageService.getText("dialog.howItWorks.step4")));
        return panel;
    }

    private JPanel createButtonPanel(LanguageService languageService) {
        JPanel panel = new JPanel();
        JButton acceptButton = new JButton(languageService.getText("common.accept"));
        acceptButton.addActionListener(event -> dispose());
        panel.add(acceptButton);
        return panel;
    }
}
