package co.edu.uptc.view.help;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public class HowItWorksDialog extends BaseHelpDialog {

    public HowItWorksDialog(JFrame parent, DialogContext context) {
        super(parent, context, "dialog.howItWorks.title");
        assemble(parent, createContentPanel(), theme.insets("ui.dialog.howItWorksInsets"));
    }

    private JPanel createContentPanel() {
        JPanel panel = createVerticalPanel();
        addSection(panel, createHeaderPanel());
        addSection(panel, createStepsPanel());
        addSection(panel, createTipLabel());
        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = createVerticalPanel();
        panel.add(createTitleLabel());
        panel.add(createSubtitleLabel());
        return panel;
    }

    private JLabel createTitleLabel() {
        Font font = theme.font(Font.BOLD, theme.intValue("howItWorksTitleFontSize"));
        JLabel label = createLabel(languageService.getText("dialog.howItWorks.heading"), font, theme.text());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createSubtitleLabel() {
        Font font = theme.font(Font.PLAIN, theme.intValue("howItWorksSubtitleFontSize"));
        JLabel label = createLabel(htmlText(languageService.getText("dialog.howItWorks.subtitle")), font, theme.muted());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createStepsPanel() {
        int count = theme.intValue("stepsCount");
        int gap = theme.intValue("stepsGap");
        JPanel panel = new JPanel(new GridLayout(count, 1, 0, gap));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        addStepCards(panel, count);
        return panel;
    }

    private void addStepCards(JPanel panel, int count) {
        for (int index = 1; index <= count; index++) {
            panel.add(createStepCard(index));
        }
    }

    private JPanel createStepCard(int index) {
        int gap = theme.intValue("stepCardGap");
        JPanel card = new JPanel(new BorderLayout(gap, 0));
        styleCard(card);
        card.add(createNumberBadge(index), BorderLayout.WEST);
        card.add(createStepText(index), BorderLayout.CENTER);
        return card;
    }

    private void styleCard(JPanel card) {
        card.setBackground(theme.cardBackground());
        card.setBorder(createCardBorder());
    }

    private Border createCardBorder() {
        Border line = BorderFactory.createLineBorder(theme.cardBorder(), 1, true);
        return BorderFactory.createCompoundBorder(line, cardPaddingBorder());
    }

    private Border cardPaddingBorder() {
        java.awt.Insets insets = theme.insets("ui.dialog.cardPadding");
        return BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right);
    }

    private JLabel createNumberBadge(int index) {
        JLabel badge = new JLabel(String.valueOf(index), JLabel.CENTER);
        styleBadge(badge);
        return badge;
    }

    private void styleBadge(JLabel badge) {
        int size = theme.intValue("badgeSize");
        badge.setOpaque(true);
        badge.setBackground(theme.accent());
        badge.setForeground(Color.WHITE);
        badge.setFont(theme.font(Font.BOLD, theme.intValue("badgeFontSize")));
        badge.setPreferredSize(new Dimension(size, size));
    }

    private JLabel createStepText(int index) {
        String title = languageService.getText("dialog.howItWorks.step" + index + "Title");
        String text = languageService.getText("dialog.howItWorks.step" + index);
        return createStepLabel(title, text);
    }

    private JLabel createStepLabel(String title, String text) {
        String width = String.valueOf(theme.intValue("stepTextWidthPx"));
        String html = "<html><body style='width: " + width + "px'><b>" + title + "</b><br>" + text + "</body></html>";
        JLabel label = new JLabel(html);
        label.setForeground(theme.text());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createTipLabel() {
        Font font = theme.font(Font.PLAIN, theme.intValue("howItWorksTipFontSize"));
        JLabel label = createLabel(languageService.getText("dialog.howItWorks.tip"), font, theme.muted());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private String htmlText(String text) {
        int width = theme.intValue("htmlWidthPx");
        return "<html><body style='width: " + width + "px'>" + text + "</body></html>";
    }
}
