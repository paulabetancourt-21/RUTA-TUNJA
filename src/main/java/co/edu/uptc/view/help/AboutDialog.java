package co.edu.uptc.view.help;

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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class AboutDialog extends JDialog {

    private static final Color ACCENT = new Color(0x1D4ED8);
    private static final Color TEXT = new Color(0x111827);
    private static final Color MUTED = new Color(0x6B7280);

    public AboutDialog(JFrame parent, LanguageService languageService) {
        super(parent, languageService.getText("dialog.about.title"), true);
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
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 28, 16, 28));
    }

    private JPanel createContentPanel(LanguageService languageService) {
        JPanel panel = createVerticalPanel();
        addSection(panel, createHeaderPanel(languageService));
        addSection(panel, createInfoPanel(languageService));
        addSection(panel, createFooterPanel(languageService));
        return panel;
    }

    private void addSection(JPanel parent, Component section) {
        parent.add(Box.createVerticalStrut(14));
        parent.add(section);
    }

    private JPanel createHeaderPanel(LanguageService languageService) {
        JPanel panel = createVerticalPanel();
        addTitleSection(panel, languageService);
        panel.add(createDescriptionLabel(languageService.getText("dialog.about.description")));
        return panel;
    }

    private void addTitleSection(JPanel panel, LanguageService languageService) {
        panel.add(createTitleLabel(languageService.getText("dialog.about.appName")));
        panel.add(createTaglineLabel(languageService.getText("dialog.about.tagline")));
        panel.add(createVersionLabel(languageService.getText("dialog.about.version")));
    }

    private JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 22));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JLabel createTaglineLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(MUTED);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JLabel createVersionLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(ACCENT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JLabel createDescriptionLabel(String text) {
        JLabel label = new JLabel("<html><body style='width: 430px'>" + text + "</body></html>", JLabel.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(MUTED);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JPanel createInfoPanel(LanguageService languageService) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.add(createInfoGrid(languageService));
        return panel;
    }

    private JPanel createInfoGrid(LanguageService languageService) {
        JPanel panel = new JPanel(new GridBagLayout());
        addInfoRow(panel, languageService, 0, "author");
        addInfoRow(panel, languageService, 1, "university");
        addInfoRow(panel, languageService, 2, "faculty");
        return panel;
    }

    private void addInfoRow(JPanel panel, LanguageService languageService, int row, String key) {
        panel.add(createKeyLabel(languageService.getText("dialog.about." + key + "Label")), createCell(row, 0, GridBagConstraints.EAST));
        panel.add(createValueLabel(languageService.getText("dialog.about." + key)), createCell(row, 1, GridBagConstraints.WEST));
    }

    private GridBagConstraints createCell(int row, int column, int anchor) {
        GridBagConstraints cell = new GridBagConstraints();
        styleCell(cell, row, column, anchor);
        return cell;
    }

    private void styleCell(GridBagConstraints cell, int row, int column, int anchor) {
        cell.gridx = column;
        cell.gridy = row;
        cell.anchor = anchor;
        cell.insets = new Insets(row == 0 ? 0 : 5, column == 0 ? 0 : 12, 0, 0);
    }

    private JLabel createKeyLabel(String text) {
        JLabel label = new JLabel(text, JLabel.RIGHT);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(MUTED);
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(TEXT);
        return label;
    }

    private JPanel createFooterPanel(LanguageService languageService) {
        JPanel panel = createVerticalPanel();
        addSeparator(panel);
        panel.add(createFooterLabel(languageService.getText("dialog.about.technologies")));
        panel.add(createFooterLabel(languageService.getText("dialog.about.copyright")));
        return panel;
    }

    private void addSeparator(JPanel panel) {
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(8));
    }

    private JLabel createFooterLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        label.setForeground(MUTED);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
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
