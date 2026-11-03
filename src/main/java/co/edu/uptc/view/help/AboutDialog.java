package co.edu.uptc.view.help;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class AboutDialog extends BaseHelpDialog {

    public AboutDialog(JFrame parent, DialogContext context) {
        super(parent, context, "dialog.about.title");
        assemble(parent, createContentPanel(), theme.insets("ui.dialog.aboutInsets"));
    }

    private JPanel createContentPanel() {
        JPanel panel = createVerticalPanel();
        addSection(panel, createHeaderPanel());
        addSection(panel, createInfoPanel());
        addSection(panel, createFooterPanel());
        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = createVerticalPanel();
        addTitleSection(panel);
        panel.add(createDescriptionLabel());
        return panel;
    }

    private void addTitleSection(JPanel panel) {
        Font titleFont = theme.font(Font.BOLD, theme.intValue("aboutTitleFontSize"));
        panel.add(centered(languageService.getText("dialog.about.appName"), titleFont, theme.text()));
        addTagline(panel);
        addVersion(panel);
    }

    private void addTagline(JPanel panel) {
        Font font = theme.font(Font.PLAIN, theme.intValue("aboutTaglineFontSize"));
        panel.add(centered(languageService.getText("dialog.about.tagline"), font, theme.muted()));
    }

    private void addVersion(JPanel panel) {
        Font font = theme.font(Font.BOLD, theme.intValue("aboutVersionFontSize"));
        panel.add(centered(languageService.getText("dialog.about.version"), font, theme.accent()));
    }

    private JLabel createDescriptionLabel() {
        Font font = theme.font(Font.PLAIN, theme.intValue("aboutDescriptionFontSize"));
        return centered(htmlText(languageService.getText("dialog.about.description")), font, theme.muted());
    }

    private String htmlText(String text) {
        int width = theme.intValue("htmlWidthPx");
        return "<html><body style='width: " + width + "px'>" + text + "</body></html>";
    }

    private JLabel centered(String text, Font font, Color color) {
        JLabel label = createLabel(text, font, color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.add(createInfoGrid());
        return panel;
    }

    private JPanel createInfoGrid() {
        JPanel panel = new JPanel(new GridBagLayout());
        addInfoRow(panel, 0, "author");
        addInfoRow(panel, 1, "university");
        addInfoRow(panel, 2, "faculty");
        return panel;
    }

    private void addInfoRow(JPanel panel, int row, String key) {
        panel.add(createKeyLabel(key), createCell(row, 0, GridBagConstraints.EAST));
        panel.add(createValueLabel(key), createCell(row, 1, GridBagConstraints.WEST));
    }

    private GridBagConstraints createCell(int row, int column, int anchor) {
        GridBagConstraints cell = new GridBagConstraints();
        cell.gridx = column;
        cell.gridy = row;
        cell.anchor = anchor;
        cell.insets = cellInsets(row, column);
        return cell;
    }

    private Insets cellInsets(int row, int column) {
        int rowGap = theme.intValue("gridRowGap");
        int columnGap = theme.intValue("gridColumnGap");
        return new Insets(row == 0 ? 0 : rowGap, column == 0 ? 0 : columnGap, 0, 0);
    }

    private JLabel createKeyLabel(String key) {
        String text = languageService.getText("dialog.about." + key + "Label");
        Font font = theme.font(Font.BOLD, theme.intValue("aboutKeyFontSize"));
        return createLabel(text, font, theme.muted());
    }

    private JLabel createValueLabel(String key) {
        String text = languageService.getText("dialog.about." + key);
        Font font = theme.font(Font.PLAIN, theme.intValue("aboutValueFontSize"));
        return createLabel(text, font, theme.text());
    }

    private JPanel createFooterPanel() {
        JPanel panel = createVerticalPanel();
        addSeparator(panel);
        panel.add(footerLabel("dialog.about.technologies"));
        panel.add(footerLabel("dialog.about.copyright"));
        return panel;
    }

    private JLabel footerLabel(String key) {
        Font font = theme.font(Font.PLAIN, theme.intValue("aboutFooterFontSize"));
        return centered(languageService.getText(key), font, theme.muted());
    }
}
