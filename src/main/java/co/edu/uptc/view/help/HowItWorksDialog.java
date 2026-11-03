package co.edu.uptc.view.help;

import co.edu.uptc.i18n.LanguageService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public class HowItWorksDialog extends JDialog {

    private static final Color ACCENT = new Color(0x2563EB);
    private static final Color TEXT = new Color(0x1F2937);
    private static final Color MUTED = new Color(0x6B7280);
    private static final Color CARD_BACKGROUND = new Color(0xF8FAFC);
    private static final Color CARD_BORDER = new Color(0xE5E7EB);

    public HowItWorksDialog(JFrame parent, LanguageService languageService) {
        super(parent, languageService.getText("dialog.howItWorks.title"), true);
        buildDialog(parent, languageService);
    }

    private void buildDialog(JFrame parent, LanguageService languageService) {
        initDialog();
        add(createContentPanel(languageService), BorderLayout.CENTER);
        add(createButtonPanel(languageService), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initDialog() {
        setLayout(new BorderLayout(0, 12));
        setResizable(false);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 24, 16, 24));
    }

    private JPanel createContentPanel(LanguageService languageService) {
        JPanel panel = createVerticalPanel();
        addSection(panel, createHeaderPanel(languageService));
        addSection(panel, createStepsPanel(languageService));
        addSection(panel, createTipLabel(languageService));
        return panel;
    }

    private void addSection(JPanel parent, Component section) {
        parent.add(Box.createVerticalStrut(14));
        parent.add(section);
    }

    private JPanel createHeaderPanel(LanguageService languageService) {
        JPanel panel = createVerticalPanel();
        panel.add(createTitleLabel(languageService.getText("dialog.howItWorks.heading")));
        panel.add(createSubtitleLabel(languageService.getText("dialog.howItWorks.subtitle")));
        return panel;
    }

    private JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 20));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel("<html><body style='width: 430px'>" + text + "</body></html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createStepsPanel(LanguageService languageService) {
        JPanel panel = new JPanel(new GridLayout(4, 1, 0, 10));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addStepCards(panel, languageService);
        return panel;
    }

    private void addStepCards(JPanel panel, LanguageService languageService) {
        for (int index = 1; index <= 4; index++) {
            panel.add(createStepCard(languageService, index));
        }
    }

    private JPanel createStepCard(LanguageService languageService, int index) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        styleCard(card);
        card.add(createNumberBadge(index), BorderLayout.WEST);
        card.add(createStepText(languageService, index), BorderLayout.CENTER);
        return card;
    }

    private void styleCard(JPanel card) {
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(createCardBorder());
    }

    private Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12));
    }

    private JLabel createNumberBadge(int index) {
        JLabel badge = new JLabel(String.valueOf(index), JLabel.CENTER);
        styleBadge(badge);
        return badge;
    }

    private void styleBadge(JLabel badge) {
        badge.setOpaque(true);
        badge.setBackground(ACCENT);
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("SansSerif", Font.BOLD, 14));
        badge.setPreferredSize(new Dimension(30, 30));
    }

    private JLabel createStepText(LanguageService languageService, int index) {
        String title = languageService.getText("dialog.howItWorks.step" + index + "Title");
        String text = languageService.getText("dialog.howItWorks.step" + index);
        return createStepLabel(title, text);
    }

    private JLabel createStepLabel(String title, String text) {
        JLabel label = new JLabel("<html><body style='width: 370px'><b>" + title + "</b><br>" + text + "</body></html>");
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createTipLabel(LanguageService languageService) {
        JLabel label = new JLabel(languageService.getText("dialog.howItWorks.tip"));
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createVerticalPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }

    private JPanel createButtonPanel(LanguageService languageService) {
        JPanel panel = new JPanel();
        JButton acceptButton = createAcceptButton(languageService);
        panel.add(acceptButton);
        getRootPane().setDefaultButton(acceptButton);
        return panel;
    }

    private JButton createAcceptButton(LanguageService languageService) {
        JButton button = new JButton(languageService.getText("common.accept"));
        button.addActionListener(event -> dispose());
        return button;
    }
}
