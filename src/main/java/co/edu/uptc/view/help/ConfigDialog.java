package co.edu.uptc.view.help;

import co.edu.uptc.i18n.LanguageService;
import co.edu.uptc.view.map.MapStyle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

public class ConfigDialog extends JDialog {

    private static final Integer[] NODE_SIZES = {6, 8, 10, 12, 14, 16, 20};
    private static final Integer[] STROKE_WIDTHS = {1, 2, 3, 4, 5, 6};

    private final LanguageService languageService;
    private JButton nodeColorButton;
    private JButton edgeColorButton;
    private JButton routeColorButton;
    private Color nodeColor;
    private Color edgeColor;
    private Color routeColor;
    private JComboBox<Integer> nodeSizeCombo;
    private JComboBox<Integer> strokeWidthCombo;
    private boolean confirmed;

    public ConfigDialog(JFrame parent, LanguageService languageService, MapStyle style) {
        super(parent, languageService.getText("dialog.config.title"), true);
        this.languageService = languageService;
        initDialog();
        initFields(style);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initDialog() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(16, 20, 16, 20));
    }

    private void initFields(MapStyle style) {
        initColors(style);
        nodeSizeCombo = createNumberCombo(NODE_SIZES, style.getNodeDiameter());
        strokeWidthCombo = createNumberCombo(STROKE_WIDTHS, Math.round(style.getEdgeWidth()));
    }

    private void initColors(MapStyle style) {
        nodeColor = style.getNodeColor();
        edgeColor = style.getEdgeColor();
        routeColor = style.getRouteColor();
        nodeColorButton = createColorButton(nodeColor);
        edgeColorButton = createColorButton(edgeColor);
        routeColorButton = createColorButton(routeColor);
        addColorListeners();
    }

    private JButton createColorButton(Color color) {
        JButton button = new JButton(languageService.getText("dialog.config.chooseColor"));
        button.setBackground(color);
        button.setOpaque(true);
        return button;
    }

    private JComboBox<Integer> createNumberCombo(Integer[] values, int selected) {
        JComboBox<Integer> combo = new JComboBox<>(values);
        combo.setSelectedItem(selected);
        return combo;
    }

    private void addColorListeners() {
        nodeColorButton.addActionListener(event -> updateNodeColor());
        edgeColorButton.addActionListener(event -> updateEdgeColor());
        routeColorButton.addActionListener(event -> updateRouteColor());
    }

    private void updateNodeColor() {
        nodeColor = chooseColor(nodeColor, "dialog.config.nodeColor");
        nodeColorButton.setBackground(nodeColor);
    }

    private void updateEdgeColor() {
        edgeColor = chooseColor(edgeColor, "dialog.config.edgeColor");
        edgeColorButton.setBackground(edgeColor);
    }

    private void updateRouteColor() {
        routeColor = chooseColor(routeColor, "dialog.config.routeColor");
        routeColorButton.setBackground(routeColor);
    }

    private Color chooseColor(Color current, String titleKey) {
        Color chosen = JColorChooser.showDialog(this, languageService.getText(titleKey), current);
        return chosen == null ? current : chosen;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(10, 1, 6, 6));
        addColorRow(panel, "dialog.config.nodeColor", nodeColorButton);
        addColorRow(panel, "dialog.config.edgeColor", edgeColorButton);
        addColorRow(panel, "dialog.config.routeColor", routeColorButton);
        addComboRow(panel, "dialog.config.nodeSize", nodeSizeCombo);
        addComboRow(panel, "dialog.config.strokeWidth", strokeWidthCombo);
        return panel;
    }

    private void addColorRow(JPanel panel, String key, JButton button) {
        addLabel(panel, key);
        panel.add(button);
    }

    private void addComboRow(JPanel panel, String key, JComboBox<Integer> combo) {
        addLabel(panel, key);
        panel.add(combo);
    }

    private void addLabel(JPanel panel, String key) {
        panel.add(new JLabel(languageService.getText(key)));
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(createButton(languageService.getText("common.save"), true));
        panel.add(createButton(languageService.getText("common.cancel"), false));
        return panel;
    }

    private JButton createButton(String text, boolean value) {
        JButton button = new JButton(text);
        button.addActionListener(event -> {
            confirmed = value;
            dispose();
        });
        return button;
    }

    public MapStyle buildStyle() {
        MapStyle style = new MapStyle();
        style.setNodeDiameter((Integer) nodeSizeCombo.getSelectedItem());
        style.setEdgeWidth(((Integer) strokeWidthCombo.getSelectedItem()).floatValue());
        style.setNodeColor(nodeColor);
        style.setEdgeColor(edgeColor);
        style.setRouteColor(routeColor);
        return style;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
