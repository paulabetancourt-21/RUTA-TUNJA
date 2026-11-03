package co.edu.uptc.view.help;

import co.edu.uptc.config.ConfigService;
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
import java.util.List;

public class ConfigDialog extends JDialog {

    private final DialogContext context;
    private JButton nodeColorButton;
    private JButton edgeColorButton;
    private JButton routeColorButton;
    private JButton originColorButton;
    private JButton destinationColorButton;
    private Color nodeColor;
    private Color edgeColor;
    private Color routeColor;
    private Color originColor;
    private Color destinationColor;
    private JComboBox<Integer> nodeSizeCombo;
    private JComboBox<Integer> strokeWidthCombo;
    private boolean confirmed;

    public ConfigDialog(JFrame parent, DialogContext context, MapStyle style) {
        super(parent, context.languageService().getText("dialog.config.title"), true);
        this.context = context;
        initDialog();
        initFields(style);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initDialog() {
        int gap = config().getInt("ui.dialogs.contentGap");
        setLayout(new BorderLayout(gap, gap));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(theme().insets("ui.dialogs.configInsets")));
    }

    private void initFields(MapStyle style) {
        initColors(style);
        nodeSizeCombo = createNumberCombo(readValues("nodeSizes"), style.getNodeDiameter());
        strokeWidthCombo = createNumberCombo(readValues("strokeWidths"), Math.round(style.getEdgeWidth()));
    }

    private Integer[] readValues(String key) {
        List<String> values = config().getList("ui.configDialog." + key);
        return values.stream().map(Integer::valueOf).toArray(Integer[]::new);
    }

    private void initColors(MapStyle style) {
        nodeColor = style.getNodeColor();
        edgeColor = style.getEdgeColor();
        routeColor = style.getRouteColor();
        originColor = style.getOriginColor();
        destinationColor = style.getDestinationColor();
        initColorButtons();
        addColorListeners();
    }

    private void initColorButtons() {
        nodeColorButton = createColorButton(nodeColor);
        edgeColorButton = createColorButton(edgeColor);
        routeColorButton = createColorButton(routeColor);
        originColorButton = createColorButton(originColor);
        destinationColorButton = createColorButton(destinationColor);
    }

    private JButton createColorButton(Color color) {
        JButton button = new JButton(context.languageService().getText("dialog.config.chooseColor"));
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
        nodeColorButton.addActionListener(event -> nodeColor = updateColor(nodeColorButton, "nodeColor"));
        edgeColorButton.addActionListener(event -> edgeColor = updateColor(edgeColorButton, "edgeColor"));
        routeColorButton.addActionListener(event -> routeColor = updateColor(routeColorButton, "routeColor"));
        originColorButton.addActionListener(event -> originColor = updateColor(originColorButton, "originColor"));
        destinationColorButton.addActionListener(event -> destinationColor = updateColor(destinationColorButton, "destinationColor"));
    }

    private Color updateColor(JButton button, String key) {
        Color chosen = chooseColor(button.getBackground(), "dialog.config." + key);
        button.setBackground(chosen);
        return chosen;
    }

    private Color chooseColor(Color current, String titleKey) {
        Color chosen = JColorChooser.showDialog(this, context.languageService().getText(titleKey), current);
        return chosen == null ? current : chosen;
    }

    private JPanel createFormPanel() {
        int gap = config().getInt("ui.dialogs.configGridGap");
        JPanel panel = new JPanel(new GridLayout(0, 1, gap, gap));
        addColorRows(panel);
        addComboRows(panel);
        return panel;
    }

    private void addColorRows(JPanel panel) {
        addColorRow(panel, "dialog.config.nodeColor", nodeColorButton);
        addColorRow(panel, "dialog.config.edgeColor", edgeColorButton);
        addColorRow(panel, "dialog.config.routeColor", routeColorButton);
        addColorRow(panel, "dialog.config.originColor", originColorButton);
        addColorRow(panel, "dialog.config.destinationColor", destinationColorButton);
    }

    private void addComboRows(JPanel panel) {
        addComboRow(panel, "dialog.config.nodeSize", nodeSizeCombo);
        addComboRow(panel, "dialog.config.strokeWidth", strokeWidthCombo);
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
        panel.add(new JLabel(context.languageService().getText(key)));
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        int padding = config().getInt("ui.dialogs.configButtonTopPadding");
        panel.setBorder(BorderFactory.createEmptyBorder(padding, 0, 0, 0));
        panel.add(createButton(context.languageService().getText("common.save"), true));
        panel.add(createButton(context.languageService().getText("common.cancel"), false));
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

    public MapStyle buildStyle() {
        MapStyle style = new MapStyle(config());
        applySizes(style);
        applyColors(style);
        return style;
    }

    private void applySizes(MapStyle style) {
        style.setNodeDiameter((Integer) nodeSizeCombo.getSelectedItem());
        style.setEdgeWidth(((Integer) strokeWidthCombo.getSelectedItem()).floatValue());
    }

    private void applyColors(MapStyle style) {
        style.setNodeColor(nodeColor);
        style.setEdgeColor(edgeColor);
        style.setRouteColor(routeColor);
        style.setOriginColor(originColor);
        style.setDestinationColor(destinationColor);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private ConfigService config() {
        return context.configService();
    }

    private DialogTheme theme() {
        return context.theme();
    }
}
